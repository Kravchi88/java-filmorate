package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper class for converting {@link Film} entities to {@link FilmDto} objects.
 * Provides utility methods for mapping domain models to Data Transfer Objects (DTOs).
 */
@Component
public class FilmMapper {

    private final GenreMapper genreMapper;
    private final MpaMapper mpaMapper;
    private final DirectorMapper directorMapper;

    /**
     * Constructs a new {@link FilmMapper} instance.
     *
     * @param genreMapper the {@link GenreMapper} for converting genres.
     * @param mpaMapper   the {@link MpaMapper} for converting MPA ratings.
     */
    public FilmMapper(GenreMapper genreMapper, MpaMapper mpaMapper, DirectorMapper directorMapper) {
        this.genreMapper = genreMapper;
        this.mpaMapper = mpaMapper;
        this.directorMapper = directorMapper;
    }

    /**
     * Converts a {@link Film} entity to a {@link FilmDto}.
     *
     * @param film the {@link Film} entity to convert.
     * @return the corresponding {@link FilmDto}.
     */
    public FilmDto toDto(Film film) {
        FilmDto filmDto = new FilmDto();
        filmDto.setId(film.getId());
        filmDto.setName(film.getName());
        filmDto.setDescription(film.getDescription());
        filmDto.setReleaseDate(film.getReleaseDate());
        filmDto.setDuration(film.getDuration());
        filmDto.setLikes(film.getLikes());
        filmDto.setMpa(mpaMapper.toDto(film.getMpa()));
        filmDto.setGenres(toSortedGenreDtoList(film.getGenres()));
        filmDto.setDirectors(toSortedDirectorDtoList(film.getDirectors()));
        return filmDto;
    }

    /**
     * Converts a {@link ResultSet} to a {@link FilmDto}.
     *
     * @param resultSet the {@link ResultSet} containing film data.
     * @return the corresponding {@link FilmDto}.
     * @throws SQLException if there is an error accessing the {@link ResultSet}.
     */
    public FilmDto toDto(ResultSet resultSet) throws SQLException {
        FilmDto filmDto = new FilmDto();

        filmDto.setId(resultSet.getLong("film_id"));
        filmDto.setName(resultSet.getString("name"));
        filmDto.setDescription(resultSet.getString("description"));
        filmDto.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        filmDto.setDuration(resultSet.getInt("duration"));
        filmDto.setLikes(resultSet.getInt("likes"));

        if (resultSet.getObject("mpa_id") != null) {
            int mpaId = resultSet.getInt("mpa_id");
            Mpa mpa = new Mpa();
            mpa.setId(mpaId);
            filmDto.setMpa(mpaMapper.toDto(mpa));
        }

        Set<Genre> genres = fetchGenres(resultSet.getLong("film_id"));
        filmDto.setGenres(toSortedGenreDtoList(genres));

        Set<Director> directors = fetchDirectors(resultSet.getLong("film_id"));
        filmDto.setDirectors(toSortedDirectorDtoList(directors));

        return filmDto;
    }

    /**
     * Converts a set of {@link Genre} entities to a sorted list of {@link GenreDto}.
     * The list is sorted by the genre IDs in ascending order.
     *
     * @param genres the set of {@link Genre} entities to convert.
     * @return a sorted list of {@link GenreDto}, or an empty list if the input is {@code null} or empty.
     */
    private List<GenreDto> toSortedGenreDtoList(Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return List.of();
        }

        return genres.stream()
                .map(genreMapper::toDto)
                .sorted(Comparator.comparingInt(GenreDto::getId))
                .collect(Collectors.toList());
    }

    private List<DirectorDto> toSortedDirectorDtoList(Set<Director> directors) {
        if (directors == null || directors.isEmpty()) {
            return List.of();
        }

        return directors.stream()
                .map(directorMapper::toDto)
                .sorted(Comparator.comparingInt(DirectorDto::getId))
                .collect(Collectors.toList());
    }

    private Set<Genre> fetchGenres(long filmId) {
        return Set.of();
    }

    private Set<Director> fetchDirectors(long filmId) {
        return Set.of();
    }
}