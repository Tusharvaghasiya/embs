package org.project.sembs.evntmngmtbksytm.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryCreation {

    @NotNull
    @Size(max = 100)
    private String name;

    @NotNull
    private String description;
}
