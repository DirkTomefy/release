package mg.bovit.release.repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mg.bovit.release.dto.MaterielStockDto;
import mg.bovit.release.model.MouvementStock;

@Repository
public interface MouvementStockRepository
                extends JpaRepository<MouvementStock, Long>, JpaSpecificationExecutor<MouvementStock> {

        // -------------------------------------------------------------------
        // MÉTHODES POUR LE CALCUL DU STOCK (correctes)
        // -------------------------------------------------------------------

        // 1. Somme des qteRestant des entrées pour chaque matériel (stock actuel)
        @Query("SELECT new mg.bovit.release.dto.MaterielStockDto(ms.materiel, SUM(ms.qteRestant)) " +
               "FROM MouvementStock ms " +
               "WHERE ms.typeMouvement = 'ENTREE' " +
               "GROUP BY ms.materiel")
        List<MaterielStockDto> findAllMaterielStockRestant();

        // 2. Stock actuel d'un matériel spécifique (somme des qteRestant des entrées)
        @Query("SELECT new mg.bovit.release.dto.MaterielStockDto(ms.materiel, SUM(ms.qteRestant)) " +
               "FROM MouvementStock ms " +
               "WHERE ms.materiel.id = :materielId AND ms.typeMouvement = 'ENTREE' " +
               "GROUP BY ms.materiel")
        MaterielStockDto findMaterielStockRestantById(@Param("materielId") Long materielId);

        // 3. Stock actuel pour les matériels d'un type donné
        @Query("SELECT new mg.bovit.release.dto.MaterielStockDto(ms.materiel, SUM(ms.qteRestant)) " +
               "FROM MouvementStock ms " +
               "WHERE ms.materiel.type.id = :typeId AND ms.typeMouvement = 'ENTREE' " +
               "GROUP BY ms.materiel")
        List<MaterielStockDto> findMaterielStockRestantByTypeId(@Param("typeId") Long typeId);

        // 4. Calcule le stock total d'un matériel (entrées - sorties) pour vérification
        @Query("SELECT COALESCE(SUM(CASE WHEN ms.typeMouvement = 'ENTREE' THEN ms.quantite ELSE -ms.quantite END), 0) " +
               "FROM MouvementStock ms WHERE ms.materiel.id = :materielId")
        double getStockActuelByMaterielId(@Param("materielId") Long materielId);

        // 5. Récupère toutes les entrées disponibles pour FIFO/LIFO (avec qteRestant > 0)
        @Query("SELECT ms FROM MouvementStock ms WHERE ms.materiel.id = :materielId AND ms.typeMouvement = 'ENTREE' AND ms.qteRestant > 0 ORDER BY ms.dateMouvement ASC")
        List<MouvementStock> findAllEntreesDisponiblesByIdMateriel(@Param("materielId") Long materielId);

        // -------------------------------------------------------------------
        // MÉTHODES POUR LES SOMMES PAR DATE (inchangées)
        // -------------------------------------------------------------------

        @Query("SELECT COALESCE(SUM(ms.quantite), 0.0) FROM MouvementStock ms WHERE ms.typeMouvement = 'ENTREE' " +
                        "AND ms.dateMouvement <= :date ")
        double findSommeEntreeToDate(Date date);

        @Query("SELECT COALESCE(SUM(ms.quantite), 0.0) FROM MouvementStock ms WHERE ms.typeMouvement = 'SORTIE' " +
                        "AND ms.dateMouvement <= :date ")
        double findSommeSortieToDate(Date date);

        @Query("SELECT COALESCE(SUM(ms.quantite), 0.0) FROM MouvementStock ms WHERE ms.typeMouvement = 'ENTREE' " +
                        "AND ms.materiel.type.id = :idTypeMateriel " +
                        "AND ms.dateMouvement <= :date ")
        double findSommeEntreeTypeMaterielToDate(@Param("idTypeMateriel") Long idTypeMateriel, @Param("date") Date date);

        @Query("SELECT COALESCE(SUM(ms.quantite), 0.0) FROM MouvementStock ms WHERE ms.typeMouvement = 'SORTIE' " +
                        "AND ms.materiel.type.id = :idTypeMateriel " +
                        "AND ms.dateMouvement <= :date ")
        double findSommeSortieTypeMaterielToDate(@Param("idTypeMateriel") Long idTypeMateriel, @Param("date") Date date);

        @Query("SELECT COALESCE(SUM(ms.quantite), 0.0) FROM MouvementStock ms WHERE ms.typeMouvement = 'ENTREE' " +
                        "AND ms.materiel.id = :idMateriel " +
                        "AND ms.dateMouvement <= :date ")
        double findSommeEntreeMaterielToDate(@Param("idMateriel") Long idMateriel, @Param("date") Date date);

        @Query("SELECT COALESCE(SUM(ms.quantite), 0.0) FROM MouvementStock ms WHERE ms.typeMouvement = 'SORTIE' " +
                        "AND ms.materiel.id = :idMateriel " +
                        "AND ms.dateMouvement <= :date ")
        double findSommeSortieMaterielToDate(@Param("idMateriel") Long idMateriel, @Param("date") Date date);
}