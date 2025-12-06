package org.svids.tbankcooldownapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private BigDecimal monthBudget = BigDecimal.valueOf(10000);

    @Column(name = "total_savings", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalSavings = BigDecimal.valueOf(15000);

    @Column(name = "month_salary", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthSalary = BigDecimal.valueOf(50000);

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}