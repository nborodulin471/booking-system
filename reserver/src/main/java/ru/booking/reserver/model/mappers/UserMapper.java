package ru.booking.reserver.model.mappers;

import org.springframework.stereotype.Component;

import ru.booking.reserver.model.dto.user.UserDto;
import ru.booking.reserver.model.entity.UserEntity;

@Component
public class UserMapper {

    public UserEntity toEntity(UserDto userDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(userDto.name());
        return userEntity;
    }

    public UserDto toDto(UserEntity userEntity) {
        return new UserDto(userEntity.getId(), userEntity.getUsername(), userEntity.getRole());
    }

}
