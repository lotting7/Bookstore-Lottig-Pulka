// walidator limitow dla zwyklego uzytkownika. ogranicza liczbe aktywnych ksiazek do 5.
package com.bookstore.service.strategy;

import com.bookstore.model.User;
import com.bookstore.repository.BorrowingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component // sprawia, ze spring zarzadza ta klasa jako komponentem
public class UserLimitValidator implements BorrowingValidator {

    @Override
    public void checkLimits(User user, BorrowingRepository borrowingRepository) {
        // pobieramy wszystkie wypozyczenia uzytkownika i zliczamy te, ktore sa aktywne lub zarezerwowane
        long activeCount = borrowingRepository.findByUserId(user.getId())
                .stream()
                .filter(b -> b.getStatus().equals("ACTIVE") || b.getStatus().equals("RESERVATION"))
                .count();

        // zwykly uzytkownik moze miec maksymalnie 5 aktywnych wypozyczen/rezerwacji
        if (activeCount >= 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reached limit of 5 active books");
        }
    }
}
