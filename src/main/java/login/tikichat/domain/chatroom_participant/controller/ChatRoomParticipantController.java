package login.tikichat.domain.chatroom_participant.controller;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import login.tikichat.domain.chatroom_participant.service.ChatRoomParticipantService;
import login.tikichat.global.auth.UserDetailInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "ChatRoom Participants", description = "채팅방 참여관련 API")
@SecurityRequirement(name = "JWT")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/chat-rooms/{chatRoomId}/participants")
public class ChatRoomParticipantController {
    private final ChatRoomParticipantService chatRoomParticipantService;

    @PostMapping
    @Operation(summary = "채팅방 입장하기", description = "채팅방에 입장하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "No Content", content = @Content)
    })
    public void joinChatRoom(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal UserDetailInfo user
    ) {
        this.chatRoomParticipantService.joinChatRoom(chatRoomId, user.getUserId());
    }

    @DeleteMapping
    @Operation(summary = "채팅방 퇴장하기", description = "채팅방에서 퇴장하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "No Content", content = @Content)
    })
    public void leaveChatRoom(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal UserDetailInfo user
    ) {
        this.chatRoomParticipantService.leaveChatRoom(chatRoomId, user.getUserId());
    }
}
