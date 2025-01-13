package cl.tempo.config;

import cl.tempo.common.ConstantsAppTempo;
import cl.tempo.components.FixedPorcentageComponent;
import cl.tempo.model.responses.FixedPorcentageResponse;
import cl.tempo.services.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.*;

import java.net.UnknownHostException;

@Configuration
public class InitialLoadObjects
{

    @Autowired
    RedisService redisService;

    @Autowired
    FixedPorcentageComponent fixedPorcentageComponent;

    @Bean
    public CommandLineRunner bulkInitialData()
    {
        return (args) ->
        {
            invokeWithRetry();
        };
    }


    public void invokeWithRetry() throws IllegalAccessException {
        Integer fixPorcentage;

        try
        {
            ResponseEntity<FixedPorcentageResponse> responseFixed = fixedPorcentageComponent.invokeCall();
            if (responseFixed.getStatusCode().is2xxSuccessful())
            {
                fixPorcentage = responseFixed.getBody().getFixed_percentage();
                redisService.guardarEnRedis("LATEST", fixPorcentage);
                redisService.guardarEnRedisConExpiracion("FIXED_AMOUNT_TENPO", fixPorcentage, 30);
                System.out.println("fixPorcentage ->"+ fixPorcentage);
            }
        }
        catch(Exception ex)
        {
            System.out.println("Exception ------->"+ex);
        }
    }


}
