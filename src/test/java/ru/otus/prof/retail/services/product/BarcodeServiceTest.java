package ru.otus.prof.retail.services.product;


import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.prof.retail.dto.product.BarcodeDTO;
import ru.otus.prof.retail.entities.product.Item;
import ru.otus.prof.retail.mappers.product.BarcodeMapper;
import ru.otus.prof.retail.repositories.product.BarcodeRepository;
import ru.otus.prof.retail.repositories.product.ItemRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class BarcodeServiceTest {

    @Autowired
    private BarcodeRepository barcodeRepository;

    @Autowired
    private BarcodeService barcodeService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    @Autowired
    private BarcodeMapper barcodeMapper;

    private Item item;

    @BeforeEach
    void setUp() {
        Optional<Item> itemOpt = itemRepository.findById(1001L);
        assertTrue(itemOpt.isPresent());
        item = itemOpt.get();
    }

    @Test
    @Transactional
    @Rollback
    void testCreateBarcode() {
        BarcodeDTO newBarcodeDTO = new BarcodeDTO("5555555555555", item.getArticle());

        BarcodeDTO savedBarcodeDTO = barcodeService.createBarcode(newBarcodeDTO);

        assertThat(savedBarcodeDTO).isNotNull();
        assertThat(savedBarcodeDTO.barcode()).isEqualTo("5555555555555");
        assertThat(savedBarcodeDTO.article()).isEqualTo(1001L);
    }

    @Test
    @Transactional
    @Rollback
    void testCreateBarcodes() {
        BarcodeDTO barcodeDTO1 = new BarcodeDTO("3333333333333", item.getArticle());
        BarcodeDTO barcodeDTO2 = new BarcodeDTO("4444444444444", item.getArticle());

        List<BarcodeDTO> savedBarcodeDTOs = barcodeService.createBarcodes(List.of(barcodeDTO1, barcodeDTO2));

        assertThat(savedBarcodeDTOs).hasSize(2);
        assertThat(savedBarcodeDTOs.get(0).barcode()).isEqualTo("3333333333333");
        assertThat(savedBarcodeDTOs.get(1).barcode()).isEqualTo("4444444444444");
    }

    @Test
    @Transactional
    @Rollback
    void testDeleteBarcode() {
        BarcodeDTO newBarcodeDTO = new BarcodeDTO("6666666666666", item.getArticle());
        barcodeService.createBarcode(newBarcodeDTO);

        List<BarcodeDTO> barcodesBeforeDeletion = barcodeService.getBarcodesByItemArticle(1001L);
        assertThat(barcodesBeforeDeletion).hasSize(3);

        barcodeService.deleteBarcode("6666666666666");

        List<BarcodeDTO> barcodesAfterDeletion = barcodeService.getBarcodesByItemArticle(1001L);
        assertThat(barcodesAfterDeletion).hasSize(2);
        assertThat(barcodesAfterDeletion).noneMatch(barcode -> barcode.barcode().equals("6666666666666"));
    }

    @Test
    @Transactional
    @Rollback
    void testDeleteAllBarcodesByItemArticle() {
        barcodeService.deleteAllBarcodesByItemArticle(1001L);

        List<BarcodeDTO> barcodes = barcodeService.getBarcodesByItemArticle(1001L);
        assertThat(barcodes).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void testGetBarcodesByItemArticle() {
        List<BarcodeDTO> barcodes = barcodeService.getBarcodesByItemArticle(1001L);

        assertThat(barcodes).hasSize(2);
        assertThat(barcodes.get(0).barcode()).isEqualTo("1001111111111");
        assertThat(barcodes.get(1).barcode()).isEqualTo("1001111111112");
    }

}
