package cyclone.otusspring.library.repository;

import cyclone.otusspring.library.model.Genre;

import java.util.List;

public interface GenreRepository {
    List<Genre> findAll();

    List<Genre> findByName(String name);

    Genre findOne(long id);

    Genre save(Genre genre);

    void delete(long id);

    void delete(Genre genre);

    boolean exists(long id);
}