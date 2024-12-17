package ru.yandex.practicum.filmorate.dbTests;

import org.springframework.test.context.TestPropertySource;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.dal.mpa.MpaDbStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@Import({MpaDbStorage.class, MpaRowMapper.class})
@TestPropertySource(locations = "classpath:application-test.properties")
class MpaDbStorageTest {

    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public MpaDbStorageTest(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    @Test
    @DisplayName("Should retrieve all MPA ratings")
    void testGetAllMpa() {
        Collection<Mpa> mpaList = mpaDbStorage.getAllMpa();

        assertThat(mpaList)
                .isNotNull()
                .hasSize(5)
                .extracting("name")
                .containsExactlyInAnyOrder("G", "PG", "PG-13", "R", "NC-17");
    }

    @Test
    @DisplayName("Should retrieve MPA by ID")
    void testGetMpaById() {
        Mpa mpa = mpaDbStorage.getMpaById(1).orElseThrow();

        assertThat(mpa)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "G");
    }

    @Test
    @DisplayName("Should throw NotFoundException for non-existing MPA ID")
    void testGetMpaByIdNotFound() {
        assertThatThrownBy(() -> mpaDbStorage.getMpaById(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("MPA rating with id 999 not found.");
    }
}
