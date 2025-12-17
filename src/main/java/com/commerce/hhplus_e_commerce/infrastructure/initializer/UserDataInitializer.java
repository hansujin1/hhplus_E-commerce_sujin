package com.commerce.hhplus_e_commerce.infrastructure.initializer;

import com.commerce.hhplus_e_commerce.domain.User;
import com.commerce.hhplus_e_commerce.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Getter
public class UserDataInitializer {

    private final UserRepository userRepository;
    
    // 생성된 사용자 ID를 다른 Initializer에서 참조할 수 있도록 저장
    private Long user1Id;
    private Long user2Id;

    public UserDataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init(){
        if (userRepository.count() > 0) {
            log.info("User 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            // 기존 사용자 ID 로드
            User user1 = userRepository.findAll().stream().findFirst().orElse(null);
            User user2 = userRepository.findAll().stream().skip(1).findFirst().orElse(null);
            if (user1 != null) this.user1Id = user1.getUserId();
            if (user2 != null) this.user2Id = user2.getUserId();
            return;
        }

        log.info("User 정보 초기 셋팅하기");
        User user1 = userRepository.save(new User("김남준", 100_000));
        User user2 = userRepository.save(new User("정호석", 105_000));
        
        // 생성된 ID 저장
        this.user1Id = user1.getUserId();
        this.user2Id = user2.getUserId();
        
        log.info("생성된 User ID - user1: {}, user2: {}", user1Id, user2Id);
    }
}
