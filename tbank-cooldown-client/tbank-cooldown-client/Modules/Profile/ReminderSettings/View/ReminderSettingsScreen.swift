import SwiftUI

struct ReminderSettingsScreen: View {
    // MARK: - Bindings из внешнего мира
    @Binding var isNotificationsEnabled: Bool
    @Binding var frequency: ReminderFrequency
    @Binding var excludedCategories: Set<PurchaseCategory>
    
    // MARK: - Init
    init(
        isNotificationsEnabled: Binding<Bool>,
        frequency: Binding<ReminderFrequency>,
        excludedCategories: Binding<Set<PurchaseCategory>>,
        
    ) {
        self._isNotificationsEnabled = isNotificationsEnabled
        self._frequency = frequency
        self._excludedCategories = excludedCategories
    }
    
    // MARK: - Body
    var body: some View {
        ZStack {
            Color(.systemGray6)
                .ignoresSafeArea()
            
            ScrollView {
                VStack(alignment: .leading, spacing: 24) {
                    
                    // MARK: - Section: Уведомления
                    VStack(alignment: .leading, spacing: 0) {
                        Text("УВЕДОМЛЕНИЯ")
                            .font(.caption)
                            .fontWeight(.semibold)
                            .foregroundStyle(.secondary)
                            .padding(.horizontal, 16)
                            .padding(.top, 12)
                            .padding(.bottom, 4)
                        
                        HStack(spacing: 12) {
                            VStack(alignment: .leading, spacing: 4) {
                                Text("Системные уведомления")
                                    .font(.headline)
                                Text("Напоминать о запланированных покупках")
                                    .font(.footnote)
                                    .foregroundStyle(.secondary)
                            }
                            
                            Spacer()
                            
                            Toggle("", isOn: $isNotificationsEnabled)
                                .labelsHidden()
                        }
                        .padding(16)
                        .background(Color.white)
                        .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
                    }
                    
                    // MARK: - Section: Частота
                    VStack(alignment: .leading, spacing: 8) {
                        Text("ЧАСТОТА НАПОМИНАНИЙ")
                            .font(.caption)
                            .fontWeight(.semibold)
                            .foregroundStyle(.secondary)
                            .padding(.horizontal, 16)
                        
                        Text("Когда напоминать об инвентаризации желаемых покупок")
                            .font(.footnote)
                            .foregroundStyle(.secondary)
                            .padding(.horizontal, 16)
                        
                        VStack(spacing: 0) {
                            ForEach(ReminderFrequency.allCases, id: \.self) { option in
                                Button {
                                    frequency = option
                                } label: {
                                    HStack {
                                        Text(option.title)
                                            .foregroundColor(.primary)
                                        Spacer()
                                        if option == frequency {
                                            Image(systemName: "checkmark")
                                                .foregroundColor(.blue)
                                        }
                                    }
                                    .padding(.horizontal, 16)
                                    .padding(.vertical, 14)
                                }
                                
                                if option != ReminderFrequency.allCases.last {
                                    Divider()
                                }
                            }
                        }
                        .background(Color.white)
                        .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
                    }
                    
                    // MARK: - Section: Фильтр покупок
                    VStack(alignment: .leading, spacing: 12) {
                        Text("ФИЛЬТР ПОКУПОК")
                            .font(.caption)
                            .fontWeight(.semibold)
                            .foregroundStyle(.secondary)
                        
                        Text("Исключить категории")
                            .font(.footnote)
                            .foregroundStyle(.secondary)
                    }
                    .padding(.horizontal, 16)
                }
                .padding(.vertical, 16)
            }
        }
        .navigationTitle("Настройки напоминаний")
        .navigationBarTitleDisplayMode(.inline)
    }
}
