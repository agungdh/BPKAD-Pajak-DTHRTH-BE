package id.my.agungdh.pajakdthrth.service;

import id.my.agungdh.pajakdthrth.dto.UserRequest;
import id.my.agungdh.pajakdthrth.dto.UserResponse;
import java.util.List;

public interface UserService {
    UserResponse create(UserRequest request);

    List<UserResponse> findAll();

    UserResponse findByUuid(String uuid);

    UserResponse update(String uuid, UserRequest request);

    void delete(String uuid);
}
