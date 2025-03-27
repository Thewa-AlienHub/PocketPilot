package org.example.pocketpilot.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.example.pocketpilot.commonlib.Controller.ResponseController;
import org.example.pocketpilot.dto.requestDTO.FinancialGoalsRequestDTO;
import org.example.pocketpilot.service.FinancialGoalService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/financial-goals")
@RequiredArgsConstructor
@Slf4j
public class FinancialGoalController extends ResponseController {

    private final FinancialGoalService financialGoalService;

    @PostMapping("/add")
    public ResponseEntity<Object> addFinancialGoal(@Valid @RequestBody FinancialGoalsRequestDTO dto) {
        log.info("HIT - /add financial goal POST | dto : {}", dto);
        return sendResponse(financialGoalService.addFinancialGoal(dto));
    }

    @GetMapping("/progress/{goalId}")
    public ResponseEntity<Object> getFinancialGoalProgress(@PathVariable ObjectId goalId) {
        log.info("HIT - /progress financial goal GET | gaol Id  : {}", goalId);
        return sendResponse(financialGoalService.getGoalProgress(goalId));
    }



}
