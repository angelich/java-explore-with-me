package ru.practicum.mainservice.category;


import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.category.model.CategoryDto;

public class CategoryMapper {
    private CategoryMapper() {
    }

    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category toCategory(CategoryDto dto) {
        return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }
}
