package ru.booking.management.models.mappers;

import org.springframework.stereotype.Component;

import jakarta.ws.rs.NotFoundException;
import ru.booking.common.models.RoomDto;
import ru.booking.management.models.entity.RoomEntity;
import ru.booking.management.repository.HotelRepository;

@Component
public class RoomMapper {

    private final HotelRepository hotelRepository;

    public RoomMapper(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public RoomEntity toEntity(RoomDto dto) {
        if (dto == null) {
            return null;
        }
        return new RoomEntity(
                null,
                hotelRepository.findById(dto.hotelId()).orElseThrow(NotFoundException::new),
                dto.number(),
                dto.availability(),
                0
        );
    }

    public RoomDto toDto(RoomEntity entity) {
        if (entity == null) {
            return null;
        }
        return new RoomDto(
                entity.getId(),
                entity.getHotel().getId(),
                entity.getNumber(),
                entity.isAvailable(),
                entity.getTimeBooked()
        );
    }

}
