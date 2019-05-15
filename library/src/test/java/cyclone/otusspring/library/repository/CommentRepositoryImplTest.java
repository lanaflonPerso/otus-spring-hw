package cyclone.otusspring.library.repository;

import cyclone.otusspring.library.model.Book;
import cyclone.otusspring.library.model.Comment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;

import static cyclone.otusspring.library.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ComponentScan("cyclone.otusspring.library.repository")
class CommentRepositoryImplTest {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    TestEntityManager tem;

    @Test
    void findOne() {
        assertThat(commentRepository.findOne(COMMENT2.getId()))
                .isEqualToIgnoringGivenFields(COMMENT2, "book");
    }

    @Test
    void findByBook() {
        assertThat(commentRepository.findByBook(BOOK1))
                .usingElementComparatorIgnoringFields("book")
                .containsExactly(COMMENT1, COMMENT3);
    }

    @Test
    void findByBookId() {
        assertThat(commentRepository.findByBookId(BOOK2.getId()))
                .usingElementComparatorIgnoringFields("book")
                .containsExactly(COMMENT2, COMMENT4);
    }

    @Test
    void findAllCommentator() {
        assertThat(commentRepository.findByCommentator(COMMENT1.getCommentator()))
                .usingElementComparatorIgnoringFields("book")
                .containsExactly(COMMENT1, COMMENT2);
    }

    @Test
    void testInsert() {
        String savedId = commentRepository.save(NEW_COMMENT).getId();

        Comment actual = commentRepository.findOne(savedId);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual).isEqualToIgnoringGivenFields(NEW_COMMENT, "id");
    }

    @Test
    void testUpdate() {
        Comment updatedComment2 = new Comment(COMMENT2.getId(), "Updated " + COMMENT2.getCommentator()
                , "Updated" + COMMENT2.getText(), COMMENT2.getDate(), COMMENT2.getBook());
        commentRepository.save(updatedComment2);
        tem.flush();

        Comment actual = commentRepository.findOne(updatedComment2.getId());

        assertThat(actual).isEqualToIgnoringGivenFields(updatedComment2, "book");

        Book actualBook = actual.getBook();
        Book updatedComment2Book = updatedComment2.getBook();
        // ignore $$_hibernate_interceptor, which is added to proxy object
        assertThat(actualBook)
                .isEqualToIgnoringGivenFields(updatedComment2Book, "comments", "$$_hibernate_interceptor");
    }

    @Test
    void delete() {
        Comment commentToDelete = tem.find(Comment.class, COMMENT1.getId());
        commentRepository.delete(commentToDelete);

        assertThat(commentRepository.findByBook(BOOK1))
                .usingElementComparatorIgnoringFields("book")
                .hasSize(1)
                .doesNotContain(COMMENT1);
    }

    @Test
    void deleteById() {
        commentRepository.delete(COMMENT3.getId());

        assertThat(commentRepository.findByBook(BOOK1))
                .usingElementComparatorIgnoringFields("book")
                .hasSize(1)
                .doesNotContain(COMMENT3);
    }
}