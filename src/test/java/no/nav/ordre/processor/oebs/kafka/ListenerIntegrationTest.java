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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class ListenerIntegrationTest {

    static final Network NETWORK = Network.newNetwork();

    @Container
    static final KafkaContainer KAFKA = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.6.0"))
            .withNetwork(NETWORK)
            .withNetworkAliases("kafka");

    // Schema Registry kobles til Kafka via det interne Docker-nettverket på port 9092 (BROKER-listener)
    @Container
    static final GenericContainer<?> SCHEMA_REGISTRY = new GenericContainer<>(
            DockerImageName.parse("confluentinc/cp-schema-registry:7.6.0"))
            .withNetwork(NETWORK)
            .withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry")
            .withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", "PLAINTEXT://kafka:9092")
            .withExposedPorts(8081)
            .dependsOn(KAFKA);

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
        registry.add("KAFKA_SCHEMA_REGISTRY",
                () -> "http://localhost:" + SCHEMA_REGISTRY.getMappedPort(8081));
        // Testcontainers bruker ikke SSL — override protokoll fra test-application.yaml
        registry.add("spring.kafka.properties.security.protocol", () -> "PLAINTEXT");
    }

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
                @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
                @Value("${KAFKA_SCHEMA_REGISTRY}") String schemaRegistryUrl) {

            Map<String, Object> props = Map.of(
                    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class,
                    "schema.registry.url", schemaRegistryUrl
            );
            return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
        }
    }
}
