package ru.yandex.practicum.filmorate.dbTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import ru.yandex.practicum.filmorate.dal.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@AutoConfigureTestDatabase
@Import({GenreDbStorage.class, GenreRowMapper.class})
@TestPropertySource(locations = "classpath:application-test.properties")
class GenreDbStorageTest {

    private final GenreDbStorage genreDbStorage;

    @Autowired
    public GenreDbStorageTest(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    @Test
    @DisplayName("Should fetch all genres")
    void testGetAllGenres() {
        Collection<Genre> genres = genreDbStorage.getAllGenres();
        assertThat(genres)
                .isNotNull()
                .isNotEmpty()
                .allSatisfy(genre -> {
                    assertThat(genre.getId()).isPositive();
                    assertThat(genre.getName()).isNotBlank();
                });
    }

    @Test
    @DisplayName("Should fetch genre by ID")
    void testGetGenreById() {
        Genre genre = genreDbStorage.getGenreById(1).orElseThrow();
        assertThat(genre.getId()).isEqualTo(1);
        assertThat(genre.getName()).isNotBlank();
    }

    @Test
    @DisplayName("Should throw exception when genre not found by ID")
    void testGetGenreByIdNotFound() {
        assertThrows(
                ru.yandex.practicum.filmorate.exception.NotFoundException.class,
                () -> genreDbStorage.getGenreById(999)
        );
    }
}