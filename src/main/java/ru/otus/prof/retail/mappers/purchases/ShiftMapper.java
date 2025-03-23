package ru.otus.prof.retail.mappers.purchases;

import org.springframework.stereotype.Component;
import ru.otus.prof.retail.dto.purchases.PurchaseDTO;
import ru.otus.prof.retail.dto.purchases.ShiftDTO;
import ru.otus.prof.retail.entities.purchases.Purchase;
import ru.otus.prof.retail.entities.purchases.Shift;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ShiftMapper {

    private final PurchaseMapper purchaseMapper;

    public ShiftMapper(PurchaseMapper purchaseMapper) {
        this.purchaseMapper = purchaseMapper;
    }

    public ShiftDTO toDTO(Shift shift) {
        if (shift == null) {
            return null;
        }

        Set<PurchaseDTO> purchaseDTOs = shift.getPurchases() == null || shift.getPurchases().isEmpty()
                ? Collections.emptySet()
                : shift.getPurchases().stream()
                .map(purchaseMapper::toDTO)
                .collect(Collectors.toSet());

        return new ShiftDTO(
                shift.getId(),
                shift.getShiftNumber(),
                shift.getShopNumber(),
                shift.getCashNumber(),
                shift.getOpenTime(),
                shift.getCloseTime(),
                shift.getTotal(),
                purchaseDTOs
        );
    }

    public Shift toEntity(ShiftDTO shiftDTO) {
        if (shiftDTO == null) {
            return null;
        }

        Shift shift = new Shift();
        shift.setId(shiftDTO.id());
        shift.setShiftNumber(shiftDTO.shiftNumber());
        shift.setShopNumber(shiftDTO.shopNumber());
        shift.setCashNumber(shiftDTO.cashNumber());
        shift.setOpenTime(shiftDTO.openTime());
        shift.setCloseTime(shiftDTO.closeTime());
        shift.setTotal(shiftDTO.total());

        if (shiftDTO.purchases() != null && !shiftDTO.purchases().isEmpty()) {
            Set<Purchase> purchases = shiftDTO.purchases().stream()
                    .map(purchaseMapper::toEntity)
                    .collect(Collectors.toSet());
            shift.setPurchases(purchases);
        } else {
            shift.setPurchases(Collections.emptySet()); // Устанавливаем пустую коллекцию
        }

        return shift;
    }
}
