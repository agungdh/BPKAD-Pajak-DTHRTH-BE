package id.my.agungdh.pajakdthrth.service;

import id.my.agungdh.pajakdthrth.dto.SkpdRequest;
import id.my.agungdh.pajakdthrth.dto.SkpdResponse;
import id.my.agungdh.pajakdthrth.mapper.SkpdMapper;
import id.my.agungdh.pajakdthrth.model.SKPD;
import id.my.agungdh.pajakdthrth.repository.SkpdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SkpdService {

    private final SkpdRepository skpdRepository;
    private final SkpdMapper skpdMapper;

    public List<SkpdResponse> findAll() {
        return skpdRepository.findAll().stream()
                .map(skpdMapper::toResponse)
                .toList();
    }

    public SkpdResponse findByUuid(String uuid) {
        return skpdRepository.findByUuid(uuid)
                .map(skpdMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SKPD not found"));
    }

    @Transactional
    public SkpdResponse create(SkpdRequest request) {
        SKPD skpd = skpdMapper.toEntity(request);
        skpd = skpdRepository.save(skpd);
        return skpdMapper.toResponse(skpd);
    }

    @Transactional
    public SkpdResponse update(String uuid, SkpdRequest request) {
        SKPD skpd = skpdRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SKPD not found"));
        skpdMapper.updateEntityFromRequest(request, skpd);
        skpd = skpdRepository.save(skpd);
        return skpdMapper.toResponse(skpd);
    }

    @Transactional
    public void delete(String uuid) {
        SKPD skpd = skpdRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SKPD not found"));
        skpdRepository.delete(skpd);
    }
}
