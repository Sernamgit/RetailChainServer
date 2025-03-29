//package ru.otus.prof.retail.services.shop;
//
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.context.ActiveProfiles;
//import ru.otus.prof.retail.STATUS;
//import ru.otus.prof.retail.dto.shop.CashDTO;
//import ru.otus.prof.retail.dto.shop.ShopDTO;
//import ru.otus.prof.retail.services.shops.CashService;
//import ru.otus.prof.retail.services.shops.ShopService;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//
//@SpringBootTest
//@ActiveProfiles("test")
//public class CashServiceTest {
//
//    @Autowired
//    private CashService cashService;
//
//    @Autowired
//    private ShopService shopService;
//
//
//    @Test
//    @Transactional
//    void testCreateCash() {
//        Optional<ShopDTO> shopOptDTO = shopService.getShopByNumber(1L);
//        assertTrue(shopOptDTO.isPresent());
//        ShopDTO shopDTO = shopOptDTO.get();
//
//        CashDTO cashDTO = new CashDTO(null, STATUS.ACTIVE, 3L, LocalDateTime.now(), LocalDateTime.now(), shopDTO.number());
//
//        CashDTO savedCashDTO = cashService.createCash(cashDTO);
//
//        assertNotNull(savedCashDTO.id());
//        assertEquals(3L, savedCashDTO.number());
//        assertEquals(shopDTO.number(), savedCashDTO.shopNumber());
//        assertEquals(STATUS.ACTIVE, savedCashDTO.status());
//    }
//
//    @Test
//    void testGetCashById() {
//        Optional<CashDTO> cashOptDTO = cashService.getCashById(1L);
//
//        assertTrue(cashOptDTO.isPresent());
//        CashDTO cashDTO = cashOptDTO.get();
//        assertEquals(1L, cashDTO.number());
//        assertEquals(1L, cashDTO.shopNumber());
//        assertEquals(STATUS.ACTIVE, cashDTO.status());
//    }
//
//    @Test
//    void testGetCashByNumberAndShopNumber() {
//        Optional<CashDTO> cashOptDTO = cashService.getCashByNumberAndShopNumber(1L, 1L);
//
//        assertTrue(cashOptDTO.isPresent());
//        CashDTO cashDTO = cashOptDTO.get();
//        assertEquals(1L, cashDTO.number());
//        assertEquals(1L, cashDTO.shopNumber());
//        assertEquals(STATUS.ACTIVE, cashDTO.status());
//    }
//
//    @Test
//    void testGetCashByShopNumber() {
//        List<CashDTO> cashDTOList = cashService.getCashByShopNumber(1L);
//
//        assertFalse(cashDTOList.isEmpty());
//        assertEquals(1, cashDTOList.size());
//        CashDTO cashDTO = cashDTOList.get(0);
//        assertEquals(1L, cashDTO.number());
//        assertEquals(1L, cashDTO.shopNumber());
//        assertEquals(STATUS.ACTIVE, cashDTO.status());
//    }
//
//    @Test
//    @Transactional
//    @Rollback
//    void testUpdateCash() {
//        Optional<CashDTO> cashOptDTO = cashService.getCashById(1L);
//        assertTrue(cashOptDTO.isPresent());
//
//        CashDTO cashDTO = cashOptDTO.get();
//        CashDTO updatedCashDTO = new CashDTO(cashDTO.id(), STATUS.DELETED, cashDTO.number(), cashDTO.createDate(), cashDTO.updateDate(), cashDTO.shopNumber());
//
//        CashDTO resultCashDTO = cashService.updateCash(updatedCashDTO);
//
//        assertEquals(STATUS.DELETED, resultCashDTO.status());
//    }
//
//    @Test
//    @Transactional
//    @Rollback
//    void testUpdateCashStatus() {
//        cashService.updateCashStatus(1L, STATUS.DELETED);
//
//        Optional<CashDTO> cashOptDTO = cashService.getCashById(1L);
//        assertTrue(cashOptDTO.isPresent());
//        assertEquals(STATUS.DELETED, cashOptDTO.get().status());
//    }
//}
