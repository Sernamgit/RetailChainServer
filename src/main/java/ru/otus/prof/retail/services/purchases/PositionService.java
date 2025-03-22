package ru.otus.prof.retail.services.purchases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.prof.retail.entities.purchases.Position;
import ru.otus.prof.retail.repositories.purchases.PositionRepository;

import java.util.List;

@Service
public class PositionService {

    @Autowired
    private PositionRepository positionRepository;

    // Запрос всех позиций по чеку
    public List<Position> getPositionsByPurchaseId(Long purchaseId) {
        return positionRepository.findByPurchase_Id(purchaseId);
    }
}
