package cl.tempo.repositories;

import cl.tempo.model.entities.InvokeHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryPostgres extends JpaRepository<InvokeHistoryEntity, Long> {

}
