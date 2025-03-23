package ru.otus.prof.retail.services.purchases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.prof.retail.dto.purchases.PositionDTO;
import ru.otus.prof.retail.entities.purchases.Position;
import ru.otus.prof.retail.mappers.purchases.PositionMapper;
import ru.otus.prof.retail.repositories.purchases.PositionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PositionService {

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private PositionMapper positionMapper;

    // Запрос всех позиций по чеку
    public List<PositionDTO> getPositionsByPurchaseId(Long purchaseId) {
        List<Position> positions = positionRepository.findByPurchase_Id(purchaseId);
        return positions.stream()
                .map(positionMapper::toDTO)
                .collect(Collectors.toList());
    }
}
