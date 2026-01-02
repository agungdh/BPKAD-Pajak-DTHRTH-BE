package id.my.agungdh.pajakdthrth.service.impl;

import id.my.agungdh.pajakdthrth.dto.DthRequest;
import id.my.agungdh.pajakdthrth.dto.DthResponse;
import id.my.agungdh.pajakdthrth.mapper.DthMapper;
import id.my.agungdh.pajakdthrth.model.Dth;
import id.my.agungdh.pajakdthrth.model.KodePajak;
import id.my.agungdh.pajakdthrth.model.SKPD;
import id.my.agungdh.pajakdthrth.repository.DthRepository;
import id.my.agungdh.pajakdthrth.repository.KodePajakRepository;
import id.my.agungdh.pajakdthrth.repository.SkpdRepository;
import id.my.agungdh.pajakdthrth.service.DthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DthServiceImpl implements DthService {

    private final DthRepository dthRepository;
    private final SkpdRepository skpdRepository;
    private final KodePajakRepository kodePajakRepository;
    private final DthMapper dthMapper;

    @Override
    public List<DthResponse> findAll() {
        return dthRepository.findAll().stream()
                .map(dthMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DthResponse findByUuid(String uuid) {
        Dth dth = dthRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DTH not found"));
        return dthMapper.toResponse(dth);
    }

    @Override
    @Transactional
    public DthResponse create(DthRequest request) {
        SKPD skpd = skpdRepository.findByUuid(request.getSkpdId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SKPD not found"));

        KodePajak kodePajak = kodePajakRepository.findByUuid(request.getKodePajakId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kode Pajak not found"));

        Dth dth = dthMapper.toEntity(request);
        dth.setSkpd(skpd);
        dth.setKodePajak(kodePajak);

        Dth savedDth = dthRepository.save(dth);
        return dthMapper.toResponse(savedDth);
    }

    @Override
    @Transactional
    public DthResponse update(String uuid, DthRequest request) {
        Dth dth = dthRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DTH not found"));

        if (!dth.getSkpd().getUuid().equals(request.getSkpdId())) {
            SKPD skpd = skpdRepository.findByUuid(request.getSkpdId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SKPD not found"));
            dth.setSkpd(skpd);
        }

        SKPD skpd = skpdRepository.findByUuid(request.getSkpdId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SKPD not found"));
        dth.setSkpd(skpd);

        KodePajak kodePajak = kodePajakRepository.findByUuid(request.getKodePajakId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kode Pajak not found"));
        dth.setKodePajak(kodePajak);

        dthMapper.updateEntityFromRequest(request, dth);

        Dth savedDth = dthRepository.save(dth);
        return dthMapper.toResponse(savedDth);
    }

    @Override
    @Transactional
    public void delete(String uuid) {
        Dth dth = dthRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DTH not found"));
        dthRepository.delete(dth);
    }
}
