// serwis ksiazek. obsluguje logike biznesowa katalogu ksiazek (dodawanie i wyszukiwanie).
package com.bookstore.service;

import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository; // wstrzykniecie repozytorium ksiazek

    // pobieranie ksiazek z opcjonalnym filtrowaniem
    public List<Book> getBooks(String title, String author, String status) {
        // tytul
        if (title != null && !title.isEmpty()) {
            return bookRepository.findByTitleContainingIgnoreCase(title);
        }
        // autor
        if (author != null && !author.isEmpty()) {
            return bookRepository.findByAuthorContainingIgnoreCase(author);
        }
        // status
        if (status != null && !status.isEmpty()) {
            return bookRepository.findByStatus(status.toUpperCase());
        }
        // w przeciwnym razie zwroc wszystkie ksiazki z bazy
        return bookRepository.findAll();
    }

    // dodawanie nowej ksiazki do bazy danych
    public Book addBook(Book book) {
        // ustawienie domyslnego statusu nowo dodawanej ksiazki
        book.setStatus("AVAILABLE");
        // zapisanie ksiazki w bazie danych
        return bookRepository.save(book);
    }

    // usuwanie ksiazki o podanym id z bazy danych
    public void deleteBook(Long id) {
        // najpierw sprawdzamy czy ksiazka w ogole istnieje w bazie
        if (!bookRepository.existsById(id)) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND, "Book not found"
            );
        }
        // usuwamy ksiazke
        bookRepository.deleteById(id);
    }
}
