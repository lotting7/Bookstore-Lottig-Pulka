// walidator wypozyczen.
package com.bookstore.service.strategy;

import com.bookstore.model.User;
import com.bookstore.repository.BorrowingRepository;

public interface BorrowingValidator {
    
    // metoda do sprawdzania czy uzytkownik moze wypozyczyc/zarezerwowac nowa ksiazke
    void checkLimits(User user, BorrowingRepository borrowingRepository);
}
