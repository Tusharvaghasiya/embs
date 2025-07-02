package org.project.sembs.evntmngmtbksytm.service;

import org.project.sembs.evntmngmtbksytm.dto.CategoryCreation;
import org.project.sembs.evntmngmtbksytm.dto.CategoryResponse;
import org.project.sembs.evntmngmtbksytm.dto.CategoryUpdate;
import org.project.sembs.evntmngmtbksytm.exception.CategoryAlreadyExistsException;
import org.project.sembs.evntmngmtbksytm.exception.CategoryInUseException;
import org.project.sembs.evntmngmtbksytm.exception.CategoryNotFoundException;
import org.project.sembs.evntmngmtbksytm.model.Category;
import org.project.sembs.evntmngmtbksytm.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    public CategoryResponse updateCategoryById(Long id, CategoryUpdate categoryUpdate) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException("Category not found for id: " + id));

        boolean isUpdateReq = false;

        if (StringUtils.hasText(categoryUpdate.getDesc()) && !categoryUpdate.getDesc().equals(category.getDescription())) {
            category.setDescription(categoryUpdate.getDesc());
            isUpdateReq = true;
        }

        if (StringUtils.hasText(categoryUpdate.getName()) && !categoryUpdate.getName().equals(category.getName())) {
            category.setName(categoryUpdate.getName());
            isUpdateReq = true;
        }

        if (isUpdateReq) {
            return CategoryResponse.fromCategory(categoryRepository.save(category));
        }
        return CategoryResponse.fromCategory(category);
    }

    public void deleteCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException("Category not found for id: " + id));

        if (!category.getEvents().isEmpty()) {
            throw new CategoryInUseException("Category cannot be deleted as it is associated with existing event.");
        }

        categoryRepository.delete(category);
    }

}
