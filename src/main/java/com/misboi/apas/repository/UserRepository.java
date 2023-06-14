package com.misboi.apas.repository;

import com.misboi.apas.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {


    public User findByUsername(String username);
}
