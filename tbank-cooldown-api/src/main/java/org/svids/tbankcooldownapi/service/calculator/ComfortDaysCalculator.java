package org.svids.tbankcooldownapi.service.calculator;

import java.math.BigDecimal;

/**
 * Интерфейс для расчета комфортных дней до покупки.
 * Каждая реализация предоставляет свой алгоритм расчета.
 */
public interface ComfortDaysCalculator {

    /**
     * Рассчитывает комфортное количество дней до покупки
     * @return количество дней или -1 если не может рассчитать
     */
    int calculateDays(BudgetData budgetData, BigDecimal purchaseCost);

    /**
     * Вес для расчета средневзвешенного
     * @return Значение, на которое будет умножаться число комфортных дней, от 0.0 до 1.0
     */
    default double getWeight() {
        return 1.0;
    }

    /**
     * Уникальное имя калькулятора для логирования и отладки
     */
    default String getName() {
        return getClass().getSimpleName();
    }
}
