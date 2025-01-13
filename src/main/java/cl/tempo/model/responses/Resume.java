package cl.tempo.model.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
public class Resume
{
	private boolean success;
	private String message;
	private String code;
	private String errorCode;
	private String timestamp;
	private String traceId;
	private String spanId;
}
