package login.tikichat.global.component.impl;

import login.tikichat.global.component.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailSenderImpl implements EmailSender {
    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(String receiverEmail, String title, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(receiverEmail);
        message.setSubject(title);
        message.setText(content);

        this.javaMailSender.send(message);
    }
}
