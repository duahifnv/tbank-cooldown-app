package org.svids.tbankcooldownapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.svids.tbankcooldownapi.entity.AutoCooling;
import org.svids.tbankcooldownapi.entity.User;
import org.svids.tbankcooldownapi.repository.AutoCoolingRepo;
import org.svids.tbankcooldownapi.service.calculator.BudgetData;
import org.svids.tbankcooldownapi.service.calculator.ComfortDaysCalculator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutoCoolingService {

    private final AutoCoolingRepo autoCoolingRepo;
    private final List<ComfortDaysCalculator> calculators;

    public void initializeCooling(User user) {
        AutoCooling autoCooling = new AutoCooling();
        autoCooling.setUser(user);
        autoCoolingRepo.save(autoCooling);
    }

    public Optional<AutoCooling> findByUserId(UUID userId) {
        return autoCoolingRepo.findByUser_Id(userId);
    }

    public int calculateDays(BudgetData budgetData, BigDecimal purchaseCost) {
        log.debug("Calculating comfortable days for purchase: {} (budget: {}, savings: {}, salary: {})",
                purchaseCost,
                budgetData.monthBudget(),
                budgetData.totalSavings(),
                budgetData.monthBudget());

        // Собираем результаты от всех калькуляторов и считаем среднее значение
        double averageDays = calculators.stream()
                .mapToDouble(calc -> {
                    try {
                        int calculated = calc.calculateDays(budgetData, purchaseCost);
                        double weight = calc.getWeight() >= 0.0 && calc.getWeight() <= 1.0 ? calc.getWeight() : 1.0;

                        log.debug("Got {} comfortable days from {} ({} coefficient)", calculated, calc.getName(), weight);
                        return calculated * weight;
                    } catch (Exception e) {
                        log.error("Calculator {} failed: {}", calc.getName(), e.getMessage());
                        return -1;
                    }
                })
                .filter(days -> days >= 0) // Игнорируем ошибки
                .average().orElse(1.0);

        int roundedAverage = (int) Math.round(averageDays);
        // Применяем правило: если не 0, то минимум 1 день
        roundedAverage = (roundedAverage == 0) ? 0 : Math.max(1, roundedAverage);

        log.debug("Got average after aggregation: {} comfortable days", roundedAverage);
        return roundedAverage;
    }
}
