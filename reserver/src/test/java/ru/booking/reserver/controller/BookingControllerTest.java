package ru.booking.reserver.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.booking.reserver.model.dto.booking.BookingDto;
import ru.booking.reserver.model.dto.booking.CreateBookingRequest;
import ru.booking.reserver.service.BookingService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateBooking_SuccessfulCreation() {
        // Arrange
        CreateBookingRequest request = mock(CreateBookingRequest.class);
        BookingDto expectedResponse = mock(BookingDto.class);

        when(bookingService.createBooking(request)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<BookingDto> response = bookingController.createBooking(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(bookingService, times(1)).createBooking(request);
    }

    @Test
    void testGetBookings_ReturnsAllBookings() {
        // Arrange
        List<BookingDto> expectedBookings = List.of(mock(BookingDto.class), mock(BookingDto.class));

        when(bookingService.getBookings()).thenReturn(expectedBookings);

        // Act
        ResponseEntity<List<BookingDto>> response = bookingController.getBookings();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedBookings, response.getBody());
        verify(bookingService, times(1)).getBookings();
    }

    @Test
    void testGetBookings_EmptyList() {
        // Arrange
        when(bookingService.getBookings()).thenReturn(List.of());

        // Act
        ResponseEntity<List<BookingDto>> response = bookingController.getBookings();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(bookingService, times(1)).getBookings();
    }

    @Test
    void testGetBooking_ValidId() {
        // Arrange
        long id = 1L;
        BookingDto expectedBooking = mock(BookingDto.class);

        when(bookingService.getBooking(id)).thenReturn(expectedBooking);

        // Act
        ResponseEntity<BookingDto> response = bookingController.getBooking(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedBooking, response.getBody());
        verify(bookingService, times(1)).getBooking(id);
    }

    @Test
    void testCancelBooking_ValidId() {
        // Arrange
        long id = 1L;

        // Act
        bookingController.cancelBooking(id);

        // Assert
        verify(bookingService, times(1)).cancelBooking(id);
    }

    @Test
    void testGetBooking_ZeroId() {
        // Arrange
        long id = 0L;

        // Act & Assert
        assertDoesNotThrow(() -> bookingController.getBooking(id));
        verify(bookingService, times(1)).getBooking(id);
    }

    @Test
    void testCancelBooking_ZeroId() {
        // Arrange
        long id = 0L;

        // Act & Assert
        assertDoesNotThrow(() -> bookingController.cancelBooking(id));
        verify(bookingService, times(1)).cancelBooking(id);
    }
}