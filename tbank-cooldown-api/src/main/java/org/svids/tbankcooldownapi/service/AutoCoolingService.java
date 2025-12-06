package org.svids.tbankcooldownapi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.svids.tbankcooldownapi.entity.AutoCooling;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
public class AutoCoolingService {
    
    /**
     * Рассчитывает, через сколько дней покупка станет комфортной
     * 
     * @param autoCooling - настройки пользователя
     * @param purchaseCost - цена покупки
     * @return количество дней, через которое можно комфортно совершить покупку
     */
    public int calculateComfortableDays(AutoCooling autoCooling, BigDecimal purchaseCost) {
        // 1. Текущие свободные средства
        BigDecimal currentSavings = autoCooling.getTotalSavings();
        
        // 2. Месячный бюджет на свободные траты
        BigDecimal monthlyBudget = autoCooling.getMonthBudget();
        
        // 3. Определяем "комфортный остаток" - минимум половина месячного бюджета
        BigDecimal minComfortableBalance = monthlyBudget.divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
        
        // 4. Рассчитываем, сколько нужно накопить
        // Сумма, которую не хватает для комфортной покупки
        BigDecimal neededAmount = purchaseCost.subtract(currentSavings);
        
        // Если уже достаточно средств
        if (neededAmount.compareTo(BigDecimal.ZERO) <= 0) {
            // Проверяем, останется ли комфортный остаток
            BigDecimal afterPurchaseBalance = currentSavings.subtract(purchaseCost);
            if (afterPurchaseBalance.compareTo(minComfortableBalance) >= 0) {
                return 0; // Можно покупать прямо сейчас
            } else {
                // Нужно подкопить до комфортного остатка
                neededAmount = minComfortableBalance.subtract(afterPurchaseBalance);
            }
        }
        
        // 5. Рассчитываем время накопления
        // Допустим, пользователь откладывает fixedAmount в месяц на свободные траты
        BigDecimal monthlySaving = calculateMonthlySaving(autoCooling);
        
        if (monthlySaving.compareTo(BigDecimal.ZERO) <= 0) {
            return Integer.MAX_VALUE; // Невозможно накопить
        }
        
        // Дней в месяце для расчета (берем 30)
        int daysInMonth = 30;
        
        // Ежедневное накопление
        BigDecimal dailySaving = monthlySaving.divide(
            new BigDecimal(daysInMonth), 2, RoundingMode.HALF_UP);
        
        // Количество дней для накопления
        BigDecimal daysNeeded = neededAmount.divide(dailySaving, 0, RoundingMode.UP);
        
        return daysNeeded.intValue();
    }
    
    /**
     * Рассчитывает, сколько пользователь может откладывать в месяц
     * Более сложная логика может учитывать:
     * 1. % от зарплаты
     * 2. Цели накопления
     * 3. Обязательные платежи
     */
    private BigDecimal calculateMonthlySaving(AutoCooling autoCooling) {
        // Простая логика: 20% от месячного бюджета на свободные траты
        return autoCooling.getMonthBudget()
                .multiply(new BigDecimal("0.20"))
                .setScale(2, RoundingMode.HALF_UP);
    }
}