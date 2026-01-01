package id.my.agungdh.pajakdthrth.service.impl;

import id.my.agungdh.pajakdthrth.dto.SkpdResponse;
import id.my.agungdh.pajakdthrth.dto.UserRequest;
import id.my.agungdh.pajakdthrth.dto.UserResponse;
import id.my.agungdh.pajakdthrth.model.SKPD;
import id.my.agungdh.pajakdthrth.model.User;
import id.my.agungdh.pajakdthrth.repository.SkpdRepository;
import id.my.agungdh.pajakdthrth.repository.UserRepository;
import id.my.agungdh.pajakdthrth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SkpdRepository skpdRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse create(UserRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNama(request.getNama());
        user.setRole(request.getRole());

        if (request.getSkpdId() != null && !request.getSkpdId().isBlank()) {
            SKPD skpd = skpdRepository.findByUuid(request.getSkpdId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SKPD not found"));
            user.setSkpd(skpd);
        }

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findByUuid(String uuid) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return mapToResponse(user);
    }

    @Override
    public UserResponse update(String uuid, UserRequest request) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!user.getUsername().equals(request.getUsername()) &&
                userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        user.setUsername(request.getUsername());
        user.setNama(request.getNama());
        user.setRole(request.getRole());

        // Update password only if provided
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getSkpdId() != null && !request.getSkpdId().isBlank()) {
            SKPD skpd = skpdRepository.findByUuid(request.getSkpdId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SKPD not found"));
            user.setSkpd(skpd);
        } else {
            user.setSkpd(null);
        }

        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    @Override
    public void delete(String uuid) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        userRepository.delete(user);
    }

    private UserResponse mapToResponse(User user) {
        SkpdResponse skpdResponse = null;
        if (user.getSkpd() != null) {
            skpdResponse = new SkpdResponse(user.getSkpd().getUuid(), user.getSkpd().getNama());
        }

        return new UserResponse(
                user.getUuid(),
                user.getUsername(),
                user.getNama(),
                user.getRole(),
                skpdResponse);
    }
}
