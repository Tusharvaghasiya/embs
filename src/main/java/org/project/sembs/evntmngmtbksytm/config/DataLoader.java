package org.project.sembs.evntmngmtbksytm.config;

import org.project.sembs.evntmngmtbksytm.dto.CategoryCreation;
import org.project.sembs.evntmngmtbksytm.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final CategoryService categoryService;

    @Autowired
    public DataLoader(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if categories already exist
        try {
            // Create 100 dummy categories
            for (int i = 1; i <= 100; i++) {
                CategoryCreation category = new CategoryCreation();
                category.setName("Category " + i);
                category.setDescription("Description for category " + i);
                
                try {
                    categoryService.createCategory(category);
                    System.out.println("Created category: " + category.getName());
                } catch (Exception e) {
                    // Skip if category already exists
                    System.out.println("Skipping category: " + category.getName() + " - " + e.getMessage());
                }
            }
            System.out.println("Finished creating dummy categories");
        } catch (Exception e) {
            System.err.println("Error creating dummy categories: " + e.getMessage());
            e.printStackTrace();
        }
    }
}