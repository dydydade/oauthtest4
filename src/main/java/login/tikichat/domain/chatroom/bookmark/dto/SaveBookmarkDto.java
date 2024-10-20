package login.tikichat.domain.chatroom.bookmark.dto;

public class SaveBookmarkDto {
    public record SaveBookmarkRes(
        Long bookmarkId,
        Long userId,
        Long chatRoomId
    ) {

    }
}
