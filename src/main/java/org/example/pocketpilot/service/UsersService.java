package org.example.pocketpilot.service;

import org.example.pocketpilot.dto.requestDTO.LoginRequestDTO;
import org.example.pocketpilot.dto.requestDTO.SignUpRequestDTO;
import org.springframework.http.ResponseEntity;

public interface UsersService {


    ResponseEntity<Object> authenticate(LoginRequestDTO dto);

    ResponseEntity<Object> signUp(SignUpRequestDTO dto);

    ResponseEntity<Object> getUsers();

    ResponseEntity<Object> getUserPrfile();

//    ResponseEntity<Object> getUserSummaryTransactions();
}
