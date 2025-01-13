package cl.tempo.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Optional;

@Component
public class Common {

    public String getCurrentDateTime()
    {
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return sdf2.format(timestamp);
    }

    public Optional<HttpServletRequest> getCurrentHttpRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(requestAttributes -> ServletRequestAttributes.class.isAssignableFrom(requestAttributes.getClass()))
                .map(requestAttributes -> ((ServletRequestAttributes) requestAttributes))
                .map(ServletRequestAttributes::getRequest);
    }

    public String prettyPrinterOneLine(Object result)
    {
        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = null;
        try
        {
            jsonResult = mapper.writer().writeValueAsString(result);
        }
        catch (JsonProcessingException e)
        {

        }
        return StringEscapeUtils.escapeJava(jsonResult);
    }
}
