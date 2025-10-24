package ru.booking.management.service;

import org.springframework.stereotype.Service;

import ru.booking.management.models.dto.HotelDto;
import ru.booking.management.models.mappers.HotelMapper;
import ru.booking.management.repository.HotelRepository;

import java.util.List;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;

    public HotelService(HotelRepository hotelRepository, HotelMapper hotelMapper) {
        this.hotelRepository = hotelRepository;
        this.hotelMapper = hotelMapper;
    }

    public HotelDto create(HotelDto hotel) {
        if (hotelRepository.existsByName(hotel.name())) {
            throw new IllegalArgumentException("Данный отель уже существует");
        }

        return hotelMapper.toDto((hotelRepository.save(hotelMapper.toEntity(hotel))));
    }

    public List<HotelDto> findAll() {
        return hotelRepository.findAll().stream()
                .map(hotelMapper::toDto)
                .toList();
    }
}
