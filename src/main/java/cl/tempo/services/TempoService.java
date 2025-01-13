package cl.tempo.services;

import cl.tempo.common.Common;
import cl.tempo.common.ConstantsAppTempo;
import cl.tempo.dto.NumerosRequest;
import cl.tempo.model.entities.InvokeHistoryEntity;
import cl.tempo.model.responses.CalculatedResponse;
import cl.tempo.model.responses.GenericResponse;
import cl.tempo.model.responses.Resume;
import cl.tempo.repositories.RepositoryPostgres;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Service
public class TempoService {

    @Autowired
    private Common common;

    @Autowired
    private RedisService redisService;

    @Autowired
    RepositoryPostgres repositoryPostgres;

    @Value("${app.fixed.percentage}")
    private Integer appFixedPorcentage;

    public Resume makeResume(boolean success_, String code_, String msg_, String timestamp_)
    {
        Resume resume = new Resume();
        resume.setSuccess(success_);
        resume.setCode(code_);
        resume.setMessage(msg_);
        resume.setTimestamp(timestamp_);
        resume.setTraceId((String) RequestContextHolder.getRequestAttributes().getAttribute("TRACE_ID", RequestAttributes.SCOPE_REQUEST));
        resume.setSpanId((String) RequestContextHolder.getRequestAttributes().getAttribute("SPAN_ID", RequestAttributes.SCOPE_REQUEST));
        return resume;
    }


    public ResponseEntity<?> calcular(NumerosRequest requestObj)
    {
        CalculatedResponse calculateObj = new CalculatedResponse();
        GenericResponse<CalculatedResponse> response = new GenericResponse<CalculatedResponse>();

        try
        {
            float suma = requestObj.getNum1() + requestObj.getNum2();
            System.out.println("suma ->"+ suma);
            int fixPorcentage;
            float calc;

            Integer responseFixedAmount = redisService.obtenerDeRedis("FIXED_AMOUNT_TENPO");
            if (responseFixedAmount != null)
            {
                System.out.println("responseFixedAmount ->"+ responseFixedAmount);
                calc = (suma * responseFixedAmount) / (float) 100;
                calculateObj.setResult(suma + calc);
                response.setData(calculateObj);
                response.setResume(this.makeResume(true, ConstantsAppTempo.CODE_OK, "Cálculo efectuado con exito - key:FIXED_AMOUNT_TENPO", common.getCurrentDateTime()));
                return ResponseEntity.ok().body(response);
            }
            else
            {
                Integer responseFixedLatest = redisService.obtenerDeRedis("LATEST");
                if (responseFixedLatest != null)
                {
                    System.out.println("responseFixedLatest ->"+ responseFixedLatest);
                    calc = (suma * responseFixedLatest) / (float) 100;
                    calculateObj.setResult(suma + calc);
                    response.setData(calculateObj);
                    response.setResume(this.makeResume(true, ConstantsAppTempo.CODE_OK, "Cálculo efectuado con exito - key:LATEST", common.getCurrentDateTime()));
                    return ResponseEntity.ok().body(response);
                }
                else
                {
                    System.out.println("appFixedPorcentage ->"+ appFixedPorcentage);
                    calc = (suma * appFixedPorcentage) / (float) 100;
                    calculateObj.setResult(suma + calc);
                    response.setData(calculateObj);
                    response.setResume(this.makeResume(true, ConstantsAppTempo.CODE_OK, "Cálculo efectuado con exito - param:app.fixed.percentage", common.getCurrentDateTime()));
                    return ResponseEntity.ok().body(response);
                }
            }



        }
        catch(Exception e)
        {
            response.setData(null);
            response.setResume(this.makeResume(false, ConstantsAppTempo.CODE_EXCEPTION, e.getMessage(), common.getCurrentDateTime()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
