package mg.bovit.release.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mg.bovit.release.model.*;

@Repository
public interface MouvementStockEntreeRepository extends JpaRepository<MouvementStockEntree, Long>, JpaSpecificationExecutor<MouvementStockEntree> {

    @Query("SELECT SUM(qte_restant) AS total_qte_restant\n" + //
                "FROM MouvementStockEntree mse\n" + //
                "WHERE mse.materiel.id = :materielId")
    Double getQuantiteRestantByIdMateriel(@Param("materielId") Long materielId);
    
}