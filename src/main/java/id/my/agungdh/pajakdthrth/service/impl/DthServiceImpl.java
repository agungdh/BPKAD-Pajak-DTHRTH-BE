package id.my.agungdh.pajakdthrth.service.impl;

import id.my.agungdh.pajakdthrth.dto.DthRequest;
import id.my.agungdh.pajakdthrth.dto.DthResponse;
import id.my.agungdh.pajakdthrth.mapper.DthMapper;
import id.my.agungdh.pajakdthrth.model.Dth;
import id.my.agungdh.pajakdthrth.model.SKPD;
import id.my.agungdh.pajakdthrth.repository.DthRepository;
import id.my.agungdh.pajakdthrth.repository.SkpdRepository;
import id.my.agungdh.pajakdthrth.service.DthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DthServiceImpl implements DthService {

    private final DthRepository dthRepository;
    private final SkpdRepository skpdRepository;
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

        Dth dth = dthMapper.toEntity(request);
        dth.setSkpd(skpd);
        calculateTotalTax(dth);

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

        dthMapper.updateEntityFromRequest(request, dth);
        calculateTotalTax(dth);

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

    private void calculateTotalTax(Dth dth) {
        BigDecimal total = Stream.of(
                dth.getPajakPpn(),
                dth.getPajakPph21(),
                dth.getPajakPph22(),
                dth.getPajakPph23(),
                dth.getPajakPph4Ayat2())
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        dth.setJumlahPajak(total);
    }
}
