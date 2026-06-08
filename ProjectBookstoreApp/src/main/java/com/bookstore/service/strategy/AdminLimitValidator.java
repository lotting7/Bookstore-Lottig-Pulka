// walidator limitow dla administratora
package com.bookstore.service.strategy;

import com.bookstore.model.User;
import com.bookstore.repository.BorrowingRepository;
import org.springframework.stereotype.Component;

@Component
public class AdminLimitValidator implements BorrowingValidator {

    @Override
    public void checkLimits(User user, BorrowingRepository borrowingRepository) {
      // brak limitu
    }
}
