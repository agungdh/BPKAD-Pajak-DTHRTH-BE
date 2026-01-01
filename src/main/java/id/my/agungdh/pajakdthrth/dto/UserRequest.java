package id.my.agungdh.pajakdthrth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import id.my.agungdh.pajakdthrth.model.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotBlank(message = "Username must not be blank")
    @Size(message = "Username must be between 3 and 50 characters", min = 3, max = 50)
    private String username;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Nama must not be blank")
    private String nama;

    @NotNull(message = "Role must not be null")
    private Role role;

    @com.fasterxml.jackson.annotation.JsonProperty("skpd_id")
    private String skpdId; // UUID of SKPD, optional
}
