package com.example.onlybuns.service;

import com.example.onlybuns.repository.AccountRepository;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.List;

@Service
public class BloomFilterService {

    private BloomFilter<String> userNameBloomFilter;

    private final AccountRepository accountRepository;

    public BloomFilterService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @PostConstruct
    public void init() {
        // Učitavamo sva postojeća korisnička imena iz baze
        List<String> allUserNames = accountRepository.findAll()
                                                     .stream()
                                                     .map(acc -> acc.getUserName())
                                                     .toList();

        // Kreiramo Bloom filter za očekivano 1k korisnika i false positive rate 1%
        userNameBloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()),
                                                 Math.max(1000, allUserNames.size()),
                                                 0.01);

        // Dodajemo sva imena u filter
        allUserNames.forEach(userNameBloomFilter::put);
    }

    public boolean mightExist(String userName) {
        return userNameBloomFilter.mightContain(userName);
    }

    public void addUserName(String userName) {
        userNameBloomFilter.put(userName);
    }
}
