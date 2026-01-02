package id.my.agungdh.pajakdthrth.service;

import java.io.ByteArrayInputStream;
import java.util.List;
import id.my.agungdh.pajakdthrth.dto.DthResponse;

public interface PdfService {
    ByteArrayInputStream generateDthReport(List<DthResponse> dthList);
}
