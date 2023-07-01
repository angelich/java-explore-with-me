package ru.practicum.mainservice.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.category.model.CategoryDto;
import ru.practicum.mainservice.error.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.mainservice.category.CategoryMapper.toCategory;
import static ru.practicum.mainservice.category.CategoryMapper.toCategoryDto;
import static ru.practicum.mainservice.error.Errors.CATEGORY_NOT_EXIST;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> getCategories(PageRequest pageRequest) {
        return categoryRepository.findAll(pageRequest)
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getOneCategory(Long categoryId) {
        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_EXIST.getMessage()));
        return toCategoryDto(category);
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        var updatedCategory = categoryRepository.save(toCategory(categoryDto));
        return toCategoryDto(updatedCategory);
    }

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        var savedCategory = categoryRepository.save(toCategory(categoryDto));
        return toCategoryDto(savedCategory);
    }

    @Override
    public void deleteCategory(Long catId) {
        categoryRepository.deleteById(catId);
    }
}
