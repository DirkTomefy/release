package mg.bovit.release.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mg.bovit.release.model.MvtCaisse;

@Repository
public interface MvtCaisseRepository extends JpaRepository<MvtCaisse, Long>, JpaSpecificationExecutor<MvtCaisse> {

    // Tous les mouvements (toutes caisses confondues) sur une période, triés par date
    List<MvtCaisse> findByDateBetweenOrderByDateAsc(Date dateDebut, Date dateFin);

    // Mouvements d'une caisse précise sur une période, triés par date
    List<MvtCaisse> findByDateBetweenAndCaisse_IdOrderByDateAsc(Date dateDebut, Date dateFin, Long caisseId);

    // Mouvements (toutes caisses) d'une cause précise sur une période, triés par date
    List<MvtCaisse> findByDateBetweenAndCauseCaisse_IdOrderByDateAsc(Date dateDebut, Date dateFin, Long causeCaisseId);

    // Mouvements d'une caisse et d'une cause précises sur une période, triés par date
    List<MvtCaisse> findByDateBetweenAndCaisse_IdAndCauseCaisse_IdOrderByDateAsc(
            Date dateDebut, Date dateFin, Long caisseId, Long causeCaisseId);

    /**
     * Solde réel à une date donnée : somme de TOUS les mouvements
     * (positifs = entrées, négatifs = sorties) depuis le tout début de
     * l'historique jusqu'à la date de fin incluse — pas seulement ceux de
     * la période filtrée. C'est ce qui représente le vrai solde de caisse
     * à cette date, contrairement à un simple "entrées - sorties" limité
     * à l'intervalle affiché.
     */
    @Query("SELECT COALESCE(SUM(m.montant), 0) FROM MvtCaisse m " +
            "WHERE m.date <= :dateFin " +
            "AND (:caisseId IS NULL OR m.caisse.id = :caisseId) " +
            "AND (:causeId IS NULL OR m.causeCaisse.id = :causeId)")
    Double sumMontantJusquA(@Param("dateFin") Date dateFin,
                            @Param("caisseId") Long caisseId,
                            @Param("causeId") Long causeId);
}