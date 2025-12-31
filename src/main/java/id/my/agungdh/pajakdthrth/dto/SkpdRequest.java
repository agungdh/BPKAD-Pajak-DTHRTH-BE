package id.my.agungdh.pajakdthrth.dto;

import jakarta.validation.constraints.NotBlank;

public record SkpdRequest(
                @NotBlank String nama) {
}
