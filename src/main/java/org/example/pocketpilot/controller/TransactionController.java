package org.example.pocketpilot.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.example.pocketpilot.commonlib.Controller.ResponseController;
import org.example.pocketpilot.dto.RequestDTO.TransactionRequestDTO;
import org.example.pocketpilot.dto.TransactionFilterDTO;
import org.example.pocketpilot.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasAuthority(UserRole.ADMIN.getRoleName() or UserRole.PREMIUM_USER.getRoleName() or UserRole.REGULAR_USER.getRoleName())")
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController extends ResponseController {

    private final TransactionService transactionService;

    @PostMapping("/add")
    public ResponseEntity<Object> addTransaction(@RequestBody TransactionRequestDTO dto) {
        log.info("HIT - /add POST | dto : {}", dto);
        return sendResponse(transactionService.addTransaction(dto));
    }

    @GetMapping()
    public ResponseEntity<Object> getFilteredTransaction(@ModelAttribute TransactionFilterDTO dto) {
        log.info("HIT - /get GET | dto : {}", dto);
        return sendResponse(transactionService.getFilteredTransactions (dto));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Object> getTransactionById(@PathVariable("id") ObjectId id) {
        log.info("HIT - /get GET | get Transaction with ID : {}", id);
        return sendResponse(transactionService.getTransactionById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Object>updateTransaction(@RequestParam ObjectId id ,@RequestBody TransactionRequestDTO dto) {
        log.info("HIT - /update PUT | Update Transaction With ID : {} | dto : {}", id,dto);
        return sendResponse(transactionService.updateTransactions(id,dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object>deleteTransaction(@RequestParam ObjectId id ) {
        log.info("HIT - /delete DELETE | Delete Transaction With ID : {} ", id);
        return sendResponse(transactionService.deleteTransactions(id));
    }


}
