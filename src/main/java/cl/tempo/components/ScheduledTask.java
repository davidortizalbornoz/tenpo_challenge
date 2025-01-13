package cl.tempo.components;

import cl.tempo.model.responses.FixedPorcentageResponse;
import cl.tempo.services.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ScheduledTask
{
    @Autowired
    RedisService redisService;

    @Autowired
    FixedPorcentageComponent fixedPorcentageComponent;

    @Scheduled(cron = "0 */30 * * * *")
    public void runEvery30Minutes() throws IllegalAccessException
    {
        try
        {
            Integer fixPorcentage;
            System.out.println("Tarea ejecutada a las: " + LocalDateTime.now());
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
