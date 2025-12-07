//
//  CoolingSettingsCard.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 06.12.2025.
//

import Foundation

import SwiftUI

struct CoolingSettingsCard: View {
    @Binding var mode: CoolingMode
    @Binding var manual: ManualCoolingDTO
    @Binding var smart: SmartCoolingSettings
    
    let priceRange: ClosedRange<Double>
    let salaryRange: ClosedRange<Double>
    
    // Локальные значения для ползунков цены
    @State private var localMinPrice: Double
    @State private var localMaxPrice: Double
    
    // MARK: - Init
    init(
        mode: Binding<CoolingMode>,
        manual: Binding<ManualCoolingDTO>,
        smart: Binding<SmartCoolingSettings>,
        priceRange: ClosedRange<Double> = 0...100_000,
        salaryRange: ClosedRange<Double> = 20_000...200_000
    ) {
        _mode = mode
        _manual = manual
        _smart = smart
        self.priceRange = priceRange
        self.salaryRange = salaryRange
        
        _localMinPrice = State(initialValue: manual.wrappedValue.minPrice)
        _localMaxPrice = State(initialValue: manual.wrappedValue.maxPrice)
    }
    
    // MARK: - Body
    var body: some View {
        VStack(spacing: 0) {
            headerSection
            Divider()
            
            if mode == .manual {
                manualSection
            } else {
                smartSection
            }
        }
        .background(Color.white)
        .clipShape(RoundedRectangle(cornerRadius: 18, style: .continuous))
        .shadow(color: .black.opacity(0.05), radius: 6, x: 0, y: 2)
        // Прокидываем локальные цены наружу
        .onChange(of: localMinPrice) { newValue in
            manual.minPrice = newValue
        }
        .onChange(of: localMaxPrice) { newValue in
            manual.maxPrice = newValue
        }
    }
}

// MARK: - Subviews

private extension CoolingSettingsCard {
    
    // Правильный диапазон только для отображения
    var priceLow: Double  { min(localMinPrice, localMaxPrice) }
    var priceHigh: Double { max(localMinPrice, localMaxPrice) }
    
    // HEADER
    var headerSection: some View {
        VStack(spacing: 0) {
            HStack {
                Text("РЕЖИМ ОХЛАЖДЕНИЯ")
                    .font(.caption)
                    .fontWeight(.semibold)
                    .foregroundStyle(.secondary)
                Spacer()
            }
            .padding(.horizontal, 16)
            .padding(.top, 12)
            
            HStack {
                Text("Включить умное охлаждение")
                    .font(.headline)
                Spacer()
                Toggle("", isOn: smartToggleBinding)
                    .labelsHidden()
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 10)
        }
    }
    
    // MANUAL SECTION (цены + ОДИН ползунок дней)
    var manualSection: some View {
        VStack(spacing: 0) {
            // PRICE
            VStack(alignment: .leading, spacing: 12) {
                Text("Диапазон цены: \(rub(priceLow)) – \(rub(priceHigh))")
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                
                VStack(alignment: .leading, spacing: 4) {
                    Text("Минимальная цена")
                        .font(.footnote)
                        .foregroundStyle(.secondary)
                    Slider(value: $localMinPrice, in: priceRange)
                }
                
                VStack(alignment: .leading, spacing: 4) {
                    Text("Максимальная цена")
                        .font(.footnote)
                        .foregroundStyle(.secondary)
                    Slider(value: $localMaxPrice, in: priceRange)
                }
            }
            .padding(16)
            
            Divider()
            
            // DAYS (один слайдер)
            VStack(alignment: .leading, spacing: 12) {
                Text("Период охлаждения")
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                
                Text("Охлаждение: \(Int(manual.coolingTimeout)) дней")
                    .font(.footnote)
                    .foregroundStyle(.secondary)
                
                Slider(value: $manual.coolingTimeout, in: 1...60)
            }
            .padding(16)
        }
    }
    
    // SMART SECTION
    var smartSection: some View {
        VStack(spacing: 0) {
            // BUDGET
            VStack(alignment: .leading, spacing: 4) {
                Text("Месячный бюджет на траты")
                    .font(.footnote)
                    .foregroundStyle(.secondary)
                HStack(spacing: 4) {
                    Text("₽")
                    TextField("0", value: $smart.monthBudget, format: .number)
                        .keyboardType(.numberPad)
                }
                .font(.title3)
            }
            .padding(16)
            
            Divider()
            
            // SAVINGS
            VStack(alignment: .leading, spacing: 4) {
                Text("Текущие накопления")
                    .font(.footnote)
                    .foregroundStyle(.secondary)
                HStack(spacing: 4) {
                    Text("₽")
                    TextField("0", value: $smart.totalSavings, format: .number)
                        .keyboardType(.numberPad)
                }
                .font(.title3)
            }
            .padding(16)
            
            Divider()
            
            // SALARY
            VStack(alignment: .leading, spacing: 8) {
                Text("Ежемесячный доход: \(rub(smart.monthSalary))")
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                
                Slider(value: $smart.monthSalary, in: salaryRange)
                
                HStack {
                    Text(rub(salaryRange.lowerBound))
                        .font(.caption)
                        .foregroundStyle(.secondary)
                    Spacer()
                    Text(rub(salaryRange.upperBound))
                        .font(.caption)
                        .foregroundStyle(.secondary)
                }
            }
            .padding(16)
        }
    }
    
    var smartToggleBinding: Binding<Bool> {
        Binding(
            get: { mode == .smart },
            set: { mode = $0 ? .smart : .manual }
        )
    }
    
    func rub(_ value: Double) -> String {
        let int = Int(value.rounded())
        return "\(int.formatted(.number.grouping(.automatic))) ₽"
    }
}
