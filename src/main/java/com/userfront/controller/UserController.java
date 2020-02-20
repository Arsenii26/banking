package com.userfront.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import com.userfront.Dao.UserDao;
import com.userfront.domain.Appointment;
import com.userfront.domain.to.UserLimit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.userfront.domain.User;
import com.userfront.service.UserService;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // when we come to page it will be get
    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String profile(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());

        model.addAttribute("user", user);

        return "profile";
    }

    // when submit form we made it post (depends on profile() method)
    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public String profilePost(@ModelAttribute("user") User newUser, Model model) {
        User user = userService.findByUsername(newUser.getUsername());
        user.setUsername(newUser.getUsername());
        user.setFirstName(newUser.getFirstName());
        user.setLastName(newUser.getLastName());
        user.setEmail(newUser.getEmail());
        user.setPhone(newUser.getPhone());

        model.addAttribute("user", user);

        userService.saveUser(user);

        return "profile";
    }


    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public String userList(Model model) {
       List users =  userService.findUserList();
        model.addAttribute("usersList", users);
        return "all-users";
    }


    //https://stackoverflow.com/questions/25322658/could-not-commit-jpa-transaction-transaction-marked-as-rollbackonly

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/enable", method = RequestMethod.GET)
    public String enable(@RequestParam("username") String username) {

        userService.enableUser(username);
        return "redirect:/user/all";

    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/disable", method = RequestMethod.GET)
    public String disable(@RequestParam("username") String username) {

        userService.disableUser(username);
        return "redirect:/user/all";

    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/limit", method = RequestMethod.POST)
    public String changeLimit(@RequestParam("username") String username, @RequestParam("userLimitId") Long id, @RequestParam("dailyLimit") Double limit) {
//    public String changeLimit(@RequestParam("username") String username, @ModelAttribute("userLimit") UserLimit newUserLimit) {
        User user = userService.findByUsername(username);

        UserLimit userLimit = userService.findUserLimit(id);
//        UserLimit userLimit = userService.findUserLimit(newUserLimit.getUserLimitId());

        userLimit.setDailyLimit(limit);
//        userLimit.setDailyLimit(newUserLimit.getDailyLimit());

        user.setUserLimit(userLimit);
        userService.save(user);
        return "redirect:/user/all";
    }
}