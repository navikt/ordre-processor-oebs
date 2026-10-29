package no.nav.ordre.processor.oebs.repository;

import no.nav.oebs.ordre.Artikkel;
import no.nav.ordre.processor.oebs.repository.entity.Ordre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OrdreJpaRepository extends JpaRepository<Ordre, Long> {

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO xxrtv.xxrtv_staging_oe_digihot_ordre
            (SYSTEMID, FODSELSNUMMER, FORMIDLERNAVN, ORDRETYPE, SAKSNUMMER, LEVERINGSINFO, CREATION_DATE, KORRELASJONSID)
            VALUES (:systemId, :fodselsnummer, :formidlernavn, :ordreType, :saksnummer, :leveringsinfo, CURRENT_TIMESTAMP, :korrelasjonsId)
            """, nativeQuery = true)
    int insertOrdre(@Param("systemId") String systemId,
                    @Param("fodselsnummer") String fodselsnummer,
                    @Param("formidlernavn") String formidlernavn,
                    @Param("ordreType") String ordreType,
                    @Param("saksnummer") String saksnummer,
                    @Param("leveringsinfo") String leveringsinfo,
                    @Param("artikler") String artikler,//todo: finne ut hva vi skal gjøre med artikler
                    @Param("korrelasjonsId") String korrelasjonsId
                    );

    //todo: Avklare hva som skal settes av avro objektet
//    Parametere som ligger i tabellen, men som er usikker på hvordan skal settes:
//    Systemid: Dette virker å være en id som blir generert automatis
//    Artikler: Kan denne være i format av en egen tabell? Nå må vi sende det inn som json. Det er ikke så heldig
//    Ferdigstilt: Skal denne settes ved innsettelse?
//    Status: Skal denne settes ved innsettelse?
//    Ordrenummer: Skal denne settes i skjemaet?
//    Hentekvittering: Skal denne settes i skjemaet?
//    Creatiion_date: Skal denne settes av java tjensten?


}
