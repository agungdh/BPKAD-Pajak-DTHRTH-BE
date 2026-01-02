package id.my.agungdh.pajakdthrth.controller;

import id.my.agungdh.pajakdthrth.dto.SkpdRequest;
import id.my.agungdh.pajakdthrth.dto.SkpdResponse;
import id.my.agungdh.pajakdthrth.service.SkpdService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skpd")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class SkpdController {

    private final SkpdService skpdService;

    @GetMapping
    public List<SkpdResponse> findAll() {
        return skpdService.findAll();
    }

    @GetMapping("/{uuid}")
    public SkpdResponse findByUuid(@PathVariable String uuid) {
        return skpdService.findByUuid(uuid);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SkpdResponse create(@RequestBody @Valid SkpdRequest request) {
        return skpdService.create(request);
    }

    @PutMapping("/{uuid}")
    public SkpdResponse update(@PathVariable String uuid, @RequestBody @Valid SkpdRequest request) {
        return skpdService.update(uuid, request);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String uuid) {
        skpdService.delete(uuid);
    }
}
