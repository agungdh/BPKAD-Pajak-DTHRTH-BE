package id.my.agungdh.pajakdthrth.service;

import id.my.agungdh.pajakdthrth.dto.DthRequest;
import id.my.agungdh.pajakdthrth.dto.DthResponse;

import java.util.List;

public interface DthService {
    List<DthResponse> findAll();

    DthResponse findByUuid(String uuid);

    DthResponse create(DthRequest request);

    DthResponse update(String uuid, DthRequest request);

    void delete(String uuid);
}
