package ru.booking.management.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.booking.common.models.RecommendResponse;
import ru.booking.common.models.RoomDto;
import ru.booking.management.service.RoomService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RoomControllerTest {

    @Mock
    private RoomService roomService;

    @InjectMocks
    private RoomController roomController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRoom() {
        // Arrange
        RoomDto roomDto = mock(RoomDto.class);
        when(roomService.createRoom(roomDto)).thenReturn(roomDto);

        // Act
        ResponseEntity<RoomDto> response = roomController.createRoom(roomDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(roomDto, response.getBody());
        verify(roomService, times(1)).createRoom(roomDto);
    }

    @Test
    void testGetRecommend() {
        // Arrange
        RecommendResponse recommendedRooms = mock(RecommendResponse.class);
        when(roomService.getRecommended()).thenReturn(recommendedRooms);

        // Act
        ResponseEntity<RecommendResponse> response = roomController.getRecommend();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(recommendedRooms, response.getBody());
        verify(roomService, times(1)).getRecommended();
    }

    @Test
    void testGetAvailable() {
        // Arrange
        RoomDto roomDto = mock(RoomDto.class);
        List<RoomDto> availableRooms = Collections.singletonList(roomDto);
        when(roomService.getAvailable()).thenReturn(availableRooms);

        // Act
        ResponseEntity<List<RoomDto>> response = roomController.getAvailable();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(availableRooms, response.getBody());
        verify(roomService, times(1)).getAvailable();
    }

    @Test
    void testConfirmAvailability() {
        // Arrange
        Long roomId = 1L;
        Boolean result = true;
        when(roomService.confirmAvailability(roomId)).thenReturn(result);

        // Act
        ResponseEntity<Boolean> response = roomController.confirmAvailability(roomId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(result, response.getBody());
        verify(roomService, times(1)).confirmAvailability(roomId);
    }

    @Test
    void testReleaseRoom() {
        // Arrange
        Long roomId = 1L;

        // Act
        ResponseEntity<Void> response = roomController.releaseRoom(roomId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(roomService, times(1)).releaseRoom(roomId);
    }

    @Test
    void testGetRecommendWhenNoRoomsRecommended() {
        // Arrange
        when(roomService.getRecommended()).thenReturn(mock(RecommendResponse.class));

        // Act
        ResponseEntity<RecommendResponse> response = roomController.getRecommend();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(roomService, times(1)).getRecommended();
    }

    @Test
    void testGetAvailableWhenNoRoomsAvailable() {
        // Arrange
        when(roomService.getAvailable()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<RoomDto>> response = roomController.getAvailable();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.emptyList(), response.getBody());
        verify(roomService, times(1)).getAvailable();
    }

    @Test
    void testReleaseRoomWithZeroId() {
        // Arrange
        Long roomId = 0L;

        // Act
        ResponseEntity<Void> response = roomController.releaseRoom(roomId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(roomService, times(1)).releaseRoom(roomId);
    }
}