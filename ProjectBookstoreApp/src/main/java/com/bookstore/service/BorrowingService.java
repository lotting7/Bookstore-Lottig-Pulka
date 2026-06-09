// serwis wypozyczen. obsluguje rezerwowanie, wypozyczanie i zwracanie ksiazek
package com.bookstore.service;

import com.bookstore.model.Book;
import com.bookstore.model.Borrowing;
import com.bookstore.model.User;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.BorrowingRepository;
import com.bookstore.service.strategy.AdminLimitValidator;
import com.bookstore.service.strategy.BorrowingValidator;
import com.bookstore.service.strategy.UserLimitValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowingService {

    private final BorrowingRepository borrowingRepository;
    private final BookRepository bookRepository;
    private final UserLimitValidator userLimitValidator;
    private final AdminLimitValidator adminLimitValidator;

    // REZERWOWANIE KSIAZKI - ID
    public Borrowing reserveBook(Long bookId, User user) {
        // pobranie ksiazki z bazy
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        // rezerwowac mozna tylko ksiazke ze statusem AVAILABLE (dostepna)
        if (!book.getStatus().equals("AVAILABLE")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book is not available for reservation");
        }

        // dynamiczny wybor walidatora i sprawdzenie limitow
        BorrowingValidator validator = selectValidator(user);
        validator.checkLimits(user, borrowingRepository);

        // RESERVED i zapis w bazie
        book.setStatus("RESERVED");
        bookRepository.save(book);

        // nowe rekord w tabeli borrowing
        Borrowing borrowing = Borrowing.builder()
                .user(user)
                .book(book)
                .borrowDate(LocalDateTime.now())
                .status("RESERVATION")
                .build();

        return borrowingRepository.save(borrowing);
    }

    // WYPOZYCZENIE KSIAZKI
    public Borrowing borrowBook(Long bookId, User user) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        // KSIAZKA BYLA ZAREZERWOWANA WCZESNIEJ
        if (book.getStatus().equals("RESERVED")) {
            // szukamy aktywnej rezerwacji tej ksiazki
            Borrowing reservation = borrowingRepository.findByBookIdAndStatusIn(book.getId(), List.of("RESERVATION"))
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Active reservation not found"));

            // sprawdzamy czy to ten sam uzytkownik
            if (!reservation.getUser().getId().equals(user.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This book is reserved by another user");
            }

            // zamieniamy rezerwacje na aktywne wypozyczenie
            reservation.setStatus("ACTIVE");
            reservation.setBorrowDate(LocalDateTime.now());
            book.setStatus("BORROWED");
            bookRepository.save(book);
            return borrowingRepository.save(reservation);
        }

        // PRZYPADEK WYPOZYCZENIA BEZPOSREDNEGO
        if (book.getStatus().equals("AVAILABLE")) {
            // sprawdzamy limity uzytkownika
            BorrowingValidator validator = selectValidator(user);
            validator.checkLimits(user, borrowingRepository);

            book.setStatus("BORROWED");
            bookRepository.save(book);

            // utworzenie nowego rekordu aktywnego wypozyczenia
            Borrowing borrowing = Borrowing.builder()
                    .user(user)
                    .book(book)
                    .borrowDate(LocalDateTime.now())
                    .status("ACTIVE")
                    .build();

            return borrowingRepository.save(borrowing);
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book is already borrowed");
    }

    // ZWROT KSIAZKI
    public Borrowing returnBook(Long borrowingId, User user) {

        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Borrowing record not found"));

        // sprawdzamy czy ksiazka nie jest juz zwrocona
        if (borrowing.getStatus().equals("RETURNED")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book is already returned");
        }

        // uzytkownik moze zwrocic tylko swoje, a admin moze za kazdego
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN"));
        if (!borrowing.getUser().getId().equals(user.getId()) && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only return your own borrowings");
        }

        // oznaczamy jako zwrocona i wpisujemy date zwrotu
        borrowing.setStatus("RETURNED");
        borrowing.setReturnDate(LocalDateTime.now());

        // zmiana statusu ksiazki na dostepna
        Book book = borrowing.getBook();
        book.setStatus("AVAILABLE");
        bookRepository.save(book);

        return borrowingRepository.save(borrowing);
    }

    // HISTORIA WYPOZYCZEN
    public List<Borrowing> getHistory(User user) {
        // sprawdzamy czy uzytkownik to admin
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return borrowingRepository.findAll(); // admin widzi cala historie ksiegarni
        } else {
            return borrowingRepository.findByUserId(user.getId()); // uzytkownik widzi tylko swoje
        }
    }

    // METODA POMOCNICZA DO POLIMORFIZMU
    private BorrowingValidator selectValidator(User user) {
        // sprawdzamy czy uzytkownik to admin
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return adminLimitValidator; // zwraca walidator bez limitu
        } else {
            return userLimitValidator; // zwraca walidator z limitem 5 ksiazek
        }
    }
}
