package ru.otus.prof.retail.mappers.shop;

import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.prof.retail.STATUS;
import ru.otus.prof.retail.dto.shop.CashDTO;
import ru.otus.prof.retail.dto.shop.ShopBasicDTO;
import ru.otus.prof.retail.dto.shop.ShopDTO;
import ru.otus.prof.retail.entities.shops.Cash;
import ru.otus.prof.retail.entities.shops.Shop;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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
    private Cash testCash;
    private Shop testShop;

    @BeforeEach
    void setup() {
        createdTime = LocalDateTime.now();
        updatedTime = LocalDateTime.now();
        testCash = createTestCash();
        testShop = createTestShop(testCash);
    }

    private Cash createTestCash() {
        Cash cash = new Cash();
        cash.setId(1L);
        cash.setStatus(STATUS.ACTIVE);
        cash.setNumber(2L);
        cash.setCreateDate(createdTime);
        cash.setUpdateDate(updatedTime);
        return cash;
    }

    private Shop createTestShop(Cash cash) {
        Shop shop = new Shop();
        shop.setId(1L);
        shop.setNumber(1L);
        shop.setName("Test Shop");
        shop.setAddress("Test Address");
        shop.setCashList(cash != null ? Collections.singletonList(cash) : null);
        return shop;
    }

    @Test
    void testToDTO() {
        when(cashMapper.toDTO(testCash)).thenReturn(new CashDTO(1L, STATUS.ACTIVE, 2L, createdTime, updatedTime, 1L));

        ShopDTO shopDTO = shopMapper.toDTO(testShop);

        assertNotNull(shopDTO);
        assertEquals(testShop.getId(), shopDTO.id());
        assertEquals(testShop.getNumber(), shopDTO.number());
        assertEquals(testShop.getName(), shopDTO.name());
        assertEquals(testShop.getAddress(), shopDTO.address());
        assertEquals(1, shopDTO.cashList().size());

        verify(cashMapper, times(1)).toDTO(testCash);
    }

    @Test
    void testToDTOWithNullCashList() {
        Shop shop = createTestShop(null);
        ShopDTO shopDTO = shopMapper.toDTO(shop);

        assertNotNull(shopDTO);
        assertNull(shopDTO.cashList());
    }

    @Test
    void testToBasicDTO() {
        ShopBasicDTO basicDTO = shopMapper.toBasicDTO(testShop);

        assertNotNull(basicDTO);
        assertEquals(testShop.getId(), basicDTO.id());
        assertEquals(testShop.getNumber(), basicDTO.number());
        assertEquals(testShop.getName(), basicDTO.name());
        assertEquals(testShop.getAddress(), basicDTO.address());
    }

    @Test
    void testToEntity() {
        CashDTO cashDTO = new CashDTO(1L, STATUS.ACTIVE, 2L, createdTime, updatedTime, 1L);
        ShopDTO shopDTO = new ShopDTO(1L, 1L, "Test Shop", "Test Address", Collections.singletonList(cashDTO));

        when(cashMapper.toEntity(cashDTO)).thenReturn(testCash);

        Shop shop = shopMapper.toEntity(shopDTO);

        assertNotNull(shop);
        assertEquals(shopDTO.id(), shop.getId());
        assertEquals(shopDTO.number(), shop.getNumber());
        assertEquals(shopDTO.name(), shop.getName());
        assertEquals(shopDTO.address(), shop.getAddress());
        assertEquals(1, shop.getCashList().size());

        verify(cashMapper, times(1)).toEntity(cashDTO);
    }

    @Test
    void testToEntityWithNullCashList() {
        ShopDTO shopDTO = new ShopDTO(1L, 1L, "Test Shop", "Test Address", null);

        Shop shop = shopMapper.toEntity(shopDTO);

        assertNotNull(shop);
        assertNotNull(shop.getCashList());
        assertTrue(shop.getCashList().isEmpty());
    }

    @Test
    void testToBasicDTOWithNullShop() {
        ShopBasicDTO result = shopMapper.toBasicDTO(null);
        assertNull(result);
    }

    @Test
    void testToDTOWithNullShop() {
        ShopDTO result = shopMapper.toDTO(null);
        assertNull(result);
    }

    @Test
    void testToDTOWithLazyInitializationException() {
        List<Cash> cashList = mock(List.class);
        when(cashList.stream()).thenThrow(new LazyInitializationException("Lazy init error"));

        Shop shop = createTestShop(null);
        shop.setCashList(cashList);

        ShopDTO shopDTO = shopMapper.toDTO(shop);

        assertNotNull(shopDTO);
        assertEquals(shop.getId(), shopDTO.id());
        assertEquals(shop.getNumber(), shopDTO.number());
        assertEquals(shop.getName(), shopDTO.name());
        assertEquals(shop.getAddress(), shopDTO.address());
        assertNotNull(shopDTO.cashList());
        assertTrue(shopDTO.cashList().isEmpty());
    }
}