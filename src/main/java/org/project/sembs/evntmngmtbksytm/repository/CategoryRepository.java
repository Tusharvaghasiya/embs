package org.project.sembs.evntmngmtbksytm.repository;

import org.project.sembs.evntmngmtbksytm.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);
}
