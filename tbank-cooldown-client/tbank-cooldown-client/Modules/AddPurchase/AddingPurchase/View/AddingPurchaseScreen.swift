import SwiftUI

// MARK: - AddingPurchaseScreen
struct AddingPurchaseScreen: View {
    // MARK: - Properties
    @ObservedObject private var viewModel: AddingPurchaseViewModel
    @State private var isResultActive = false
    let onFlowFinished: () -> Void
    
    // MARK: - init
    init(
        viewModel: AddingPurchaseViewModel,
        onFlowFinished: @escaping () -> Void
    ) {
        self.viewModel = viewModel
        self.onFlowFinished = onFlowFinished
    }
    
    // MARK: - GUI
    var body: some View {
        ZStack {
            Color(.systemGray6)
                .ignoresSafeArea()
            
            VStack(spacing: 16) {
                // Item name
                VStack(alignment: .leading, spacing: 0) {
                    Text("Название")
                        .font(.footnote)
                        .foregroundStyle(.secondary)
                        .padding(.horizontal, 14)
                        .padding(.top, 12)
                    
                    TextField("Введите название товара", text: $viewModel.textNamePurchase)
                        .font(.body)
                        .padding(.horizontal, 14)
                        .padding(.vertical, 12)
                }
                .background(.white)
                .clipShape(RoundedRectangle(cornerRadius: 10))
                
                // Item price
                VStack(alignment: .leading, spacing: 0) {
                    Text("Цена")
                        .font(.footnote)
                        .foregroundStyle(.secondary)
                        .padding(.horizontal, 14)
                        .padding(.top, 12)
                    
                    HStack {
                        Image(systemName: "rublesign")
                            .foregroundStyle(.secondary)
                            .font(.subheadline)
                        
                        TextField("0.00", text: $viewModel.textPricePurchase)
                            .font(.body)
                            .keyboardType(.decimalPad)
                    }
                    .padding(.horizontal, 14)
                    .padding(.vertical, 12)
                }
                .background(.white)
                .clipShape(RoundedRectangle(cornerRadius: 10))
                
                // Category
                VStack(alignment: .leading, spacing: 0) {
                    Text("Категория")
                        .font(.footnote)
                        .foregroundStyle(.secondary)
                        .padding(.horizontal, 14)
                        .padding(.top, 12)
                    
                    VStack(alignment: .leading) {
                        ForEach(viewModel.categories, id: \.self) { category in
                            HStack {
                                Button(action: {
                                    viewModel.didTapCategory(category)
                                }) {
                                    Text(category.rawValue)
                                }
                                .foregroundStyle(Color.black)
                                
                                Spacer()
                                
                                if viewModel.categoryPurchase == category {
                                    Image(systemName: "checkmark.circle.fill")
                                        .foregroundStyle(Color(.systemBlue))
                                }
                            }
                            .padding(8)
                            .frame(maxWidth: .infinity)
                            
                            if category != viewModel.categories.last {
                                Divider()
                            }
                        }
                    }
                    .padding(.horizontal, 14)
                    .padding(.vertical, 12)
                }
                .background(.white)
                .clipShape(RoundedRectangle(cornerRadius: 10))
                
                if let error = viewModel.validationError {
                    Text(error)
                        .font(.footnote)
                        .foregroundColor(.red)
                        .padding(.horizontal, 4)
                }
                
                Spacer()
                
                NavigationLink(
                    destination: resultDestination,
                    isActive: $isResultActive,
                    label: { EmptyView() }
                )
                .hidden()

                Button(action: {
                    viewModel.didTapAddPurchase()
                }, label: {
                    Group {
                        if viewModel.isLoading {
                            ProgressView()
                                .tint(.white)
                        } else {
                            Text("Добавить покупку")
                        }
                    }
                    .foregroundStyle(.white)
                    .padding()
                    .frame(maxWidth: .infinity)
                    .background(Color(.systemBlue))
                    .clipShape(RoundedRectangle(cornerRadius: 10))
                })
                .disabled(viewModel.isLoading)
            }
            .padding()
        }

        .onReceive(viewModel.$analysisResult) { result in
            if result != nil {
                isResultActive = true
            }
        }
    }
}

// MARK: - Destination
private extension AddingPurchaseScreen {
    @ViewBuilder
    var resultDestination: some View {
        if
            let result = viewModel.analysisResult,
            let category = viewModel.categoryPurchase
        {
            // безопасно парсим цену
            let price = Double(
                viewModel.textPricePurchase
                    .replacingOccurrences(of: " ", with: "")
                    .replacingOccurrences(of: ",", with: ".")
            ) ?? 0
            
            PurchaseResultScreen(
                coolingMode: .manual,                               // пока захардкожено
                isShouldBuy: result.bannedCategory ? false : !result.isCooling,                    // пример логики
                daysCount: result.coolingTimeout,
                viewModel: PurchaseResultViewModel(
                    result: result,
                    purchase: Purchase(
                        name: viewModel.textNamePurchase,
                        price: price,
                        category: category,
                        date: "",
                        status: .all
                    ),
                    purchaseService: PurchaseService(repository: PurchaseRepository())
                ),
                onFlowFinished: onFlowFinished
            )
            .navigationTitle("Результаты анализа")
        } else {
            Text("Ошибка анализа")
        }
    }
}
