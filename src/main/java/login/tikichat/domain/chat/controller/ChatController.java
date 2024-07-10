package login.tikichat.domain.chat.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import login.tikichat.domain.chat.dto.AddChatReactionDto;
import login.tikichat.domain.chat.dto.FindChatsDto;
import login.tikichat.domain.chat.dto.SendMessageDto;
import login.tikichat.domain.chat.service.ChatService;
import login.tikichat.global.auth.UserDetailInfo;
import login.tikichat.global.response.ResultCode;
import login.tikichat.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat-rooms/{chatRoomId}/chats")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")gi
public class ChatController {
    private final ChatService chatService;

    @PostMapping("")
    public ResponseEntity<ResultResponse> sendChat(
            @RequestBody @Valid SendMessageDto.SendMessageReqDto sendMessageReqDto,
            @AuthenticationPrincipal UserDetailInfo user,
            @PathVariable("chatRoomId") Long chatRoomId
    ) {
        ResultResponse result = ResultResponse.of(
                ResultCode.SEND_CHAT_SUCCESS,
                this.chatService.sendMessage(user.getUserId(), chatRoomId, sendMessageReqDto)
        );

        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @PostMapping("/{chatId}/reaction")
    public void addChatReaction(
            @RequestBody @Valid AddChatReactionDto.AddChatReactionReq addChatReactionReq,
            @AuthenticationPrincipal UserDetailInfo user,
            @PathVariable("chatId") Long chatId
    ) {
        this.chatService.addChatReaction(
                chatId,
                user.getUserId(),
                addChatReactionReq.chatReactionType()
        );
    }

    @DeleteMapping("/{chatId}/reaction")
    public void removeChatReaction(
            @RequestBody @Valid AddChatReactionDto.AddChatReactionReq addChatReactionReq,
            @AuthenticationPrincipal UserDetailInfo user,
            @PathVariable("chatId") Long chatId
    ) {
        this.chatService.removeChatReaction(
                chatId,
                user.getUserId(),
                addChatReactionReq.chatReactionType()
        );
    }

    @GetMapping("")
    public ResponseEntity<ResultResponse> getChats(
            @Valid FindChatsDto.FindChatsReq findChatsReq,
            @AuthenticationPrincipal UserDetailInfo user,
            @PathVariable("chatRoomId") Long chatRoomId
    ) {
        ResultResponse result = ResultResponse.of(
                ResultCode.FIND_CHAT_LIST_SUCCESS,
                this.chatService.findChats(user.getUserId(), chatRoomId, findChatsReq)
        );

        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }
}