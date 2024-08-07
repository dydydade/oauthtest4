package login.tikichat.domain.category.service;

import login.tikichat.domain.category.dto.FindCategoryDto;
import login.tikichat.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public FindCategoryDto.FindCategoryRes findCategories() {
        return new FindCategoryDto.FindCategoryRes(categoryRepository.findAllByOrderByOrderNumAsc()
                .stream().map((category -> new FindCategoryDto.FindCategoryItemRes(
                        category.getCode(),
                        category.getName(),
                        category.getOrderNum()
                ))).collect(Collectors.toList())
        );
    }
}
