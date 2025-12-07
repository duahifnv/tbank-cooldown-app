import Foundation
import UserNotifications

/// Частота напоминаний
enum ReminderFrequency: CaseIterable, Hashable {
    case daily
    case weekly
    case monthly
    
    var title: String {
        switch self {
        case .daily:   return "Раз в день"
        case .weekly:  return "Раз в неделю"
        case .monthly: return "Раз в месяц"
        }
    }
}

/// Менеджер локальных уведомлений для напоминаний по желаемым покупкам
final class NotificationManager {
    
    static let shared = NotificationManager()
    
    private let center = UNUserNotificationCenter.current()
    
    /// Один общий идентификатор для нашего «ремайндера»
    private let reminderIdentifier = "wishedPurchases.reminder"
    
    private init() {}
    
    
    // Запросить разрешение (если нужно)
    func requestAuthorization(completion: @escaping (Bool) -> Void) {
        center.requestAuthorization(options: [.alert, .sound, .badge]) { granted, _ in
            DispatchQueue.main.async {
                completion(granted)
            }
        }
    }

    // Поставить напоминание по конкретной покупке
    func scheduleReminder(
        for purchase: WishedPurchase,
        frequency: ReminderFrequency,
        debug: Bool = false
    ) {
        let content = UNMutableNotificationContent()
        content.title = "Пора вернуться к покупкам"
        content.body  = "Решите, что делать с «\(purchase.name)»."
        content.sound = .default

        let timeInterval: TimeInterval
        if debug {
            // для отладки — через несколько секунд
            timeInterval = 5
        } else {
            switch frequency {
            case .daily:
                timeInterval = 24 * 60 * 60
            case .weekly:
                timeInterval = 7 * 24 * 60 * 60
            case .monthly:
                timeInterval = 30 * 24 * 60 * 60
            }
        }

        let trigger = UNTimeIntervalNotificationTrigger(timeInterval: timeInterval,
                                                        repeats: false)

        let id = "purchase-\(purchase.id)"
        let request = UNNotificationRequest(
            identifier: id,
            content: content,
            trigger: trigger
        )

        center.add(request, withCompletionHandler: nil)
    }

    /// Удаляем все отложенные напоминания по покупкам
    func cancelAllPurchaseReminders() {
        center.removeAllPendingNotificationRequests()
    }
}

// MARK: - Публичные методы, которые использует ViewModel

extension NotificationManager {
    
    /// Применить настройки с экрана:
    /// - enabled: включены ли вообще уведомления
    /// - frequency: как часто напоминать
    /// - completion(false) вернётся, если пользователь не выдал разрешение
    func applySettings(
        enabled: Bool,
        frequency: ReminderFrequency,
        completion: @escaping (Bool) -> Void
    ) {
        if !enabled {
            // Просто выключаем: очищаем все отложенные нотификации
            cancelReminders()
            completion(true)
            return
        }
        
        // Нужно включить — сначала проверяем/запрашиваем разрешение
        requestAuthorizationIfNeeded { [weak self] granted in
            guard let self else { return }
            
            guard granted else {
                // Разрешения нет — сообщаем VM, чтобы она откатила тумблер
                completion(false)
                return
            }
            
            // Разрешение есть — пересоздаём расписание
            self.rescheduleReminders(frequency: frequency)
            completion(true)
        }
    }
    
    /// Обновить частоту, если уведомления уже включены.
    /// Используется, когда юзер меняет «Раз в день / Раз в неделю / Раз в месяц»
    func updateFrequencyIfNeeded(
        isEnabled: Bool,
        newFrequency: ReminderFrequency
    ) {
        guard isEnabled else { return }
        rescheduleReminders(frequency: newFrequency)
    }
}

// MARK: - Внутренняя логика

private extension NotificationManager {
    
    /// Проверяем текущий статус, при необходимости запрашиваем разрешение
    func requestAuthorizationIfNeeded(completion: @escaping (Bool) -> Void) {
        center.getNotificationSettings { settings in
            switch settings.authorizationStatus {
            case .authorized, .provisional, .ephemeral:
                completion(true)
            case .denied:
                completion(false)
            case .notDetermined:
                self.center.requestAuthorization(options: [.alert, .badge, .sound]) { granted, _ in
                    completion(granted)
                }
            @unknown default:
                completion(false)
            }
        }
    }
    
    /// Полное пересоздание расписания напоминаний
    func rescheduleReminders(frequency: ReminderFrequency) {
        cancelReminders()
        scheduleReminder(frequency: frequency)
    }
    
    /// Удаляем наши напоминания
    func cancelReminders() {
        center.removePendingNotificationRequests(withIdentifiers: [reminderIdentifier])
    }
    
    /// Создаём одно повторяющееся уведомление в зависимости от частоты
    func scheduleReminder(frequency: ReminderFrequency) {
        // Текст можно потом сделать более умным
        let content = UNMutableNotificationContent()
        content.title = "Пора проверить желаемые покупки"
        content.body  = "Загляните в список и решите, что купить или удалить."
        content.sound = .default
        
        let trigger: UNNotificationTrigger
        
        switch frequency {
        case .daily:
            // Каждый день в 20:00
            var components = DateComponents()
            components.hour = 20
            trigger = UNCalendarNotificationTrigger(dateMatching: components, repeats: true)
            
        case .weekly:
            // Раз в неделю по понедельникам в 20:00
            var components = DateComponents()
            components.weekday = 2      // 1 — воскресенье, 2 — понедельник
            components.hour = 20
            trigger = UNCalendarNotificationTrigger(dateMatching: components, repeats: true)
            
        case .monthly:
            // Раз в месяц, 1-го числа в 20:00
            var components = DateComponents()
            components.day = 1
            components.hour = 20
            trigger = UNCalendarNotificationTrigger(dateMatching: components, repeats: true)
        }
        
        let request = UNNotificationRequest(
            identifier: reminderIdentifier,
            content: content,
            trigger: trigger
        )
        
        center.add(request) { error in
            if let error = error {
                print("🟥 [NotificationManager] failed to schedule reminder: \(error)")
            } else {
                print("🟩 [NotificationManager] reminder scheduled: \(frequency)")
            }
        }
    }
}


extension NotificationManager {
    func notifyCoolingFinished(for purchase: WishedPurchase) {
        // если уведомления выключены – можно просто выйти
        // (если у тебя есть флаг в менеджере, проверь его тут)

        let content = UNMutableNotificationContent()
        content.title = "Покупка готова"
        content.body  = "«\(purchase.name)» теперь можно купить — период охлаждения завершён."
        content.sound = .default

        // Показываем сразу
        let trigger = UNTimeIntervalNotificationTrigger(timeInterval: 1, repeats: false)
        let request = UNNotificationRequest(
            identifier: "cooling_finished_\(purchase.id)",
            content: content,
            trigger: trigger
        )

        UNUserNotificationCenter.current().add(request) { error in
            if let error = error {
                print("🟥 notifyCoolingFinished error: \(error)")
            } else {
                print("🟩 notifyCoolingFinished sent for \(purchase.id)")
            }
        }
    }
}
