package ru.otus.prof.retail.services.purchases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.prof.retail.entities.purchases.Purchase;
import ru.otus.prof.retail.repositories.purchases.PurchaseRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    public List<Purchase> getPurchaseByShiftId(Long shiftId){
        return purchaseRepository.findPurchaseByShiftId(shiftId);
    }

    public List<Purchase> getPurchaseByShoNumberAndDate(Long shopNumber, LocalDate endDate){
        return purchaseRepository.findPurchaseByShopNumberAndDate(shopNumber, endDate);
    }

    public List<Purchase> getPurchaseByShopNumberAndCashNumberAndDate(Long shopNumber, Long cashNumber, LocalDate endDate) {
        return purchaseRepository.findPurchasesByShopNumberAndCashNumberAndDate(shopNumber, cashNumber, endDate);
    }

}
