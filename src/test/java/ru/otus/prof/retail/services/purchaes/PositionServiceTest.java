package ru.otus.prof.retail.services.purchaes;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.prof.retail.dto.purchases.PositionDTO;
import ru.otus.prof.retail.services.purchases.PositionService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class PositionServiceTest {

    @Autowired
    private PositionService positionService;

    @Test
    void testGetPositionByPurchaseId() {
        Long purchaseId = 1L;

        List<PositionDTO> positions = positionService.getPositionsByPurchaseId(purchaseId);

        assertEquals(2, positions.size());
    }
}