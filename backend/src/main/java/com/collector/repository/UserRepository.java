package com.collector.repository;

import com.collector.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    // String car la clé primaire est keycloakId (String)
}