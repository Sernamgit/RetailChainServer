package ru.otus.prof.retail.services.product;


import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.prof.retail.entities.product.Barcode;
import ru.otus.prof.retail.entities.product.Item;
import ru.otus.prof.retail.repositories.product.BarcodeRepository;

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
    private ItemService itemService;

    private Item item;

    @BeforeEach
    void setUp(){
        Optional<Item> itemOpt = itemService.getItem(1001L);
        assertTrue(itemOpt.isPresent());
        item = itemOpt.get();
    }

    @Test
    @Transactional
    @Rollback
    void testCreateBarcode() {
        Barcode newBarcode = new Barcode();
        newBarcode.setBarcode("5555555555555");
        newBarcode.setItem(item);

        Barcode savedBarcode = barcodeService.createBarcode(newBarcode);

        assertThat(savedBarcode).isNotNull();
        assertThat(savedBarcode.getBarcode()).isEqualTo("5555555555555");
        assertThat(savedBarcode.getItem().getArticle()).isEqualTo(1001L);
    }

    @Test
    @Transactional
    @Rollback
    void testCreateBarcodes() {
        Barcode barcode1 = new Barcode();
        barcode1.setBarcode("3333333333333");
        barcode1.setItem(item);

        Barcode barcode2 = new Barcode();
        barcode2.setBarcode("4444444444444");
        barcode2.setItem(item);

        List<Barcode> savedBarcodes = barcodeService.createBarcodes(List.of(barcode1, barcode2));

        assertThat(savedBarcodes).hasSize(2);
        assertThat(savedBarcodes.get(0).getBarcode()).isEqualTo("3333333333333");
        assertThat(savedBarcodes.get(1).getBarcode()).isEqualTo("4444444444444");
    }

    @Test
    @Transactional
    @Rollback
    void testDeleteBarcode() {
        Barcode newBarcode = new Barcode();
        newBarcode.setBarcode("6666666666666");
        newBarcode.setItem(item);
        barcodeService.createBarcode(newBarcode);

        List<Barcode> barcodesBeforeDeletion = barcodeService.getBarcodesByItemArticle(1001L);
        assertThat(barcodesBeforeDeletion).hasSize(3);

        barcodeService.deleteBarcode("6666666666666");

        List<Barcode> barcodesAfterDeletion = barcodeService.getBarcodesByItemArticle(1001L);
        assertThat(barcodesAfterDeletion).hasSize(2);
        assertThat(barcodesAfterDeletion).noneMatch(barcode -> barcode.getBarcode().equals("6666666666666"));
    }

    @Test
    @Transactional
    @Rollback
    void testDeleteAllBarcodesByItemArticle() {
        barcodeService.deleteAllBarcodesByItemArticle(1001L);

        List<Barcode> barcodes = barcodeService.getBarcodesByItemArticle(1001L);
        assertThat(barcodes).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void testGetBarcodesByItemArticle() {
        List<Barcode> barcodes = barcodeService.getBarcodesByItemArticle(1001L);

        assertThat(barcodes).hasSize(2);
        assertThat(barcodes.get(0).getBarcode()).isEqualTo("1001111111111");
        assertThat(barcodes.get(1).getBarcode()).isEqualTo("1001111111112");
    }

}
