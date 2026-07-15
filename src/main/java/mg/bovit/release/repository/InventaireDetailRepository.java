package mg.bovit.release.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mg.bovit.release.model.InventaireDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventaireDetailRepository extends JpaRepository<InventaireDetail, Long> {

    List<InventaireDetail> findByInventaire_Id(Long inventaireId);

    @Query("SELECT d.inventaire.id, SUM(d.quantiteFinale) FROM InventaireDetail d WHERE d.inventaire.id IN :ids GROUP BY d.inventaire.id")
    List<Object[]> sumQuantiteFinaleByInventaireIds(@Param("ids") List<Long> ids);
}
