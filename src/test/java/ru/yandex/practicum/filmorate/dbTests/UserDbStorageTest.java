package ru.yandex.practicum.filmorate.dbTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.dal.user.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, UserRowMapper.class})
@TestPropertySource(locations = "classpath:application-test.properties")
class UserDbStorageTest {

    private final UserDbStorage userDbStorage;

    @Autowired
    public UserDbStorageTest(UserDbStorage userDbStorage) {
        this.userDbStorage = userDbStorage;
    }

    @Test
    @DisplayName("Should retrieve all users")
    void testGetAllUsers() {
        Collection<User> users = userDbStorage.getAllUsers();
        assertThat(users).isNotNull().hasSize(5);
        users.forEach(user -> {
            assertThat(user.getId()).isPositive();
            assertThat(user.getEmail()).isNotBlank();
            assertThat(user.getLogin()).isNotBlank();
        });
    }

    @Test
    @DisplayName("Should retrieve user by ID")
    void testGetUserById() {
        User user = userDbStorage.getUserById(1);
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.getEmail()).isEqualTo("user1@example.com");
    }

    @Test
    @DisplayName("Should throw exception for non-existent user ID")
    void testGetUserByIdNotFound() {
        assertThrows(ru.yandex.practicum.filmorate.exception.NotFoundException.class, () -> {
            userDbStorage.getUserById(999);
        });
    }

    @Test
    @DisplayName("Should add a new user")
    void testAddUser() {
        User user = new User();
        user.setEmail("newuser@example.com");
        user.setLogin("newuser");
        user.setName("New User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User savedUser = userDbStorage.addUser(user);
        assertThat(savedUser.getId()).isPositive();
        assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(savedUser.getLogin()).isEqualTo(user.getLogin());
    }

    @Test
    @DisplayName("Should update an existing user")
    void testUpdateUser() {
        User user = userDbStorage.getUserById(1);
        user.setEmail("updateduser@example.com");
        user.setLogin("updateduser");
        user.setName("Updated User");

        User updatedUser = userDbStorage.updateUser(user);
        assertThat(updatedUser.getEmail()).isEqualTo("updateduser@example.com");
        assertThat(updatedUser.getLogin()).isEqualTo("updateduser");
        assertThat(updatedUser.getName()).isEqualTo("Updated User");
    }

    @Test
    @DisplayName("Should delete a user")
    void testDeleteUser() {
        long userId = 1;
        userDbStorage.deleteUser(userId);
        assertThrows(ru.yandex.practicum.filmorate.exception.NotFoundException.class, () -> {
            userDbStorage.getUserById(userId);
        });
    }

    @Test
    @DisplayName("Should add a friend")
    void testAddFriend() {
        userDbStorage.addFriend(1, 2);
        Collection<User> friends = userDbStorage.getFriends(1);
        assertThat(friends).extracting(User::getId).contains(2L);
    }

    @Test
    @DisplayName("Should remove a friend")
    void testRemoveFriend() {
        userDbStorage.addFriend(1, 2);
        userDbStorage.removeFriend(1, 2);
        Collection<User> friends = userDbStorage.getFriends(1);
        assertThat(friends).extracting(User::getId).doesNotContain(2L);
    }

    @Test
    @DisplayName("Should retrieve user's friends")
    void testGetFriends() {
        Collection<User> friends = userDbStorage.getFriends(1);
        assertThat(friends).isNotNull().hasSize(1);
    }

    @Test
    @DisplayName("Should retrieve common friends between two users")
    void testGetCommonFriends() {
        Collection<User> commonFriends = userDbStorage.getCommonFriends(1, 2);
        assertThat(commonFriends).isNotNull().hasSize(0);
    }
}