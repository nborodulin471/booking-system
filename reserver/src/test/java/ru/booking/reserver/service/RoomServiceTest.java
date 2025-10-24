package ru.booking.reserver.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.booking.common.models.RecommendResponse;
import ru.booking.common.models.RoomDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RoomServiceTest {

    private RestTemplate restTemplate;
    private RoomService roomService;
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        roomService = new RoomService(restTemplate);

        Jwt jwt = Mockito.mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("test-jwt-token");
        var authentication = mock(JwtAuthenticationToken.class);
        when(authentication.getToken()).thenReturn(jwt);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        headers = new HttpHeaders();
        headers.setBearerAuth("test-jwt-token");
    }

    @Test
    void testFindFirstAvailableRoom_WithRooms() {
        // Arrange
        var recommendResponse = new RecommendResponse(List.of(
                new RoomDto(1L, 1L, 1, true, 0),
                new RoomDto(2L, 1L, 2, false, 0)
        ));

        var responseEntity = new ResponseEntity<>(recommendResponse, headers, HttpStatus.OK);

        when(restTemplate.exchange(eq(RoomService.API_ROOMS_RECOMMEND),
                eq(HttpMethod.GET),
                any(),
                eq(RecommendResponse.class)
        )).thenReturn(responseEntity);

        // Act
        long result = roomService.findFirstAvailableRoom();

        // Assert
        assertEquals(1L, result);
        verify(restTemplate).exchange(eq(RoomService.API_ROOMS_RECOMMEND),
                eq(HttpMethod.GET),
                argThat(request -> request.getHeaders().equals(headers)),
                eq(RecommendResponse.class));
    }

    @Test
    void testFindFirstAvailableRoom_NoRooms() {
        // Arrange
        var recommendResponse = new RecommendResponse(List.of(
                new RoomDto(1L, 1L, 1, false, 0),
                new RoomDto(2L, 1L, 2, false, 0)
        ));

        var responseEntity = new ResponseEntity<>(recommendResponse, headers, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(RoomService.API_ROOMS_RECOMMEND),
                eq(HttpMethod.GET),
                argThat(request -> request.getHeaders().equals(headers)),
                eq(RecommendResponse.class)
        )).thenReturn(responseEntity);

        // Act
        long result = roomService.findFirstAvailableRoom();

        // Assert
        assertEquals(0L, result);
        verify(restTemplate, times(1)).exchange(eq(RoomService.API_ROOMS_RECOMMEND),
                eq(HttpMethod.GET),
                argThat(request -> request.getHeaders().equals(headers)),
                eq(RecommendResponse.class));
    }

    @Test
    void testFindFirstAvailableRoom_ApiError() {
        // Arrange
        when(restTemplate.exchange(
                eq(RoomService.API_ROOMS_RECOMMEND),
                eq(HttpMethod.GET),
                argThat(request -> request.getHeaders().equals(headers)),
                eq(RecommendResponse.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.SERVICE_UNAVAILABLE));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> roomService.findFirstAvailableRoom());
        verify(restTemplate, times(1)).exchange(eq(RoomService.API_ROOMS_RECOMMEND),
                eq(HttpMethod.GET),
                argThat(request -> request.getHeaders().equals(headers)),
                eq(RecommendResponse.class));
    }

    @Test
    void testConfirmRoom_Success() {
        // Arrange
        var roomId = 123L;
        var responseEntity = new ResponseEntity<>("Confirmed", headers, HttpStatus.CREATED);

        when(restTemplate.postForEntity(
                eq(String.format(RoomService.API_CONFIRM_ROOMS_TEMPLATE, roomId)),
                any(),
                eq(String.class)
        )).thenReturn(responseEntity);

        // Act
        ResponseEntity result = roomService.confirmRoom(roomId);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("Confirmed", result.getBody());
        verify(restTemplate, times(1)).postForEntity(anyString(), any(), any());
    }

    @Test
    void testConfirmRoom_ApiError() {
        // Arrange
        var roomId = 123L;

        when(restTemplate.postForEntity(
                eq(String.format(RoomService.API_CONFIRM_ROOMS_TEMPLATE, roomId)),
                any(),
                eq(String.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> roomService.confirmRoom(roomId));
        verify(restTemplate, times(1)).postForEntity(anyString(), any(), any());
    }

    @Test
    void testExecuteCompensation_Success() {
        // Arrange
        var roomId = 456L;

        doReturn(new ResponseEntity<>("Released", headers, HttpStatus.OK))
                .when(restTemplate).postForEntity(
                        eq(String.format(RoomService.API_RELEASE_TEMPLATE, roomId)),
                        any(),
                        eq(String.class)
                );

        // Act
        roomService.executeCompensation(roomId);

        // Assert
        verify(restTemplate, times(1)).postForEntity(anyString(), any(), any());
    }

    @Test
    void testExecuteCompensation_Failure() {
        // Arrange
        var roomId = 456L;

        doThrow(new RuntimeException("Network error"))
                .when(restTemplate).postForEntity(
                        eq(String.format(RoomService.API_RELEASE_TEMPLATE, roomId)),
                        any(),
                        eq(String.class)
                );

        // Act
        roomService.executeCompensation(roomId);

        // Assert
        verify(restTemplate, times(1)).postForEntity(anyString(), any(), any());
        // Verify logging would be done in real implementation
    }
}