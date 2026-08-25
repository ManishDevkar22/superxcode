package com.eudhari.dao.shopkepperdao;

import com.eudhari.model.shopkeppermodel.TransactionModel;
import java.util.List;

public interface TransactionDAO {
    List<TransactionModel> getAllTransactions();

    List<TransactionModel> getTransactionsByShopId(String shopId);

    void saveTransaction(TransactionModel transaction);

    void updateTransaction(TransactionModel transaction);

    void deleteTransaction(String billId);
}
