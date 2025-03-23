package ru.otus.prof.retail.mappers.shop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.prof.retail.STATUS;
import ru.otus.prof.retail.dto.shop.CashDTO;
import ru.otus.prof.retail.dto.shop.ShopDTO;
import ru.otus.prof.retail.entities.shops.Cash;
import ru.otus.prof.retail.entities.shops.Shop;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShopMapperTest {

    @Mock
    private CashMapper cashMapper;

    @InjectMocks
    private ShopMapper shopMapper;

    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    @BeforeEach
    void setup(){
        createdTime = LocalDateTime.now();
        updatedTime = LocalDateTime.now();
    }

    @Test
    void testToDTO() {
        Cash cash = new Cash();
        cash.setId(1L);
        cash.setStatus(STATUS.ACTIVE);
        cash.setNumber(2L);
        cash.setCreateDate(createdTime);
        cash.setUpdateDate(updatedTime);

        Shop shop = new Shop();
        shop.setId(1L);
        shop.setNumber(1L);
        shop.setName("Test Shop");
        shop.setAddress("Test Address");
        shop.setCashList(Collections.singletonList(cash));

        when(cashMapper.toDTO(cash)).thenReturn(new CashDTO(1L, STATUS.ACTIVE, 2L, createdTime, updatedTime, 1L));

        ShopDTO shopDTO = shopMapper.toDTO(shop);

        assertNotNull(shopDTO);
        assertEquals(shop.getId(), shopDTO.id());
        assertEquals(shop.getNumber(), shopDTO.number());
        assertEquals(shop.getName(), shopDTO.name());
        assertEquals(shop.getAddress(), shopDTO.address());
        assertEquals(1, shopDTO.cashList().size());

        verify(cashMapper, times(1)).toDTO(cash);
    }

    @Test
    void testToEntity() {
        CashDTO cashDTO = new CashDTO(1L, STATUS.ACTIVE, 2L, createdTime, updatedTime, 1L);
        ShopDTO shopDTO = new ShopDTO(1L, 1L, "Test Shop", "Test Address", Collections.singletonList(cashDTO));

        Cash cash = new Cash();
        cash.setId(1L);
        cash.setStatus(STATUS.ACTIVE);
        cash.setNumber(2L);
        cash.setCreateDate(createdTime);
        cash.setUpdateDate(updatedTime);

        when(cashMapper.toEntity(cashDTO)).thenReturn(cash);

        Shop shop = shopMapper.toEntity(shopDTO);

        assertNotNull(shop);
        assertEquals(shopDTO.id(), shop.getId());
        assertEquals(shopDTO.number(), shop.getNumber());
        assertEquals(shopDTO.name(), shop.getName());
        assertEquals(shopDTO.address(), shop.getAddress());
        assertEquals(1, shop.getCashList().size());

        verify(cashMapper, times(1)).toEntity(cashDTO);
    }

}
