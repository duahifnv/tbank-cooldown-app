import SwiftUI

// MARK: - ShouldntBuyView
struct ShouldntBuyView: View {
    // MARK: - Properties
    private let daysCount: Int
    private let isBannedCategory: Bool
    
    // MARK: - Init
    init(daysCount: Int, isBannedCategory: Bool) {
        self.daysCount = daysCount
        self.isBannedCategory = isBannedCategory
    }
    
    // MARK: - GUI
    var body: some View {
        ZStack {
            content
        }
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 12, style: .continuous)
                .stroke(Color(.systemRed), lineWidth: 1.5)
        )
        .background(
            RoundedRectangle(cornerRadius: 12, style: .continuous)
                .fill(Color.white)
        )
        .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
    }
}

// MARK: - Subviews
private extension ShouldntBuyView {
    @ViewBuilder
    var content: some View {
        if isBannedCategory {
            bannedContent
        } else {
            normalContent
        }
    }
    
    /// Обычный режим — просто «ждём N дней»
    var normalContent: some View {
        VStack(alignment: .leading, spacing: 16) {
            // header
            HStack(spacing: 12) {
                statusIcon
                
                VStack(alignment: .leading, spacing: 8) {
                    Text("Сейчас покупать не стоит")
                        .font(.system(size: 20, weight: .semibold))
                        .foregroundStyle(Color(.systemRed))
                    
                    Text("Рекомендуем подождать")
                        .foregroundStyle(.secondary)
                        .font(.footnote)
                }
            }
            
            // days block
            VStack(alignment: .leading, spacing: 0) {
                (
                    Text("Комфортно будет купить через ")
                        .font(.footnote)
                    +
                    Text("\(daysCount) ")
                        .font(.footnote)
                        .foregroundStyle(Color(.systemRed))
                    +
                    Text(dayWord(for: daysCount))
                        .font(.footnote)
                        .foregroundStyle(Color(.systemRed))
                )
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(.vertical, 20)
            .padding(.horizontal, 12)
            .background(Color(.systemRed).opacity(0.05))
            .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
        }
    }
    
    /// Режим для запрещённой категории
    var bannedContent: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack(spacing: 12) {
                statusIcon
                
                VStack(alignment: .leading, spacing: 6) {
                    Text("Категория под ограничением")
                        .font(.system(size: 20, weight: .semibold))
                        .foregroundStyle(Color(.systemRed))
                    
                    Text("Вы отметили эту категорию как ограниченную для импульсных покупок.")
                        .foregroundStyle(.secondary)
                        .font(.footnote)
                }
            }
            
            VStack(alignment: .leading, spacing: 8) {
                Text("Мы не рекомендуем оформлять покупку сейчас, чтобы вы оставались в рамках своих финансовых правил.")
                    .font(.footnote)
                    .foregroundStyle(.secondary)
                
                Text("Если решение осознанное, вы всё равно можете купить товар — но помните, что он из ограничённого списка.")
                    .font(.footnote)
                    .foregroundStyle(.secondary)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(.vertical, 16)
            .padding(.horizontal, 12)
            .background(Color(.systemRed).opacity(0.05))
            .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
        }
    }
    
    var statusIcon: some View {
        Circle()
            .fill(Color(.systemRed).opacity(0.15))
            .overlay {
                Image(systemName: "exclamationmark.circle")
                    .resizable()
                    .scaledToFit()
                    .foregroundStyle(Color(.systemRed))
                    .scaleEffect(0.55)
            }
            .frame(width: 40, height: 40)
    }
    
    func dayWord(for value: Int) -> String {
        // простая русская склонялка: 1 день, 2–4 дня, остальное дней
        let n = abs(value) % 100
        let last = n % 10
        
        if n > 10 && n < 20 { return "дней" }
        if last == 1 { return "день" }
        if (2...4).contains(last) { return "дня" }
        return "дней"
    }
}

// MARK: - Preview
#Preview {
    VStack(spacing: 16) {
        ShouldntBuyView(daysCount: 12, isBannedCategory: false)
            .frame(height: 170)
        
        ShouldntBuyView(daysCount: 0, isBannedCategory: true)
            .frame(height: 200)
    }
    .padding()
    .background(Color(.systemGray6))
}
