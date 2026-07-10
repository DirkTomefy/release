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
public interface MouvementStockRepository extends JpaRepository<MouvementStock, Long>, JpaSpecificationExecutor<MouvementStock> {

    // Calcule la somme de qteRestant sur les mouvements de type ENTREE
    @Query("SELECT SUM(ms.qteRestant) FROM MouvementStock ms WHERE ms.materiel.id = :materielId AND ms.typeMouvement = 'ENTREE'")
    public Double getQuantiteRestantByIdMateriel(@Param("materielId") Long materielId);

    // Recupere les lignes d'entree pour pouvoir appliquer FIFO/LIFO lors d'une sortie
    @Query("SELECT ms FROM MouvementStock ms WHERE ms.materiel.id = :materielId AND ms.typeMouvement = 'ENTREE' AND ms.qteRestant > 0 ORDER BY ms.dateMouvement ASC")
    public List<MouvementStock> findAllEntreesDisponiblesByIdMateriel(@Param("materielId") Long materielId);
    
    @Query("SELECT new mg.bovit.release.dto.MaterielStockDto(ms.materiel, SUM(ms.qteRestant)) " +
            "FROM MouvementStock ms " +
            "WHERE ms.typeMouvement = 'ENTREE' " +
            "GROUP BY ms.materiel")
    public List<MaterielStockDto> findAllMaterielStockRestant();

    @Query("SELECT new mg.bovit.release.dto.MaterielStockDto(ms.materiel, SUM(ms.qteRestant)) " +
            "FROM MouvementStock ms " +
            "WHERE ms.typeMouvement = 'ENTREE' " +
            "AND ms.materiel.id = :idMateriel " +
            "AND ms.dateMouvement <= :date " +
            "GROUP BY ms.materiel")
    public Optional<MaterielStockDto> findMaterielStockRestantOnDate(@Param("date") Date date,Long idMateriel);

    @Query("SELECT new mg.bovit.release.dto.MaterielStockDto(ms.materiel, SUM(ms.qteRestant)) " +
            "FROM MouvementStock ms " +
            "WHERE ms.materiel.id = :materielId AND ms.typeMouvement = 'ENTREE' " +
            "GROUP BY ms.materiel")
    public MaterielStockDto findMaterielStockRestantById(Long materielId);

    @Query("SELECT new mg.bovit.release.dto.MaterielStockDto(ms.materiel, SUM(ms.qteRestant)) " +
            "FROM MouvementStock ms " +
            "WHERE ms.materiel.type.id = :typeId AND ms.typeMouvement = 'ENTREE' " +
            "GROUP BY ms.materiel")
    public List<MaterielStockDto> findMaterielStockRestantByTypeId(Long typeId);
}