package login.tikichat.domain.category.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import login.tikichat.domain.category.dto.FindCategoryDto;
import login.tikichat.domain.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "ChatRoom Category API", description = "채팅방 카테고리 API")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(
            summary = "카테고리 리스트를 불러옵니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 리스트 조회가 완료 되었습니다.",
                    content = {@Content(schema = @Schema(implementation = FindCategoryDto.FindCategoryRes.class))}
            )
    })
    @GetMapping("")
    public FindCategoryDto.FindCategoryRes findCategories() {
        return this.categoryService.findCategories();
    }
}
