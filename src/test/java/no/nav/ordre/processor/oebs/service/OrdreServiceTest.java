package no.nav.ordre.processor.oebs.service;

import no.nav.oebs.ordre.Artikkel;
import no.nav.oebs.ordre.Ordre;
import no.nav.ordre.processor.oebs.exception.InvalidOrdreException;
import no.nav.ordre.processor.oebs.repository.ArtikkelJpaRepository;
import no.nav.ordre.processor.oebs.repository.OrdreJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdreServiceTest {

    private static final String CORRELATION_ID = "test-correlation-id";
    private static final String SYSTEM_ID = "test-system";
    private static final String SYNTHETIC_FNR = "01010050000";

    @Mock
    private OrdreJpaRepository ordreJpaRepository;

    @Mock
    private ArtikkelJpaRepository artikkelJpaRepository;

    @InjectMocks
    private OrdreService ordreService;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void createOrdrePersistsAllOrderFieldsAndArticlesWithSameCorrelationId(boolean ferdigstill) {
        var ordre = validOrdre(ferdigstill,
                List.of(artikkel("ART-101", "2"), artikkel("ART-202", "7")));

        ordreService.createOrdre(ordre, CORRELATION_ID, SYSTEM_ID);

        InOrder writes = inOrder(artikkelJpaRepository, ordreJpaRepository);
        writes.verify(artikkelJpaRepository).insertArtikkel(CORRELATION_ID, "ART-101", "2");
        writes.verify(artikkelJpaRepository).insertArtikkel(CORRELATION_ID, "ART-202", "7");
        writes.verify(ordreJpaRepository).insertOrdre(
                CORRELATION_ID,
                SYSTEM_ID,
                SYNTHETIC_FNR,
                "BESTILLING",
                "SAK-001",
                "Test Formidler",
                Boolean.toString(ferdigstill),
                "Testlevering"
        );
        writes.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @MethodSource("invalidOrderFields")
    void createOrdreRejectsInvalidOrderTypeOrFnrBeforeWrites(String ordreType, String fnr) {
        var ordre = Ordre.newBuilder()
                .setFnr(fnr)
                .setFormidlerNavn("Test Formidler")
                .setSaksnummer("SAK-001")
                .setOrdreType(ordreType)
                .setFerdigstill(true)
                .setArtikler(List.of(artikkel("ART-101", "2")))
                .setForsendelsesInstruksjoner("Testlevering")
                .build();

        assertThatThrownBy(() -> ordreService.createOrdre(ordre, CORRELATION_ID, SYSTEM_ID))
                .isInstanceOf(InvalidOrdreException.class);

        verifyNoInteractions(artikkelJpaRepository, ordreJpaRepository);
    }

    static Stream<Arguments> invalidOrderFields() {
        return Stream.of(
                Arguments.of("ANNEN_TYPE", SYNTHETIC_FNR),
                Arguments.of("BESTILLING", "123")
        );
    }

    @Test
    void createOrdreRejectsInvalidArticleWithoutInsertingOrder() {
        var invalidArtikkel = mock(Artikkel.class);
        var ordre = validOrdre(true, List.of(invalidArtikkel));

        assertThatThrownBy(() -> ordreService.createOrdre(ordre, CORRELATION_ID, SYSTEM_ID))
                .isInstanceOf(InvalidOrdreException.class);

        verifyNoInteractions(artikkelJpaRepository, ordreJpaRepository);
    }

    @Test
    void createOrdrePropagatesArticleRepositoryFailureAndDoesNotInsertOrder() {
        var ordre = validOrdre(true, List.of(artikkel("ART-101", "2")));
        var failure = new IllegalStateException("article repository failure");
        when(artikkelJpaRepository.insertArtikkel(CORRELATION_ID, "ART-101", "2"))
                .thenThrow(failure);

        assertThatThrownBy(() -> ordreService.createOrdre(ordre, CORRELATION_ID, SYSTEM_ID))
                .isSameAs(failure);

        verify(artikkelJpaRepository).insertArtikkel(CORRELATION_ID, "ART-101", "2");
        verifyNoInteractions(ordreJpaRepository);
    }

    @Test
    void createOrdrePropagatesOrderRepositoryFailure() {
        var ordre = validOrdre(true, List.of(artikkel("ART-101", "2")));
        var failure = new IllegalStateException("order repository failure");
        when(ordreJpaRepository.insertOrdre(
                CORRELATION_ID,
                SYSTEM_ID,
                SYNTHETIC_FNR,
                "BESTILLING",
                "SAK-001",
                "Test Formidler",
                "true",
                "Testlevering"
        )).thenThrow(failure);

        assertThatThrownBy(() -> ordreService.createOrdre(ordre, CORRELATION_ID, SYSTEM_ID))
                .isSameAs(failure);

        verify(artikkelJpaRepository).insertArtikkel(CORRELATION_ID, "ART-101", "2");
        verify(ordreJpaRepository).insertOrdre(
                CORRELATION_ID,
                SYSTEM_ID,
                SYNTHETIC_FNR,
                "BESTILLING",
                "SAK-001",
                "Test Formidler",
                "true",
                "Testlevering"
        );
    }

    private static Ordre validOrdre(boolean ferdigstill, List<Artikkel> artikler) {
        return Ordre.newBuilder()
                .setFnr(SYNTHETIC_FNR)
                .setFormidlerNavn("Test Formidler")
                .setSaksnummer("SAK-001")
                .setOrdreType("BESTILLING")
                .setFerdigstill(ferdigstill)
                .setArtikler(artikler)
                .setForsendelsesInstruksjoner("Testlevering")
                .build();
    }

    private static Artikkel artikkel(String artikkelnummer, String antall) {
        return Artikkel.newBuilder()
                .setArtikkelnummer(artikkelnummer)
                .setAntall(antall)
                .build();
    }
}
