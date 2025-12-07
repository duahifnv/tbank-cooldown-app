import Foundation
import Combine

/// Экран настроек напоминаний
final class ReminderSettingsViewModel: ObservableObject {
    // MARK: - Published свойства для биндингов с View
    
    /// Включены ли вообще системные напоминания
    @Published var notificationsEnabled: Bool = false
    
    /// Частота напоминаний (раз в день / неделю / месяц)
    @Published var frequency: ReminderFrequency = .daily
    
    /// Категории, которые НЕ должны попадать в напоминания
    @Published var excludedCategories: Set<PurchaseCategory> = []
    
    // MARK: - Private
    
    private let notificationManager: NotificationManager
    private var cancellables = Set<AnyCancellable>()
    
    // Флаг, чтобы не ловить рекурсивные обновления,
    // когда мы сами же меняем notificationsEnabled внутри sink
    private var isUpdatingToggleInternally = false
    
    // MARK: - Init
    
    init(notificationManager: NotificationManager = .shared) {
        self.notificationManager = notificationManager
        
        // Если в NotificationManager ты хранишь настройки в UserDefaults –
        // тут можно один раз их прочитать и проставить:
        //
        // let stored = notificationManager.currentSettings()
        // self.notificationsEnabled   = stored.isEnabled
        // self.frequency              = stored.frequency
        // self.excludedCategories     = stored.excludedCategories
        
        bind()
    }
    
    // MARK: - Bindings
    
    private func bind() {
        // 1. Реакция на переключение тумблера "Уведомления"
        $notificationsEnabled
            .dropFirst()                // пропускаем начальное значение из init
            .sink { [weak self] enabled in
                guard let self else { return }
                
                // Если мы сами программно меняем значение — не триггерим логику повторно
                if self.isUpdatingToggleInternally { return }
                
                self.notificationManager.applySettings(
                    enabled: enabled,
                    frequency: self.frequency
                ) { success in
                    // completion приходит с фонового потока — вернёмся на main
                    DispatchQueue.main.async {
                        if !success {
                            // если не получилось включить (например, нет разрешения) –
                            // откатываем тумблер обратно в false
                            self.isUpdatingToggleInternally = true
                            self.notificationsEnabled = false
                            self.isUpdatingToggleInternally = false
                        }
                    }
                }
            }
            .store(in: &cancellables)
        
        // 2. Реакция на смену частоты напоминаний
        $frequency
            .dropFirst()
            .sink { [weak self] newFrequency in
                guard let self else { return }
                self.notificationManager.updateFrequencyIfNeeded(
                    isEnabled: self.notificationsEnabled,
                    newFrequency: newFrequency
                )
            }
            .store(in: &cancellables)
        
        // 3. Если хочешь хранить исключённые категории в NotficationManager / UserDefaults
        //    (можно использовать потом при планировании уведомлений по товарам)
        $excludedCategories
            .dropFirst()
            .sink { [weak self] newExcluded in
                guard let self else { return }
                // Реализуй этот метод в NotificationManager, если нужно сохранять
                // self.notificationManager.updateExcludedCategories(newExcluded)
                print("🟦 [ReminderSettingsViewModel] excluded categories: \(newExcluded)")
            }
            .store(in: &cancellables)
    }
}
