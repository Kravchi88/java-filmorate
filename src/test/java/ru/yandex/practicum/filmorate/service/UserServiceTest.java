package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserService userService;
    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
    }

    @Test
    void shouldAddUserSuccessfully() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User addedUser = userService.addUser(user);
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
        List<User> friends = userService.getFriends(1).stream().toList();

        assertEquals(1, friends.size());
        assertEquals("User Two", friends.get(0).getName());
    }
}
