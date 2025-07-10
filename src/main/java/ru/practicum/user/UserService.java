package ru.practicum.user;
import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUserById(long userId);

    UserDto saveNewUser(UserDto userDto);

    UserDto updateUser(long userId, UserDto userDto);

    void deleteUser(long userId);
}
