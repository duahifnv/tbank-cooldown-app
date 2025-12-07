//
//  WishedPurchase+Ext.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 06.12.2025.
//

import Foundation

extension WishedPurchase {
    private static let iso8601Formatter: ISO8601DateFormatter = {
        let f = ISO8601DateFormatter()
        f.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
        return f
    }()
    
    /// Парсим wishedDate в Date
    var wishedAtDate: Date? {
        Self.iso8601Formatter.date(from: wishedDate)
    }
    
    /// Сколько дней осталось до конца охлаждения.
    /// Если coolingTimeout == nil или всё уже остыло – возвращаем nil.
    func remainingDays(now: Date = Date()) -> Int? {
        guard
            let totalDays = coolingTimeout,
            let start = wishedAtDate
        else { return nil }
        
        let passed = Calendar.current.dateComponents([.day], from: start, to: now).day ?? 0
        let left = totalDays - passed
        
        return left > 0 ? left : nil
    }
    
    /// Охлаждение закончено (можно показывать кнопки)
    func isCoolingFinished(now: Date = Date()) -> Bool {
        remainingDays(now: now) == nil
    }
}
