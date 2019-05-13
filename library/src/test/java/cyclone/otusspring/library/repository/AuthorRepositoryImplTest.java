package cyclone.otusspring.library.repository;

import cyclone.otusspring.library.dbmigrationtest.DBMigrationTestConfig;
import cyclone.otusspring.library.model.Author;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.persistence.EntityNotFoundException;
import java.util.stream.Stream;

import static cyclone.otusspring.library.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataMongoTest
@ComponentScan("cyclone.otusspring.library.repository")
@Import(DBMigrationTestConfig.class)
//@ContextConfiguration(classes = {DBMigrationTestConfig.class})
class AuthorRepositoryImplTest {

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
//    private MongodExecutable mongodExecutable;
    private MongoTemplate mongoTemplate;


    @Test
    void findAll() {
        assertThat(authorRepository.findAll()).containsExactly(AUTHOR1, AUTHOR3, AUTHOR2); // 1, 3, 2 because of ordering
    }

    @ParameterizedTest
    @MethodSource("findByNameParameters")
    void findByName(String nameQuery, Author[] expected) {
        assertThat(authorRepository.findByName(nameQuery)).containsExactly(expected);
    }

    private static Stream<Arguments> findByNameParameters() {
        return Stream.of(
                Arguments.of("GabrI", new Author[]{AUTHOR3}),
                Arguments.of("aR", new Author[]{AUTHOR1, AUTHOR3})
        );
    }

    @Test
    void findOne() {
        assertThat(authorRepository.findOne("2")).isEqualTo(AUTHOR2);
    }

    @Test
    @DisplayName("finding non existent ID throws exception")
    void findOne_nonExistent() {
        assertThatThrownBy(() -> authorRepository.findOne(NO_SUCH_ID)).isInstanceOf(EntityNotFoundException.class);
    }



    @Test
    void testInsert() {
        String savedId = authorRepository.save(NEW_AUTHOR).getId();

        Author actual = authorRepository.findOne(savedId);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual).isEqualToIgnoringGivenFields(NEW_AUTHOR, "authorId");
    }

    @Test
    void testUpdate() {
        Author updatedAuthor2 = new Author(AUTHOR2.getId(), "Updated " + AUTHOR2.getFirstname(), "Updated " + AUTHOR2.getLastname(), "Updated " + AUTHOR2.getHomeland());
        authorRepository.save(updatedAuthor2);
//        tem.flush(); // send update to database

        Author actual = authorRepository.findOne(updatedAuthor2.getId());

        assertThat(actual).isEqualToComparingFieldByField(updatedAuthor2);
    }

//    @Test
//    void testDelete() {
//        Author bookToDelete = mongoTemplate. tem.find(Author.class, AUTHOR2.getId());
//
//        authorRepository.delete(bookToDelete);
//        assertThat(authorRepository.findAll()).doesNotContain(AUTHOR2);
//    }

    @Test
    void testDeleteById() {
        authorRepository.delete(AUTHOR1.getId());
        assertThat(authorRepository.findAll()).doesNotContain(AUTHOR1);
    }

    @Test
    @DisplayName("deleting non existent ID throws exception")
    void testDeleteNonExistent() {
        assertThatThrownBy(() -> authorRepository.delete(NO_SUCH_ID)).isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    void testExistsTrue() {
        assertThat(authorRepository.exists(AUTHOR2.getId())).isTrue();
    }

    @Test
    void testExistsFalse() {
        assertThat(authorRepository.exists(NO_SUCH_ID)).isFalse();
    }

    @Test
    @DisplayName("adding non unique records throws exception")
    void uniqueViolationThrowsException() {
        assertThatThrownBy(() -> {
            authorRepository.save(new Author(NEW_AUTHOR.getFirstname(), NEW_AUTHOR.getLastname(), NEW_AUTHOR.getHomeland()));
            authorRepository.save(new Author(NEW_AUTHOR.getFirstname(), NEW_AUTHOR.getLastname(), NEW_AUTHOR.getHomeland()));
        }).isInstanceOf(DataIntegrityViolationException.class);
    }
}