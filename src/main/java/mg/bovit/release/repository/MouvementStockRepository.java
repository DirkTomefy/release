package mg.bovit.release.repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import mg.bovit.release.model.MouvementStock;

@Repository
public interface MouvementStockRepository
                extends JpaRepository<MouvementStock, Long>, JpaSpecificationExecutor<MouvementStock> {

        // -------------------------------------------------------------------
        // ANCIENNES MÉTHODES ERRONÉES – COMMENTÉES / SUPPRIMÉES
        // -------------------------------------------------------------------
        // @Query("SELECT SUM(ms.qteRestant) FROM MouvementStock ms WHERE ms.materiel.id = :materielId AND ms.typeMouvement = 'ENTREE'")
        // public Double getQuantiteRestantByIdMateriel(@Param("materielId") Long materielId);

        // @Query("SELECT new mg.bovit.release.dto.MaterielStockDto(ms.materiel, SUM(ms.qteRestant)) FROM MouvementStock ms WHERE ms.typeMouvement = 'ENTREE' GROUP BY ms.materiel")
        // public List<MaterielStockDto> findAllMaterielStockRestant();

        // @Query("SELECT new mg.bovit.release.dto.MaterielStockDto(ms.materiel, SUM(ms.qteRestant)) FROM MouvementStock ms WHERE ms.materiel.id = :materielId AND ms.typeMouvement = 'ENTREE' GROUP BY ms.materiel")
        // public MaterielStockDto findMaterielStockRestantById(Long materielId);

        // -------------------------------------------------------------------
        // NOUVELLES MÉTHODES CORRECTES
        // -------------------------------------------------------------------

        // 1. Retourne le dernier mouvement d'un matériel (pour obtenir qteRestant actuel)
        Optional<MouvementStock> findTopByMaterielIdOrderByDateMouvementDescIdDesc(Long materielId);

        // 2. Calcule le stock actuel d'un matériel (entrées - sorties)
        @Query("SELECT COALESCE(SUM(CASE WHEN ms.typeMouvement = 'ENTREE' THEN ms.quantite ELSE -ms.quantite END), 0) " +
               "FROM MouvementStock ms WHERE ms.materiel.id = :materielId")
        double getStockActuelByMaterielId(@Param("materielId") Long materielId);

        // 3. Retourne le dernier stock pour chaque matériel (requête native pour performance)
        @Query(value = "SELECT m.id, m.libelle, m.id_type_materiel, t.libelle AS type_libelle, " +
                       "COALESCE(ms.qte_restant, 0) AS qte_restant " +
                       "FROM materiel m " +
                       "LEFT JOIN type_materiel t ON t.id = m.id_type_materiel " +
                       "LEFT JOIN mouvement_stock ms ON ms.id = ( " +
                       "    SELECT ms2.id FROM mouvement_stock ms2 " +
                       "    WHERE ms2.id_materiel = m.id " +
                       "    ORDER BY ms2.date_mouvement DESC, ms2.id DESC LIMIT 1" +
                       ")", nativeQuery = true)
        List<Object[]> findAllMaterielWithLastStock();

        // 4. Retourne le dernier stock pour les matériels d'un type donné
        @Query(value = "SELECT m.id, m.libelle, m.id_type_materiel, t.libelle AS type_libelle, " +
                       "COALESCE(ms.qte_restant, 0) AS qte_restant " +
                       "FROM materiel m " +
                       "LEFT JOIN type_materiel t ON t.id = m.id_type_materiel " +
                       "LEFT JOIN mouvement_stock ms ON ms.id = ( " +
                       "    SELECT ms2.id FROM mouvement_stock ms2 " +
                       "    WHERE ms2.id_materiel = m.id " +
                       "    ORDER BY ms2.date_mouvement DESC, ms2.id DESC LIMIT 1" +
                       ") " +
                       "WHERE m.id_type_materiel = :typeId", nativeQuery = true)
        List<Object[]> findMaterielStockRestantByTypeIdNative(@Param("typeId") Long typeId);

        // -------------------------------------------------------------------
        // MÉTHODES EXISTANTES CONSERVÉES (pas de changement)
        // -------------------------------------------------------------------

        @Query("SELECT ms FROM MouvementStock ms WHERE ms.materiel.id = :materielId AND ms.typeMouvement = 'ENTREE' AND ms.qteRestant > 0 ORDER BY ms.dateMouvement ASC")
        public List<MouvementStock> findAllEntreesDisponiblesByIdMateriel(@Param("materielId") Long materielId);

        @Query("SELECT IFNULL(SUM(ms.quantite), 0.0) FROM MouvementStock ms WHERE ms.typeMouvement = 'ENTREE' " +
                        "AND ms.dateMouvement <= :date ")
        public double findSommeEntreeToDate(Date date);

        @Query("SELECT IFNULL(SUM(ms.quantite), 0.0) FROM MouvementStock ms WHERE ms.typeMouvement = 'SORTIE' " +
                        "AND ms.dateMouvement <= :date ")
        public double findSommeSortieToDate(Date date);

        @Query("SELECT IFNULL(SUM(ms.quantite), 0.0) FROM MouvementStock ms WHERE ms.typeMouvement = 'ENTREE' " +
                        "AND ms.materiel.type.id = :idTypeMateriel " +
                        "AND ms.dateMouvement <= :date ")
        public double findSommeEntreeTypeMaterielToDate(Long idTypeMateriel, Date date);

        @Query("SELECT IFNULL(SUM(ms.quantite), 0.0) FROM MouvementStock ms WHERE ms.typeMouvement = 'SORTIE' " +
                        "AND ms.materiel.type.id = :idTypeMateriel " +
                        "AND ms.dateMouvement <= :date ")
        public double findSommeSortieTypeMaterielToDate(Long idTypeMateriel, Date date);

        @Query("SELECT IFNULL(SUM(ms.quantite), 0.0) FROM MouvementStock ms WHERE ms.typeMouvement = 'ENTREE' " +
                        "AND ms.materiel.id = :idMateriel " +
                        "AND ms.dateMouvement <= :date ")
        public double findSommeEntreeMaterielToDate(Long idMateriel, Date date);

        @Query("SELECT IFNULL(SUM(ms.quantite), 0.0) FROM MouvementStock ms WHERE ms.typeMouvement = 'SORTIE' " +
                        "AND ms.materiel.id = :idMateriel " +
                        "AND ms.dateMouvement <= :date ")
        public double findSommeSortieMaterielToDate(Long idMateriel, Date date);
}