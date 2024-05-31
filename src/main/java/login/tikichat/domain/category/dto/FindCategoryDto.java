package login.tikichat.domain.category.dto;

import java.util.List;

public class FindCategoryDto {
    public record FindCategoryItemRes (
        String code,
        String name,
        Integer orderNum
    ) {

    }

    public record FindCategoryRes (
        List<FindCategoryItemRes> categories
    ) {

    }
}
