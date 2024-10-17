package login.tikichat.domain.chatroom.constants;

public enum ChatRoomSortType {
    BOOKMARK_EXIST("BOOKMARK_EXIST"),
    UNREAD_CHAT_EXIST("UNREAD_CHAT_EXIST"),
    LAST_CHAT_TIME("LAST_CHAT_TIME");

    private String type;

    ChatRoomSortType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
