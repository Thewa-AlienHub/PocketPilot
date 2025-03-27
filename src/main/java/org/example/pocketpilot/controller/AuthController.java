package org.example.pocketpilot.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pocketpilot.commonlib.Controller.ResponseController;
import org.example.pocketpilot.dto.requestDTO.LoginRequestDTO;
import org.example.pocketpilot.dto.requestDTO.SignUpRequestDTO;
import org.example.pocketpilot.model.NotificationModel;
import org.example.pocketpilot.service.Impl.NotificationServiceImpl;
import org.example.pocketpilot.service.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Thewan Damnidu on 20/02/2025
 */

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j

public class AuthController extends ResponseController {


    private final UsersService usersService;
    private final NotificationServiceImpl notificationServiceImpl;

    @PostMapping("/sign-in")
    public ResponseEntity<Object> SignIn(@RequestBody LoginRequestDTO dto) {
        log.info("HIT - /auth POST | User : {}", dto.getUsername().toString());
        return sendResponse(usersService.authenticate(dto));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Object> SignUp(@Valid @RequestBody SignUpRequestDTO dto) {
        log.info("HIT - /auth POST | User SignUp ");
        return sendResponse(usersService.signUp(dto));
    }

//    @PostMapping("/notify")
//    public ResponseEntity<String> notify (@RequestBody NotificationModel notificationModel){
//        log.info("HIT - /auth POST | User Notify ");
//        notificationServiceImpl.sendNotification(notificationModel);
//        return ResponseEntity.ok("Notification sent successfully");
//    }


}
