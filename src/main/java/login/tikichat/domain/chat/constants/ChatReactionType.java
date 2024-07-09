package login.tikichat.domain.chat.constants;

public enum ChatReactionType {
    LIKE("LIKE"),
    HEART("HEART"),
    CRYING_FACE("CRYING_FACE"),
    CHECK_MARK("CHECK_MARK"),
    SMILE_FACE("SMILE_FACE");

    private String type;

    ChatReactionType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
