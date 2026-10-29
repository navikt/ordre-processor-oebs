package no.nav.ordre.processor.oebs.kafka;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import no.nav.oebs.ordre.Ordre;
import no.nav.ordre.processor.oebs.config.KafkaConfig;
import no.nav.ordre.processor.oebs.service.OrdreService;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {KafkaConfig.class, Listener.class, ListenerIntegrationTest.TestProducerConfig.class})
@EmbeddedKafka(partitions = 1, topics = {"team-oebs.oebs-ordre.v1"})
@DirtiesContext
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.properties.security.protocol=PLAINTEXT",
        "spring.kafka.properties.schema.registry.url=mock://listener-integration-test"
})
class ListenerIntegrationTest {

    @Autowired
    private KafkaTemplate<String, Ordre> kafkaTemplate;

    @MockitoBean
    private OrdreService ordreService;

    @Test
    void shouldDeserializeAndConsumeOrder() throws Exception {
        var ordre = Ordre.newBuilder()
                .setFodselNummer("01010050000") // syntetisk fødselsnummer
                .setFormidlerNavn("Test Formidler")
                .setSaksNummer("SAK-001")
                .setArtikler(List.of())
                .setLeveringsInfo("Testlevering")
                .build();

        var message = MessageBuilder.withPayload(ordre)
                .setHeader(KafkaHeaders.TOPIC, "team-oebs.oebs-ordre.v1")
                .setHeader("x-correlation-id", "test-correlation-id")
                .setHeader("systemid", "test-system")
                .build();

        kafkaTemplate.send(message).get(10, SECONDS);

        await().atMost(10, SECONDS).untilAsserted(() ->
                verify(ordreService).createOrdre(ordre, "test-correlation-id", "test-system")
        );
    }

    @TestConfiguration
    @EnableConfigurationProperties(KafkaProperties.class)
    static class TestProducerConfig {

        @Bean
        KafkaTemplate<String, Ordre> kafkaTemplate(
                @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {

            Map<String, Object> props = Map.of(
                    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class,
                    "schema.registry.url", "mock://listener-integration-test"
            );
            return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
        }
    }
}
