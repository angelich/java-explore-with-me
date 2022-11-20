package ru.practicum.mainservice.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.model.UserDto;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.mainservice.user.UserMapper.toUser;
import static ru.practicum.mainservice.user.UserMapper.toUserDto;

/**
 * Сервис по работе с пользователями
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getUsers(List<Long> ids, PageRequest pageRequest) {
        return userRepository.findAllByIdIn(ids, pageRequest)
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = toUser(userDto);
        return toUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
