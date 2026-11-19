package no.nav.ordre.processor.oebs.repository;

import no.nav.ordre.processor.oebs.repository.entity.Ordre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OrdreJpaRepository extends JpaRepository<Ordre, String> {

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO XXRTV.XXRTV_OE_DIGIHOT_ORDRE_STAGING
            (SYSTEM, FNR, ORDRETYPE, SAKSNUMMER, FORMIDLERNAVN, FORSENDELSESINSTRUKSER, FERDIGSTILL, KORRELASJONSID,
             CREATED_BY, CREATION_DATE, LAST_UPDATED_BY, LAST_UPDATE_DATE)
            VALUES (:systemId, :fodselsnummer, :ordreType, :saksnummer, :formidlernavn, :leveringsinfo, :ferdigstill, :korrelasjonsId,
                    81760, CURRENT_TIMESTAMP, 81760, CURRENT_TIMESTAMP)
            """, nativeQuery = true)
    int insertOrdre(@Param("korrelasjonsId") String korrelasjonsId,
                    @Param("systemId") String systemId,
                    @Param("fodselsnummer") String fodselsnummer,
                    @Param("ordreType") String ordreType,
                    @Param("saksnummer") String saksnummer,
                    @Param("formidlernavn") String formidlernavn,
                    @Param("ferdigstill") String ferdigstill,
                    @Param("leveringsinfo") String leveringsinfo
                    );

}
