package cl.tempo.services;

import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService
{
    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;

    public void guardarEnRedisConExpiracion(String key, Integer valor, long tiempoExpiracionMinutos) {
        try
        {
            redisTemplate.opsForValue().set(key, valor, tiempoExpiracionMinutos, TimeUnit.MINUTES);
        }
        catch(Exception e){
            System.err.println("Stacktrace - guardarEnRedisConExpiracion:");
            e.printStackTrace(System.err);
            throw e;
        }

    }

    public void guardarEnRedis(String key, Integer valor) {
        try{
            redisTemplate.opsForValue().set(key, valor);
        }
        catch(Exception e){
            System.err.println("Stacktrace - guardarEnRedis:");
            e.printStackTrace(System.err);
            throw e;
        }

    }

    public Integer obtenerDeRedis(String key) {
        return (Integer) redisTemplate.opsForValue().get(key);
    }

}
