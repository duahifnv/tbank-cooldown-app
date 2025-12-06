package org.svids.tbankcooldownapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "manual_coolings")
public class ManualCooling {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "min_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal minCost;

    @Column(name = "max_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal maxCost;

    @Column(name = "cooling_timeout", nullable = false)
    private Integer coolingTimeout;

}