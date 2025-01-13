package cl.tempo.aspects;

import java.io.IOException;
import cl.tempo.common.Common;
import cl.tempo.dto.NumerosRequest;

import cl.tempo.model.entities.InvokeHistoryEntity;
import cl.tempo.model.responses.GenericResponse;
import cl.tempo.services.InvokeHistoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;



@Aspect
@Order(1)
@Component
@ConditionalOnExpression("${endpoint.aspect.enabled:true}")
public class EndpointAspect {

    private static final Logger logger = LoggerFactory.getLogger(EndpointAspect.class);
    
    @Autowired
    private Common common;

    @Autowired
    private Tracer tracer;

    @Autowired
    private InvokeHistoryService invokeHistoryService;

    private final TaskExecutor taskExecutor;

    @Autowired
    public EndpointAspect(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    @Before("within(cl.tempo.controllers..*)")
    public void endpointBefore(JoinPoint p)
    {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Span span = tracer.spanBuilder("span-tempo").startSpan();
        String traceId = span.getSpanContext().getTraceId();
        String spanId =  span.getSpanContext().getSpanId();
        System.out.println("TRACE_ID: "+ traceId);
        System.out.println("SPAN_ID: "+ spanId);
        RequestContextHolder.getRequestAttributes().setAttribute("TRACE_ID", traceId, RequestAttributes.SCOPE_REQUEST);
        RequestContextHolder.getRequestAttributes().setAttribute("SPAN_ID", spanId, RequestAttributes.SCOPE_REQUEST);
        RequestContextHolder.getRequestAttributes().setAttribute("HTTP_VERB", request.getMethod(), RequestAttributes.SCOPE_REQUEST);
        RequestContextHolder.getRequestAttributes().setAttribute("URI", request.getRequestURI(), RequestAttributes.SCOPE_REQUEST);
        span.end();

        taskExecutor.execute(() ->
        {
            InvokeHistoryEntity newInvokeRequest = new InvokeHistoryEntity();

            if (logger.isInfoEnabled())
            {
                Object[] signatureArgs = p.getArgs();
                if (request.getMethod().equals(HttpMethod.GET.name()))
                {
                    logger.info("{}", common.prettyPrinterOneLine(signatureArgs));
                    newInvokeRequest.setIoRequestResponse(common.prettyPrinterOneLine(signatureArgs));
                }

                if (request.getMethod().equals(HttpMethod.POST.name()))
                {
                    if (signatureArgs[0] instanceof NumerosRequest)
                    {
                        logger.info("{}", common.prettyPrinterOneLine(signatureArgs[0]));
                        newInvokeRequest.setIoRequestResponse(common.prettyPrinterOneLine(signatureArgs[0]));
                    }
                    else
                    {
                        try
                        {
                            logger.info("{}", common.prettyPrinterOneLine(IOUtils.toString(request.getReader())));
                            newInvokeRequest.setIoRequestResponse(common.prettyPrinterOneLine(IOUtils.toString(request.getReader())));
                        } catch (IOException e) {

                        }

                    }
                }

                newInvokeRequest.setTraceId(traceId);
                newInvokeRequest.setSpanId(spanId);
                newInvokeRequest.setDateTimeInvoke(common.getCurrentDateTime());
                newInvokeRequest.setHttpVerb(request.getMethod());
                newInvokeRequest.setEndpoint(request.getRequestURI());
                newInvokeRequest.setHttpResponseCode(null);
                newInvokeRequest.setTypeInvoke("REQUEST");

                invokeHistoryService.saveInvokeHistory(newInvokeRequest);
            }
        });

    }

    @AfterReturning(value = ("within(cl.tempo.controllers..*)"), returning = "returnValue")
    public void endpointAfterReturning(JoinPoint p, Object returnValue)
    {
        String httpVerb = ((String) RequestContextHolder.getRequestAttributes().getAttribute("HTTP_VERB", RequestAttributes.SCOPE_REQUEST));
        String httpUri = ((String) RequestContextHolder.getRequestAttributes().getAttribute("URI", RequestAttributes.SCOPE_REQUEST));

        taskExecutor.execute(() ->
        {
            InvokeHistoryEntity newInvokeResponse = new InvokeHistoryEntity();

            if (logger.isInfoEnabled())
            {
                ResponseEntity<?> responseEntity = (ResponseEntity<?>) returnValue;
                Object body = responseEntity.getBody();

                if (body instanceof GenericResponse) {
                    ResponseEntity<GenericResponse> responseEntityAux = ResponseEntity
                            .status(responseEntity.getStatusCode())
                            .headers(responseEntity.getHeaders())
                            .body((GenericResponse) body);

                    GenericResponse genericResponse = responseEntityAux.getBody();
                    logger.info("{}", common.prettyPrinterOneLine(returnValue));

                    newInvokeResponse.setTraceId(genericResponse.getResume().getTraceId());
                    newInvokeResponse.setSpanId(genericResponse.getResume().getSpanId());
                    newInvokeResponse.setDateTimeInvoke(common.getCurrentDateTime());
                    newInvokeResponse.setHttpVerb(httpVerb);
                    newInvokeResponse.setEndpoint(httpUri);
                    newInvokeResponse.setHttpResponseCode(String.valueOf(responseEntityAux.getStatusCode().value()));
                    newInvokeResponse.setTypeInvoke("RESPONSE");
                    newInvokeResponse.setIoRequestResponse(common.prettyPrinterOneLine(returnValue));
                    invokeHistoryService.saveInvokeHistory(newInvokeResponse);
                }
            }
        });
    }


    @AfterThrowing(pointcut = ("within(cl.tempo.controllers..*)"), throwing = "e")
    public void endpointAfterThrowing(JoinPoint p, Exception e) throws Exception {
        if (logger.isInfoEnabled()) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            logger.error(p.getTarget().getClass().getSimpleName() + " " + p.getSignature().getName() + " " + e.getMessage());
        }
        throw e;
    }
}