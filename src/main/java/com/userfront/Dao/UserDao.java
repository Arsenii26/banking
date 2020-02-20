package com.userfront.Dao;

import com.userfront.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserDao extends CrudRepository<User, Long> {
    //magic patterns
    User findByUsername(String username);
    User findByEmail(String email);
    List<User> findAll();
}
