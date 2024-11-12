package com.example.onlybuns.repository;

import com.example.onlybuns.domain.EmailVerificationToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailVerificationTokenRepository extends CrudRepository<EmailVerificationToken,String> {
    EmailVerificationToken findByConfirmationToken (String confirmationToken);
}
