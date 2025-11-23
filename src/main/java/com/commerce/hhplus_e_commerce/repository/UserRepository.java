package com.commerce.hhplus_e_commerce.repository;

import com.commerce.hhplus_e_commerce.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findByUserId(Long userId);

    List<User> findAll();

}
