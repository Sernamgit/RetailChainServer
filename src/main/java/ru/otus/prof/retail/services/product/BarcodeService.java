package ru.otus.prof.retail.services.product;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.prof.retail.entities.product.Barcode;
import ru.otus.prof.retail.repositories.product.BarcodeRepository;

import java.util.List;

@Service
public class BarcodeService {

    @Autowired
    private BarcodeRepository barcodeRepository;

    @Transactional
    public Barcode createBarcode(Barcode barcode){
        return barcodeRepository.save(barcode);
    }

    @Transactional
    public List<Barcode> createBarcodes(List<Barcode> barcodes){
        return barcodeRepository.saveAll(barcodes);
    }

    @Transactional
    public void deleteBarcode(String barcode){
        barcodeRepository.deleteByBarcode(barcode);
    }

    @Transactional
    public void deleteAllBarcodesByItemArticle(Long article){
        barcodeRepository.deleteAllByItemArticle(article);
    }

    public List<Barcode> getBarcodesByItemArticle(Long article){
        return barcodeRepository.findByItemArticle(article);
    }

}
