package id.my.agungdh.pajakdthrth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KodePajakResponse(
                @JsonProperty("uuid") String uuid,

                @JsonProperty("nama") String nama) {
}
