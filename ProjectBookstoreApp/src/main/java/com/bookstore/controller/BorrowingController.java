// kontroler wypozyczen. obsluguje endpointy rezerwacji, wypozyczeń, zwrotów i historii.
package com.bookstore.controller;

import com.bookstore.model.Borrowing;
import com.bookstore.model.User;
import com.bookstore.repository.UserRepository;
import com.bookstore.service.BorrowingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/borrowings")
@RequiredArgsConstructor
public class BorrowingController {

    private final BorrowingService borrowingService;
    private final UserRepository userRepository;

    // rezerwowanie ksiazki o podanym id
    @PostMapping("/reserve/{bookId}")
    public ResponseEntity<Borrowing> reserveBook(@PathVariable Long bookId, Principal principal) {
        // pobranie zalogowanego uzytkownika z bazy
        User user = getUserByPrincipal(principal);
        // wywolanie serwisu
        Borrowing borrowing = borrowingService.reserveBook(bookId, user);
        return new ResponseEntity<>(borrowing, HttpStatus.CREATED);
    }

    // wypozyczanie ksiazki o podanym id
    @PostMapping("/borrow/{bookId}")
    public ResponseEntity<Borrowing> borrowBook(@PathVariable Long bookId, Principal principal) {

        User user = getUserByPrincipal(principal);

        Borrowing borrowing = borrowingService.borrowBook(bookId, user);
        return new ResponseEntity<>(borrowing, HttpStatus.CREATED);
    }

    // zwracanie ksiazki o podanym id wypozyczenia
    @PostMapping("/return/{borrowingId}")
    public ResponseEntity<Borrowing> returnBook(@PathVariable Long borrowingId, Principal principal) {

        User user = getUserByPrincipal(principal);

        Borrowing borrowing = borrowingService.returnBook(borrowingId, user);
        return ResponseEntity.ok(borrowing);
    }

    // pobieranie historii wypozyczen zalogowanego uzytkownika
    @GetMapping("/history")
    public ResponseEntity<List<Borrowing>> getHistory(Principal principal) {

        User user = getUserByPrincipal(principal);

        List<Borrowing> history = borrowingService.getHistory(user);
        return ResponseEntity.ok(history);
    }

    // pomocnicza metoda do szybkiego pobierania zalogowanego uzytkownika po loginie
    private User getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));
    }
}
