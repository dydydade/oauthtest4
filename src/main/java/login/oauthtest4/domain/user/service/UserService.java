package login.oauthtest4.domain.user.service;

import login.oauthtest4.domain.user.model.Role;
import login.oauthtest4.domain.user.model.User;
import login.oauthtest4.domain.user.dto.*;
import login.oauthtest4.domain.user.repository.UserRepository;
import login.oauthtest4.global.exception.user.RegisteredUserNotFoundException;
import login.oauthtest4.global.exception.user.UnauthorizedAccountAttemptException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NormalSignUpStrategy normalSignUpStrategy;
    private final SocialSignUpStrategy socialSignUpStrategy;

    /**
     * [일반 회원가입 메서드]
     * @param baseUserSignUpRequest
     * @return
     */
    @Transactional
    public UserSignUpResponse signUp(BaseUserSignUpRequest baseUserSignUpRequest) {
        return normalSignUpStrategy.signUp(baseUserSignUpRequest);
    }

    /**
     * [소셜 회원가입 메서드]
     * @param baseUserSignUpRequest
     * @return
     */
    @Transactional
    public UserSignUpResponse socialSignUp(BaseUserSignUpRequest baseUserSignUpRequest) {
        return socialSignUpStrategy.signUp(baseUserSignUpRequest);
    }

    /**
     * [회원 탈퇴 메서드]
     * @param userId 탈퇴하려는 계정의 ID
     * @param currentUser 로그인 사용자 정보 (UserDetails)
     */
    @Transactional
    public UserSignOffResponse signOff(Long userId, UserDetails currentUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RegisteredUserNotFoundException());

        if (!user.isSameUser(currentUser.getUsername())) { // username 에 email 저장됨
            throw new UnauthorizedAccountAttemptException();
        }

        userRepository.deleteById(userId);
        return UserSignOffResponse.builder()
                .userId(userId)
                .build();
    }

    /**
     * [등록된 email 로 유저를 조회하는 메서드]
     * @param email
     * @return
     */
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(RegisteredUserNotFoundException::new);
    }

    /**
     * [등록된 email 로 유저를 조회하고 응답으로 바꿔서 반환하는 메서드]
     * 응답에 계정이 일반계정인지/소셜ONLY계정인지 구분하는 필드도 포함
     * @param email
     * @return
     */
    @Transactional(readOnly = true)
    public FindUserResponse findUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(RegisteredUserNotFoundException::new);
        boolean passwordExists = user.getRole().equals(Role.USER); // 비밀번호가 존재하면 일반 계정
        return new FindUserResponse(user.getEmail(), passwordExists);
    }

    /**
     * [닉네임 중복 체크하는 메서드]
     * @param nickname
     * @return
     */
    @Transactional(readOnly = true)
    public boolean checkNicknameAvailability(String nickname) {
        Optional<User> userOptional = userRepository.findByNickname(nickname);
        if (userOptional.isPresent()) {
            return false; // 닉네임이 이미 존재할 경우
        } else {
            return true; // 닉네임이 존재하지 않을 경우
        }
    }

    @Transactional(readOnly = true)
    public void setUserPassword(
            PasswordChangeRequest passwordChangeRequest
    ) {
        String email = passwordChangeRequest.getEmail();
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new RegisteredUserNotFoundException();
        }

        User user = userOptional.get();
        user.updatePassword(passwordEncoder.encode(passwordChangeRequest.getNewPassword()));
    }
}


