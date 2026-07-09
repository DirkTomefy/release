package mg.bovit.release.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import mg.bovit.release.dto.MortaliteStatsDTO;
import mg.bovit.release.model.Bovin;
import mg.bovit.release.repository.BovinRepository;
import mg.bovit.release.repository.MortaliteRepository;
import mg.bovit.release.repository.PeseBovinRepository;
import mg.bovit.release.repository.VenteDetailRepository;

@ExtendWith(MockitoExtension.class)
class MortaliteServiceTest {

    @Mock
    private MortaliteRepository mortaliteRepository;

    @Mock
    private BovinRepository bovinRepository;

    @Mock
    private PeseBovinRepository peseBovinRepository;

    @Mock
    private VenteDetailRepository venteDetailRepository;

    @InjectMocks
    private MortaliteService mortaliteService;

    @Test
    void getStats_shouldReturnTotalPriceAndMonthlyPrices() {
        when(mortaliteRepository.countMortalitesWithFilters(any(), any(), any())).thenReturn(2L);
        when(mortaliteRepository.sumPrixMortalitesWithFilters(any(), any(), any())).thenReturn(4500.0);
        when(mortaliteRepository.findMortaliteStatsGroupedByMonth(any(), any(), any())).thenReturn(List.of(
                new Object[]{"2024-01", 2L, 3000.0},
                new Object[]{"2024-02", 1L, 1500.0}
        ));

        MortaliteStatsDTO stats = mortaliteService.getStats(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 2, 28), 1L);

        assertThat(stats.getTotalMortalites()).isEqualTo(2L);
        assertThat(stats.getTotalPrixMortalites()).isEqualTo(4500.0);
        assertThat(stats.getPrixTotals()).containsExactly(3000.0, 1500.0);
    }

    @Test
    void declareMortalite_shouldRejectBovinReferencedInSale() throws Exception {
        Bovin bovin = new Bovin();
        bovin.setId(10L);

        when(bovinRepository.findById(10L)).thenReturn(Optional.of(bovin));
        when(venteDetailRepository.existsByBovin_Id(10L)).thenReturn(true);

        assertThatThrownBy(() -> mortaliteService.declareMortalite(10L, LocalDate.of(2024, 1, 15)))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("encore associé à une vente");

        verifyNoInteractions(peseBovinRepository);
    }
}
