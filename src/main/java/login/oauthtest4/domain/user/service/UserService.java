package login.oauthtest4.domain.user.service;

import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import login.oauthtest4.domain.user.Role;
import login.oauthtest4.domain.user.SocialProfile;
import login.oauthtest4.domain.user.User;
import login.oauthtest4.domain.user.dto.*;
import login.oauthtest4.domain.user.exception.AlreadySignedUpUserException;
import login.oauthtest4.domain.user.exception.NicknameAlreadyInUseException;
import login.oauthtest4.domain.user.exception.RegisteredUserNotFoundException;
import login.oauthtest4.domain.user.exception.UnauthorizedAccountAttemptException;
import login.oauthtest4.domain.user.repository.SocialProfileRepository;
import login.oauthtest4.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SocialProfileRepository socialProfileRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원 가입
     * @param userSignUpRequest
     */
    @Transactional
    public UserSignUpResponse signUp(UserSignUpRequest userSignUpRequest) {
        if (userRepository.findByEmail(userSignUpRequest.getEmail()).isPresent()) {
            throw new AlreadySignedUpUserException();
        }

        if (userRepository.findByNickname(userSignUpRequest.getNickname()).isPresent()) {
            throw new NicknameAlreadyInUseException();
        }

        User user = User.builder()
                .email(userSignUpRequest.getEmail())
                .password(userSignUpRequest.getPassword())
                .nickname(userSignUpRequest.getNickname())
                .role(Role.USER)
                .build();

        user.passwordEncode(passwordEncoder);
        User savedUser = userRepository.save(user);

        // 회원가입 요청에 소셜 로그인 연계 정보가 함께 넘어온 경우
        if (userSignUpRequest.getSocialProfileDto() != null) {
            linkSocialProfile(userSignUpRequest, user);
        }

        return UserSignUpResponse.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .nickname(savedUser.getNickname())
                .build();
    }

    /**
     * 소셜 프로필 연계 메서드
     * @param userSignUpRequest
     * @param user
     */
    private void linkSocialProfile(UserSignUpRequest userSignUpRequest, User user) {
        UserSignUpSocialProfileDto socialProfileDto = userSignUpRequest.getSocialProfileDto();

        SocialProfile socialProfile = SocialProfile.builder()
                .socialId(socialProfileDto.getSocialId())
                .socialEmail(socialProfileDto.getSocialEmail())
                .socialType(socialProfileDto.getSocialType())
                .user(user)
                .build();

        socialProfileRepository.save(socialProfile);
    }

    /**
     * 회원 탈퇴
     * @param userId 탈퇴하려는 계정의 ID
     * @param currentUser 로그인 사용자 정보 (UserDetails)
     */
    @Transactional
    public UserSignOffResponse signOff(Long userId, UserDetails currentUser) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new RegisteredUserNotFoundException("가입된 계정을 찾을 수 없습니다.");
        }

        User user = userOptional.get();

        String targetUserEmail = user.getEmail();
        String currentUserEmail = currentUser.getUsername(); // username 대신 email을 등록하여 인증함

        if (!targetUserEmail.equals(currentUserEmail)) {
            throw new UnauthorizedAccountAttemptException("회원 탈퇴를 요청한 계정 정보와 로그인 사용자 정보가 일치하지 않습니다.");
        }

        userRepository.deleteById(userId);
        return UserSignOffResponse.builder()
                .userId(userId)
                .build();
    }

    public FindUserResponse findUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(RegisteredUserNotFoundException::new);
        boolean passwordExists = StringUtils.isNotEmpty(user.getPassword());
        return new FindUserResponse(user.getEmail(), passwordExists);
    }

    public boolean checkNicknameAvailability(String nickname) {
        Optional<User> userOptional = userRepository.findByNickname(nickname);
        if (userOptional.isPresent()) {
            return false; // 닉네임이 이미 존재할 경우
        } else {
            return true; // 닉네임이 존재하지 않을 경우
        }
    }

    @Transactional
    public void setUserPassword(
            PasswordChangeRequest passwordChangeRequest
    ) {
        String email = passwordChangeRequest.getEmail();
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new RegisteredUserNotFoundException();
        }

        User user = userOptional.get();
        user.updatePassword(passwordChangeRequest.getNewPassword(), passwordEncoder);
    }
}


