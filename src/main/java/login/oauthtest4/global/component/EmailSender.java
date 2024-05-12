package login.oauthtest4.global.component;

public interface EmailSender {
    void sendEmail(String receiverEmail, String title, String content);
}
