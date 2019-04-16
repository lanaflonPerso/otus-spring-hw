package cyclone.otusspring.library.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "book")
@Data
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long bookId;

    private String title;
    private Integer year;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;
    @ManyToOne
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;
    @OneToMany(
            mappedBy = "book",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Comment> comments = new ArrayList<>();

    public Book(String title, Integer year) {
        this.title = title;
        this.year = year;
    }

    public Book(String title, Integer year, Author author, Genre genre) {
        this(null, title, year, author, genre);
    }

    public Book(Long bookId, String title, Integer year, Author author, Genre genre) {
        if (StringUtils.isEmpty(title)) {
            throw new IllegalArgumentException("book title must not be empty");
        }

        this.bookId = bookId;
        this.title = title;
        this.year = year;
        this.author = author;
        this.genre = genre;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setBook(this);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setBook(null);
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", title='" + title + '\'' +
                ", year=" + year +
                ", author=" + author +
                ", genre=" + genre +
                '}';
    }
}
