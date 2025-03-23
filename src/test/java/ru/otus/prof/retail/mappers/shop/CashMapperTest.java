package ru.otus.prof.retail.mappers.shop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.prof.retail.STATUS;
import ru.otus.prof.retail.dto.shop.CashDTO;
import ru.otus.prof.retail.entities.shops.Cash;
import ru.otus.prof.retail.entities.shops.Shop;
import ru.otus.prof.retail.repositories.shops.ShopRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CashMapperTest {

    @Mock
    private ShopRepository shopRepository;

    @InjectMocks
    private CashMapper cashMapper;

    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private Shop shop;

    @BeforeEach
    void setup(){
        createdTime = LocalDateTime.now();
        updatedTime = LocalDateTime.now();
        shop = new Shop();
        shop.setNumber(1L);
    }

    @Test
    void testToDTO() {
        Cash cash = new Cash(1L, STATUS.ACTIVE, 2L, createdTime, updatedTime, shop);

        CashDTO cashDTO = cashMapper.toDTO(cash);

        assertNotNull(cashDTO);
        assertEquals(cash.getId(), cashDTO.id());
        assertEquals(cash.getStatus(), cashDTO.status());
        assertEquals(cash.getNumber(), cashDTO.number());
        assertEquals(cash.getCreateDate(), cashDTO.createDate());
        assertEquals(cash.getUpdateDate(), cashDTO.updateDate());
        assertEquals(cash.getShop().getNumber(), cashDTO.shopNumber());
    }

    @Test
    void testToEntity() {
        CashDTO cashDTO = new CashDTO(1L, STATUS.ACTIVE, 2L, createdTime, updatedTime, 1L);

        when(shopRepository.findByNumber(1L)).thenReturn(Optional.of(shop));

        Cash cash = cashMapper.toEntity(cashDTO);

        assertNotNull(cash);
        assertEquals(cashDTO.id(), cash.getId());
        assertEquals(cashDTO.status(), cash.getStatus());
        assertEquals(cashDTO.number(), cash.getNumber());
        assertEquals(cashDTO.createDate(), cash.getCreateDate());
        assertEquals(cashDTO.updateDate(), cash.getUpdateDate());
        assertEquals(cashDTO.shopNumber(), cash.getShop().getNumber());

        verify(shopRepository, times(1)).findByNumber(1L);
    }
}
