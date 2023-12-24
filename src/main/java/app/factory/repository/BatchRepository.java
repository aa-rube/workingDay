package app.factory.repository;

import app.factory.model.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;
import java.util.List;
@Repository
public interface BatchRepository extends JpaRepository<Batch, Integer> {
    @Modifying
    @Query("DELETE FROM Batch b WHERE b.id = :id")
    void deleteCustomQuery(@Param("id") int id);
}