package no.nav.ordre.processor.oebs.config;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import no.nav.oebs.ordre.Ordre;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class KafkaConfigTest {

    @Autowired
    ConsumerFactory<String, Ordre> consumerFactory;

    @Test
    void consumerFactory_usesErrorHandlingDeserializerWithAvroDelegate() {
        assertThat(consumerFactory.getConfigurationProperties())
                .containsEntry(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class)
                .containsEntry(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, KafkaAvroDeserializer.class);
    }

    @Test
    void consumerFactory_hasSpecificAvroReader() {
        assertThat(consumerFactory.getConfigurationProperties())
                .containsEntry(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
    }

    @Test
    void consumerFactory_hasSchemaRegistryUrl() {
        assertThat(consumerFactory.getConfigurationProperties())
                .containsKey("schema.registry.url");

        String url = (String) consumerFactory.getConfigurationProperties()
                .get("schema.registry.url");
        assertThat(url).isNotBlank();
    }

    @Test
    void consumerFactory_hasCorrectGroupId() {
        assertThat(consumerFactory.getConfigurationProperties())
                .containsEntry(ConsumerConfig.GROUP_ID_CONFIG, "ordre-processor-oebs");
    }

    @Test
    void consumerFactory_hasAutoOffsetResetEarliest() {
        assertThat(consumerFactory.getConfigurationProperties())
                .containsEntry(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    }
}
