package com.commerce.hhplus_e_commerce.infrastructure.initializer;

import com.commerce.hhplus_e_commerce.domain.User;
import com.commerce.hhplus_e_commerce.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;


@Slf4j
@Component
public class UserDataInitializer {

    private final UserRepository userRepository;

    public UserDataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init(){
        log.info("User 정보 초기 셋팅하기");
        userRepository.save(new User(20250202L,"김남준",100_000, new Date()));
        userRepository.save(new User(20250222L,"정호석",105_000, new Date()));
    }



}
