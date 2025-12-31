package id.my.agungdh.pajakdthrth.mapper;

import id.my.agungdh.pajakdthrth.dto.KodePajakRequest;
import id.my.agungdh.pajakdthrth.dto.KodePajakResponse;
import id.my.agungdh.pajakdthrth.model.KodePajak;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface KodePajakMapper {
    KodePajakResponse toResponse(KodePajak kodePajak);

    KodePajak toEntity(KodePajakRequest request);

    void updateEntityFromRequest(KodePajakRequest request, @MappingTarget KodePajak entity);
}
