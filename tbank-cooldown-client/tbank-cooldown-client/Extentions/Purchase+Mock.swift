//
//  Purchase+Mock.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 06.12.2025.
//

import Foundation

extension Purchase {
    static let mockData: [Purchase] = [
        // Электроника / Дом
        Purchase(
            name: "MacBook Air M2",
            price: 199_900,
            category: .home,
            date: "Nov 25, 2024",
            status: .purchased
        ),
        Purchase(
            name: "iPhone 16 Pro",
            price: 149_990,
            category: .home,
            date: "Dec 05, 2024",
            status: .purchased
        ),
        Purchase(
            name: "Наушники Sony WH-1000XM5",
            price: 29_990,
            category: .home,
            date: "Oct 12, 2024",
            status: .cancalled
        ),
        
        // Одежда
        Purchase(
            name: "Кроссовки Nike",
            price: 12_490,
            category: .clothing,
            date: "Sep 01, 2024",
            status: .purchased
        ),
        Purchase(
            name: "Зимняя куртка",
            price: 18_990,
            category: .clothing,
            date: "Nov 10, 2024",
            status: .cancalled
        ),
        Purchase(
            name: "Футболка Uniqlo",
            price: 1_990,
            category: .clothing,
            date: "Aug 20, 2024",
            status: .purchased
        ),
        
        // Еда
        Purchase(
            name: "Продукты на неделю",
            price: 4_500,
            category: .food,
            date: "Dec 01, 2024",
            status: .purchased
        ),
        Purchase(
            name: "Ужин в ресторане",
            price: 3_200,
            category: .food,
            date: "Nov 30, 2024",
            status: .cancalled
        ),
        Purchase(
            name: "Кофе и десерт",
            price: 650,
            category: .food,
            date: "Nov 15, 2024",
            status: .purchased
        ),
        
        // Дом / Другое
        Purchase(
            name: "Стул в рабочий угол",
            price: 8_990,
            category: .home,
            date: "Jul 05, 2024",
            status: .purchased
        ),
        Purchase(
            name: "Настольная лампа",
            price: 2_490,
            category: .home,
            date: "Jul 10, 2024",
            status: .cancalled
        ),
        Purchase(
            name: "Подарок другу",
            price: 5_000,
            category: .other,
            date: "Dec 02, 2024",
            status: .purchased
        ),
        Purchase(
            name: "Подписка на сервис",
            price: 499,
            category: .other,
            date: "Nov 01, 2024",
            status: .purchased
        )
    ]
}
