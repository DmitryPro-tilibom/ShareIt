package ru.practicum.user.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.user.User;

@UtilityClass
public class UserMapper {
    public UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public UserDtoIdName toUserDtoIdName(User user) {
        return new UserDtoIdName(
                user.getId(),
                user.getName()
        );
    }

    public User toUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }
}
