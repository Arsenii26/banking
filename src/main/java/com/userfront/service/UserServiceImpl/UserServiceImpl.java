package com.userfront.service.UserServiceImpl;

import com.userfront.dao.RoleDao;
import com.userfront.dao.UserDao;
import com.userfront.dao.UserLimitDao;
import com.userfront.domain.User;
import com.userfront.domain.security.UserRole;

import com.userfront.domain.to.UserLimit;
import com.userfront.service.AccountService;
import com.userfront.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {


    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserLimitDao userLimitDao;

    @Autowired
    private AccountService accountService;


    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public UserLimit findUserLimit(Long id) {
        return userLimitDao.findByUserLimitId(id);
    }

    @Override
    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public boolean checkUserExists(String username, String email) {
        if(checkUsernameExists(username) || checkEmailExists(email)){
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public boolean checkUsernameExists(String username) {
        if(null != findByUsername(username)){
            return true;
        }
        return false;
    }

    @Override
    public boolean checkEmailExists(String email) {

        if(null != findByEmail(email)){
            return true;
        }

        return false;
    }

    @Override
    public void save(User user) {
        userDao.save(user);
    }


    public User createUser(User user, Set<UserRole> userRoles) {
        //check again if user exists
        User localUser = userDao.findByUsername(user.getUsername());

        if (localUser != null) {
            // if exists
            LOG.info("User with username {} already exist. Nothing will be done. ", user.getUsername());
        } else {
            //if not
            //encrypted password
            String encryptedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encryptedPassword);

            for (UserRole ur : userRoles) {
                roleDao.save(ur.getRole());
            }

            // assign rolls and accounts
            user.getUserRoles().addAll(userRoles);

            user.setPrimaryAccount(accountService.createPrimaryAccount());
            user.setSavingsAccount(accountService.createSavingsAccount());
            user.setUserLimit(createUserLimit());

            localUser = userDao.save(user);
        }

        return localUser;
    }

    public User saveUser (User user) {
        return userDao.save(user);
    }


    public UserLimit createUserLimit(){
        UserLimit userLimit = new UserLimit();
        userLimit.setDailyWithdrawMade(0.0);
        userLimit.setDailyLimit(1000.0);

        return userLimitDao.save(userLimit);
    }

    public List<User> findUserList() {
        return userDao.findAll();
    }

    public void enableUser (String username) {
        User user = findByUsername(username);
        user.setEnabled(true);
        userDao.save(user);
    }

    public void disableUser (String username) {
        User user = findByUsername(username);
        user.setEnabled(false);
        System.out.println(user.isEnabled());
        userDao.save(user);
        System.out.println(username + " is disabled.");
    }



//    public List<UserLimit> findAllUserLimits(){
//        return userLimitDao.findAll();
//    }

}
