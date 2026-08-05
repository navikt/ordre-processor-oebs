package no.nav.ordre.processor.oebs.kafka;

import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.ordre.Ordre;
import no.nav.ordre.processor.oebs.db.CreateOrdreProcedure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Listener {

    Logger logger = LoggerFactory.getLogger(Listener.class);

    private final CreateOrdreProcedure createOrdreProcedure;

    public Listener(CreateOrdreProcedure createOrdreProcedure) {
        this.createOrdreProcedure = createOrdreProcedure;
    }

    @KafkaListener(id = "ordrelistener", topics = "${spring.kafka.ordre-topic}")
    public void listen(Ordre ordre) {
        logger.info("Received message: {}", ordre);
        createOrdreProcedure.executeCreateOrdre(ordre);
        //todo: Legge til feilhantering
    }

}
