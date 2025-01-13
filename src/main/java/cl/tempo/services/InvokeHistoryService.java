package cl.tempo.services;

import cl.tempo.common.Common;
import cl.tempo.common.ConstantsAppTempo;
import cl.tempo.model.entities.InvokeHistoryEntity;
import cl.tempo.model.responses.CalculatedResponse;
import cl.tempo.model.responses.GenericResponse;
import cl.tempo.model.responses.Resume;
import cl.tempo.repositories.RepositoryPostgres;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;

@Service
public class InvokeHistoryService
{

    @Autowired
    private Common common;

    @Autowired
    private RepositoryPostgres repositoryPostgres;

    public InvokeHistoryEntity saveInvokeHistory(InvokeHistoryEntity invokeHistory) {
        return repositoryPostgres.saveAndFlush(invokeHistory);
    }


    public ResponseEntity<?> getAllHistoryCallWithPagination(Pageable pageable)
    {
        GenericResponse<Page<InvokeHistoryEntity>> response = new GenericResponse<Page<InvokeHistoryEntity>>();
        try
        {
            Page<InvokeHistoryEntity> results =  repositoryPostgres.findAll(pageable);
            if (!results.isEmpty()){
                response.setData(results);
                response.setResume(this.makeResume(true, ConstantsAppTempo.CODE_OK, "Historial de invocaciones obtenido con éxito", common.getCurrentDateTime()));
                return ResponseEntity.ok().body(response);
            }
            else
            {
                response.setData(null);
                response.setResume(this.makeResume(false, ConstantsAppTempo.CODE_WARNING, "Historial de invocaciones no encontrado", common.getCurrentDateTime()));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        }
        catch(Exception e)
        {
            response.setData(null);
            response.setResume(this.makeResume(false, ConstantsAppTempo.CODE_EXCEPTION, e.getMessage(), common.getCurrentDateTime()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

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
}
