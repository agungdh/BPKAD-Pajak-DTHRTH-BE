package id.my.agungdh.pajakdthrth.dto;

import id.my.agungdh.pajakdthrth.model.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String uuid;
    private String username;
    private String nama;
    private Role role;
    private SkpdResponse skpd;
}
