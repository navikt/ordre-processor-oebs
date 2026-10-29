package no.nav.ordre.processor.oebs.config;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.ordre.Ordre;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.util.backoff.ExponentialBackOff;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Configuration
@EnableKafka
@Slf4j
public class KafkaConfig {

    @Value("${spring.kafka.properties.schema.registry.url:registry}")
    private String schemaRegistryUrl;

    @Bean
    public ConsumerFactory<String, Ordre> consumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();
        // ErrorHandlingDeserializer fanger feil fra KafkaAvroDeserializer, slik at en ugyldig melding
        // ikke stopper poll(), men sendes til error handleren som en DeserializationException.
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, KafkaAvroDeserializer.class);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
        props.put("schema.registry.url", schemaRegistryUrl);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Ordre> kafkaListenerContainerFactory(
            ConsumerFactory<String, Ordre> consumerFactory, DefaultErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, Ordre> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    @Bean
    public DefaultErrorHandler errorHandler() {
        ExponentialBackOff backOff = new ExponentialBackOff();
        backOff.setInitialInterval(1_000L);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(4 * 60_000L);
        backOff.setMaxElapsedTime(Long.MAX_VALUE);

        // Egen recoverer slik at Springs standardlogging (som skriver ut hele recorden) ikke brukes
        DefaultErrorHandler handler = new DefaultErrorHandler(
                (consumerRecord, ex) -> log.error("Skipping message. {} exception={}",
                        safeDescription(consumerRecord), ex.getClass().getSimpleName()),
                backOff);

        handler.addNotRetryableExceptions(
                DeserializationException.class,
                IllegalArgumentException.class
        );

        handler.setRetryListeners((consumerRecord, ex, attempt) ->
                log.warn("Retry {} failed. {} exception={}",
                        attempt, safeDescription(consumerRecord), ex.getClass().getSimpleName()));

        return handler;
    }

    /**
     * Loggbeskrivelse uten persondata: kun offset, korrelasjonsid, systemid og saksnummer.
     */
    private static String safeDescription(ConsumerRecord<?, ?> consumerRecord) {
        String saksnummer = consumerRecord.value() instanceof Ordre ordre
                ? String.valueOf(ordre.getSaksNummer())
                : null;
        return String.format("offset=%d korrelasjonsid=%s systemid=%s saksnummer=%s",
                consumerRecord.offset(),
                headerValue(consumerRecord, "x-correlation-id"),
                headerValue(consumerRecord, "systemid"),
                saksnummer);
    }

    private static String headerValue(ConsumerRecord<?, ?> consumerRecord, String key) {
        Header header = consumerRecord.headers().lastHeader(key);
        return header == null || header.value() == null
                ? null
                : new String(header.value(), StandardCharsets.UTF_8);
    }


}
