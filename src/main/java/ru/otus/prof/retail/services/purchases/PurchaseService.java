package ru.otus.prof.retail.services.purchases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.prof.retail.dto.purchases.PurchaseDTO;
import ru.otus.prof.retail.entities.purchases.Purchase;
import ru.otus.prof.retail.mappers.purchases.PurchaseMapper;
import ru.otus.prof.retail.repositories.purchases.PurchaseRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private PositionService positionService;

    @Autowired
    private PurchaseMapper purchaseMapper;

    public List<PurchaseDTO> getPurchaseByShiftId(Long shiftId){
        List<Purchase> purchases = purchaseRepository.findPurchaseByShiftId(shiftId);
        return purchases.stream()
                .map(purchaseMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<PurchaseDTO> getPurchaseByShoNumberAndDate(Long shopNumber, LocalDate endDate){
        List<Purchase> purchases = purchaseRepository.findPurchaseByShopNumberAndDate(shopNumber, endDate);
        return purchases.stream()
                .map(purchaseMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<PurchaseDTO> getPurchaseByShopNumberAndCashNumberAndDate(Long shopNumber, Long cashNumber, LocalDate endDate) {
        List<Purchase> purchases = purchaseRepository.findPurchasesByShopNumberAndCashNumberAndDate(shopNumber, cashNumber, endDate);
        return purchases.stream()
                .map(purchaseMapper::toDTO)
                .collect(Collectors.toList());
    }

}
