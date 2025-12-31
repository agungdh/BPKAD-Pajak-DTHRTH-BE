package id.my.agungdh.pajakdthrth.service;

import id.my.agungdh.pajakdthrth.dto.KodePajakRequest;
import id.my.agungdh.pajakdthrth.dto.KodePajakResponse;
import id.my.agungdh.pajakdthrth.mapper.KodePajakMapper;
import id.my.agungdh.pajakdthrth.model.KodePajak;
import id.my.agungdh.pajakdthrth.repository.KodePajakRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class KodePajakService {

    private final KodePajakRepository repository;
    private final KodePajakMapper mapper;

    public KodePajakService(KodePajakRepository repository, KodePajakMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public KodePajakResponse create(KodePajakRequest request) {
        if (repository.existsByNama(request.nama())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Kode Pajak already exists");
        }
        KodePajak entity = mapper.toEntity(request);
        KodePajak saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<KodePajakResponse> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public KodePajakResponse findByUuid(String uuid) {
        return repository.findByUuid(uuid)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kode Pajak not found"));
    }

    public KodePajakResponse update(String uuid, KodePajakRequest request) {
        KodePajak entity = repository.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kode Pajak not found"));

        if (!entity.getNama().equals(request.nama()) && repository.existsByNama(request.nama())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Kode Pajak already exists");
        }

        mapper.updateEntityFromRequest(request, entity);
        KodePajak updated = repository.save(entity);
        return mapper.toResponse(updated);
    }

    public void delete(String uuid) {
        KodePajak entity = repository.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kode Pajak not found"));
        repository.delete(entity);
    }
}
