package no.nav.ordre.processor.oebs.kafka;

import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.ordre.Ordre;
import no.nav.ordre.processor.oebs.service.OrdreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Listener {

    Logger logger = LoggerFactory.getLogger(Listener.class);
    private final OrdreService ordreService;

    public Listener(OrdreService ordreService) {
        this.ordreService = ordreService;
    }


    @KafkaListener(id = "ordrelistener", topics = "${spring.kafka.ordre-topic}")
    public void listen(@Payload Ordre ordre,
                       @Header(name = "x-correlation-id", required = false) String correlationId,
                       @Header(name = "systemid", required = false) String systemId) {
        logger.info("Received message. saksnummer={} korrelasjonsid={} systemid={}",
                ordre.getSaksNummer(), correlationId, systemId);

        //Hvis correlasjonsId eller systemId ikke er satt, så hoppes meldingen over.
        if (correlationId == null || systemId == null) {
            logger.warn("Message with saksnummer {} is ignored. Missing header x-correlation-id {} or systemId {}.", ordre.getSaksNummer(), correlationId, systemId);
            return;
        }

        //todo: Kast InvalidOrdreException hvis formateringen er feil, og logg feilen.
        // Hopp over meldingen ved å legge til exceptionen i listen over ikke-retryable exceptions i errorHandler() i KafkaConfig.java


        ordreService.createOrdre(ordre, correlationId, systemId);

    }

}
