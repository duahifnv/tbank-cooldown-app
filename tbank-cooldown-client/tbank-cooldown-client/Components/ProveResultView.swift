import SwiftUI

// MARK: - ProveResultView
struct ProveResultView: View {
    // MARK: - Properties
    private let result: PurchaseAnalysisResult
    private let coolingMode: CoolingMode

    // MARK: - Init
    init(result: PurchaseAnalysisResult, coolingMode: CoolingMode) {
        self.result = result
        self.coolingMode = coolingMode
    }

    // MARK: - Выбор текста
    /// true -> использовать текст ДЛЯ УМНОГО охлаждения (без диапазона цен)
    /// false -> текст ДЛЯ РУЧНОГО охлаждения (с min/max)
    private var isSmartExplanation: Bool {
        coolingMode == .smart || result.autoCoolingEnabled
    }

    private var explanationText: String {
        if result.bannedCategory {
            return """
                Вы выбрали товар из категории, которую сами отметили как ограниченную.
                Такие покупки вы решили либо отложить, либо делать осознанно и реже.
                Мы не рекомендуем покупку сейчас, чтобы помочь вам придерживаться выбранных финансовых правил.
                """
        } else {
            if isSmartExplanation {
                // УМНОЕ охлаждение: бюджет / накопления / доход
                let budget  = formatCurrency(result.monthBudget)
                let savings = formatCurrency(result.totalSavings)
                let salary  = formatCurrency(result.monthSalary)
                
                return """
            Анализ учёл ваш ежемесячный бюджет в \(budget) ₽, текущие накопления \(savings) ₽ и уровень дохода \(salary) ₽. На основе запланированных трат в этом месяце (аренда, продукты, другие обязательные платежи) система определила оптимальный момент для покупки. Также учтены ваши привычки расходов и допустимый финансовый риск.
            """
            } else {
                // РУЧНОЕ охлаждение: диапазон цен
                let min = formatCurrency(result.minCost)
                let max = formatCurrency(result.maxCost)
                
                return """
            На основе установленных параметров ручного охлаждения (диапазон цен от \(min) до \(max) ₽ и заданный период ожидания) система проанализировала данную покупку. Цена товара сопоставлена с выбранным диапазоном и текущей нагрузкой на бюджет, чтобы помочь принять более взвешенное решение и избежать импульсивной траты.
            """
            }
        }
    }

    // MARK: - GUI
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text("Почему такой результат")
                .font(.headline)

            Text(explanationText)
                .foregroundStyle(.secondary)
                .font(.footnote)
        }
        .padding()
        .background(Color.white)
        .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
    }

    // MARK: - Helpers
    private func formatCurrency(_ value: Double?) -> String {
        guard let value = value else { return "0" }
        let intValue = Int(value.rounded())
        let formatter = NumberFormatter()
        formatter.numberStyle = .decimal
        formatter.groupingSeparator = " "
        return formatter.string(from: NSNumber(value: intValue)) ?? "\(intValue)"
    }
}
