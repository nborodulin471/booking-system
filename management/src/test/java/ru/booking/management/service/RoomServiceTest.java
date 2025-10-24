package ru.booking.management.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.booking.common.models.RecommendResponse;
import ru.booking.common.models.RoomDto;
import ru.booking.management.models.entity.RoomEntity;
import ru.booking.management.models.mappers.RoomMapper;
import ru.booking.management.repository.HotelRepository;
import ru.booking.management.repository.RoomRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomMapper roomMapper;

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private RoomService roomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRoom_SuccessfulCreation() {
        // Arrange
        RoomDto roomDto = new RoomDto(1L, 3L, 101, true, 1);
        when(roomRepository.existsByNumber(anyInt())).thenReturn(false);
        when(roomRepository.save(any(RoomEntity.class))).thenReturn(new RoomEntity());
        when(roomMapper.toEntity(any())).thenReturn(new RoomEntity());
        when(roomMapper.toDto(any(RoomEntity.class))).thenReturn(roomDto);
        when(hotelRepository.existsById(any())).thenReturn(true);

        // Act
        RoomDto result = roomService.createRoom(roomDto);

        // Assert
        assertEquals(roomDto, result);
        verify(hotelRepository, times(1)).existsById(roomDto.hotelId());
        verify(roomRepository, times(1)).existsByNumber(roomDto.number());
        verify(roomMapper, times(1)).toEntity(roomDto);
        verify(roomRepository, times(1)).save(any(RoomEntity.class));
        verify(roomMapper, times(1)).toDto(any(RoomEntity.class));
    }

    @Test
    void testCreateRoom_HotelDoesNotExist() {
        // Arrange
        RoomDto roomDto = new RoomDto(1L, 3L, 101, true, 1);
        when(roomRepository.existsByNumber(anyInt())).thenReturn(false);
        when((hotelRepository.existsById(any()))).thenReturn(false);

        // Act
        assertThrows(IllegalArgumentException.class, () -> roomService.createRoom(roomDto));

        // Assert
        verify(hotelRepository, times(1)).existsById(roomDto.hotelId());
        verify(roomRepository, times(1)).existsByNumber(roomDto.number());
        verify(roomMapper, never()).toEntity(roomDto);
        verify(roomRepository, never()).save(any(RoomEntity.class));
    }

    @Test
    void testGetRecommendedRooms() {
        // Arrange
        RoomEntity room1 = new RoomEntity();
        RoomEntity room2 = new RoomEntity();
        RoomDto dto1 = new RoomDto(1L, 3L, 101, true, 1);
        RoomDto dto2 = new RoomDto(2L, 3L, 102, true, 2);

        when(roomRepository.findRecommendedRooms()).thenReturn(List.of(room1, room2));
        when(roomMapper.toDto(room1)).thenReturn(dto1);
        when(roomMapper.toDto(room2)).thenReturn(dto2);

        // Act
        RecommendResponse result = roomService.getRecommended();

        // Assert
        assertEquals(2, result.roomDtos().size());
        assertEquals(dto1, result.roomDtos().getFirst());
        assertEquals(dto2, result.roomDtos().get(1));
        verify(roomRepository, times(1)).findRecommendedRooms();
        verify(roomMapper, times(2)).toDto(any(RoomEntity.class));
    }

    @Test
    void testGetRecommendedRooms_WhenEmpty() {
        // Arrange
        when(roomRepository.findRecommendedRooms()).thenReturn(List.of());

        // Act
        RecommendResponse result = roomService.getRecommended();

        // Assert
        assertTrue(result.roomDtos().isEmpty());
        verify(roomRepository, times(1)).findRecommendedRooms();
        verify(roomMapper, never()).toDto(any(RoomEntity.class));
    }

    @Test
    void testGetAvailableRooms() {
        // Arrange
        RoomEntity room1 = new RoomEntity();
        RoomEntity room2 = new RoomEntity();
        RoomDto dto1 = new RoomDto(1L, 3L, 101, true, 1);
        RoomDto dto2 = new RoomDto(2L, 3L, 102, true, 2);

        when(roomRepository.findAvailableRooms()).thenReturn(List.of(room1, room2));
        when(roomMapper.toDto(room1)).thenReturn(dto1);
        when(roomMapper.toDto(room2)).thenReturn(dto2);

        // Act
        List<RoomDto> result = roomService.getAvailable();

        // Assert
        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
        verify(roomRepository, times(1)).findAvailableRooms();
        verify(roomMapper, times(2)).toDto(any(RoomEntity.class));
    }

    @Test
    void testGetAvailableRooms_WhenEmpty() {
        // Arrange
        when(roomRepository.findAvailableRooms()).thenReturn(List.of());

        // Act
        List<RoomDto> result = roomService.getAvailable();

        // Assert
        assertTrue(result.isEmpty());
        verify(roomRepository, times(1)).findAvailableRooms();
        verify(roomMapper, never()).toDto(any(RoomEntity.class));
    }

    @Test
    void testConfirmAvailability_RoomFoundAndAvailable() {
        // Arrange
        Long roomId = 1L;
        RoomEntity room = new RoomEntity();
        room.setId(roomId);
        room.setAvailable(true);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        // Act
        Boolean result = roomService.confirmAvailability(roomId);

        // Assert
        assertTrue(result);
        assertFalse(room.isAvailable());
        verify(roomRepository, times(1)).findById(roomId);
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    void testConfirmAvailability_RoomFoundButNotAvailable() {
        // Arrange
        Long roomId = 1L;
        RoomEntity room = new RoomEntity();
        room.setId(roomId);
        room.setAvailable(false);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        // Act
        Boolean result = roomService.confirmAvailability(roomId);

        // Assert
        assertFalse(result);
        verify(roomRepository, times(1)).findById(roomId);
        verify(roomRepository, never()).save(room);
    }

    @Test
    void testConfirmAvailability_RoomNotFound() {
        // Arrange
        Long roomId = 1L;

        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> roomService.confirmAvailability(roomId));
        verify(roomRepository, times(1)).findById(roomId);
        verify(roomRepository, never()).save(any(RoomEntity.class));
    }

    @Test
    void testReleaseRoom_RoomFound() {
        // Arrange
        Long roomId = 1L;
        RoomEntity room = new RoomEntity();
        room.setId(roomId);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        // Act
        roomService.releaseRoom(roomId);

        // Assert
        assertTrue(room.isAvailable());
        verify(roomRepository, times(1)).findById(roomId);
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    void testReleaseRoom_RoomNotFound() {
        // Arrange
        Long roomId = 1L;

        when(roomRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> roomService.releaseRoom(roomId));
        verify(roomRepository, times(1)).findById(roomId);
        verify(roomRepository, never()).save(any(RoomEntity.class));
    }

}