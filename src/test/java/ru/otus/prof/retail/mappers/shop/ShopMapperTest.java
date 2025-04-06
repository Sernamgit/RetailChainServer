package ru.otus.prof.retail.mappers.shop;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.prof.retail.dto.shop.CashDTO;
import ru.otus.prof.retail.dto.shop.ShopBasicDTO;
import ru.otus.prof.retail.dto.shop.ShopDTO;
import ru.otus.prof.retail.entities.shops.Cash;
import ru.otus.prof.retail.entities.shops.Shop;
import ru.otus.prof.retail.STATUS;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShopMapperTest {

    @Mock
    private CashMapper cashMapper;

    @InjectMocks
    private ShopMapper shopMapper;

    private final LocalDateTime testTime = LocalDateTime.now();

    @Test
    void toBasicDTO_ShouldConvertShopToBasicDTO() {
        Shop shop = new Shop(1L, 101L, "Test Shop", "Address", null);

        ShopBasicDTO dto = shopMapper.toBasicDTO(shop);

        assertNotNull(dto);
        assertEquals(shop.getId(), dto.id());
        assertEquals(shop.getNumber(), dto.number());
        assertEquals(shop.getName(), dto.name());
        assertEquals(shop.getAddress(), dto.address());
    }

    @Test
    void toBasicDTO_ShouldReturnNullWhenShopIsNull() {
        assertNull(shopMapper.toBasicDTO(null));
    }

    @Test
    void toDTO_ShouldConvertShopToDTOWithoutCashList() {
        Shop shop = new Shop(1L, 101L, "Test Shop", "Address", null);

        ShopDTO dto = shopMapper.toDTO(shop);

        assertNotNull(dto);
        assertEquals(shop.getId(), dto.id());
        assertEquals(shop.getNumber(), dto.number());
        assertEquals(shop.getName(), dto.name());
        assertEquals(shop.getAddress(), dto.address());
        assertTrue(dto.cashList().isEmpty());
    }

    @Test
    void toDTO_ShouldConvertShopToDTOWithCashList() {
        Cash cash = new Cash(1L, STATUS.ACTIVE, 1L, testTime, testTime, null);
        Shop shop = new Shop(1L, 101L, "Test Shop", "Address", List.of(cash));
        CashDTO cashDTO = new CashDTO(1L, STATUS.ACTIVE, 1L, testTime, testTime, 101L);

        when(cashMapper.toDTO(cash)).thenReturn(cashDTO);

        ShopDTO dto = shopMapper.toDTO(shop);

        assertNotNull(dto);
        assertEquals(1, dto.cashList().size());
        assertEquals(cashDTO, dto.cashList().get(0));
    }

    @Test
    void toDTO_ShouldSkipNullCashDTO() {
        Cash cash = new Cash(1L, STATUS.ACTIVE, 1L, testTime, testTime, null);
        Shop shop = new Shop(1L, 101L, "Test Shop", "Address", List.of(cash));

        when(cashMapper.toDTO(cash)).thenReturn(null);

        ShopDTO dto = shopMapper.toDTO(shop);

        assertNotNull(dto);
        assertTrue(dto.cashList().isEmpty());
    }

    @Test
    void toEntity_ShouldConvertShopDTOToEntityWithoutCashList() {
        ShopDTO dto = new ShopDTO(1L, 101L, "Test Shop", "Address", null);

        Shop shop = shopMapper.toEntity(dto);

        assertNotNull(shop);
        assertEquals(dto.id(), shop.getId());
        assertEquals(dto.number(), shop.getNumber());
        assertEquals(dto.name(), shop.getName());
        assertEquals(dto.address(), shop.getAddress());
        assertTrue(shop.getCashList().isEmpty());
    }

    @Test
    void toEntity_ShouldConvertShopDTOToEntityWithCashList() {
        CashDTO cashDTO = new CashDTO(1L, STATUS.ACTIVE, 1L, testTime, testTime, 101L);
        ShopDTO dto = new ShopDTO(1L, 101L, "Test Shop", "Address", List.of(cashDTO));
        Cash cash = new Cash(1L, STATUS.ACTIVE, 1L, testTime, testTime, null);

        when(cashMapper.toEntity(cashDTO)).thenReturn(cash);

        Shop shop = shopMapper.toEntity(dto);

        assertNotNull(shop);
        assertEquals(1, shop.getCashList().size());
        assertEquals(cash, shop.getCashList().get(0));
    }

    @Test
    void toEntity_ShouldSkipNullCash() {
        CashDTO cashDTO = new CashDTO(1L, STATUS.ACTIVE, 1L, testTime, testTime, 101L);
        ShopDTO dto = new ShopDTO(1L, 101L, "Test Shop", "Address", List.of(cashDTO));

        when(cashMapper.toEntity(cashDTO)).thenReturn(null);

        Shop shop = shopMapper.toEntity(dto);

        assertNotNull(shop);
        assertTrue(shop.getCashList().isEmpty());
    }

    @Test
    void toEntity_ShouldReturnNullWhenDTOIsNull() {
        assertNull(shopMapper.toEntity(null));
    }
}