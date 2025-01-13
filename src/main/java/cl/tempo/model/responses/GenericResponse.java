package cl.tempo.model.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenericResponse<T> 
{
	private T data;
	
	@JsonProperty("resume")
	private Resume resume;
	

}
