package no.nav.ordre.processor.oebs.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.ordre.Ordre;
import no.nav.ordre.processor.oebs.exception.InvalidOrdreException;
import no.nav.ordre.processor.oebs.repository.ArtikkelJpaRepository;
import no.nav.ordre.processor.oebs.repository.OrdreJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class OrdreService {

    private final OrdreJpaRepository ordreJpaRepository;
    private final ArtikkelJpaRepository artikkelJpaRepository;

    @Transactional
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

        ordre.getArtikler().forEach(
                artikkel -> {
                    if (artikkel.getArtikkelnummer() == null || artikkel.getAntall() == null) {
                        log.warn("Order rejected. saksnummer={} korrelasjonsid={} system={} reason=Invalid artikkel data artikkelNummer={} antall={}",
                                ordre.getSaksnummer(), correlationid, systemId, artikkel.getArtikkelnummer(), artikkel.getAntall());
                        throw new InvalidOrdreException("Invalid artikkel data. Artikkelnummer and Antall must be valid.");
                    }
                    artikkelJpaRepository.insertArtikkel(
                            correlationid,
                            artikkel.getArtikkelnummer(),
                            artikkel.getAntall()
                    );
                }
        );
        ordreJpaRepository.insertOrdre(
                correlationid,
                systemId,
                ordre.getFnr(),
                ordre.getOrdreType(),
                ordre.getSaksnummer(),
                ordre.getFormidlerNavn(),
                ordre.getFerdigstill().toString(),
                ordre.getForsendelsesInstruksjoner()
                );
    }

}
