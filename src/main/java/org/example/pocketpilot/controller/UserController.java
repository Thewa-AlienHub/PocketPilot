package org.example.pocketpilot.controller;

import lombok.RequiredArgsConstructor;
import org.example.pocketpilot.commonlib.Controller.ResponseController;
import org.example.pocketpilot.service.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor

public class UserController extends ResponseController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UsersService usersService;


    @GetMapping("/profile")
    public ResponseEntity<Object> getUserPrfile() {
        log.info("HIT - /users GET | Fetching  User Profile ");
        return sendResponse(usersService.getUserPrfile());
    }
}
