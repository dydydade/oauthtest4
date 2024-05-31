package login.tikichat.domain.category.repository;

import login.tikichat.domain.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByOrderByOrderNumAsc();
    Optional<Category> findByCode(String code);
}
