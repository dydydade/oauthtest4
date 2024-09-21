package login.tikichat.global.listener;

import login.tikichat.domain.user.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final UserStatusService userStatusService;
    private final StringRedisTemplate redisTemplate;

    @EventListener
    @Transactional
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userIdHeader = headerAccessor.getFirstNativeHeader("userId");
        String sessionId = headerAccessor.getSessionId();

        if (userIdHeader == null || sessionId == null) {
            log.error("Invalid WebSocket connection: missing userId or sessionId");
            throw new IllegalArgumentException("Invalid connection: userId or sessionId is missing");
        }

        Long userId = Long.valueOf(userIdHeader);

        if (redisTemplate.opsForValue().get(sessionId) == null) {
            userStatusService.setUserStatus(userId, true);
            redisTemplate.opsForValue().set(sessionId, userIdHeader);
            log.info("User with ID {} connected with session {}", userId, sessionId);
        } else {
            log.warn("Session {} is already associated with a user", sessionId);
        }
    }

    @EventListener
    @Transactional
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        if (sessionId == null) {
            log.error("Invalid WebSocket disconnection: missing sessionId");
            return;
        }

        String userIdStr = redisTemplate.opsForValue().getAndDelete(sessionId);
        if (userIdStr != null) {
            Long userId = Long.valueOf(userIdStr);
            userStatusService.setUserStatus(userId, false);
            log.info("User with ID {} disconnected with session {}", userId, sessionId);
        } else {
            log.warn("No associated user found for session {}", sessionId);
        }
    }
}
