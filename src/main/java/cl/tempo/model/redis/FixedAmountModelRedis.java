package cl.tempo.model.redis;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@RedisHash("FixedAmountModelRedis")
public class FixedAmountModelRedis
{
    @Id
    private String id;
    private Integer fixed_percentage;
}
