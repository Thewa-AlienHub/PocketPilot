package org.example.pocketpilot.controller;

import lombok.RequiredArgsConstructor;
import org.example.pocketpilot.commonlib.Controller.ResponseController;
import org.example.pocketpilot.dto.RequestDTO.LoginRequestDTO;
import org.example.pocketpilot.dto.RequestDTO.SignUpRequestDTO;
import org.example.pocketpilot.service.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Thewan Damnidu on 20/02/2025
 */

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor

public class AuthController extends ResponseController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UsersService usersService;

    @PostMapping("/sign-in")
    public ResponseEntity<Object> SignIn(@RequestBody LoginRequestDTO dto) {
        log.info("HIT - /auth POST | User : {}", dto.getUsername().toString());
        return sendResponse(usersService.authenticate(dto));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Object> SignUp(@RequestBody SignUpRequestDTO dto) {
        log.info("HIT - /auth POST | User SignUp ");
        return sendResponse(usersService.signUp(dto));
    }

}
