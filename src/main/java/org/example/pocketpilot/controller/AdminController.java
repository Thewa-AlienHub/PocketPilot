package org.example.pocketpilot.controller;

import lombok.RequiredArgsConstructor;
import org.example.pocketpilot.commonlib.Controller.ResponseController;
import org.example.pocketpilot.service.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController extends ResponseController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final UsersService usersService;


    @GetMapping("/get-users")
    public ResponseEntity<Object> getUsers() {
        log.info("HIT - /users GET | Fetching  Users ");
        return sendResponse(usersService.getUsers());
    }
}
