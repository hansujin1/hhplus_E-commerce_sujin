package com.commerce.hhplus_e_commerce.repository.impl;

import com.commerce.hhplus_e_commerce.domain.User;
import com.commerce.hhplus_e_commerce.repository.UserRepository;
import com.commerce.hhplus_e_commerce.repository.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;


    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public Optional<User> findByUserId(Long userId) {
        return userJpaRepository.findById(userId);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll();
    }

    @Override
    public long count() {
        return userJpaRepository.count();
    }
}
