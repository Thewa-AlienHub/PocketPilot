package org.example.pocketpilot.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.example.pocketpilot.commonlib.Controller.ResponseController;
import org.example.pocketpilot.dto.RequestDTO.FinancialGoalsRequestDTO;
import org.example.pocketpilot.dto.RequestDTO.TransactionRequestDTO;
import org.example.pocketpilot.dto.TransactionFilterDTO;
import org.example.pocketpilot.service.FinancialGoalService;
import org.example.pocketpilot.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasAuthority(UserRole.ADMIN.getRoleName() or UserRole.PREMIUM_USER.getRoleName() or UserRole.REGULAR_USER.getRoleName())")
@RequestMapping("/api/financial-goals")
@RequiredArgsConstructor
@Slf4j
public class FinancialGoalController extends ResponseController {

    private final FinancialGoalService financialGoalService;

    @PostMapping("/add")
    public ResponseEntity<Object> addFinancialGoal(@RequestBody FinancialGoalsRequestDTO dto) {
        log.info("HIT - /add financial goal POST | dto : {}", dto);
        return sendResponse(financialGoalService.addFinancialGoal(dto));
    }

    @GetMapping("/progress/{goalId}")
    public ResponseEntity<Object> getFinancialGoalProgress(@PathVariable ObjectId goalId) {
        log.info("HIT - /progress financial goal GET | gaol Id  : {}", goalId);
        return sendResponse(financialGoalService.getGoalProgress(goalId));
    }



}
