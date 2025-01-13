package cl.tempo.dto;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
public class NumerosRequest {

    @NotNull
    private Integer num1;


    @NotNull
    private Integer num2;
}
