package id.my.agungdh.pajakdthrth.controller;

import id.my.agungdh.pajakdthrth.dto.DthRequest;
import id.my.agungdh.pajakdthrth.dto.DthResponse;
import id.my.agungdh.pajakdthrth.service.DthService;
import id.my.agungdh.pajakdthrth.service.PdfService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dth")
@RequiredArgsConstructor
public class DthController {

    private final DthService dthService;
    private final PdfService pdfService;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<DthResponse> findAll() {
        return dthService.findAll();
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public DthResponse findByUuid(@PathVariable String uuid) {
        return dthService.findByUuid(uuid);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public DthResponse create(@RequestBody @Valid DthRequest request) {
        return dthService.create(request);
    }

    @PutMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public DthResponse update(@PathVariable String uuid, @RequestBody @Valid DthRequest request) {
        return dthService.update(uuid, request);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable String uuid) {
        dthService.delete(uuid);
    }

    @GetMapping("/export/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public org.springframework.http.ResponseEntity<byte[]> exportPdf() {
        List<DthResponse> dthList = dthService.findAll();
        java.io.ByteArrayInputStream bis = pdfService.generateDthReport(dthList);

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=dth_report.pdf");

        return org.springframework.http.ResponseEntity
                .ok()
                .headers(headers)
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(bis.readAllBytes());
    }
}
