package no.nav.ordre.processor.oebs.repository;

import no.nav.ordre.processor.oebs.repository.entity.Artikkel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ArtikkelJpaRepository extends JpaRepository<Artikkel, String> {

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO XXRTV.XXRTV_OE_DIGIHOT_ARTIKLER_STAGING
            (KORRELASJONSID, ARTIKKELNUMMER, ANTALL, CREATED_BY, CREATION_DATE, LAST_UPDATED_BY, LAST_UPDATE_DATE)
            VALUES (:korrelasjonsId, :artikkelNummer, :antall, 81760, CURRENT_TIMESTAMP, 81760, CURRENT_TIMESTAMP)
            """, nativeQuery = true)
    int insertArtikkel(@Param("korrelasjonsId") String korrelasjonsId,
                       @Param("artikkelNummer") String artikkelNummer,
                       @Param("antall") String antall);
}
