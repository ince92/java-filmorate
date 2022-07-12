package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class MpaDBStorage {

    private final JdbcTemplate jdbcTemplate;


    public MpaDBStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;

    }
    public Optional<MPA> findMpaById(long id)  {
        String sql = "select * from MPA where MPA_ID = ?";

        List<MPA> mpas = jdbcTemplate.query(sql,(rs, rowNum) -> makeMPA(rs),id);

        if (mpas.size()==0){
            return Optional.empty();
        }else{
            return Optional.of(mpas.get(0));
        }
    }
    public MPA makeMPA(ResultSet rs) throws SQLException {

        int id = rs.getInt("MPA_ID");

        String name = rs.getString("MPA_NAME");
        return new MPA(name,id);
    }
    public List<MPA> findAll() {
        String sql = "select * from MPA ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMPA(rs));
    }

}
