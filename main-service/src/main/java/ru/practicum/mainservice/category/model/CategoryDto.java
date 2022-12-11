package ru.practicum.mainservice.category.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.mainservice.validation.Create;
import ru.practicum.mainservice.validation.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CategoryDto {
    @NotNull(groups = Update.class, message = "Id cannot be empty")
    private Long id;

    @NotBlank(groups = {Create.class, Update.class}, message = "Name can't be empty")
    private String name;
}
