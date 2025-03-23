package ru.otus.prof.retail.services.purchases;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.prof.retail.dto.purchases.ShiftDTO;
import ru.otus.prof.retail.entities.purchases.Shift;
import ru.otus.prof.retail.mappers.purchases.ShiftMapper;
import ru.otus.prof.retail.repositories.purchases.ShiftRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShiftService {

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private ShiftMapper shiftMapper;

    //для всех магазинов
    @Transactional
    public List<ShiftDTO> getAllShitsByCloseDate(LocalDate endDate, boolean withPositions) {
        List<Shift> shifts = shiftRepository.findShiftByCloseDate(endDate, withPositions);
        return shifts.stream()
                .map(shiftMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ShiftDTO> getAllShiftsByCloseDateRange(LocalDate startDate, LocalDate endDate, boolean withPositions) {
        List<Shift> shifts = shiftRepository.findShiftByCloseDateRange(startDate, endDate, withPositions);
        return shifts.stream()
                .map(shiftMapper::toDTO)
                .collect(Collectors.toList());
    }

    //конкретный магазин
    @Transactional
    public List<ShiftDTO> getShiftByShopNumberAndCloseDate(Long shopNumber, LocalDate endDate, boolean withPositions) {
        List<Shift> shifts = shiftRepository.findShiftsByShopNumberAndCloseDate(shopNumber, endDate, withPositions);
        return shifts.stream()
                .map(shiftMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ShiftDTO> getShiftsByShopNumberAndCloseRange(Long shopNumber, LocalDate startDate, LocalDate endDate, boolean withPositions) {
        List<Shift> shifts = shiftRepository.findShiftsByShopNumberAndCloseDateRange(shopNumber, startDate, endDate, withPositions);
        return shifts.stream()
                .map(shiftMapper::toDTO)
                .collect(Collectors.toList());
    }

    //конкретная касса
    @Transactional
    public List<ShiftDTO> getShiftsByShopNumberAndCashNumberAndCloseDate(Long shopNumber, Long cashNumber, LocalDate endDate, boolean withPositions) {
        List<Shift> shifts = shiftRepository.findShiftsByShopNumberAndCashNumberAndCloseDate(shopNumber, cashNumber, endDate, withPositions);
        return shifts.stream()
                .map(shiftMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ShiftDTO> getShiftsByShopNumberAndCashNumberAndCloseDateRange(Long shopNumber, Long cashNumber, LocalDate startDate, LocalDate endDate, boolean withPositions) {
        List<Shift> shifts = shiftRepository.findShiftsByShopNumberAndCashNumberAndCloseDateRange(shopNumber, cashNumber, startDate, endDate, withPositions);
        return shifts.stream()
                .map(shiftMapper::toDTO)
                .collect(Collectors.toList());
    }
}
