// kontroler autoryzacji. obsluguje rejestracje nowych uzytkownikow.
package com.bookstore.controller;

import com.bookstore.dto.UserRegisterDto;
import com.bookstore.model.User;
import com.bookstore.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // endpoint rejestracji nowego uzytkownika
    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody UserRegisterDto dto) {
        // wywolanie serwisu do zapisania uzytkownika w bazie
        User registeredUser = userService.registerUser(dto);
        
        // zwrocenie zapisanego uzytkownika i statusu 201 created
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }
}
