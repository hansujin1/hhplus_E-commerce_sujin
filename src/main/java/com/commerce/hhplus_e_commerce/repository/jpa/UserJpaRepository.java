package com.commerce.hhplus_e_commerce.repository.jpa;

import com.commerce.hhplus_e_commerce.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserJpaRepository extends JpaRepository<User,Long> {


}
