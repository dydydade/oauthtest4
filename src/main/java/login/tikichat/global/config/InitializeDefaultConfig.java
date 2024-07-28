package login.tikichat.global.config;

import login.tikichat.domain.category.model.Category;
import login.tikichat.domain.category.repository.CategoryRepository;
import login.tikichat.domain.chat.model.Chat;
import login.tikichat.domain.chat.model.ChatReaction;
import login.tikichat.domain.chat.repository.ChatRepository;
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

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

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
    private final ChatRepository chatRepository;

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
            new Category("C_1001", "썸 · 연애", 1),
            new Category("C_1002", "다이어트 · 헬스", 2),
            new Category("C_1003", "직장인/취업", 3),
            new Category("C_1004", "공부/대입", 4),
            new Category("C_1005", "자기계발/재테크", 5),
            new Category("C_1006", "가족 · 결혼", 6),
            new Category("C_1007", "연예인/팬", 7),
            new Category("C_1008", "드라마 · 영화", 8),
            new Category("C_1009", "뷰티/패션", 9),
            new Category("C_1010", "취미", 10)
        );

        ChatRoom chatRoom = new ChatRoom(1L, "테스트 채팅 룸1", 10, List.of("고민"), categories.get(0));
        ChatRoom chatRoom2 = new ChatRoom(2L, "테스트 채팅 룸2", 10, List.of("고민"), categories.get(0));
        ChatRoom chatRoom3 = new ChatRoom(3L, "테스트 채팅 룸3", 10, List.of("고민"), categories.get(1));
        ChatRoom chatRoom4 = new ChatRoom(4L, "테스트 채팅 룸4", 10, List.of("고민"), categories.get(2));
        ChatRoom chatRoom5 = new ChatRoom(5L, "테스트 채팅 룸5", 10, List.of("고민"), categories.get(3));

        List<ChatRoom> chatRooms = List.of(
                chatRoom, chatRoom2, chatRoom3, chatRoom4, chatRoom5
        );

        userRepository.save(user);
        userRepository.save(user2);
        categoryRepository.saveAll(categories);
        chatRoomRepository.saveAll(chatRooms);

        Instant now = Instant.now();
        Instant end = now.truncatedTo(ChronoUnit.DAYS);
        Instant start = end.minus(1, ChronoUnit.DAYS);

        ChatReaction chatReaction1 = new ChatReaction();

        Chat chat = new Chat(1L, "stradfs", 1L, chatRoom, end.minus(3, ChronoUnit.HOURS), Set.of(chatReaction1));
        Chat chat2 = new Chat(2L, "stradfs", 1L, chatRoom, end.minus(3, ChronoUnit.HOURS), Set.of(chatReaction1));
        Chat chat3 = new Chat(3L, "stradfs", 1L, chatRoom, end.minus(3, ChronoUnit.HOURS), Set.of(chatReaction1));
        Chat chat4 = new Chat(4L, "asdf", 2L, chatRoom2, end.minus(4, ChronoUnit.HOURS), Set.of(chatReaction1));
        Chat chat5 = new Chat(5L, "asdf", 2L, chatRoom2, end.minus(4, ChronoUnit.HOURS), Set.of(chatReaction1));
        Chat chat6 = new Chat(6L, "asdf", 2L, chatRoom3, end.minus(4, ChronoUnit.HOURS), Set.of(chatReaction1));
        Chat chat7 = new Chat(7L, "asdf", 2L, chatRoom4, end.minus(4, ChronoUnit.HOURS), Set.of(chatReaction1));
        Chat chat8 = new Chat(8L, "asdf", 2L, chatRoom5, end.minus(4, ChronoUnit.HOURS), Set.of(chatReaction1));
        Chat chat9 = new Chat(9L, "asdf", 2L, chatRoom5, end.minus(4, ChronoUnit.HOURS), Set.of(chatReaction1));

        List<Chat> chats = List.of(
                chat, chat2, chat3, chat4, chat5, chat6, chat7, chat8, chat9
        );

        chatRepository.saveAll(chats);

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
