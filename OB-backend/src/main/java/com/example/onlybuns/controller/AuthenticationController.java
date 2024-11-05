package com.example.onlybuns.controller;

import com.example.onlybuns.domain.Account;
import com.example.onlybuns.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authentication")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register") //mozda bi trebalo AccountDTO jer je sa fronta,razmislicu jos
    public ResponseEntity<String> registerAccount(@RequestBody Account acc){
        authenticationService.registerAccount(acc);
        return  ResponseEntity.ok("Account registered OK");
    }

}
