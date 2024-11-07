package com.example.onlybuns.controller;

import com.example.onlybuns.config.JWTDecoder;
import com.example.onlybuns.domain.Account;
import com.example.onlybuns.domain.JWTUser;
import com.example.onlybuns.domain.Token;
import com.example.onlybuns.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/authentication")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    /*@PostMapping("/register") //mozda bi trebalo AccountDTO jer je sa fronta,razmislicu jos
    public ResponseEntity<JWTUser> registerAccount(@RequestBody Account acc){
        authenticationService.registerAccount(acc);
        return  ResponseEntity.ok(JWTDecoder.createToken(new JWTUser(acc.id, acc.userName, acc.role.toString()), 604800000L));
    }*/
    @PostMapping("/register")
    public ResponseEntity<String> registerAccount(@RequestBody Account account) {
        try {
            Account acc = authenticationService.registerAccount(account);
            JWTUser jwtUser = new JWTUser(acc.getId(), acc.getUserName(), acc.getRole().toString());
            String token = JWTDecoder.createToken(jwtUser, 604800000L);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            System.out.println("Add user failed - POST ADD USER");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to register user");
        }
    }
    
    @GetMapping("/logIn/{username}/{password}")
    public ResponseEntity<String> logIn(@PathVariable String username, @PathVariable String password) {
        try {
            Account acc = authenticationService.findAccountByUserNameAndPassword(username, password);
            if (acc != null) {
                JWTUser jwtUser = new JWTUser(acc.getId(), acc.getUserName(), acc.getRole().toString());
                String token = JWTDecoder.createToken(jwtUser, 604800000L);
                return ResponseEntity.ok(token);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                    .body("Invalid username or password");
            }
        } catch (Exception e) {
            System.out.println("User does not exist: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("An error occurred during login");
        }
    }






    @PostMapping("/jwt/decode")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<JWTUser> getJWTUser(@RequestBody Token token){
        try {
            
            JWTUser user = JWTDecoder.verifyToken(token.token);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

}
