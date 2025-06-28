package org.project.sembs.evntmngmtbksytm.controller;

import org.project.sembs.evntmngmtbksytm.dto.CategoryCreation;
import org.project.sembs.evntmngmtbksytm.dto.CategoryResponse;
import org.project.sembs.evntmngmtbksytm.model.Category;
import org.project.sembs.evntmngmtbksytm.service.CategoryService;
import org.project.sembs.evntmngmtbksytm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;

    @Autowired
    public CategoryController(CategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryCreation category) {
        return ResponseEntity.ok(categoryService.createCategory(category));
    }

    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> getAllCategories(@PageableDefault(size = 10, page = 0) Pageable pagable) {
        return ResponseEntity.ok(categoryService.getAllCategories(pagable));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long categoryId) {
        return categoryService.findCategoryById(categoryId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
