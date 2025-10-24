package ru.booking.reserver.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import ru.booking.reserver.model.BookingStatus;
import ru.booking.reserver.model.dto.booking.BookingDto;
import ru.booking.reserver.model.dto.booking.CreateBookingRequest;
import ru.booking.reserver.model.entity.BookingEntity;
import ru.booking.reserver.model.mappers.BookingMapper;
import ru.booking.reserver.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserService userService;
    private final RoomService roomService;

    public BookingService(BookingRepository bookingRepository, BookingMapper bookingMapper,
                          UserService userService, RoomService roomService) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.userService = userService;
        this.roomService = roomService;
    }

    @Transactional
    public BookingDto createBooking(CreateBookingRequest createBookingRequest) {
        final var booking = createBookingRequest.booking();
        var autoSelect = createBookingRequest.autoSelect();

        // Если не выбрана комната, то выбираем случайную
        var roomId = booking.roomId();
        if (autoSelect) {
            roomId = roomService.findFirstAvailableRoom();
            if (roomId == 0) {
                throw new IllegalArgumentException("Нет доступных комнат");
            }
        }

        // Проверка на пересечение с правильным roomId
        var existingBookings = bookingRepository.findByRoomIdAndDateRange(
                        roomId, booking.dateStart(), booking.dateEnd()).stream()
                .filter(b -> b.getStatus() != BookingStatus.CANCELLED)
                .toList();
        var isOverlapping = existingBookings.stream()
                .anyMatch(b -> isTimeOverlap(b.getDateStart(), b.getDateEnd(),
                        booking.dateStart(), booking.dateEnd()));
        if (isOverlapping) {
            throw new IllegalArgumentException("Выбранное время пересекается с уже забронированным");
        }

        var result = new BookingEntity();
        result.setRoomId(roomId);
        result.setDateStart(booking.dateStart());
        result.setDateEnd(booking.dateEnd());
        result.setStatus(BookingStatus.PENDING);
        result = bookingRepository.save(result);

        try {
            var response = roomService.confirmRoom(roomId);

            if (response.getStatusCode().is2xxSuccessful() && "true".equals(response.getBody())) {
                result.setStatus(BookingStatus.CONFIRMED);
            } else {
                // Компенсация при отрицательном ответе
                roomService.executeCompensation(roomId);
                result.setStatus(BookingStatus.CANCELLED);
            }
        } catch (Exception _) {
            // Компенсация при исключении
            roomService.executeCompensation(roomId);
            result.setStatus(BookingStatus.CANCELLED);
        }

        return bookingMapper.toDto(bookingRepository.save(result));
    }

    public List<BookingDto> getBookings() {
        return bookingRepository.findAllByUserId(userService.getCurrentUser().getId())
                .stream()
                .map(bookingMapper::toDto)
                .toList();

    }

    public BookingDto getBooking(long id) {
        var bookingEntity = bookingRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Бронирование не найдено"));

        return bookingMapper.toDto(bookingEntity);
    }

    public void cancelBooking(long id) {
        var bookingEntity = bookingRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Бронирование не найдено"));

        bookingRepository.deleteById(bookingEntity.getId());
    }

    private boolean isTimeOverlap(LocalDateTime bookingStart, LocalDateTime bookingEnd,
                                  LocalDateTime newStart, LocalDateTime newEnd) {
        return newStart.isBefore(bookingEnd) && newEnd.isAfter(bookingStart);
    }

}
