package ru.practicum.mainservice.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.mainservice.validation.Create;
import ru.practicum.mainservice.validation.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(groups = Create.class, message = "Name can't be empty")
    private String name;

    @Email(groups = {Create.class, Update.class}, message = "Invalid email")
    private String email;
}
