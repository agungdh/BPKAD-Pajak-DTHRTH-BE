package id.my.agungdh.pajakdthrth.mapper;

import id.my.agungdh.pajakdthrth.dto.DthRequest;
import id.my.agungdh.pajakdthrth.dto.DthResponse;
import id.my.agungdh.pajakdthrth.model.Dth;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = { SkpdMapper.class })
public interface DthMapper {
    DthResponse toResponse(Dth dth);

    @Mapping(target = "skpd", ignore = true) // Handled in Service
    @Mapping(target = "jumlahPajak", ignore = true) // Calculated in Service
    Dth toEntity(DthRequest request);

    @Mapping(target = "skpd", ignore = true) // Handled in Service
    @Mapping(target = "jumlahPajak", ignore = true) // Calculated in Service
    void updateEntityFromRequest(DthRequest request, @MappingTarget Dth entity);
}
