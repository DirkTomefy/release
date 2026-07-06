package mg.bovit.release.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mg.bovit.release.dto.MaterielStockDto;
import mg.bovit.release.model.*;

@Repository
public interface MouvementStockEntreeRepository extends JpaRepository<MouvementStockEntree, Long>, JpaSpecificationExecutor<MouvementStockEntree> {

    @Query("SELECT SUM(mse.qteRestant) AS total_qte_restant\n" + //
                "FROM MouvementStockEntree mse\n" + //
                "WHERE mse.materiel.id = :materielId")
    public Double getQuantiteRestantByIdMateriel(@Param("materielId") Long materielId);

    @Query("SELECT mse FROM MouvementStockEntree mse WHERE mse.materiel.id = :materielId")
    public List<MouvementStockEntree> findAllByIdMateriel(@Param("materielId") Long materielId);
    
    @Query("SELECT new mg.bovit.release.dto.MaterielStockDto(mse.materiel, SUM(mse.qteRestant)) " +
            "FROM MouvementStockEntree mse " +
            "GROUP BY mse.materiel")
    public List<MaterielStockDto> findAllMaterielStockRestant();
}