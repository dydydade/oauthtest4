package login.tikichat.domain.chatroom_participant.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import login.tikichat.domain.chatroom_participant.service.ChatRoomParticipantService;
import login.tikichat.global.auth.UserDetailInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirement(name = "JWT")
@RequiredArgsConstructor
@Tag(name = "ChatRoom Participants", description = "채팅방 참여관련 API")
@Controller("/api/v1/chat-rooms/{chatRoomId}/participants")
public class ChatRoomParticipantController {
    private final ChatRoomParticipantService chatRoomParticipantService;

    @PostMapping
    public void joinChatRoom(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal UserDetailInfo user
    ) {
        this.chatRoomParticipantService.joinChatRoom(chatRoomId, user.getUserId());
    }

    @DeleteMapping
    public void leaveChatRoom(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal UserDetailInfo user
    ) {
        this.chatRoomParticipantService.leaveChatRoom(chatRoomId, user.getUserId());
    }
}
