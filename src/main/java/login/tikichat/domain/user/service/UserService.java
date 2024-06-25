package login.tikichat.domain.user.service;

import login.tikichat.domain.user.model.Role;
import login.tikichat.domain.user.model.User;
import login.tikichat.domain.user.dto.*;
import login.tikichat.domain.user.repository.UserRepository;
import login.tikichat.global.component.FileStorage;
import login.tikichat.global.component.FileUrlGenerator;
import login.tikichat.global.exception.user.NicknameAlreadyInUseException;
import login.tikichat.global.exception.user.RegisteredUserNotFoundException;
import login.tikichat.global.exception.user.UnauthorizedAccountAttemptException;
import login.tikichat.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NormalSignUpStrategy normalSignUpStrategy;
    private final SocialSignUpStrategy socialSignUpStrategy;
    private final FileStorage fileStorage;
    private final FileUrlGenerator fileUrlGenerator;

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

        this.verifyUserMatch(currentUser, user);

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
    public FindUserResponse findUserByEmail(String email) {
        User user = this.findByEmail(email);
        boolean passwordExists = user.getRole().equals(Role.USER); // 비밀번호가 존재하면 일반 계정
        return new FindUserResponse(user.getEmail(), passwordExists);
    }

    /**
     * [닉네임 중복 체크하는 메서드]
     * @param nickname
     * @return
     */
    public boolean checkNicknameAvailability(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }

    /**
     * [비밀번호 설정(소셜 계정 → 일반 계정으로 전환) 메서드]
     * @param email
     * @param passwordChangeRequest
     */
    @Transactional
    public void setUserPassword(String email, PasswordChangeRequest passwordChangeRequest) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RegisteredUserNotFoundException());

        if (user.getRole().equals(Role.SOCIAL)) {
            user.setRoleAsUser();
        }

        user.updatePassword(passwordEncoder.encode(passwordChangeRequest.getNewPassword()));
    }

    /**
     * [닉네임 설정 메서드]
     * @param email
     * @param request
     */
    @Transactional
    public void setUserNickname(String email, UserDetails currentUser, UserNicknameChangeRequest request) {
        userRepository.findByNickname(request.getNewNickname()).ifPresent(nickname -> {
            throw new NicknameAlreadyInUseException();
        });
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RegisteredUserNotFoundException());
        this.verifyUserMatch(currentUser, user);
        user.updateNickname(request.getNewNickname());
    }

    /**
     * [프로필 사진 설정하는 메서드]
     * @param email
     * @param multipartFile
     * @return
     * @throws IOException
     */
    @Transactional
    public URL setUserProfileImage(String email, UserDetails currentUser, MultipartFile multipartFile) throws IOException {
        final var ext = FileUtils.getExtByContentType(multipartFile.getContentType());
        final var user = userRepository.findByEmail(email).orElseThrow(() -> new RegisteredUserNotFoundException());
        this.verifyUserMatch(currentUser, user);

        final var path = "profile/" + FileUtils.getTimePath();  // 프로필 이미지용 디렉토리 구분
        final var filename = FileUtils.getRandomFilename(ext);

        fileStorage.upload(path + "/" + filename, multipartFile.getInputStream());
        final var fileUrl = fileUrlGenerator.generatePublicUrl(path + "/" + filename);

        user.updateImageUrl(fileUrl); // 사용자 프로필 이미지 정보 업데이트
        userRepository.save(user);

        return user.getImageUrl();
    }

    private static void verifyUserMatch(UserDetails currentUser, User user) {
        if (!user.isSameUser(currentUser.getUsername())) { // username 에 email 저장됨
            throw new UnauthorizedAccountAttemptException();
        }
    }
}


