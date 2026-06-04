// kontroler uzytkownikow. obsluguje operacje na kontach, dostepne tylko dla administratora.
package com.bookstore.controller;

import com.bookstore.model.User;
import com.bookstore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // zmiana roli uzytkownika o podanym id
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> changeRole(@PathVariable Long id, @RequestParam String roleName) {
        User updatedUser = userService.changeRole(id, roleName);
        return ResponseEntity.ok(updatedUser);
    }
}
