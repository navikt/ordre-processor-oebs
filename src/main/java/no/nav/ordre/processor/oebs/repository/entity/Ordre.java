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
@Table(name = "staging_xxrtv_oe_digihot_ordre", schema = "XXRTV")
public class Ordre {

    @Id
    @Column(name= "KORRELASJONSID")
    private String korrelasjonsId;

    @Column(name= "SYSTEMID")
    private String systemId;

    @Column(name = "FODSELSNUMMER")
    private String fnr;

    @Column(name = "ORDRETYPE") //todo: Er det bare "BESTILLING" som skal settes her?
    private String ordreType;

    @Column(name = "FORMIDLERNAVN")
    private String formidlerNavn;

    @Column(name = "SAKSNUMMER")
    private String saksnummer;

    @Column(name = "ARTIKLER", columnDefinition = "CLOB")
    private String artikler;

    @Column(name = "LEVERINGINFO")
    private String leveringsinfo;

    @Column(name = "FERDIGSTILT")
    private String ferdigstilt;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "ORDRENUMMER")
    private LocalDateTime ordrenummer;

    @Column(name = "HENTEKVITTERING")
    private String hentekvittering;
}
