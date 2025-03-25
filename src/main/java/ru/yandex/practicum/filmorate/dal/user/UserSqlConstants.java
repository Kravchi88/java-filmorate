package ru.yandex.practicum.filmorate.dal.user;

public interface UserSqlConstants {
    String SELECT_ALL_USERS = "SELECT * FROM users";
    String SELECT_USER_BY_ID = "SELECT * FROM users WHERE user_id = ?";
    String INSERT_USER = "INSERT INTO users (user_email, user_login, user_name, user_birthday) VALUES (?, ?, ?, ?)";
    String UPDATE_USER = "UPDATE users SET user_email = ?, user_login = ?, user_name = ?, user_birthday = ? WHERE user_id = ?";
    String DELETE_USER = "DELETE FROM users WHERE user_id = ?";
    String SELECT_USER_COUNT_BY_ID = "SELECT COUNT(*) FROM users WHERE user_id = ?";
    String SELECT_USER_FRIENDSHIPS = """
            SELECT requester_id, recipient_id, is_confirmed
            FROM user_friendships
            WHERE (requester_id = ? AND recipient_id = ?)
               OR (requester_id = ? AND recipient_id = ?)
            """;
    String INSERT_USER_FRIENDSHIP = """
            INSERT INTO user_friendships (requester_id, recipient_id, is_confirmed)
            VALUES (?, ?, ?)
            """;
    String UPDATE_USER_FRIENDSHIP = """
            UPDATE user_friendships
            SET is_confirmed = true
            WHERE requester_id = ? AND recipient_id = ?
            """;
    String DELETE_USER_FRIENDSHIP = """
            DELETE FROM user_friendships
            WHERE (requester_id = ? AND recipient_id = ?)
               OR (requester_id = ? AND recipient_id = ?)
            """;
    String SELECT_USER_FRIENDS = """
            SELECT u.*
            FROM users u
            JOIN user_friendships uf
              ON (u.user_id = uf.recipient_id AND uf.requester_id = ?)
               OR (u.user_id = uf.requester_id AND uf.recipient_id = ? AND uf.is_confirmed = true)
            """;
    String DELETE_USER_FROM_USER_EVENTS = """
            DELETE FROM user_events WHERE user_id = ?
            """;
}
