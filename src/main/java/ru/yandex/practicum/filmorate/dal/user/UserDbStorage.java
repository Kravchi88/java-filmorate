package ru.yandex.practicum.filmorate.dal.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.feed.FeedDbStorage;
import ru.yandex.practicum.filmorate.dal.mappers.UserEventRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserEvent;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Database-backed implementation of {@link UserStorage}.
 * Provides CRUD operations and friendship management for {@link User} entities.
 */
@Repository("userDbStorage")
public class UserDbStorage implements UserStorage, UserSqlConstants {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> userRowMapper;
    private final UserEventRowMapper userEventRowMapper;
    private final FeedDbStorage feedDbStorage;

    /**
     * Constructs a {@link UserDbStorage} with its dependencies.
     *
     * @param jdbcTemplate  the {@link JdbcTemplate} used for database operations.
     * @param userRowMapper the {@link RowMapper} used to map result sets to {@link User} objects.
     */
    public UserDbStorage(JdbcTemplate jdbcTemplate, RowMapper<User> userRowMapper, UserEventRowMapper userEventRowMapper, FeedDbStorage feedDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRowMapper = userRowMapper;
        this.userEventRowMapper = userEventRowMapper;
        this.feedDbStorage = feedDbStorage;
    }

    /**
     * Retrieves all users from the database.
     *
     * @return a {@link Collection} of all {@link User} objects.
     */
    @Override
    public Collection<User> getAllUsers() {
        return extractUsers(SELECT_ALL_USERS).values();
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
        Map<Long, User> userMap = extractUsers(SELECT_USER_BY_ID, id);

        if (userMap.isEmpty()) {
            throw new NotFoundException("User with id = " + id + " doesn't exist");
        }

        return userMap.get(id);
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

        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        return getUserById(user.getId());
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

        return getUserById(user.getId());
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

            UserEvent userEvent = new UserEvent();
            userEvent.setUserId(String.valueOf(userId));
            userEvent.setEventType("FRIEND");
            userEvent.setOperation("ADD");
            userEvent.setEntityId(friendId);
            userEvent.setTimestamp(Instant.now().toEpochMilli());
            feedDbStorage.addEvent(userEvent);

        } else {
            Map<String, Object> friendship = existingFriendships.get(0);
            boolean isConfirmed = (boolean) friendship.get("is_confirmed");
            long requesterId = (long) friendship.get("requester_id");

            if (!isConfirmed && requesterId == friendId) {
                jdbcTemplate.update(UPDATE_USER_FRIENDSHIP, friendId, userId);
            }

        }
    }

    @Override
    public List<UserEvent> getUserEvents(long userId) {
        String sql = "SELECT * FROM user_events WHERE user_Id = ?";
        return jdbcTemplate.query(sql, userEventRowMapper, userId);
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
            UserEvent userEvent = new UserEvent();
            userEvent.setUserId(String.valueOf(userId));
            userEvent.setEventType("FRIEND");
            userEvent.setOperation("REMOVE");
            userEvent.setEntityId(friendId);
            userEvent.setTimestamp(Instant.now().toEpochMilli());
            feedDbStorage.addEvent(userEvent);
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
     * @param userId  the ID of the first user.
     * @param otherId the ID of the second user.
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
     * Extracts user data from the database based on the provided SQL query and parameters.
     * This method retrieves basic user information, enriches it with their friends and liked films,
     * and maps all users into a `Map<Long, User>` where the key is the user ID.
     *
     * @param sql    the SQL query string to execute.
     * @param params the parameters to include in the SQL query (e.g., user IDs, conditions).
     * @return a {@link Map} where the key is the user ID and the value is the {@link User} object
     * enriched with their friends and liked films.
     * @throws RuntimeException if there is an issue while mapping user data.
     */
    private Map<Long, User> extractUsers(String sql, Object... params) {
        Map<Long, User> userMap = new HashMap<>();

        jdbcTemplate.query(sql, rs -> {
            long userId = rs.getLong("user_id");

            userMap.computeIfAbsent(userId, id -> {
                User user;
                try {
                    user = userRowMapper.mapRow(rs, rs.getRow());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                assert user != null;
                user.setFriends(new HashSet<>());
                user.setLikedFilms(new HashSet<>());
                return user;
            });
        }, params);

        enrichUserFriends(userMap);
        enrichUserLikes(userMap);

        return userMap;
    }

    /**
     * Enriches the provided users with their friends' IDs.
     * This method retrieves friendship information for all users in the map and updates each user's
     * `friends` set based on their friendships in the database.
     *
     * @param userMap a {@link Map} of users to enrich with their friends' IDs.
     */
    private void enrichUserFriends(Map<Long, User> userMap) {
        if (userMap.isEmpty()) return;

        String sql = """
                SELECT uf.requester_id, uf.recipient_id, uf.is_confirmed
                FROM user_friendships uf
                WHERE uf.requester_id IN (%s) OR uf.recipient_id IN (%s)
                """.formatted(
                userMap.keySet().stream().map(String::valueOf).collect(Collectors.joining(", ")),
                userMap.keySet().stream().map(String::valueOf).collect(Collectors.joining(", "))
        );

        jdbcTemplate.query(sql, rs -> {
            long requesterId = rs.getLong("requester_id");
            long recipientId = rs.getLong("recipient_id");
            boolean isConfirmed = rs.getBoolean("is_confirmed");

            if (userMap.containsKey(requesterId)) {
                userMap.get(requesterId).getFriends().add(recipientId);
            }
            if (isConfirmed && userMap.containsKey(recipientId)) {
                userMap.get(recipientId).getFriends().add(requesterId);
            }
        });
    }

    /**
     * Enriches the provided users with the IDs of films they liked.
     * This method retrieves film likes for all users in the map and updates each user's
     * `likedFilms` set based on their likes in the database.
     *
     * @param userMap a {@link Map} of users to enrich with their liked films' IDs.
     */
    private void enrichUserLikes(Map<Long, User> userMap) {
        if (userMap.isEmpty()) return;

        String sql = """
                SELECT user_id, film_id
                FROM user_film_likes
                WHERE user_id IN (%s)
                """.formatted(
                userMap.keySet().stream().map(String::valueOf).collect(Collectors.joining(", "))
        );

        jdbcTemplate.query(sql, rs -> {
            long userId = rs.getLong("user_id");
            long filmId = rs.getLong("film_id");

            userMap.get(userId).getLikedFilms().add(filmId);
        });
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


}