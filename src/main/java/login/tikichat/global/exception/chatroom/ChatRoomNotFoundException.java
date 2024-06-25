package login.tikichat.global.exception.chatroom;

import login.tikichat.global.exception.BusinessException;
import login.tikichat.global.exception.ErrorCode;

public class ChatRoomNotFoundException extends BusinessException {
    public ChatRoomNotFoundException() {
        super(ErrorCode.NOT_FOUND_CHAT_ROOM);
    }
}