package login.tikichat.domain.user.service;

import login.tikichat.domain.user.model.User;
import login.tikichat.domain.user.model.UserRefreshToken;
import login.tikichat.domain.user.repository.UserRefreshTokenRepository;
import login.tikichat.global.exception.filter.InvalidJsonWebTokenException;
import login.tikichat.global.auth.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRefreshTokenService {

    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final UserService userService;
    private final JwtService jwtService;

    /**
     * [리프레시 토큰 DB에 있는지 체크 & DB에 리프레시 토큰 업데이트 메서드]
     * 기존 리프레시 토큰이 DB에 있는지 체크하고
     * 있으면 리프레시 토큰을 재발급하는 메서드
     * @param oldRefreshToken
     * @return
     */
    @Transactional
    public UserRefreshToken checkRefreshTokenAndUpdate(String oldRefreshToken) {
        return userRefreshTokenRepository.findByRefreshToken(oldRefreshToken)
                .map(oldUserRefreshToken -> {
                    User user = oldUserRefreshToken.getUser();
                    String deviceId = oldUserRefreshToken.getDeviceId();
                    deleteOldUserRefreshToken(oldUserRefreshToken);
                    UserRefreshToken userRefreshToken = reIssueUserRefreshToken(user, deviceId);
                    return saveUserRefreshToken(userRefreshToken);
                })
                .orElseThrow(() -> new InvalidJsonWebTokenException());
    }

    /**
     * [기존에 대상 이메일/기기ID로 등록된 리프레시 토큰을 조회, 있으면 업데이트/없으면 추가하는 메서드]
     * @param email
     * @param deviceId
     * @param refreshToken
     */
    @Transactional
    public void findAndUpdateUserRefreshToken(String email, String deviceId, String refreshToken) {
        User user = userService.findByEmail(email);
        this.findByUserEmailAndDeviceId(email, deviceId)
            .ifPresentOrElse(oldUserRefreshToken -> {
                this.deleteOldUserRefreshToken(oldUserRefreshToken);
                UserRefreshToken newUserRefreshToken = this.createUserRefreshToken(user, deviceId, refreshToken);
                this.saveUserRefreshToken(newUserRefreshToken);
            },
            () -> {
                UserRefreshToken newUserRefreshToken = this.createUserRefreshToken(user, deviceId, refreshToken);
                this.saveUserRefreshToken(newUserRefreshToken);
        });
    }

    /**
     * [기존 리프레시 토큰 삭제 메서드]
     * @param oldUserRefreshToken
     */
    @Transactional
    private void deleteOldUserRefreshToken(UserRefreshToken oldUserRefreshToken) {
        userRefreshTokenRepository.delete(oldUserRefreshToken);
    }

    /**
     * [리프레시 토큰 재발급 매서드 - 토큰 자체 생성]
     * @param user
     * @param deviceId
     * @return
     */
    private UserRefreshToken reIssueUserRefreshToken(User user, String deviceId) {
        // 새 리프레시 토큰 발급
        return UserRefreshToken.builder()
                .user(user)
                .refreshToken(jwtService.createRefreshToken())
                .deviceId(deviceId)
                .build();
    }

    /**
     * [리프레시 토큰 생성 매서드 - 토큰 외부에서 주입]
     * @param user
     * @param deviceId
     * @return
     */
    private UserRefreshToken createUserRefreshToken(User user, String deviceId, String refreshToken) {
        // 새 리프레시 토큰 발급
        return UserRefreshToken.builder()
                .user(user)
                .refreshToken(refreshToken)
                .deviceId(deviceId)
                .build();
    }

    /**
     * [리프레시 토큰 저장 메서드]
     * @param userRefreshToken
     * @return
     */
    @Transactional
    private UserRefreshToken saveUserRefreshToken(UserRefreshToken userRefreshToken) {
        userRefreshTokenRepository.save(userRefreshToken);
        return userRefreshToken;
    }

    /**
     * [사용자 이메일과 디바이스 ID로 기존에 DB에 저장된 리프레시 토큰을 조회하는 메서드]
     * @param email
     * @param deviceId
     * @return
     */
    @Transactional(readOnly = true)
    private Optional<UserRefreshToken> findByUserEmailAndDeviceId(String email, String deviceId) {
        return userRefreshTokenRepository.findByUserEmailAndDeviceId(email, deviceId);
    }
}
