# OEBS Order Exchange – Utvalgte arkitekturalternativer

## Formål

Etablere et generelt grensesnitt for ordre og kvitteringer mellom systemer.

Foreslått applikasjonsnavn:

**oebs-order-exchange**

---

# 1. Kafka-only

```text
System A
     |
     v
order.commands
     |
     v
Ordremotor
     |
     v
order.receipts
```

## Fordeler

- Løs kobling
- Høy skalerbarhet
- Naturlig asynkron modell
- God robusthet

## Ulemper

- Alle integrasjonspartnere må bruke Kafka
- Høyere terskel for integrasjon

---


---

# Integrasjonsstrategier

## Strategi A – Felles topic

```text
order.receipts
```

Alle mottar meldinger og filtrerer på relevante attributter.

### Fordeler

- Enkel topic-struktur
- Få topics

### Ulemper

- Krever filtrering
- Svakere isolasjon mellom konsumenter

---

## Strategi B – Topic per partner

```text
order.receipts.partnera
order.receipts.partnerb
order.receipts.partnerc
```

### Fordeler

- Enkel tilgangsstyring
- Tydelig eierskap
- God dataseparasjon

### Ulemper

- Flere topics å administrere

---

## Strategi C – Topic per domene

```text
orders.commands
orders.receipts
orders.status
```

### Fordeler

- Enkelt å forstå
- Lett å utvide

### Ulemper

- Krever filtrering eller tilgangsstyring

---

# Foreløpig anbefaling

Gitt at Kafka-plattform allerede finnes:

```text
REST inn
    |
    v
oebs-order-exchange
    |
    v
Kafka
```

Kombinert med:

```text
Topic per partner
```

for kvitteringer og svarmeldinger.


XXRTV_CS_DIGIHOT_OPPRETT_SF.startup_mottak'

{"Fodselsnummer": "06115735339"
,"FormidlerNavn": "GLENN ARNE ALVAD"
,"OrdreType": "REKVISISJON"
,"SaksNummer": "HOTSAK 20"} Er denne unik? Vi ønsker en korrelasjonsid

kvitteringsmeling OrdreOpprettelse her
Typer kvitteringsmelinger:
Feil
osv

OpprettOrdre

SFINFO
INFO
KVITTERING
KVITTERINGFEIL

oversikt over alle tabeller/views som brukes
oversikt over pakkekall i kjeden

Testrammeverk for kafka, slik at vi ikke trenger å involvere alle integrasjonspartnere i testingen.
Dette kan være en egen topic som brukes til å simulere meldinger.

Oebs-iac repo for topics


## Kvitteringer og svarmeldinger

For ordreoppfølging bør alle meldinger ha en korrelasjons-id (`requestId`), slik at vi kan knytte svar og kvitteringer til riktig ordre gjennom hele kjeden.

her er en ordreopprettelse som eksempel:
```json
{
  "Fodselsnummer": "123456789012",
  "FormidlerNavn": "OLA NORDMANN",
  "OrdreType": "REKVISISJON",
  "SaksNummer": "HOTSAK XX"
}
```
Eksempel på felter vi ønsker i meldinger inn:
- `requestId` (obligatorisk korrelasjon)

her er en kvittering som eksempel:
```json
{
  "id": "123456789012",
  "saksNummer": "HOTSAK XX"
}
```

Eksempel på felter vi ønsker i meldinger inn:
- `requestId` (obligatorisk korrelasjon)
- `status` (for eksempel `RECEIVED`, `PROCESSING`, `COMPLETED`, `FAILED`)
- `eventTime`
- `sourceSystem`
- `errorCode` og `errorMessage` ved feil

Forslag til mapping av eksisterende kvitteringstyper:
- `ORDREKVITT`: vellykket kvittering
- `ORDREFEIL`: feilmelding
- `INFO`: generell informasjon om behandlingen
- `SF_INFO`: fagspesifikk eller ekstra informasjon i behandlingsløpet

## Dokumentasjon av eksisterende integrasjoner

Lagt ved dokumentasjon om integrasjonene. `XXRTVDHOT` består av:
- [ORDREFEIL](https://github.com/navikt/oebs/blob/main/bin/XXRTVDHOT_ORDREFEIL.prog)
- [ORDREKVITT](https://github.com/navikt/oebs/blob/main/bin/XXRTVDHOT_ORDREKVITT.prog)
- [INFO](https://github.com/navikt/oebs/blob/main/bin/XXRTVDHOT_INFO.prog)
- [SF_INFO](https://github.com/navikt/oebs/blob/main/bin/XXRTVDHOT_SFINFO.prog)

I tillegg ligger `OpprettOrdre` i oebs integration-repository, og pakkene som kalles finnes her:
- [XXRTV_DIGIHOT_ONT_API_PKG.pks](https://github.com/navikt/oebs/blob/main/admin/sql/xxrtv_digihot_ont_api_pkg.pks)
- [XXRTV_DIGIHOT_ONT_API_PKG.pkb](https://github.com/navikt/oebs/blob/main/admin/sql/xxrtv_digihot_ont_api_pkg.pkb)

## Videre dokumentasjon som bør på plass

- Oversikt over alle tabeller og views som brukes i kjeden
- Oversikt over pakkekall i riktig rekkefølge
- Testrammeverk for Kafka med egne test-topics, slik at vi kan teste uten å involvere alle integrasjonspartnere
- Topic-definisjoner og policyer i `oebs-iac`-repoet