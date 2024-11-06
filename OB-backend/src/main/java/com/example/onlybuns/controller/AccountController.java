package com.example.onlybuns.controller;

import com.example.onlybuns.domain.Account;
import com.example.onlybuns.service.AccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
  @Autowired
  private AccountService accountService;

  @GetMapping("/getAll")
  public Page<Account> getAccounts(
          @RequestParam(required = false) String firstName,
          @RequestParam(required = false) String lastName,
          @RequestParam(required = false) String email,
          @RequestParam(required = false) String address,
          @RequestParam(required = false) Integer minPosts,
          @RequestParam(required = false) Integer maxPosts,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "999") int size,
          @RequestParam(defaultValue = "email") String sortField,
          @RequestParam(defaultValue = "asc") String sortDir) {
          System.out.println("USLOOO");
    return accountService.getAccounts(firstName, lastName, email, address, minPosts, maxPosts, page, size, sortField, sortDir);
  }
}
