package ru.otus.prof.retail.services.product;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.prof.retail.dto.product.BarcodeDTO;
import ru.otus.prof.retail.exception.BarcodeAlreadyExistsException;
import ru.otus.prof.retail.exception.BarcodeNotFoundException;
import ru.otus.prof.retail.repositories.product.BarcodeRepository;
import ru.otus.prof.retail.repositories.product.ItemRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BarcodeServiceTest {

    @Autowired
    private BarcodeRepository barcodeRepository;

    @Autowired
    private BarcodeService barcodeService;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    @Rollback
    void testCreateBarcode() {
        BarcodeDTO newBarcodeDTO = new BarcodeDTO("5555555555555", 1001L);

        BarcodeDTO savedBarcodeDTO = barcodeService.createBarcode(newBarcodeDTO);

        assertThat(savedBarcodeDTO).isNotNull();
        assertThat(savedBarcodeDTO.barcode()).isEqualTo("5555555555555");
        assertThat(savedBarcodeDTO.article()).isEqualTo(1001L);
        assertTrue(barcodeRepository.existsById("5555555555555"));
    }

    @Test
    @Rollback
    void testCreateBarcode_shouldThrowWhenBarcodeExists() {
        BarcodeDTO existingBarcodeDTO = new BarcodeDTO("1001111111111", 1001L);

        assertThrows(BarcodeAlreadyExistsException.class, () -> barcodeService.createBarcode(existingBarcodeDTO));
    }

    @Test
    @Rollback
    void testCreateBarcodes() {
        BarcodeDTO barcodeDTO1 = new BarcodeDTO("3333333333333", 1001L);
        BarcodeDTO barcodeDTO2 = new BarcodeDTO("4444444444444", 1001L);

        List<BarcodeDTO> savedBarcodeDTOs = barcodeService.createBarcodes(List.of(barcodeDTO1, barcodeDTO2));

        assertThat(savedBarcodeDTOs).hasSize(2);
        assertThat(savedBarcodeDTOs)
                .extracting(BarcodeDTO::barcode)
                .containsExactlyInAnyOrder("3333333333333", "4444444444444");
        assertThat(savedBarcodeDTOs)
                .allSatisfy(dto -> assertThat(dto.article()).isEqualTo(1001L));
        assertTrue(barcodeRepository.existsById("3333333333333"));
        assertTrue(barcodeRepository.existsById("4444444444444"));
    }

    @Test
    @Rollback
    void testCreateBarcodes_shouldThrowWhenAnyBarcodeExists() {
        BarcodeDTO barcodeDTO1 = new BarcodeDTO("3333333333333", 1001L);
        BarcodeDTO barcodeDTO2 = new BarcodeDTO("1001111111111", 1001L);

        assertThrows(BarcodeAlreadyExistsException.class, () -> barcodeService.createBarcodes(List.of(barcodeDTO1, barcodeDTO2)));
    }

    @Test
    @Rollback
    void testDeleteBarcode() {
        barcodeService.createBarcode(new BarcodeDTO("6666666666666", 1001L));

        assertTrue(barcodeRepository.existsById("6666666666666"));
        barcodeService.deleteBarcode("6666666666666");
        assertFalse(barcodeRepository.existsById("6666666666666"));
    }

    @Test
    @Rollback
    void testDeleteBarcode_shouldThrowWhenBarcodeNotFound() {
        assertThrows(BarcodeNotFoundException.class, () -> barcodeService.deleteBarcode("9999999999999"));
    }

    @Test
    @Rollback
    void testDeleteAllBarcodesByItemArticle() {
        List<BarcodeDTO> barcodesBefore = barcodeService.getBarcodesByItemArticle(1001L);
        assertThat(barcodesBefore).isNotEmpty();

        barcodeService.deleteAllBarcodesByItemArticle(1001L);

        List<BarcodeDTO> barcodesAfter = barcodeService.getBarcodesByItemArticle(1001L);
        assertThat(barcodesAfter).isEmpty();
    }

    @Test
    @Rollback
    void testDeleteAllBarcodesByItemArticle_withNonExistentArticle() {
        assertDoesNotThrow(() -> barcodeService.deleteAllBarcodesByItemArticle(9999L));
    }

    @Test
    @Rollback
    void testGetBarcodesByItemArticle() {
        List<BarcodeDTO> barcodes = barcodeService.getBarcodesByItemArticle(1001L);

        assertThat(barcodes).hasSize(2);
        assertThat(barcodes)
                .extracting(BarcodeDTO::barcode)
                .contains("1001111111111", "1001111111112");
    }

    @Test
    @Rollback
    void testGetBarcodesByItemArticle_withNonExistentArticle() {
        List<BarcodeDTO> barcodes = barcodeService.getBarcodesByItemArticle(9999L);
        assertThat(barcodes).isEmpty();
    }

    @Test
    @Rollback
    void testGetBarcode() {
        BarcodeDTO barcodeDTO = barcodeService.getBarcode("1001111111111");

        assertThat(barcodeDTO).isNotNull();
        assertThat(barcodeDTO.barcode()).isEqualTo("1001111111111");
        assertThat(barcodeDTO.article()).isEqualTo(1001L);
    }

    @Test
    @Rollback
    void testGetBarcode_withNonExistentBarcode() {
        BarcodeDTO barcodeDTO = barcodeService.getBarcode("9999999999999");
        assertThat(barcodeDTO).isNull();
    }
}