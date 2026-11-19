package no.nav.ordre.processor.oebs.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor
@Table(name = "XXRTV_OE_DIGIHOT_ARTIKLER_STAGING", schema = "XXRTV")
public class Artikkel {

    @Id
    @Column(name= "KORRELASJONSID")
    private String korrelasjonsId;

    @Column(name= "ARTIKKELNUMMER")
    private String artikkelNummer;

    @Column(name = "ANTALL")
    private String antall;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATION_DATE")
    private LocalDateTime creationDate;

    @Column(name = "LAST_UPDATED_BY")
    private String lastUpdatedBy;

    @Column(name = "LAST_UPDATE_DATE")
    private LocalDateTime lastUpdateDate;
}
