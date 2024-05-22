package login.oauthtest4.domain.category.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import login.oauthtest4.domain.category.dto.FindCategoryDto;
import login.oauthtest4.domain.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "ChatRoom Category", description = "채팅방 카테고리")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(
            summary = "카테고리 리스트를 불러옵니다."
    )
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200"
                )
            }
    )
    @GetMapping("")
    public FindCategoryDto.FindCategoryRes findCategories() {
        return this.categoryService.findCategories();
    }
}
