package com.userfront.dao;

import com.userfront.domain.to.UserLimit;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserLimitDao extends CrudRepository<UserLimit, Long> {
    UserLimit findByUserLimitId(Long id);
    List<UserLimit> findAll();
}
