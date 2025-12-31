package id.my.agungdh.pajakdthrth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record KodePajakRequest(
                @NotBlank @JsonProperty("nama") String nama) {
}
