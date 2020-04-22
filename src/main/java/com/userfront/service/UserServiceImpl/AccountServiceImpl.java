package com.userfront.service.UserServiceImpl;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;

import com.userfront.dao.PrimaryAccountDao;
import com.userfront.dao.SavingsAccountDao;
import com.userfront.dao.UserLimitDao;
import com.userfront.domain.to.UserLimit;
import com.userfront.exceptions.NegativeBalanceException;
import com.userfront.exceptions.OverlimitException;
import com.userfront.service.AccountService;
import com.userfront.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import com.userfront.domain.PrimaryAccount;
import com.userfront.domain.PrimaryTransaction;
import com.userfront.domain.SavingsAccount;
import com.userfront.domain.SavingsTransaction;
import com.userfront.domain.User;

import com.userfront.service.UserService;


@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private PrimaryAccountDao primaryAccountDao;

    @Autowired
    private SavingsAccountDao savingsAccountDao;

    @Autowired
    private UserService userService;

    @Autowired
    private UserLimitDao userLimitDao;

    @Autowired
    private TransactionService transactionService;

    public PrimaryAccount createPrimaryAccount() {
        PrimaryAccount primaryAccount = new PrimaryAccount();
        primaryAccount.setAccountBalance(new BigDecimal(0.0));

        primaryAccountDao.save(primaryAccount);

        return primaryAccountDao.findByAccountNumber(primaryAccount.getAccountNumber());
    }

    public SavingsAccount createSavingsAccount() {
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setAccountBalance(new BigDecimal(0.0));

        savingsAccountDao.save(savingsAccount);

        return savingsAccountDao.findByAccountNumber(savingsAccount.getAccountNumber());
    }

    public void deposit(String accountType, double amount, Principal principal) {
        User user = userService.findByUsername(principal.getName());

        if (accountType.equalsIgnoreCase("Primary")) {
            PrimaryAccount primaryAccount = user.getPrimaryAccount();
            primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().add(new BigDecimal(amount)));
            primaryAccountDao.save(primaryAccount);

            Date date = new Date();

            PrimaryTransaction primaryTransaction = new PrimaryTransaction(date, "Deposit to Primary Account", "Account", "Finished", amount, primaryAccount.getAccountBalance(), primaryAccount);
            transactionService.savePrimaryDepositTransaction(primaryTransaction);

        } else if (accountType.equalsIgnoreCase("Savings")) {
            SavingsAccount savingsAccount = user.getSavingsAccount();
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().add(new BigDecimal(amount)));
            savingsAccountDao.save(savingsAccount);

            Date date = new Date();
            SavingsTransaction savingsTransaction = new SavingsTransaction(date, "Deposit to savings Account", "Account", "Finished", amount, savingsAccount.getAccountBalance(), savingsAccount);
            transactionService.saveSavingsDepositTransaction(savingsTransaction);
        }
    }

    public void withdraw(String accountType, double amount, Principal principal) {
        User user = userService.findByUsername(principal.getName());

        // checking that account balance can't be negative
        if((user.getPrimaryAccount().getAccountBalance().compareTo(new BigDecimal(0)) < 0) ||
                (user.getSavingsAccount().getAccountBalance().compareTo(new BigDecimal(0)) < 0)){
            throw new NegativeBalanceException("Your balance is negative. Please deposit money", HttpStatus.CONFLICT);
        }

        // checking daily limit of the user
        if (user.getUserLimit().getDailyLimit() < user.getUserLimit().getDailyWithdrawMade()) {
            Double limit = user.getUserLimit().getDailyLimit();
            throw new OverlimitException("Daily limit exceeded. You can withdraw today only: " + limit, HttpStatus.CONFLICT);
//            System.out.println("Exceed!");
        } else {
            UserLimit userLimit = user.getUserLimit();
            userLimit.setDailyWithdrawMade(userLimit.getDailyWithdrawMade() + amount); //set limit

            if (accountType.equalsIgnoreCase("Primary")) {

                PrimaryAccount primaryAccount = user.getPrimaryAccount();
                primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().subtract(new BigDecimal(amount)));
                // check here again to avoid overlimit
                if (user.getUserLimit().getDailyLimit() < user.getUserLimit().getDailyWithdrawMade()) {

                    Double limit = user.getUserLimit().getDailyLimit();
                    throw new OverlimitException("Daily limit exceeded. You can withdraw today only: " + limit + "$", HttpStatus.CONFLICT);
//                    System.out.println("Exceed!");

                } else {
                   // checking for the possibility of negative balance
                    if(primaryAccount.getAccountBalance().compareTo(new BigDecimal(0)) >= 0){
                        userLimitDao.save(userLimit); // save

                        primaryAccountDao.save(primaryAccount);

                        Date date = new Date();
                        PrimaryTransaction primaryTransaction = new PrimaryTransaction(date, "Withdraw from Primary Account", "Account", "Finished", amount, primaryAccount.getAccountBalance(), primaryAccount);
                        transactionService.savePrimaryWithdrawTransaction(primaryTransaction);
                    }
                }

            } else if (accountType.equalsIgnoreCase("Savings")) {

                SavingsAccount savingsAccount = user.getSavingsAccount();
                savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().subtract(new BigDecimal(amount)));

                // check here again to avoid overlimit
                if (user.getUserLimit().getDailyLimit() < user.getUserLimit().getDailyWithdrawMade()) {

                    Double limit = user.getUserLimit().getDailyLimit();
                    throw new OverlimitException("Daily limit exceeded. You can withdraw today only: " + limit + "$", HttpStatus.CONFLICT);
//                    System.out.println("Exceed!");

                } else {
                    // checking for the possibility of negative balance
                    if(savingsAccount.getAccountBalance().compareTo(new BigDecimal(0)) >= 0){
                        userLimitDao.save(userLimit); // save

                        savingsAccountDao.save(savingsAccount);

                        Date date = new Date();
                        SavingsTransaction savingsTransaction = new SavingsTransaction(date, "Withdraw from savings Account", "Account", "Finished", amount, savingsAccount.getAccountBalance(), savingsAccount);
                        transactionService.saveSavingsWithdrawTransaction(savingsTransaction);
                    }


                }
            }
        }

    }

    /**
     * Will increase money by 5% every N time for all savings accounts
     */
    // https://stackoverflow.com/questions/30887822/spring-cron-vs-normal-cron
//    @Scheduled(fixedRate=20000) //every 20 sec
    @Scheduled(cron="0 */5 * * * *") // every 5 min
    public void increaseMoney() {
        //https://stackoverflow.com/questions/49710713/how-to-set-a-method-in-spring-boot-to-execute-at-specific-date

        Iterable<SavingsAccount> allAccounts = savingsAccountDao.findAll();

        allAccounts.forEach(savingsAccount -> savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().multiply
                (new BigDecimal(0.05)).add(savingsAccount.getAccountBalance())));

        //https://www.programcreek.com/2012/10/loop-a-iterable-in-java/
        Iterator<SavingsAccount> iter = allAccounts.iterator();
        while (iter.hasNext()) {
            savingsAccountDao.save(iter.next());
        }
    }

    //    @Scheduled(fixedRate=60000)  //every min
        @Scheduled(fixedRate=420000)  //every 7 min
    public void resetDailyLimit() {
        Iterable<UserLimit> allUserLimits = userLimitDao.findAll();

        allUserLimits.forEach(userLimit -> userLimit.setDailyWithdrawMade(0.0));

        Iterator<UserLimit> iterator = allUserLimits.iterator();
        while (iterator.hasNext()) {
            userLimitDao.save(iterator.next());
        }
    }
}