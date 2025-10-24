package ru.booking.management.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.booking.management.models.dto.HotelDto;
import ru.booking.management.service.HotelService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HotelControllerTest {

    @Mock
    private HotelService hotelService;

    @InjectMocks
    private HotelController hotelController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddHotel_SuccessfulCreation() {
        // Arrange
        HotelDto hotelDto = new HotelDto(123123L, "Test Hotel", "Test");

        when(hotelService.create(any(HotelDto.class))).thenReturn(hotelDto);

        // Act
        ResponseEntity<HotelDto> response = hotelController.addHotel(hotelDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(hotelDto, response.getBody());
        verify(hotelService, times(1)).create(hotelDto);
    }

    @Test
    void testFindAll_ReturnsHotelsList() {
        // Arrange
        List<HotelDto> hotels = Arrays.asList(
                new HotelDto(123123L, "Hotel 1", "Test"),
                new HotelDto(123123L, "Hotel 2", "Test")
        );

        when(hotelService.findAll()).thenReturn(hotels);

        // Act
        ResponseEntity<List<HotelDto>> response = hotelController.findAll();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(hotels, response.getBody());
        verify(hotelService, times(1)).findAll();
    }

    @Test
    void testFindAll_EmptyList() {
        // Arrange
        when(hotelService.findAll()).thenReturn(Arrays.asList());

        // Act
        ResponseEntity<List<HotelDto>> response = hotelController.findAll();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
        verify(hotelService, times(1)).findAll();
    }

}