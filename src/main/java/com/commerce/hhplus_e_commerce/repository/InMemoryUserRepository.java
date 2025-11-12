package com.commerce.hhplus_e_commerce.repository;

import com.commerce.hhplus_e_commerce.domain.User;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryUserRepository implements UserRepository{

    private final Map<Long, User> userMap = new HashMap<>();

    @Override
    public User save(User user) {

        if(user.getUserId() == null){
            throw new IllegalArgumentException("ID가 넘어오지 않음");
        }
        userMap.put(user.getUserId(), user);

        return user;
    }

    @Override
    public Optional<User> findByUserId(Long userId) {
        if(userId == null){
            throw new IllegalArgumentException("ID가 넘어오지 않음");
        }
        return Optional.ofNullable(userMap.get(userId));
    }
}
