package login.tikichat.domain.chat.repository;

import login.tikichat.domain.chat.constants.ChatReactionType;
import login.tikichat.domain.chat.model.ChatReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatReactionRepository extends JpaRepository<ChatReaction, Long> {
    Integer deleteByChatIdAndUserIdAndChatReactionType(Long chatId, Long userId, ChatReactionType chatReactionType);
}
