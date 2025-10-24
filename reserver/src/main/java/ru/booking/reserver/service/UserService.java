package ru.booking.reserver.service;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ru.booking.reserver.model.Role;
import ru.booking.reserver.model.dto.auth.RegisterRequest;
import ru.booking.reserver.model.dto.user.UserDto;
import ru.booking.reserver.model.entity.UserEntity;
import ru.booking.reserver.model.mappers.UserMapper;
import ru.booking.reserver.repository.UserRepository;

import java.util.NoSuchElementException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto createUser(RegisterRequest user) {
        var userEntity = new UserEntity();
        userEntity.setUsername(user.username());
        userEntity.setPassword(passwordEncoder.encode(user.password()));
        userEntity.setRole(Role.ROLE_ADMIN);

        return userMapper.toDto(createUser(userEntity));
    }

    public UserEntity createUser(UserEntity user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UsernameNotFoundException("Пользователь с таким именем уже существует");
        }

        return userRepository.save(user);
    }

    public void deleteUser(long id) {
        if (userRepository.existsById(id)) {
            throw new NoSuchElementException("Пользователь с таким идентификатором не существует");
        }

        userRepository.deleteById(id);
    }

    public UserDto updateUser(long id, UserDto user) {
        var userEntity = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с таким идентификатором не существует"));

        var userByUsername = userRepository.findByUsername(user.name());
        if (userByUsername.isPresent() && userByUsername.get().getId() != id) {
            throw new DuplicateKeyException("Пользователь с таким именем уже существует");
        }

        userEntity.setUsername(user.name());

        return userMapper.toDto(userRepository.save(userEntity));
    }

    public UserEntity getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

    }

    public UserEntity getCurrentUser() {
        return getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}

