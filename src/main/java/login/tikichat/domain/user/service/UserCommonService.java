package login.tikichat.domain.user.service;

import login.tikichat.domain.user.model.User;
import login.tikichat.domain.user.repository.UserRepository;
import login.tikichat.global.exception.BusinessException;
import login.tikichat.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserCommonService {
    private final UserRepository userRepository;

    public User findById(Long userId) {
        return this.userRepository.findById(userId).orElseThrow(() ->
                new BusinessException(ErrorCode.NOT_FOUND_USER)
        );
    }
}
