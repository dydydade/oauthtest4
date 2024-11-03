package login.tikichat.domain.chatroom.constants;

public enum ChatRoomSortType {
    BOOKMARK_CREATED_TIME("BOOKMARK"),
    UNREAD_CHAT_EXIST("UNREAD_MESSAGE_TIME"),
    LAST_CHAT_TIME("RECENT_MESSAGE_TIME");

    private String type;

    ChatRoomSortType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
