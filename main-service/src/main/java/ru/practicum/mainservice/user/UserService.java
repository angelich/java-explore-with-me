package ru.practicum.mainservice.user;

import org.springframework.data.domain.PageRequest;
import ru.practicum.mainservice.user.model.UserDto;

import java.util.List;

/**
 * Сервис по работе с пользователями
 */
public interface UserService {

    List<UserDto> getUsers(List<Long> ids, PageRequest pageRequest);

    UserDto createUser(UserDto userDto);

    void deleteUser(Long userId);
}
