// kontroler ksiazek. obsluguje udostepnianie katalogu oraz dodawanie nowych ksiazek.
package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    // pobieranie listy ksiazek
    @GetMapping
    public ResponseEntity<List<Book>> getBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String status) {
        
        // wywolanie serwisu z parametrami wyszukiwania
        List<Book> books = bookService.getBooks(title, author, status);
        return ResponseEntity.ok(books); // zwrocenie listy z kodem 200 ok
    }

    // dodawanie nowej ksiazki (admin)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        

        Book savedBook = bookService.addBook(book);
        return new ResponseEntity<>(savedBook, HttpStatus.CREATED); // zwrocenie ksiazki z kodem 201 created
    }

    // usuwanie ksiazki (admin)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        

        bookService.deleteBook(id);
        return ResponseEntity.noContent().build(); // zwraca kod 204
    }
}
