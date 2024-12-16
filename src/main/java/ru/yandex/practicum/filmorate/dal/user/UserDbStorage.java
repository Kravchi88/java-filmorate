package ru.yandex.practicum.filmorate.dal.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Database-backed implementation of {@link UserStorage}.
 * Provides CRUD operations and friendship management for {@link User} entities.
 */
@Repository("userDbStorage")
public class UserDbStorage implements UserStorage {

    private static final String SELECT_ALL_USERS = "SELECT * FROM users";
    private static final String SELECT_USER_BY_ID = "SELECT * FROM users WHERE user_id = ?";
    private static final String INSERT_USER = "INSERT INTO users (user_email, user_login, user_name, user_birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE users SET user_email = ?, user_login = ?, user_name = ?, user_birthday = ? WHERE user_id = ?";
    private static final String DELETE_USER = "DELETE FROM users WHERE user_id = ?";
    private static final String SELECT_USER_COUNT_BY_ID = "SELECT COUNT(*) FROM users WHERE user_id = ?";
    private static final String SELECT_USER_LIKED_FILMS = "SELECT film_id FROM user_film_likes WHERE user_id = ?";
    private static final String SELECT_USER_FRIENDSHIPS = """
        SELECT requester_id, recipient_id, is_confirmed
        FROM user_friendships
        WHERE (requester_id = ? AND recipient_id = ?)
           OR (requester_id = ? AND recipient_id = ?)
        """;
    private static final String INSERT_USER_FRIENDSHIP = """
        INSERT INTO user_friendships (requester_id, recipient_id, is_confirmed)
        VALUES (?, ?, ?)
        """;
    private static final String UPDATE_USER_FRIENDSHIP = """
        UPDATE user_friendships
        SET is_confirmed = true
        WHERE requester_id = ? AND recipient_id = ?
        """;
    private static final String DELETE_USER_FRIENDSHIP = """
        DELETE FROM user_friendships
        WHERE (requester_id = ? AND recipient_id = ?)
           OR (requester_id = ? AND recipient_id = ?)
        """;
    private static final String SELECT_USER_FRIENDS = """
        SELECT u.*
        FROM users u
        JOIN user_friendships uf
          ON (u.user_id = uf.recipient_id AND uf.requester_id = ?)
           OR (u.user_id = uf.requester_id AND uf.recipient_id = ? AND uf.is_confirmed = true)
        """;

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> userRowMapper;

    /**
     * Constructs a {@link UserDbStorage} with its dependencies.
     *
     * @param jdbcTemplate  the {@link JdbcTemplate} used for database operations.
     * @param userRowMapper the {@link RowMapper} used to map result sets to {@link User} objects.
     */
    public UserDbStorage(JdbcTemplate jdbcTemplate, RowMapper<User> userRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRowMapper = userRowMapper;
    }

    /**
     * Retrieves all users from the database.
     *
     * @return a {@link Collection} of all {@link User} objects.
     */
    @Override
    public Collection<User> getAllUsers() {
        Collection<User> users = jdbcTemplate.query(SELECT_ALL_USERS, userRowMapper);
        users.forEach(this::populateUserDetails);
        return users;
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve.
     * @return the {@link User} if found.
     * @throws NotFoundException if the user does not exist.
     */
    @Override
    public User getUserById(long id) {
        return jdbcTemplate.query(SELECT_USER_BY_ID, userRowMapper, id)
                .stream()
                .findFirst()
                .map(this::populateUserDetails)
                .orElseThrow(() -> new NotFoundException("User with id = " + id + " doesn't exist"));
    }

    /**
     * Adds a new user to the database.
     *
     * @param user the {@link User} to add.
     * @return the added {@link User} with its generated ID.
     */
    @Override
    public User addUser(User user) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        user.setId(generatedId);

        return populateUserDetails(user);
    }

    /**
     * Updates an existing user in the database.
     *
     * @param user the {@link User} with updated information.
     * @return the updated {@link User}.
     * @throws NotFoundException if the user does not exist.
     */
    @Override
    public User updateUser(User user) {
        validateUserExists(user.getId());
        int updatedRows = jdbcTemplate.update(UPDATE_USER,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId()
        );

        if (updatedRows == 0) {
            throw new NotFoundException("User with id = " + user.getId() + " doesn't exist");
        }

        return populateUserDetails(user);
    }

    /**
     * Deletes a user from the database by their ID.
     *
     * @param id the ID of the user to delete.
     */
    @Override
    public void deleteUser(long id) {
        validateUserExists(id);
        jdbcTemplate.update(DELETE_USER, id);
    }

    /**
     * Adds a friendship between two users.
     *
     * @param userId   the ID of the first user.
     * @param friendId the ID of the second user.
     */
    @Override
    public void addFriend(long userId, long friendId) {
        validateUserExists(userId);
        validateUserExists(friendId);

        List<Map<String, Object>> existingFriendships = jdbcTemplate.queryForList(
                SELECT_USER_FRIENDSHIPS, userId, friendId, friendId, userId);

        if (existingFriendships.isEmpty()) {
            jdbcTemplate.update(INSERT_USER_FRIENDSHIP, userId, friendId, false);
        } else {
            Map<String, Object> friendship = existingFriendships.get(0);
            boolean isConfirmed = (boolean) friendship.get("is_confirmed");
            long requesterId = (long) friendship.get("requester_id");

            if (!isConfirmed && requesterId == friendId) {
                jdbcTemplate.update(UPDATE_USER_FRIENDSHIP, friendId, userId);
            }
        }
    }

    /**
     * Removes a friendship between two users.
     *
     * @param userId   the ID of the first user.
     * @param friendId the ID of the second user.
     */
    @Override
    public void removeFriend(long userId, long friendId) {
        validateUserExists(userId);
        validateUserExists(friendId);

        List<Map<String, Object>> results = jdbcTemplate.queryForList(
                SELECT_USER_FRIENDSHIPS, userId, friendId, friendId, userId);

        jdbcTemplate.update(DELETE_USER_FRIENDSHIP, userId, friendId, friendId, userId);

        for (Map<String, Object> record : results) {
            long recipientId = (long) record.get("recipient_id");
            boolean isConfirmed = (boolean) record.get("is_confirmed");

            if (isConfirmed || recipientId == userId) {
                jdbcTemplate.update(INSERT_USER_FRIENDSHIP, friendId, userId, false);
            }
        }
    }

    /**
     * Retrieves a collection of a user's friends.
     *
     * @param userId the ID of the user.
     * @return a {@link Collection} of the user's friends.
     */
    @Override
    public Collection<User> getFriends(long userId) {
        validateUserExists(userId);
        return jdbcTemplate.query(SELECT_USER_FRIENDS, userRowMapper, userId, userId);
    }

    /**
     * Retrieves a collection of mutual friends between two users.
     *
     * @param userId   the ID of the first user.
     * @param otherId  the ID of the second user.
     * @return a {@link Collection} of mutual friends.
     */
    @Override
    public Collection<User> getCommonFriends(long userId, long otherId) {
        validateUserExists(userId);
        validateUserExists(otherId);

        Collection<User> userFriends = getFriends(userId);
        Collection<User> otherFriends = getFriends(otherId);

        Set<Long> commonFriendIds = userFriends.stream()
                .map(User::getId)
                .filter(id -> otherFriends.stream().map(User::getId).collect(Collectors.toSet()).contains(id))
                .collect(Collectors.toSet());

        return userFriends.stream()
                .filter(user -> commonFriendIds.contains(user.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Validates if a user exists by their ID.
     *
     * @param userId the ID of the user.
     * @throws NotFoundException if the user does not exist.
     */
    private void validateUserExists(long userId) {
        Integer count = jdbcTemplate.queryForObject(SELECT_USER_COUNT_BY_ID, Integer.class, userId);
        if (count == null || count == 0) {
            throw new NotFoundException("User with ID " + userId + " does not exist.");
        }
    }

    /**
     * Enriches a {@link User} with their friends and liked films.
     *
     * @param user the {@link User} to populate.
     * @return the enriched {@link User}.
     */
    private User populateUserDetails(User user) {
        getFriends(user.getId()).forEach(friend -> user.getFriends().add(friend.getId()));
        getUserLikedFilms(user.getId()).forEach(user.getLikedFilms()::add);
        return user;
    }

    /**
     * Retrieves a set of film IDs liked by a user.
     *
     * @param userId the ID of the user.
     * @return a {@link Set} of film IDs.
     */
    private Set<Long> getUserLikedFilms(long userId) {
        return new HashSet<>(jdbcTemplate.queryForList(SELECT_USER_LIKED_FILMS, Long.class, userId));
    }
}