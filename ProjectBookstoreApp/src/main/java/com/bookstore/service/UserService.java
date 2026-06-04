// serwis uzytkownika. obsluguje rejestracje nowych kont, walidacje unikalnosci oraz szyfrowanie hasel.
package com.bookstore.service;

import com.bookstore.dto.UserRegisterDto;
import com.bookstore.model.Role;
import com.bookstore.model.User;
import com.bookstore.repository.RoleRepository;
import com.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(UserRegisterDto dto) {
        // sprawdzenie czy login juz istnieje
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is already taken");
        }

        // sprawdzenie czy email juz istnieje
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already taken");
        }

        // pobranie domyslnej roli role_user z bazy
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Default user role not found"));

        // stworzenie obiektu uzytkownika i szyfrowanie hasla
        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .enabled(true)
                .roles(Collections.singleton(userRole))
                .build();

        return userRepository.save(user);
    }

    // zmiana roli uzytkownika o podanym id na role "admin" lub "user"
    public User changeRole(Long id, String roleName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // zdefiniowanie roli do zmiany
        Role targetRole;
        if (roleName.equalsIgnoreCase("admin")) {
            targetRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Admin role not found"));
        } else if (roleName.equalsIgnoreCase("user")) {
            targetRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User role not found"));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role name (use 'admin' or 'user')");
        }

        user.getRoles().clear();
        user.getRoles().add(targetRole);
        return userRepository.save(user);
    }
}
