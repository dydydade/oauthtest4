package login.tikichat.domain.chat.service;

import login.tikichat.domain.chat.model.Chat;
import login.tikichat.domain.chat.repository.ChatRepository;
import login.tikichat.global.exception.BusinessException;
import login.tikichat.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor()
public class ChatCommonService {
    private final ChatRepository chatRepository;

    public Chat findById(Long id) {
        return this.chatRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CHAT));
    }
}
