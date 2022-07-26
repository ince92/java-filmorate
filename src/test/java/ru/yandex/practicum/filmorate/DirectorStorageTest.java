package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.storageInterface.DirectorStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DirectorStorageTest {

    private final DirectorStorage directorStorage;
    private Director director;

    @BeforeEach
    public void createDirector() {
        director = new Director(1, "Джеймс Кэмерон");
    }

    @Test
    public void testFindDirectorById() {
        directorStorage.create(director);
        Optional<Director> directorOptional = directorStorage.findDirectorById(1);
        assertThat(directorOptional)
                .isPresent()
                .hasValueSatisfying(directorTest ->
                        assertThat(directorTest).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void testFindDirectorByIncorrectId() {
        Optional<Director> directorOptional = directorStorage.findDirectorById(-1);
        assertTrue(directorOptional.isEmpty(),"Найден режиссёр по некорректному id");
    }

    @Test
    public void testCreateDirector() {
        Optional<Director> directorOptional = directorStorage.create(director);
        assertThat(directorOptional)
                .isPresent()
                .hasValueSatisfying(director ->
                        assertThat(director).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void testUpdateDirector() {
        directorStorage.create(director);
        director.setName("Джеймс Фрэнсис Кэмерон");
        directorStorage.update(director);
        Optional<Director> directorOptional = directorStorage.findDirectorById(1);
        assertThat(directorOptional)
                .isPresent()
                .hasValueSatisfying(director ->
                        assertThat(director).hasFieldOrPropertyWithValue("name", "Джеймс Фрэнсис Кэмерон"));
    }

    @Test
    public void testDeleteDirector() {
        directorStorage.create(director);
        directorStorage.delete(director.getId());
        Optional<Director> directorOptional = directorStorage.findDirectorById(1);
        assertThat(directorOptional).isEmpty();
    }

    @Test
    public void testFindAllDirectors() {
        directorStorage.create(director);
        List<Director> directors = directorStorage.findAll();
        List<Director> directors2 = new ArrayList<>();
        directors2.add(director);
        assertTrue(directors.equals(directors2), "Список со всеми режиссёрами не равен ожидаемому");
    }
}
