package ru.otus.prof.retail.mappers.shop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.prof.retail.STATUS;
import ru.otus.prof.retail.dto.shop.CashDTO;
import ru.otus.prof.retail.dto.shop.CashDeleteRequestDTO;
import ru.otus.prof.retail.dto.shop.CashStatusRequestDTO;
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
    private Cash cash;

    @BeforeEach
    void setup() {
        createdTime = LocalDateTime.now();
        updatedTime = LocalDateTime.now();
        shop = new Shop();
        shop.setNumber(1L);

        cash = new Cash(1L, STATUS.ACTIVE, 2L, createdTime, updatedTime, shop);
    }

    @Test
    void testToDTO() {
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

        Cash result = cashMapper.toEntity(cashDTO);

        assertNotNull(result);
        assertEquals(cashDTO.id(), result.getId());
        assertEquals(cashDTO.status(), result.getStatus());
        assertEquals(cashDTO.number(), result.getNumber());
        assertEquals(cashDTO.createDate(), result.getCreateDate());
        assertEquals(cashDTO.updateDate(), result.getUpdateDate());
        assertEquals(cashDTO.shopNumber(), result.getShop().getNumber());

        verify(shopRepository, times(1)).findByNumber(1L);
    }

    @Test
    void testToStatusRequestDTO() {
        CashStatusRequestDTO dto = cashMapper.toStatusRequestDTO(cash);

        assertNotNull(dto);
        assertEquals(cash.getId(), dto.id());
        assertEquals(cash.getNumber(), dto.number());
        assertEquals(cash.getShop().getNumber(), dto.shopNumber());
        assertEquals(cash.getStatus(), dto.status());
    }

    @Test
    void testToDeleteRequestDTO() {
        CashDeleteRequestDTO dto = cashMapper.toDeleteRequestDTO(cash);

        assertNotNull(dto);
        assertEquals(cash.getId(), dto.id());
        assertEquals(cash.getNumber(), dto.number());
        assertEquals(cash.getShop().getNumber(), dto.shopNumber());
    }

    @Test
    void testToEntityWhenShopNotFound() {
        CashDTO cashDTO = new CashDTO(1L, STATUS.ACTIVE, 2L, createdTime, updatedTime, 99L);

        when(shopRepository.findByNumber(99L)).thenReturn(Optional.empty());

        Cash result = cashMapper.toEntity(cashDTO);

        assertNotNull(result);
        assertEquals(cashDTO.id(), result.getId());
        assertEquals(cashDTO.status(), result.getStatus());
        assertEquals(cashDTO.number(), result.getNumber());
        assertEquals(cashDTO.createDate(), result.getCreateDate());
        assertEquals(cashDTO.updateDate(), result.getUpdateDate());
        assertNull(result.getShop());

        verify(shopRepository, times(1)).findByNumber(99L);
    }
}