package ru.booking.reserver.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ru.booking.common.models.RecommendResponse;
import ru.booking.common.models.RoomDto;

import java.util.Map;
import java.util.Objects;

@Service
public class RoomService {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(RoomService.class);
    public static final String API_ROOMS_RECOMMEND = "http://localhost:8080/api/rooms/recommend";
    public static final String API_CONFIRM_ROOMS_TEMPLATE = "http://localhost:8082/rooms/%s/confirm-availability";
    public static final String API_RELEASE_TEMPLATE = "http://localhost:8082/rooms/%s/release";

    private final RestTemplate restTemplate;

    public RoomService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public long findFirstAvailableRoom() {
        var headers = getAuthHttpHeaders();

        var request = new HttpEntity<>(
                Map.of(),
                headers);

        var response = restTemplate.exchange(
                API_ROOMS_RECOMMEND,
                HttpMethod.GET,
                request,
                RecommendResponse.class
        );

        return Objects.requireNonNull(response.getBody()).roomDtos().stream()
                .filter(RoomDto::availability)
                .map(RoomDto::id)
                .findFirst()
                .orElse(0L);
    }

    public ResponseEntity confirmRoom(long roomId) {
        var headers = getAuthHttpHeaders();

        var request = new HttpEntity<>(
                Map.of(),
                headers);

        return restTemplate.postForEntity(
                API_CONFIRM_ROOMS_TEMPLATE.formatted(roomId),
                request,
                String.class
        );
    }

    public void executeCompensation(long roomId) {
        try {
            var headers = getAuthHttpHeaders();
            var request = new HttpEntity<>(Map.of(), headers);
            restTemplate.postForEntity(API_RELEASE_TEMPLATE.formatted(roomId), request, String.class);
        } catch (Exception e) {
            LOGGER.error("Failed to execute release for room with id{}", roomId, e);
        }
    }

    private HttpHeaders getAuthHttpHeaders() {
        var jwtToken = ((JwtAuthenticationToken) SecurityContextHolder.getContext()
                .getAuthentication()).getToken().getTokenValue();
        var headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        return headers;
    }


}
