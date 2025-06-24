package org.project.sembs.evntmngmtbksytm.dto;

import lombok.Getter;
import lombok.Setter;
import org.project.sembs.evntmngmtbksytm.model.Category;

import java.time.OffsetDateTime;

@Getter
@Setter
public class CategoryResponse {

    private Long id;
    private String name;
    private String desc;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static CategoryResponse fromCategory(Category category) {
        CategoryResponse dto = new CategoryResponse();
        dto.setId(category.getId());
        dto.setDesc(category.getDescription());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());

        return dto;
    }

}
