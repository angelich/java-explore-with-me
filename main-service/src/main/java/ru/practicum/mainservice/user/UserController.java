package ru.practicum.mainservice.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.user.model.UserDto;
import ru.practicum.mainservice.validation.Create;

import java.util.List;


/**
 * Контроллер для работы с пользователями
 */
@RestController
@Validated
@Log4j2
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    List<UserDto> getUsers(@RequestParam(name = "ids") List<Long> ids,
                           @RequestParam(name = "from", defaultValue = "0") Integer from,
                           @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get users={}, from={}, size={}", ids, from, size);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return userService.getUsers(ids, pageRequest);
    }

    @PostMapping
    UserDto createUser(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.info("Creating new user={}", userDto);
        return userService.createUser(userDto);
    }

    @DeleteMapping(path = "/{userId}")
    @ResponseStatus(HttpStatus.OK)
    void deleteUser(@PathVariable Long userId) {
        log.info("Delete user={}", userId);
        userService.deleteUser(userId);
    }
}
