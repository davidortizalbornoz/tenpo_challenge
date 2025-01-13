package cl.tempo.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(schema = "tempo_schema", name = "invoke_history")
@Getter
@Setter
public class InvokeHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_invoke")
    private Long idInvoke;

    @Column(name = "trace_id", nullable = false)
    private String traceId;

    @Column(name = "span_id", nullable = false)
    private String spanId;

    @Column(name = "date_time_invoke", nullable = false)
    private String dateTimeInvoke;

    @Column(name = "http_verb")
    private String httpVerb;

    @Column(name = "endpoint", nullable = false)
    private String endpoint;

    @Column(name = "io_request_response")
    private String ioRequestResponse;

    @Column(name = "http_response_code")
    private String httpResponseCode;

    @Column(name = "type_invoke", nullable = false)
    private String typeInvoke;


}
