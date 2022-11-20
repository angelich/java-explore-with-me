package ru.practicum.mainservice.category;


import org.springframework.data.domain.PageRequest;
import ru.practicum.mainservice.category.model.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto updateCategory(CategoryDto categoryDto);

    CategoryDto createCategory(CategoryDto categoryDto);

    void deleteCategory(Long catId);

    List<CategoryDto> getCategories(PageRequest pageRequest);

    CategoryDto getOneCategory(Long categoryId);
}
