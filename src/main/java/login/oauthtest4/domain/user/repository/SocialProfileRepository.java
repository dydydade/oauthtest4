package login.oauthtest4.domain.user.repository;

import login.oauthtest4.domain.user.SocialProfile;
import login.oauthtest4.domain.user.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SocialProfileRepository extends JpaRepository<SocialProfile, Long> {

    @Query("select sp from SocialProfile sp join fetch sp.user u where sp.socialEmail = :email and sp.socialType = :socialType")
    Optional<SocialProfile> findBySocialEmailAndSocialTypeWithUser(String email, SocialType socialType);

}
