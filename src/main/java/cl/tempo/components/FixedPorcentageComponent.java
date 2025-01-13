package cl.tempo.components;

import cl.tempo.model.responses.FixedPorcentageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Component
public class FixedPorcentageComponent {

    @Value("${mock.fixed.percentage}")
    private String uri;

    @Autowired
    RetryTemplate retryTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Environment env;

    public ResponseEntity<FixedPorcentageResponse> invokeCall() throws ResourceAccessException, IllegalAccessException
    {

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        return  retryTemplate.execute(context -> restTemplate.exchange(uri, HttpMethod.GET, entity, FixedPorcentageResponse.class));

    }
}
