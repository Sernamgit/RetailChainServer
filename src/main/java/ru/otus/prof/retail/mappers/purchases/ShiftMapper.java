package ru.otus.prof.retail.mappers.purchases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.otus.prof.retail.dto.purchases.PurchaseDTO;
import ru.otus.prof.retail.dto.purchases.ShiftDTO;
import ru.otus.prof.retail.entities.purchases.Purchase;
import ru.otus.prof.retail.entities.purchases.Shift;
import ru.otus.prof.retail.exception.MappingException;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ShiftMapper {
    private static final Logger logger = LoggerFactory.getLogger(ShiftMapper.class);
    private final PurchaseMapper purchaseMapper;

    public ShiftMapper(PurchaseMapper purchaseMapper) {
        this.purchaseMapper = purchaseMapper;
    }

    public ShiftDTO toDTO(Shift shift, boolean withPurchases) {
        if (shift == null) {
            logger.warn("Попытка преобразования null Shift в DTO");
            return null;
        }

        logger.debug("Преобразование Shift в DTO (ID: {})", shift.getId());

        try {
            Set<PurchaseDTO> purchaseDTOs = withPurchases && shift.getPurchases() != null
                    ? shift.getPurchases().stream()
                    .map(purchaseMapper::toDTO)
                    .collect(Collectors.toSet())
                    : Collections.emptySet();

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
        } catch (Exception e) {
            String errorMsg = String.format("Ошибка преобразования Shift в DTO (ID: %s). Причина: %s",
                    shift.getId(), e.getMessage());
            logger.error(errorMsg, e);
            throw new MappingException(errorMsg, e);
        }
    }

    public Shift toEntity(ShiftDTO shiftDTO) {
        if (shiftDTO == null) {
            logger.warn("Попытка преобразования null ShiftDTO в сущность");
            return null;
        }

        logger.debug("Преобразование ShiftDTO в сущность (ID: {})", shiftDTO.id());

        try {
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
                shift.setPurchases(Collections.emptySet());
            }

            return shift;
        } catch (Exception e) {
            String errorMsg = String.format("Ошибка преобразования ShiftDTO в сущность (ID: %s). Причина: %s",
                    shiftDTO.id(), e.getMessage());
            logger.error(errorMsg, e);
            throw new MappingException(errorMsg, e);
        }
    }
}