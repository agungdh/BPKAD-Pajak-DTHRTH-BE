package id.my.agungdh.pajakdthrth.controller;

import id.my.agungdh.pajakdthrth.dto.UserRequest;
import id.my.agungdh.pajakdthrth.dto.UserResponse;
import id.my.agungdh.pajakdthrth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        UserResponse response = userService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<UserResponse> findByUuid(@PathVariable String uuid) {
        return ResponseEntity.ok(userService.findByUuid(uuid));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<UserResponse> update(@PathVariable String uuid, @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.update(uuid, request));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable String uuid) {
        userService.delete(uuid);
        return ResponseEntity.noContent().build();
    }
}
