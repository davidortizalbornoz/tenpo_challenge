package cl.tempo.model.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FixedPorcentageResponse {
    private String about_mock;
    private String candidate_name;
    private String candidate_email;
    private Integer fixed_percentage;
}
