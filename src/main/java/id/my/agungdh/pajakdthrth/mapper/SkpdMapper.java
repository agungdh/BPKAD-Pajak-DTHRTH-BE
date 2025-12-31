package id.my.agungdh.pajakdthrth.mapper;

import id.my.agungdh.pajakdthrth.dto.SkpdRequest;
import id.my.agungdh.pajakdthrth.dto.SkpdResponse;
import id.my.agungdh.pajakdthrth.model.SKPD;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SkpdMapper {
    SkpdResponse toResponse(SKPD skpd);

    SKPD toEntity(SkpdRequest request);

    void updateEntityFromRequest(SkpdRequest request, @MappingTarget SKPD skpd);
}
