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
@Table(name = "XXRTV_OE_DIGIHOT_ORDRE_STAGING", schema = "XXRTV")
public class Ordre {

    @Id
    @Column(name= "KORRELASJONSID")
    private String korrelasjonsId;

    @Column(name= "SYSTEM")
    private String systemId;

    @Column(name = "FNR")
    private String fnr;

    @Column(name = "FORMIDLERNAVN")
    private String formidlerNavn;

    @Column(name = "ORDRETYPE")
    private String ordreType;

    @Column(name = "SAKSNUMMER")
    private String saksnummer;

    @Column(name = "FORSENDELSESINSTRUKSER")
    private String leveringsinfo;

    @Column(name = "FERDIGSTILL")
    private String ferdigstilt;

    @Column(name = "ORDRENUMMER")
    private String ordrenummer;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATION_DATE")
    private LocalDateTime creationDate;

    @Column(name = "LAST_UPDATED_BY")
    private String lastUpdatedBy;

    @Column(name = "LAST_UPDATE_DATE")
    private LocalDateTime lastUpdateDate;
}
