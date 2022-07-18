package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MPA {
    private String name;
    private int id;

    public MPA(int id) {
        this.id = id;
    }
}
