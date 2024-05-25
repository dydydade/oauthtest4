package login.tikichat.domain.user.repository;

import login.tikichat.domain.user.model.SocialProfile;
import login.tikichat.domain.user.model.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialProfileRepository extends JpaRepository<SocialProfile, Long>, SocialProfileRepositoryCustom {

    Optional<SocialProfile> findBySocialEmailAndSocialTypeWithUser(String email, SocialType socialType);
}
