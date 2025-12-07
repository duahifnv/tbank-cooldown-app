import SwiftUI

// MARK: - PurchaseResultScreen
struct PurchaseResultScreen: View {
    // MARK: - Properties
    private let coolingMode: CoolingMode
    private let isShouldBuy: Bool
    private let daysCount: Int

    var viewModel: PurchaseResultViewModel
    let onFlowFinished: () -> Void

    // Удобное краткое свойство
    private var isBannedCategory: Bool {
        viewModel.result.bannedCategory == true
    }

    // MARK: - Init
    init(
        coolingMode: CoolingMode,
        isShouldBuy: Bool,
        daysCount: Int,
        viewModel: PurchaseResultViewModel,
        onFlowFinished: @escaping () -> Void
    ) {
        self.coolingMode = coolingMode
        self.isShouldBuy = isShouldBuy
        self.daysCount = daysCount
        self.viewModel = viewModel
        self.onFlowFinished = onFlowFinished
    }
    
    // MARK: - GUI
    var body: some View {
        ZStack {
            Color(.systemGray6)
                .ignoresSafeArea()
            
            VStack(spacing: 16) {
                // Контент, который скроллится
                ScrollView(.vertical, showsIndicators: false) {
                    VStack(spacing: 16) {
                        PurchaseCardResultView(
                            name: viewModel.purchase.name,
                            category: viewModel.purchase.category.rawValue,
                            coolingMode: viewModel.result.autoCoolingEnabled ? .smart : .manual,
                            price: "\(viewModel.purchase.price)"
                        )
                        .frame(height: 140)
                        .applyShadow()
                        
                        if isBannedCategory {
                            ShouldntBuyView(daysCount: 0, isBannedCategory: true)
                                .background(.white)
                                .applyShadow()
                        } else {
                            if !viewModel.result.isCooling {
                                ShouldBuyView()
                                    .applyShadow()
                            } else {
                                ShouldntBuyView(daysCount: daysCount, isBannedCategory: false)
                                    .background(.white)
                                    .applyShadow()
                            }
                        }
                        
                        VStack(spacing: 12) {
                            ProveResultView(
                                result: viewModel.result,
                                coolingMode: viewModel.result.autoCoolingEnabled ? .smart : .manual
                            )
                            .applyShadow()
                            
                            Text("Результат основан на выбранном типе анализа: \(coolingMode.rawValue)")
                                .font(.footnote)
                                .foregroundStyle(.secondary)
                                .multilineTextAlignment(.center)
                        }
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.top, 8)
                    .padding(.bottom, 8)
                }
                
                // Кнопки снизу (фиксированный блок)
                VStack(spacing: 12) {
                    // Основная кнопка покупки
                    Button {
                        viewModel.didTapAddToPurchased()
                        onFlowFinished()
                    } label: {
                        Text(isShouldBuy ? "Добавить в купленные"
                                         : "Всё равно купить")
                            .font(.headline)
                            .foregroundStyle(.white)
                            .padding()
                            .frame(maxWidth: .infinity)
                            .background(isShouldBuy ? Color(.systemGreen) : Color(.systemRed))
                            .clipShape(RoundedRectangle(cornerRadius: 10, style: .continuous))
                    }
                    
                    // Вторая кнопка: «Добавить в желаемое» ИЛИ «Отменить покупку»
                    Button {
                        if isBannedCategory {
                            // TODO: реализуй в VM
                            viewModel.didTapCancel()
                        } else {
                            viewModel.didTapAddToWishlist()
                        }
                        onFlowFinished()
                    } label: {
                        Group {
                            if isBannedCategory {
                                Text("Отменить покупку")
                                    .font(.headline)
                                    .foregroundStyle(Color(.systemRed))
                                    .padding()
                                    .frame(maxWidth: .infinity)
                                    .background(Color(.systemRed).opacity(0.08))
                                    .clipShape(RoundedRectangle(cornerRadius: 10, style: .continuous))
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 10, style: .continuous)
                                            .stroke(Color(.systemRed), lineWidth: 1)
                                    )
                            } else {
                                Text("Добавить в желаемое")
                                    .font(.headline)
                                    .foregroundStyle(isShouldBuy ? Color(.systemBlue) : .white)
                                    .padding()
                                    .frame(maxWidth: .infinity)
                                    .background(
                                        isShouldBuy
                                        ? Color.clear
                                        : Color(.systemBlue)
                                    )
                                    .clipShape(RoundedRectangle(cornerRadius: 10, style: .continuous))
                                    .overlay {
                                        if isShouldBuy {
                                            RoundedRectangle(cornerRadius: 10, style: .continuous)
                                                .stroke(Color(.systemBlue), lineWidth: 1)
                                        }
                                    }
                            }
                        }
                    }
                }
            }
            .padding()
        }
    }
}
