package cl.tempo.repositories;

import cl.tempo.model.redis.FixedAmountModelRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryRedis extends CrudRepository<FixedAmountModelRedis, String> {
}
