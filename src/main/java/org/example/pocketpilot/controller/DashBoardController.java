package org.example.pocketpilot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pocketpilot.commonlib.Controller.ResponseController;
import org.example.pocketpilot.dto.RequestDTO.TransactionRequestDTO;
import org.example.pocketpilot.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
public class DashBoardController extends ResponseController {

    private final DashboardService dashboardService;

    @PreAuthorize("hasAuthority(UserRole.ADMIN.getRoleName() or UserRole.PREMIUM_USER.getRoleName() or UserRole.REGULAR_USER.getRoleName())")
    @GetMapping("/get/user-transactions")
    public ResponseEntity<Object> getUserTransactions() {
        log.info("HIT - /get user transactions  GET  ");
        return sendResponse(dashboardService.getUserTransactions());
    }

//    @PreAuthorize("hasAuthority(UserRole.ADMIN.getRoleName())")
//    @GetMapping("/get/user-transactions")
//    public ResponseEntity<Object> getTotalTransactions() {
//        log.info("HIT - /get user transactions  GET  ");
//        return sendResponse(dashboardService.getUserTransactions());
//    }

}
