package com.userfront;

import com.userfront.dao.RoleDao;
import com.userfront.domain.security.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {

    private RoleDao roleDao;

    @Autowired
    public DataLoader(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    // insert role if not inserted
    public void run(ApplicationArguments args) {
        if(roleDao.findByName("ROLE_USER") == null && roleDao.findByName("ROLE_ADMIN") == null) {
        roleDao.save(new Role(1, "ROLE_USER"));
        roleDao.save(new Role(2, "ROLE_ADMIN"));
        }
    }
}