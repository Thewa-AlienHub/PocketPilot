package org.example.pocketpilot.service;

import org.springframework.http.ResponseEntity;

public interface DashboardService {
    ResponseEntity<Object> getUserTransactions();
}
