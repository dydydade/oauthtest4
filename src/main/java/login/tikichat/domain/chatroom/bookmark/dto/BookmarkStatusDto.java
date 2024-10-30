package login.tikichat.domain.chatroom.bookmark.dto;

public class BookmarkStatusDto {
    public record BookmarkStatusRes(
        Long bookmarkId,
        Long userId,
        Long chatRoomId
    ) {

    }
}
