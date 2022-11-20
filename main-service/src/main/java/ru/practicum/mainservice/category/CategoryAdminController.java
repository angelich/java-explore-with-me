package ru.practicum.mainservice.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.category.model.CategoryDto;
import ru.practicum.mainservice.validation.Create;
import ru.practicum.mainservice.validation.Update;


/**
 * Приватный контроллер для работы с категориями
 */
@RestController
@Validated
@Log4j2
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PatchMapping
    CategoryDto updateCategory(@Validated(Update.class) @RequestBody CategoryDto categoryDto) {
        log.info("Updating category={}", categoryDto);
        return categoryService.updateCategory(categoryDto);
    }

    @PostMapping
    CategoryDto createCategory(@Validated(Create.class) @RequestBody CategoryDto categoryDto) {
        log.info("Creating category={}", categoryDto);
        return categoryService.createCategory(categoryDto);
    }

    @DeleteMapping(path = "/{catId}")
    @ResponseStatus(HttpStatus.OK)
    void deleteUCategory(@PathVariable Long catId) {
        log.info("Delete category={}", catId);
        categoryService.deleteCategory(catId);
    }
}
