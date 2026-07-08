package mg.bovit.release.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.bovit.release.model.MvtCaisse;

@Repository
public interface MvtCaisseRepository extends JpaRepository<MvtCaisse, Long> {

    // Tous les mouvements (toutes caisses confondues) sur une période, triés par date
    List<MvtCaisse> findByDateBetweenOrderByDateAsc(Date dateDebut, Date dateFin);

    // Mouvements d'une caisse précise sur une période, triés par date
    List<MvtCaisse> findByDateBetweenAndCaisse_IdOrderByDateAsc(Date dateDebut, Date dateFin, Long caisseId);
}