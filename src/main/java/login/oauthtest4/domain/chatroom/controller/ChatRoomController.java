package login.oauthtest4.domain.chatroom.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import login.oauthtest4.domain.chatroom.dto.CreateChatRoomDto;
import login.oauthtest4.domain.chatroom.service.ChatRootService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name = "JWT")
@RestController
@Tag(name = "ChatRoom", description = "채팅방")
@RequestMapping("/api/v1/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRootService chatRootService;

    @PostMapping("")
    public CreateChatRoomDto.CreateChatRoomRes createChatRoom(
            @RequestBody @Valid CreateChatRoomDto.CreateChatRoomReq createChatRoomReq
    ) {
        // @TOOD: 인증된 사용자의 userId로 치환 해야 함.
        return new CreateChatRoomDto.CreateChatRoomRes(
                this.chatRootService.createChatRoom(1L, createChatRoomReq)
        );
    }
}
