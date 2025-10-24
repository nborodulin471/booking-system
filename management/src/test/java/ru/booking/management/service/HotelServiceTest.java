package ru.booking.management.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.booking.management.models.dto.HotelDto;
import ru.booking.management.models.entity.HotelEntity;
import ru.booking.management.models.mappers.HotelMapper;
import ru.booking.management.repository.HotelRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HotelServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private HotelMapper hotelMapper;

    @InjectMocks
    private HotelService hotelService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateNewHotel() {
        // Arrange
        HotelDto hotelDto = new HotelDto(1L, "Grand Hotel", "City Center");
        when(hotelRepository.existsByName("Grand Hotel")).thenReturn(false);
        when(hotelMapper.toEntity(hotelDto)).thenReturn(new HotelEntity());
        when(hotelMapper.toDto(any())).thenReturn(hotelDto);

        // Act
        HotelDto result = hotelService.create(hotelDto);

        // Assert
        assertEquals(hotelDto, result);
        verify(hotelRepository, times(1)).existsByName("Grand Hotel");
        verify(hotelMapper, times(1)).toEntity(hotelDto);
        verify(hotelRepository, times(1)).save(any());
        verify(hotelMapper, times(1)).toDto(any());
    }

    @Test
    void testCreateExistingHotel() {
        // Arrange
        HotelDto hotelDto = new HotelDto(1L, "Grand Hotel", "City Center");
        when(hotelRepository.existsByName("Grand Hotel")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> hotelService.create(hotelDto));
        verify(hotelRepository, times(1)).existsByName("Grand Hotel");
        verify(hotelMapper, never()).toEntity(hotelDto);
        verify(hotelRepository, never()).save(any());
    }

    @Test
    void testFindAllHotels() {
        // Arrange
        HotelDto hotel1 = new HotelDto(1L, "Hotel One", "Address 1");
        HotelDto hotel2 = new HotelDto(2L, "Hotel Two", "Address 2");
        List<HotelEntity> entityList = List.of(
                new HotelEntity(),
                new HotelEntity()
        );
        when(hotelRepository.findAll()).thenReturn(entityList);
        when(hotelMapper.toDto(entityList.get(0))).thenReturn(hotel1);
        when(hotelMapper.toDto(entityList.get(1))).thenReturn(hotel2);

        // Act
        List<HotelDto> result = hotelService.findAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals(hotel1, result.get(0));
        assertEquals(hotel2, result.get(1));
        verify(hotelRepository, times(1)).findAll();
        verify(hotelMapper, times(2)).toDto(any());
    }

    @Test
    void testFindAllHotelsWhenEmpty() {
        // Arrange
        when(hotelRepository.findAll()).thenReturn(List.of());

        // Act
        List<HotelDto> result = hotelService.findAll();

        // Assert
        assertTrue(result.isEmpty());
        verify(hotelRepository, times(1)).findAll();
        verify(hotelMapper, never()).toDto(any());
    }
}