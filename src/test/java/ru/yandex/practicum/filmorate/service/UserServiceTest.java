/*package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dal.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.dal.genre.InMemoryGenreStorage;
import ru.yandex.practicum.filmorate.dal.mpa.InMemoryMpaStorage;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dal.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserService userService;

    @BeforeEach

    void setUp() {
        FilmService filmService = new FilmService(
                new InMemoryFilmStorage(new InMemoryGenreStorage(), new InMemoryMpaStorage()),
                new FilmMapper(new GenreMapper(), new MpaMapper())
        );
        userService = new UserService(new InMemoryUserStorage(), new UserMapper(), filmService);
    }

    @Test
    void shouldAddUserSuccessfully() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        UserDto addedUser = userService.addUser(user);
        assertEquals(1, addedUser.getId());
        assertEquals("test@example.com", addedUser.getEmail());
        assertEquals(1, userService.getAllUsers().size());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUpdatingNonExistentUser() {
        User user = new User();
        user.setId(999);
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.updateUser(user));
        assertEquals("User with id = 999 doesn't exist", exception.getMessage());
    }

    @Test
    void shouldAddFriendSuccessfully() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(2000, 1, 1));

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(2000, 1, 2));

        userService.addUser(user1);
        userService.addUser(user2);

        userService.addFriend(1, 2);
        List<UserDto> friends = userService.getFriends(1).stream().toList();

        assertEquals(1, friends.size());
        assertEquals("User Two", friends.get(0).getName());
    }
}*/
