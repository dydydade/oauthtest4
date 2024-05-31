package login.tikichat.global.config;

import login.tikichat.domain.category.model.Category;
import login.tikichat.domain.category.repository.CategoryRepository;
import login.tikichat.domain.chatroom.model.ChatRoom;
import login.tikichat.domain.chatroom.repository.ChatRoomRepository;
import login.tikichat.domain.terms.dto.TermsCreateRequest;
import login.tikichat.domain.terms.model.TermsType;
import login.tikichat.domain.terms.service.TermsService;
import login.tikichat.domain.user.model.Role;
import login.tikichat.domain.user.model.SocialProfile;
import login.tikichat.domain.user.model.SocialType;
import login.tikichat.domain.user.model.User;
import login.tikichat.domain.user.repository.SocialProfileRepository;
import login.tikichat.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

/**
 * 초기 상태 등록 Config
 */
@Configuration
@RequiredArgsConstructor
@Profile(value = "!test") // test 에서는 제외
public class InitializeDefaultConfig implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SocialProfileRepository socialProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final TermsService termsService;
    private final CategoryRepository categoryRepository;
    private final ChatRoomRepository chatRoomRepository;

    /**
     * 앱 계정(User) 및 소셜 연동 정보 저장
     */
    @Override
    public void run(String... args) throws Exception {
        User user = User.builder()
                .id(1L)
                .email("dydydade@gmail.com")
                .role(Role.SOCIAL)
//                .password(passwordEncoder.encode("1234"))
                .build();
        User user2 = User.builder()
                .id(2L)
                .email("n4oahdev@gmail.com")
                .role(Role.USER)
                .password(passwordEncoder.encode("1234"))
                .build();
        SocialProfile naver = SocialProfile.builder()
                .id(1L)
                .socialEmail("xx5882@naver.com")
                .socialType(SocialType.NAVER)
                .socialId("id")
                .user(user)
                .build();
        SocialProfile kakao = SocialProfile.builder()
                .id(2L)
                .socialEmail("xx5882@naver.com")
                .socialType(SocialType.KAKAO)
                .socialId("id")
                .user(user)
                .build();
        SocialProfile google = SocialProfile.builder()
                .id(3L)
                .socialEmail("dydydade@gmail.com")
                .socialType(SocialType.GOOGLE)
                .socialId("sub")
                .user(user)
                .build();

        List<Category> categories = List.of(
            new Category("C_1001", "게임", 1),
            new Category("C_1002", "연애", 1)
        );

        List<ChatRoom> chatRooms = List.of(
                new ChatRoom(
                        2L,
                        "테스트 채팅 룸",
                        10,
                        List.of("고민"),
                        categories.get(0)
                )
        );

        userRepository.save(user);
        userRepository.save(user2);
        categoryRepository.saveAll(categories);
        chatRoomRepository.saveAll(chatRooms);

//        socialProfileRepository.save(naver);
//        socialProfileRepository.save(kakao);
//        socialProfileRepository.save(google);


        // 이용약관 저장
        TermsCreateRequest termsOfService = TermsCreateRequest.builder()
                .termsType(TermsType.TERMS_OF_SERVICE)
                .version("1.0")
                .content("서비스 이용 동의입니다.")
                .mandatory(true)
                .effectiveDate(LocalDate.now())
                .build();
        TermsCreateRequest privacyPolicy = TermsCreateRequest.builder()
                .termsType(TermsType.PRIVACY_POLICY)
                .version("1.0")
                .content("개인정보 이용약관입니다.")
                .mandatory(true)
                .effectiveDate(LocalDate.now())
                .build();

        termsService.createTerms(termsOfService);
        termsService.createTerms(privacyPolicy);
    }
}
