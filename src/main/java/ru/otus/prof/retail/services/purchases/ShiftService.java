package ru.otus.prof.retail.services.purchases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.prof.retail.entities.purchases.Shift;
import ru.otus.prof.retail.repositories.purchases.ShiftRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class ShiftService {

    @Autowired
    private ShiftRepository shiftRepository;

    //для всех магазинов
    public List<Shift> getAllShitsByCloseDate(LocalDate endDate, boolean withPositions) {
        return shiftRepository.findShiftByCloseDate(endDate, withPositions);
    }

    public List<Shift> getAllShiftsByCloseDateRange(LocalDate startDate, LocalDate endDate, boolean withPositions) {
        return shiftRepository.findShiftByCloseDateRange(startDate, endDate, withPositions);
    }

    //конкретный магазин
    public List<Shift> getShiftByShopNumberAndCloseDate(Long shopNumber, LocalDate endDate, boolean withPositions) {
        return shiftRepository.findShiftsByShopNumberAndCloseDate(shopNumber, endDate, withPositions);
    }

    public List<Shift> getShiftsByShopNumberAndCloseRange(Long shopNumber, LocalDate startDate, LocalDate endDate, boolean withPositions) {
        return shiftRepository.findShiftsByShopNumberAndCloseDateRange(shopNumber, startDate, endDate, withPositions);
    }

    //конкретная касса
    public List<Shift> getShiftsByShopNumberAndCashNumberAndCloseDate(Long shopNumber, Long cashNumber, LocalDate endDate, boolean withPositions) {
        return shiftRepository.findShiftsByShopNumberAndCashNumberAndCloseDate(shopNumber, cashNumber, endDate, withPositions);
    }

    public List<Shift> getShiftsByShopNumberAndCashNumberAndCloseDateRange(Long shopNumber, Long cashNumber, LocalDate startDate, LocalDate endDate, boolean withPositions) {
        return shiftRepository.findShiftsByShopNumberAndCashNumberAndCloseDateRange(shopNumber, cashNumber, startDate, endDate, withPositions);
    }


}
