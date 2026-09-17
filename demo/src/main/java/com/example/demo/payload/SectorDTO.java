package com.example.demo.payload;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SectorDTO {
    @Schema(description = "Sector ID for a particular sector",example = "101")
    private Integer sectorId; // if you want to hide categoryId just do not use this field in dto

    @NotBlank(message = "category can not be blank")
    @Size(min = 3, message = "Category name must contain atleast 5 characters")
    @JsonAlias({"SectorName", "name"})
    @JsonProperty("sectorName")
    private String sectorName;
}
