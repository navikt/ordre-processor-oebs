package no.nav.ordre.processor.oebs.service;

import lombok.AllArgsConstructor;
import no.nav.oebs.ordre.Ordre;
import no.nav.ordre.processor.oebs.repository.OrdreJpaRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class OrdreService {

    private final OrdreJpaRepository ordreJpaRepository;

    public void createOrdre(Ordre ordre, String correlationid, String systemId) {
        ordreJpaRepository.insertOrdre(
                systemId,
                ordre.getFodselNummer(),
                ordre.getFormidlerNavn(),
                "BESTILLING",
                ordre.getSaksNummer(),
                ordre.getLeveringsInfo(),
                ordre.getArtikler().toString(),
                correlationid
                );
    }

    //todo: Prøve å endre til å benytte artikler som en egen tabell
}
