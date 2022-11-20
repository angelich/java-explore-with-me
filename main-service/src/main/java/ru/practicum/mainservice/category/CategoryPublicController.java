package ru.practicum.mainservice.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.category.model.CategoryDto;

import java.util.List;


/**
 * Публичный контроллер для работы с категориями
 */
@RestController
@Validated
@Log4j2
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping
    List<CategoryDto> getCategories(@RequestParam(name = "from", defaultValue = "0") Integer from,
                                    @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get categories from={}, size={}", from, size);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return categoryService.getCategories(pageRequest);
    }

    @GetMapping("/{catId}")
    CategoryDto getOneCategory(@RequestParam(name = "catId", required = true) Long categoryId) {
        return categoryService.getOneCategory(categoryId);
    }
}
