package com.example.onlybuns.controller;

import com.example.onlybuns.config.JWTDecoder;
import com.example.onlybuns.domain.Account;
import com.example.onlybuns.domain.EmailVerificationToken;
import com.example.onlybuns.domain.JWTUser;
import com.example.onlybuns.domain.Token;
import com.example.onlybuns.repository.EmailVerificationTokenRepository;
import com.example.onlybuns.service.AccountService;
import com.example.onlybuns.service.AuthenticationService;
import com.example.onlybuns.service.BloomFilterService;
import com.example.onlybuns.service.EmailService;
import com.example.onlybuns.service.LoginRateLimiter;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/authentication")
@CrossOrigin(origins = "http://localhost:5173") // Vaš frontend URL
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Autowired
    private AccountService accountService;
    @Autowired
    private BloomFilterService bloomFilterService;
    @Autowired
    private LoginRateLimiter loginRateLimiter;

    /*@PostMapping("/register") //mozda bi trebalo AccountDTO jer je sa fronta,razmislicu jos
    public ResponseEntity<JWTUser> registerAccount(@RequestBody Account acc){
        authenticationService.registerAccount(acc);
        return  ResponseEntity.ok(JWTDecoder.createToken(new JWTUser(acc.id, acc.userName, acc.role.toString()), 604800000L));
    }*/
    @PostMapping("/register")
    public ResponseEntity<String> registerAccount(@RequestBody Account account) {
        try {
            System.out.println("CAOOO");
            account.setRole(Account.Role.unauthenticated);
            //account.setActive(false);
            Account acc = authenticationService.registerAccount(account);
            JWTUser jwtUser = new JWTUser(acc.getId(), acc.getUserName(), acc.getRole().toString());
            String token = JWTDecoder.createToken(jwtUser, 604800000L);

            EmailVerificationToken emailVerificationToken = new EmailVerificationToken(account);
            emailVerificationTokenRepository.save(emailVerificationToken);

            String message = "To confirm your account, please click here : " +
                    "http://localhost:8081/api/authentication/confirm-account?token=" +
                    emailVerificationToken.getConfirmationToken();


            emailService.sendEmail(account.getEmail(),"Verification Email",message);

            return ResponseEntity.ok(token);
        } catch (Exception e) {
            System.out.println("Add user failed - POST ADD USER" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to register user");
        }
    }

    @GetMapping("/check-username/{username}")
    public ResponseEntity<Boolean> checkUserName(@PathVariable String username) {
        boolean mightExist = bloomFilterService.mightExist(username);
        return ResponseEntity.ok(!mightExist); // true znači da je slobodno
    }

    @RequestMapping(value = "/confirm-account", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> confirmUserAccount(@RequestParam("token") String confirmationToken){
        try {
            EmailVerificationToken token = emailVerificationTokenRepository.findByConfirmationToken( confirmationToken);

            if (token != null){
                Account account = token.getAccount();
                //account.setActive(true);
                account.setRole(Account.Role.registered);

                authenticationService.registerAccount(account);

                //System.out.println("Status acc: " + account.isActive());

                return ResponseEntity.ok("Account successfully verified!");
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token.");
            }
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to confirm account");
        }
    }

    @GetMapping("/logIn/{username}/{password}")
    public ResponseEntity<String> logIn(@PathVariable String username, @PathVariable String password,HttpServletRequest request) {
        
        String ip = request.getRemoteAddr();

        if (!loginRateLimiter.tryConsume(ip)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many login attempts. Try again later.");
        }
        
        try {
            String email = username;
            Account acc = authenticationService.findAccountByEmailAndPassword(email, password);
            if (acc != null && acc.getRole() != Account.Role.unauthenticated) {
                JWTUser jwtUser = new JWTUser(acc.getId(), acc.getUserName(), acc.getRole().toString());
                String token = JWTDecoder.createToken(jwtUser, 604800000L);
                accountService.updateLastLogin(acc);
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
    public ResponseEntity<JWTUser> getJWTUser(@RequestBody Token token){
        try {
            
            JWTUser user = JWTDecoder.verifyToken(token.token);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

}
