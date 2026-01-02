package id.my.agungdh.pajakdthrth.controller;

import id.my.agungdh.pajakdthrth.dto.KodePajakRequest;
import id.my.agungdh.pajakdthrth.dto.KodePajakResponse;
import id.my.agungdh.pajakdthrth.service.KodePajakService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kode-pajak")
@PreAuthorize("hasRole('ADMIN')")
public class KodePajakController {

    private final KodePajakService service;

    public KodePajakController(KodePajakService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<KodePajakResponse> create(@Valid @RequestBody KodePajakRequest request) {
        return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<KodePajakResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<KodePajakResponse> findByUuid(@PathVariable String uuid) {
        return ResponseEntity.ok(service.findByUuid(uuid));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<KodePajakResponse> update(@PathVariable String uuid,
            @Valid @RequestBody KodePajakRequest request) {
        return ResponseEntity.ok(service.update(uuid, request));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable String uuid) {
        service.delete(uuid);
        return ResponseEntity.noContent().build();
    }
}
