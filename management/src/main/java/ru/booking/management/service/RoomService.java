package ru.booking.management.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import ru.booking.common.models.RecommendResponse;
import ru.booking.common.models.RoomDto;
import ru.booking.management.models.mappers.RoomMapper;
import ru.booking.management.repository.HotelRepository;
import ru.booking.management.repository.RoomRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final RoomMapper roomMapper;

    public RoomService(RoomRepository roomRepository, HotelRepository hotelRepository, RoomMapper roomMapper) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
        this.roomMapper = roomMapper;
    }

    @Transactional
    public RoomDto createRoom(RoomDto roomDto) {
        if (roomRepository.existsByNumber(roomDto.number())) {
            throw new IllegalArgumentException("Данный номер уже существует");
        }

        if (!hotelRepository.existsById(roomDto.hotelId())) {
            throw new IllegalArgumentException("Отеля не существует");
        }

        return roomMapper.toDto(
                roomRepository.save(roomMapper.toEntity(roomDto))
        );
    }

    public RecommendResponse getRecommended() {
        return new RecommendResponse(
                roomRepository.findRecommendedRooms().stream()
                        .map(roomMapper::toDto)
                        .toList()
        );
    }

    public List<RoomDto> getAvailable() {
        return roomRepository.findAvailableRooms().stream()
                .map(roomMapper::toDto)
                .toList();
    }

    public Boolean confirmAvailability(Long id) {
        var room = roomRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Room is not found"));

        if (!room.isAvailable()) {
            return false;
        }

        room.setAvailable(false);
        room.setTimeBooked(room.getTimeBooked() + 1);
        roomRepository.save(room);

        return true;
    }

    public void releaseRoom(Long id) {
        var room = roomRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Room is not found"));

        room.setTimeBooked(room.getTimeBooked() - 1);
        room.setAvailable(true);

        roomRepository.save(room);
    }
}
