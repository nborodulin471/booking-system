package ru.booking.management.models.mappers;

import org.springframework.stereotype.Component;

import ru.booking.management.models.dto.HotelDto;
import ru.booking.management.models.entity.HotelEntity;

@Component
public class HotelMapper {

    public HotelEntity toEntity(HotelDto dto) {
        if (dto == null) {
            return null;
        }
        return new HotelEntity(
                dto.id(),
                dto.name(),
                dto.address()
        );
    }

    public HotelDto toDto(HotelEntity entity) {
        if (entity == null) {
            return null;
        }
        return new HotelDto(
                entity.getId(),
                entity.getName(),
                entity.getAddress()
        );
    }

}
