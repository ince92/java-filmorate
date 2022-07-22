package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    private long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NonNull
    private LocalDate releaseDate;
    @Positive
    private long duration;
    @NonNull
    private MPA mpa;

    private Set<Genre> genres;
    private Set<Director> directors;
}
