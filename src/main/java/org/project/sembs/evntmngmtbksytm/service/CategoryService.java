package org.project.sembs.evntmngmtbksytm.service;

import org.project.sembs.evntmngmtbksytm.dto.CategoryCreation;
import org.project.sembs.evntmngmtbksytm.dto.CategoryResponse;
import org.project.sembs.evntmngmtbksytm.exception.CategoryAlreadyExistsException;
import org.project.sembs.evntmngmtbksytm.model.Category;
import org.project.sembs.evntmngmtbksytm.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryResponse createCategory(CategoryCreation category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new CategoryAlreadyExistsException("Category with name '" + category.getName() + "' already exists.");
        }
        Category newCategory = new Category();
        newCategory.setName(category.getName());
        newCategory.setDescription(category.getDescription());
        return CategoryResponse.fromCategory(categoryRepository.save(newCategory));
    }

    public Page<CategoryResponse> getAllCategories(Pageable pagable) {
        Page<Category> categoriesPage = categoryRepository.findAll(pagable);
        return categoriesPage.map(CategoryResponse::fromCategory);
    }

    public Optional<CategoryResponse> findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(CategoryResponse::fromCategory);
    }
}
