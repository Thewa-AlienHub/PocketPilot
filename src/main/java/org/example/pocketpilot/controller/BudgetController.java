package org.example.pocketpilot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pocketpilot.commonlib.Controller.ResponseController;
import org.example.pocketpilot.dto.BudgetFilterDTO;
import org.example.pocketpilot.dto.requestDTO.BudgetRequestDTO;
import org.example.pocketpilot.service.BudgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@Slf4j
public class BudgetController extends ResponseController {

    private final BudgetService budgetService;

    @PostMapping("/set")
    public ResponseEntity<Object> setBudgetPlan(@RequestBody BudgetRequestDTO dto){
        log.info("HIT - /add POST | setBudgetPlane dto : {}", dto);
        return sendResponse(budgetService.setBudgetPlan(dto));
    }

    @GetMapping()
    public ResponseEntity<Object> getBudgetPlan(@ModelAttribute BudgetFilterDTO dto){
        log.info("HIT - /get GET | budgetFilterDTO : {}", dto);
        return sendResponse(budgetService.getBudgetPlanFilter(dto));
    }

}
