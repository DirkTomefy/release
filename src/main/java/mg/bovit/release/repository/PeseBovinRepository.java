package mg.bovit.release.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mg.bovit.release.model.*;

@Repository
public interface PeseBovinRepository extends JpaRepository<PeseBovin, Long> {

    // function to get latest bovin pese by id
    @Query(value = "SELECT * FROM pese_bovin pb WHERE pb.id_bovin = :id_bovin ORDER BY pb.date_pese DESC LIMIT 1", nativeQuery = true)
    PeseBovin getLatestPeseByBovin(@Param("id_bovin") Long id_bovin);
}