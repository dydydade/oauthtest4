package login.oauthtest4.domain.user.repository;

import login.oauthtest4.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("select u from User u left join fetch u.socialProfiles where u.email = :email")
    Optional<User> findByEmailWithSocialProfiles(String email);

    @Query("select u from User u left join fetch u.socialProfiles sp where sp.socialEmail = :email")
    Optional<User> checkUserSocialEmailMatched(String email);

    Optional<User> findByNickname(String nickname);
}
