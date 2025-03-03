package org.example.pocketpilot.service;

import org.example.pocketpilot.dto.RequestDTO.LoginRequestDTO;
import org.example.pocketpilot.dto.RequestDTO.SignUpRequestDTO;
import org.springframework.http.ResponseEntity;

public interface UsersService {


    ResponseEntity<Object> authenticate(LoginRequestDTO dto);

    ResponseEntity<Object> signUp(SignUpRequestDTO dto);

    ResponseEntity<Object> getUsers();

    ResponseEntity<Object> getUserPrfile();
}
