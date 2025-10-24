package ru.booking.reserver.model.mappers;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ru.booking.reserver.model.dto.booking.BookingDto;
import ru.booking.reserver.model.entity.BookingEntity;
import ru.booking.reserver.service.UserService;

@Component
public class BookingMapper {

    private final UserService userService;

    public BookingMapper(UserService userService) {
        this.userService = userService;
    }

    public BookingDto toDto(BookingEntity booking) {
        if (booking != null) {
            return new BookingDto(booking.getId(), booking.getRoomId(), booking.getDateStart(), booking.getDateEnd(), booking.getStatus());
        } else {
            return null;
        }
    }

    public BookingEntity toEntity(BookingDto booking) {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userService.getByUsername(username);
        if (booking != null) {
            return new BookingEntity(
                    booking.id(),
                    booking.roomId(),
                    user,
                    booking.dateStart(),
                    booking.dateEnd(),
                    booking.status()
            );
        } else {
            return null;
        }
    }

}
