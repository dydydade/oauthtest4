package login.tikichat.global.component;

public interface EmailSender {
    void sendEmail(String receiverEmail, String title, String content);
}
