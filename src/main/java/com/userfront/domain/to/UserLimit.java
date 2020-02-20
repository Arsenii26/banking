package com.userfront.domain.to;

import javax.persistence.*;

// second way is creation user TO
@Entity
public class UserLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "limit_id", nullable = false, updatable = false)
    private Long userLimitId;

    private Double dailyWithdrawMade;

    private Double dailyLimit;


    public Double getDailyWithdrawMade() {
        return dailyWithdrawMade;
    }

    public void setDailyWithdrawMade(Double dailyWithdrawMade) {
        this.dailyWithdrawMade = dailyWithdrawMade;
    }

    public Double getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(Double dailyLimit) {
        this.dailyLimit = dailyLimit;
    }

    public Long getUserLimitId() {
        return userLimitId;
    }

    public void setUserLimitId(Long userLimitId) {
        this.userLimitId = userLimitId;
    }

    @Override
    public String toString() {
        return "UserLimit{" +
                "userLimitId=" + userLimitId +
                ", dailyWithdrawMade=" + dailyWithdrawMade +
                ", dailyLimit=" + dailyLimit +
                '}';
    }
}
