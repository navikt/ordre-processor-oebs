package no.nav.ordre.processor.oebs.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.ordre.Ordre;
import no.nav.ordre.processor.oebs.exception.InvalidOrdreException;
import no.nav.ordre.processor.oebs.repository.OrdreJpaRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class OrdreService {

    private final OrdreJpaRepository ordreJpaRepository;

    public void createOrdre(Ordre ordre, String correlationid, String systemId) {
        if (!"BESTILLING".equals(ordre.getOrdreType())) {
            log.warn("Order rejected. saksnummer={} korrelasjonsid={} system={} reason=Invalid ordreType ordreType={}",
                    ordre.getSaksnummer(), correlationid, systemId, ordre.getOrdreType());
            throw new InvalidOrdreException("Invalid ordreType. Expected BESTILLING.");
        }

        if (ordre.getFnr() == null || !ordre.getFnr().matches("\\d{11}")) {
            log.warn("Order rejected. saksnummer={} korrelasjonsid={} system={} reason=Invalid fodselNummer format",
                    ordre.getSaksnummer(), correlationid, systemId);
            throw new InvalidOrdreException("Invalid fodselNummer format. Expected exactly 11 digits.");
        }

        //todo: Slette nar digihot switcher til å sende ordre via kafka og ikke via gammel løsning
        if (ordre != null)  {
            log.info("Not saving ordre to database. saksnummer={} korrelasjonsid={} system={}", ordre.getSaksnummer(), correlationid, systemId);
            return;
        }

        ordreJpaRepository.insertOrdre(
                systemId,
                ordre.getFnr(),
                ordre.getFormidlerNavn(),
                ordre.getOrdreType(),
                ordre.getSaksnummer(),
                ordre.getForsendelsesInstruksjoner(),
                ordre.getArtikler().toString(), //todo: endre til å gjøre kall mot artikkel
                correlationid
                );
    }

    //todo: Prøve å endre til å benytte artikler som en egen tabell
}
