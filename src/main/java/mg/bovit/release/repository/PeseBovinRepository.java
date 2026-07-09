package mg.bovit.release.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mg.bovit.release.model.PeseBovin;

@Repository
public interface PeseBovinRepository extends JpaRepository<PeseBovin, Long>, JpaSpecificationExecutor<PeseBovin> {

    // function to get latest bovin pese by id
    @Query(value = "SELECT * FROM pese_bovin pb WHERE pb.id_bovin = :id_bovin ORDER BY pb.date_pese DESC, pb.id DESC LIMIT 1", nativeQuery = true)
    PeseBovin getLatestPeseByBovin(@Param("id_bovin") Long id_bovin);

    @Query("SELECT p FROM PeseBovin p WHERE p.bovin.id = :bovinId ORDER BY p.date_pese ASC")
    List<PeseBovin> findByBovinIdOrderByDatePeseAsc(@Param("bovinId") Long bovinId);
}