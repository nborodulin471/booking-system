package ru.booking.reserver.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.booking.reserver.model.BookingStatus;
import ru.booking.reserver.model.dto.booking.BookingDto;
import ru.booking.reserver.model.dto.booking.CreateBookingRequest;
import ru.booking.reserver.model.entity.BookingEntity;
import ru.booking.reserver.model.entity.UserEntity;
import ru.booking.reserver.model.mappers.BookingMapper;
import ru.booking.reserver.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private UserService userService;

    @Mock
    private RoomService roomService;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateBooking_AutoSelectRoomWithAvailableRooms() {
        // Arrange
        var createBookingRequest = new CreateBookingRequest(
                new BookingDto(0L, 1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null),
                true
        );

        var roomId = 123L;
        when(roomService.findFirstAvailableRoom()).thenReturn(roomId);

        var existingBookings = new ArrayList<BookingEntity>();
        when(bookingRepository.findByRoomIdAndDateRange(roomId,
                createBookingRequest.booking().dateStart(),
                createBookingRequest.booking().dateEnd())).thenReturn(existingBookings);

        var savedBooking = new BookingEntity();
        savedBooking.setId(1L);
        savedBooking.setRoomId(roomId);
        savedBooking.setDateStart(createBookingRequest.booking().dateStart());
        savedBooking.setDateEnd(createBookingRequest.booking().dateEnd());
        savedBooking.setStatus(BookingStatus.PENDING);

        when(bookingRepository.save(any(BookingEntity.class))).thenReturn(savedBooking);

        var confirmResponse = new ResponseEntity<>("true", HttpStatus.OK);
        when(roomService.confirmRoom(roomId)).thenReturn(confirmResponse);

        var updatedBooking = new BookingEntity();
        updatedBooking.setId(1L);
        updatedBooking.setRoomId(roomId);
        updatedBooking.setDateStart(createBookingRequest.booking().dateStart());
        updatedBooking.setDateEnd(createBookingRequest.booking().dateEnd());
        updatedBooking.setStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.save(updatedBooking)).thenReturn(updatedBooking);
        when(bookingMapper.toDto(any())).thenReturn(new BookingDto(1L,
                roomId,
                createBookingRequest.booking().dateStart(),
                createBookingRequest.booking().dateEnd(),
                BookingStatus.CONFIRMED));

        // Act
        var result = bookingService.createBooking(createBookingRequest);

        // Assert
        assertNotNull(result);
        assertEquals(BookingStatus.CONFIRMED, result.status());
        verify(bookingRepository, times(2)).save(any(BookingEntity.class));
        verify(roomService, times(1)).confirmRoom(roomId);
        verify(roomService, never()).executeCompensation(roomId);
    }

    @Test
    void testCreateBooking_AutoSelectRoomNoAvailableRooms() {
        // Arrange
        var createBookingRequest = new CreateBookingRequest(
                new BookingDto(0L, 0L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null),
                true
        );

        when(roomService.findFirstAvailableRoom()).thenReturn(0L);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(createBookingRequest));
        verify(bookingRepository, never()).save(any(BookingEntity.class));
        verify(roomService, never()).confirmRoom(anyLong());
        verify(roomService, never()).executeCompensation(anyLong());
    }

    @Test
    void testCreateBooking_ExistingBookingOverlap() {
        // Arrange
        var createBookingRequest = new CreateBookingRequest(
                new BookingDto(0L, 123L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null),
                false
        );

        var roomId = 123L;
        var existingBooking = new BookingEntity();
        existingBooking.setId(1L);
        existingBooking.setRoomId(roomId);
        existingBooking.setDateStart(LocalDateTime.now().plusDays(1));
        existingBooking.setDateEnd(LocalDateTime.now().plusDays(2));
        existingBooking.setStatus(BookingStatus.CONFIRMED);

        var existingBookings = List.of(existingBooking);
        when(bookingRepository.findByRoomIdAndDateRange(roomId,
                createBookingRequest.booking().dateStart(),
                createBookingRequest.booking().dateEnd())).thenReturn(existingBookings);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(createBookingRequest));
        verify(bookingRepository, never()).save(any(BookingEntity.class));
        verify(roomService, never()).confirmRoom(anyLong());
        verify(roomService, never()).executeCompensation(anyLong());
    }

    @Test
    void testCreateBooking_SuccessfulCreationAndCancellation() {
        // Arrange
        var createBookingRequest = new CreateBookingRequest(
                new BookingDto(0L, 123L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null),
                false
        );

        var roomId = 123L;
        var existingBookings = new ArrayList<BookingEntity>();
        when(bookingRepository.findByRoomIdAndDateRange(roomId,
                createBookingRequest.booking().dateStart(),
                createBookingRequest.booking().dateEnd())).thenReturn(existingBookings);

        var savedBooking = new BookingEntity();
        savedBooking.setId(1L);
        savedBooking.setRoomId(roomId);
        savedBooking.setDateStart(createBookingRequest.booking().dateStart());
        savedBooking.setDateEnd(createBookingRequest.booking().dateEnd());
        savedBooking.setStatus(BookingStatus.PENDING);

        when(bookingRepository.save(any(BookingEntity.class))).thenReturn(savedBooking);

        var confirmResponse = new ResponseEntity<>("false", HttpStatus.BAD_REQUEST);
        when(roomService.confirmRoom(roomId)).thenReturn(confirmResponse);
        when((bookingMapper.toDto(savedBooking))).thenReturn(new BookingDto(1L, 123L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), BookingStatus.CANCELLED));

        // Act
        var result = bookingService.createBooking(createBookingRequest);

        // Assert
        assertNotNull(result);
        assertEquals(BookingStatus.CANCELLED, result.status());
        verify(bookingRepository, times(2)).save(any(BookingEntity.class));
        verify(roomService, times(1)).confirmRoom(roomId);
        verify(roomService, times(1)).executeCompensation(roomId);
        verify(bookingMapper, times(1)).toDto(any(BookingEntity.class));
    }

    @Test
    void testCreateBooking_ExceptionDuringConfirmation() {
        // Arrange
        var createBookingRequest = new CreateBookingRequest(
                new BookingDto(0L, 123L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null),
                false
        );

        var roomId = 123L;
        var existingBookings = new ArrayList<BookingEntity>();
        when(bookingRepository.findByRoomIdAndDateRange(roomId,
                createBookingRequest.booking().dateStart(),
                createBookingRequest.booking().dateEnd())).thenReturn(existingBookings);

        var savedBooking = new BookingEntity();
        savedBooking.setId(1L);
        savedBooking.setRoomId(roomId);
        savedBooking.setDateStart(createBookingRequest.booking().dateStart());
        savedBooking.setDateEnd(createBookingRequest.booking().dateEnd());
        savedBooking.setStatus(BookingStatus.PENDING);

        when(bookingRepository.save(any(BookingEntity.class))).thenReturn(savedBooking);
        when((bookingMapper.toDto(savedBooking))).thenReturn(new BookingDto(1L, 123L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), BookingStatus.CANCELLED));


        doThrow(new RuntimeException("Network error")).when(roomService).confirmRoom(roomId);

        // Act
        var result = bookingService.createBooking(createBookingRequest);

        // Assert
        assertNotNull(result);
        assertEquals(BookingStatus.CANCELLED, result.status());
        verify(bookingRepository, times(2)).save(any(BookingEntity.class));
        verify(roomService, times(1)).confirmRoom(roomId);
        verify(roomService, times(1)).executeCompensation(roomId);
        verify(bookingMapper, times(1)).toDto(any(BookingEntity.class));
    }

    @Test
    void testGetBookings() {
        // Arrange
        var currentUser = new UserEntity();
        currentUser.setId(1L);
        when(userService.getCurrentUser()).thenReturn(currentUser);

        var bookings = List.of(new BookingEntity(), new BookingEntity());
        when(bookingRepository.findAllByUserId(1L)).thenReturn(bookings);

        var dto1 = new BookingDto(1L, 123L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), BookingStatus.CONFIRMED);
        var dto2 = new BookingDto(2L, 456L, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3), BookingStatus.PENDING);

        when(bookingMapper.toDto(bookings.get(0))).thenReturn(dto1);
        when(bookingMapper.toDto(bookings.get(1))).thenReturn(dto2);

        // Act
        var result = bookingService.getBookings();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
        verify(bookingRepository, times(1)).findAllByUserId(1L);
        verify(bookingMapper, times(2)).toDto(any(BookingEntity.class));
    }

    @Test
    void testGetBooking_ExistingBooking() {
        // Arrange
        var bookingId = 123L;
        var bookingEntity = new BookingEntity();
        bookingEntity.setId(bookingId);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingEntity));

        var bookingDto = new BookingDto(bookingId, 456L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), BookingStatus.CONFIRMED);
        when(bookingMapper.toDto(bookingEntity)).thenReturn(bookingDto);

        // Act
        var result = bookingService.getBooking(bookingId);

        // Assert
        assertNotNull(result);
        assertEquals(bookingDto, result);
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingMapper, times(1)).toDto(bookingEntity);
    }

    @Test
    void testGetBooking_NonExistingBooking() {
        // Arrange
        var bookingId = 123L;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> bookingService.getBooking(bookingId));
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingMapper, never()).toDto(any(BookingEntity.class));
    }

    @Test
    void testCancelBooking_ExistingBooking() {
        // Arrange
        var bookingId = 123L;
        var bookingEntity = new BookingEntity();
        bookingEntity.setId(bookingId);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingEntity));

        // Act
        bookingService.cancelBooking(bookingId);

        // Assert
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, times(1)).deleteById(bookingId);
    }

    @Test
    void testCancelBooking_NonExistingBooking() {
        // Arrange
        var bookingId = 123L;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> bookingService.cancelBooking(bookingId));
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, never()).deleteById(bookingId);
    }
}