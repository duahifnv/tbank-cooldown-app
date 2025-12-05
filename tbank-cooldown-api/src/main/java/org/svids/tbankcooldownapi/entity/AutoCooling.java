package org.svids.tbankcooldownapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "auto_coolings")
public class AutoCooling {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "month_budget", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthBudget;

    @Column(name = "total_savings", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalSavings;

    @Column(name = "month_salary", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthSalary;

}