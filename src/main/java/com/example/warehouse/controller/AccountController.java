package com.example.warehouse.controller;

import com.example.warehouse.dto.AccountProfileRequest;
import com.example.warehouse.model.AccountLog;
import com.example.warehouse.model.AccountProfile;
import com.example.warehouse.service.AccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/profile")
    public AccountProfile profile(@RequestParam String username) {
        return accountService.getProfile(username);
    }

    @PostMapping("/profile")
    public AccountProfile updateProfile(@RequestBody AccountProfileRequest request) {
        return accountService.updateProfile(request);
    }

    @GetMapping("/logs")
    public List<AccountLog> logs(@RequestParam String username, @RequestParam(required = false) Integer limit) {
        return accountService.getLogs(username, limit == null ? 50 : limit);
    }
}
