package no.nav.ordre.processor.oebs.kafka;

import no.nav.oebs.ordre.Ordre;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class Listener {

    @KafkaListener(id = "ordrelistener", topics = "${spring.kafka.ordre-topic}")
    public void listen(Ordre ordre) {
        System.out.println("Received message: " + ordre.toString());

    }

}
