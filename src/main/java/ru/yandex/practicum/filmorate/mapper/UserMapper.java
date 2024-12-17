package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;

/**
 * Mapper class for converting {@link User} entities to {@link UserDto} objects.
 * Provides a method for mapping domain models to Data Transfer Objects (DTOs).
 */
@Component
public class UserMapper {

    /**
     * Converts a {@link User} entity to a {@link UserDto}.
     *
     * @param user the {@link User} entity to convert.
     * @return the corresponding {@link UserDto}.
     */
    public UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setLogin(user.getLogin());
        userDto.setName(user.getName());
        userDto.setBirthday(user.getBirthday());
        userDto.setFriends(new ArrayList<>(user.getFriends()));
        userDto.setLikedFilms(new ArrayList<>(user.getLikedFilms()));

        return userDto;
    }
}