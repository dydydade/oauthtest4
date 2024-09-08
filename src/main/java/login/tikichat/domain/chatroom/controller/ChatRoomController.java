package login.tikichat.domain.chatroom.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import login.tikichat.domain.chatroom.dto.CreateChatRoomDto;
import login.tikichat.domain.chatroom.dto.FindChatRoomDto;
import login.tikichat.domain.chatroom.service.ChatRoomService;
import login.tikichat.global.auth.UserDetailInfo;
import login.tikichat.global.response.ResultCode;
import login.tikichat.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "JWT")
@RestController
@Tag(name = "ChatRoom API", description = "채팅방 API")
@RequestMapping("/api/v1/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @PostMapping(
            value = ""
    )
    @Operation(summary = "채팅방 생성", description = "채팅방을 생성하는 API 입니다.")
    public ResponseEntity<ResultResponse> createChatRoom(
            @RequestBody
            @Valid
            @Parameter(required = true)
            CreateChatRoomDto.CreateChatRoomReq createChatRoomReq,
            @AuthenticationPrincipal UserDetailInfo user
    ) {
        ResultResponse result = ResultResponse.of(
                ResultCode.FIND_USER_INFO_SUCCESS,
                this.chatRoomService.createChatRoom(user.getUserId(), createChatRoomReq)
        );
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @GetMapping("")
    @Operation(summary = "채팅방 조회(키워드)", description = "검색 키워드를 통해 해당하는 채팅방을 조회하는 API 입니다.")
    public ResponseEntity<ResultResponse> findChatRooms(
            @RequestBody
            @Valid
            @Parameter(required = true)
            FindChatRoomDto.FindChatRoomReq findChatRoomReq,
            @AuthenticationPrincipal UserDetailInfo user
    ) {
        ResultResponse result = ResultResponse.of(
                ResultCode.FIND_USER_INFO_SUCCESS,
                this.chatRoomService.findChatRooms(findChatRoomReq, user.getUserId())
        );
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @GetMapping("/ranked/message-count")
    @Operation(summary = "채팅방 조회(메세지수 순)", description = "[홈 화면용] 메세지수 순으로 채팅방을 조회하는 API 입니다.")
    public ResponseEntity<ResultResponse> findMessageCountRankedChatRooms(
            @RequestBody
            @Valid
            @Parameter(required = true)
            FindChatRoomDto.FindChatRoomByPopularityReq findChatRoomReq,
            @AuthenticationPrincipal UserDetailInfo user
    ) {
        ResultResponse result = ResultResponse.of(
                ResultCode.FIND_CHAT_ROOMS_SUCCESS,
                this.chatRoomService.findMessageCountRankedChatRooms(findChatRoomReq)
        );
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @GetMapping("/ranked/member-count")
    @Operation(summary = "채팅방 조회(멤버수 순)", description = "[홈 화면용] 멤버수 순으로 채팅방을 조회하는 API 입니다.")
    public ResponseEntity<ResultResponse> findMemberCountRankedChatRooms(
            @RequestBody
            @Valid
            @Parameter(required = true)
            FindChatRoomDto.FindChatRoomByPopularityReq findChatRoomReq,
            @AuthenticationPrincipal UserDetailInfo user
    ) {
        ResultResponse result = ResultResponse.of(
                ResultCode.FIND_CHAT_ROOMS_SUCCESS,
                this.chatRoomService.findMemberCountRankedChatRooms(findChatRoomReq)
        );
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }
}
