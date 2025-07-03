package org.project.sembs.evntmngmtbksytm.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class EventCreation {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String venueName;

    @Min(value = 1, message = "Capacity must be atleast 1")
    private int capacity;

    @NotBlank
    private String city;

    @NotBlank
    private String country;

    @NotBlank
    private String addressLine;

    @NotBlank
    private String postalCode;

    @NotBlank
    private String endDateTime;

    @NotBlank
    private String startDateTime;

    private BigDecimal latitude;
    private BigDecimal longitude;

    @NotEmpty
    private List<Long> categoryList;

}
