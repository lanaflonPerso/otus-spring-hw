package cyclone.otusspring.library.repository.jpa;

import cyclone.otusspring.library.model.Author;
import cyclone.otusspring.library.repository.AuthorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;

import javax.persistence.EntityNotFoundException;
import java.util.stream.Stream;

import static cyclone.otusspring.library.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ComponentScan("cyclone.otusspring.library.repository.jpa")
class AuthorRepositoryJpaTest {

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    TestEntityManager tem;


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
                Arguments.of("gabri", new Author[]{AUTHOR3}),
                Arguments.of("ar", new Author[]{AUTHOR1, AUTHOR3})
        );
    }

    @Test
    void findOne() {
        assertThat(authorRepository.findOne(2)).isEqualTo(AUTHOR2);
    }

    @Test
    @DisplayName("finding non existent ID throws exception")
    void findOne_nonExistent() {
        assertThatThrownBy(() -> authorRepository.findOne(NO_SUCH_ID)).isInstanceOf(EntityNotFoundException.class);
    }



    @Test
    void testInsert() {
        long savedId = authorRepository.save(NEW_AUTHOR).getAuthorId();

        Author actual = authorRepository.findOne(savedId);

        assertThat(actual.getAuthorId()).isNotNull();
        assertThat(actual).isEqualToIgnoringGivenFields(NEW_AUTHOR, "authorId");
    }

    @Test
    void testUpdate() {
        Author updatedAuthor2 = new Author(AUTHOR2.getAuthorId(), "Updated " + AUTHOR2.getFirstname(), "Updated " + AUTHOR2.getLastname(), "Updated " + AUTHOR2.getHomeland());
        authorRepository.save(updatedAuthor2);

        Author actual = authorRepository.findOne(updatedAuthor2.getAuthorId());

        assertThat(actual).isEqualToComparingFieldByField(updatedAuthor2);
    }

    @Test
    void testDelete() {
        Author bookToDelete = tem.find(Author.class, AUTHOR2.getAuthorId());

        authorRepository.delete(bookToDelete);
        assertThat(authorRepository.findAll()).doesNotContain(AUTHOR2);
    }

    @Test
    void testDeleteById() {
        authorRepository.delete(AUTHOR1.getAuthorId());
        assertThat(authorRepository.findAll()).doesNotContain(AUTHOR1);
    }

    @Test
    @DisplayName("deleting non existent ID throws exception")
    void testDeleteNonExistent() {
        assertThatThrownBy(() -> authorRepository.delete(NO_SUCH_ID)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void testExistTrue() {
        assertThat(authorRepository.exists(AUTHOR2.getAuthorId())).isTrue();
    }

    @Test
    void testExistFalse() {
        assertThat(authorRepository.exists(NO_SUCH_ID)).isFalse();
    }
}