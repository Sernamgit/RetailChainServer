package ru.otus.prof.retail.mappers.shop;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.prof.retail.dto.shop.CashDTO;
import ru.otus.prof.retail.dto.shop.CashDeleteRequestDTO;
import ru.otus.prof.retail.dto.shop.CashStatusRequestDTO;
import ru.otus.prof.retail.entities.shops.Cash;
import ru.otus.prof.retail.entities.shops.Shop;
import ru.otus.prof.retail.exception.MappingException;
import ru.otus.prof.retail.exception.shop.ShopNotFoundException;
import ru.otus.prof.retail.repositories.shops.ShopRepository;
import ru.otus.prof.retail.STATUS;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CashMapperTest {

    @Mock
    private ShopRepository shopRepository;

    @InjectMocks
    private CashMapper cashMapper;

    private final LocalDateTime testTime = LocalDateTime.now();
    private final Shop testShop = new Shop(1L, 101L, "Test Shop", "Address", null);

    @Test
    void toDTO_ShouldConvertCashToDTO() {
        Cash cash = new Cash(1L, STATUS.ACTIVE, 1L, testTime, testTime, testShop);

        CashDTO dto = cashMapper.toDTO(cash);

        assertNotNull(dto);
        assertEquals(cash.getId(), dto.id());
        assertEquals(cash.getStatus(), dto.status());
        assertEquals(cash.getNumber(), dto.number());
        assertEquals(cash.getCreateDate(), dto.createDate());
        assertEquals(cash.getUpdateDate(), dto.updateDate());
        assertEquals(cash.getShop().getNumber(), dto.shopNumber());
    }

    @Test
    void toDTO_ShouldThrowExceptionWhenCashIsNull() {
        assertNull(cashMapper.toDTO(null));
    }

    @Test
    void toDTO_ShouldThrowExceptionWhenShopIsNull() {
        Cash cash = new Cash();
        cash.setId(1L);

        assertThrows(MappingException.class, () -> cashMapper.toDTO(cash));
    }

    @Test
    void toStatusRequestDTO_ShouldConvertCashToStatusRequestDTO() {
        Cash cash = new Cash(1L, STATUS.ACTIVE, 1L, testTime, testTime, testShop);

        CashStatusRequestDTO dto = cashMapper.toStatusRequestDTO(cash);

        assertNotNull(dto);
        assertEquals(cash.getId(), dto.id());
        assertEquals(cash.getNumber(), dto.number());
        assertEquals(cash.getShop().getNumber(), dto.shopNumber());
        assertEquals(cash.getStatus(), dto.status());
    }

    @Test
    void toDeleteRequestDTO_ShouldConvertCashToDeleteRequestDTO() {
        Cash cash = new Cash(1L, STATUS.ACTIVE, 1L, testTime, testTime, testShop);

        CashDeleteRequestDTO dto = cashMapper.toDeleteRequestDTO(cash);

        assertNotNull(dto);
        assertEquals(cash.getId(), dto.id());
        assertEquals(cash.getNumber(), dto.number());
        assertEquals(cash.getShop().getNumber(), dto.shopNumber());
    }

    @Test
    void toEntity_ShouldConvertCashDTOToEntity() {
        CashDTO dto = new CashDTO(1L, STATUS.ACTIVE, 1L, testTime, testTime, 101L);
        when(shopRepository.findByNumber(anyLong())).thenReturn(Optional.of(testShop));

        Cash cash = cashMapper.toEntity(dto);

        assertNotNull(cash);
        assertEquals(dto.id(), cash.getId());
        assertEquals(dto.status(), cash.getStatus());
        assertEquals(dto.number(), cash.getNumber());
        assertEquals(dto.createDate(), cash.getCreateDate());
        assertEquals(dto.updateDate(), cash.getUpdateDate());
        assertEquals(testShop, cash.getShop());
    }

    @Test
    void toEntity_ShouldThrowExceptionWhenShopNotFound() {
        CashDTO dto = new CashDTO(1L, STATUS.ACTIVE, 1L, testTime, testTime, 101L);
        when(shopRepository.findByNumber(anyLong())).thenReturn(Optional.empty());

        assertThrows(ShopNotFoundException.class, () -> cashMapper.toEntity(dto));
    }

    @Test
    void toEntity_ShouldReturnNullWhenDTOIsNull() {
        assertNull(cashMapper.toEntity(null));
    }
}