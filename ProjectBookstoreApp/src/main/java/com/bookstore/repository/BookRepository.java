// repozytorium ksiazek. odpowiada za komunikacje z tabela books w bazie danych.
package com.bookstore.repository;

import com.bookstore.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // wyszukiwanie ksiazek po tytule
    List<Book> findByTitleContainingIgnoreCase(String title);

    // wyszukiwanie ksiazek po autorze
    List<Book> findByAuthorContainingIgnoreCase(String author);

    // wyszukiwanie ksiazek po statusie (np. AVAILABLE)
    List<Book> findByStatus(String status);
}
