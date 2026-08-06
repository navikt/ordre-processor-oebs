package no.nav.ordre.processor.oebs.kafka;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import no.nav.oebs.ordre.Ordre;
import no.nav.oebs.ordre.OrdreType;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Map;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"team-oebs.oebs-ordre.v1"})
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.properties.security.protocol=PLAINTEXT",
        "KAFKA_SCHEMA_REGISTRY=mock://test"
})
class ListenerIntegrationTest {

    @Autowired
    private KafkaTemplate<String, Ordre> kafkaTemplate;

    @MockitoSpyBean
    private Listener listener;

    @Test
    void skalDeserialisereOgKonsumereOrdre() throws Exception {
        var ordre = Ordre.newBuilder()
                .setFodselNummer("01010050000") // syntetisk fødselsnummer
                .setFormidlerNavn("Test Formidler")
                .setOrdreType(OrdreType.REKVISISJON)
                .setSaksNummer("SAK-001")
                .setRequestId(UUID.randomUUID().toString())
                .build();

        kafkaTemplate.send("team-oebs.oebs-ordre.v1", ordre).get();

        await().atMost(10, SECONDS).untilAsserted(() ->
                verify(listener).listen(argThat(mottatt ->
                        "SAK-001".equals(mottatt.getSaksNummer()) &&
                        OrdreType.REKVISISJON == mottatt.getOrdreType()
                ))
        );
    }

    @TestConfiguration
    static class TestProducerConfig {

        @Bean
        KafkaTemplate<String, Ordre> kafkaTemplate(
                @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {

            Map<String, Object> props = Map.of(
                    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class,
                    "schema.registry.url", "mock://test"
            );
            return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
        }
    }
}
