package ru.otus.prof.retail.services.product;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.prof.retail.dto.product.BarcodeDTO;
import ru.otus.prof.retail.entities.product.Barcode;
import ru.otus.prof.retail.mappers.product.BarcodeMapper;
import ru.otus.prof.retail.repositories.product.BarcodeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BarcodeService {

    @Autowired
    private BarcodeRepository barcodeRepository;

    @Autowired
    private BarcodeMapper barcodeMapper;

    @Transactional
    public BarcodeDTO createBarcode(BarcodeDTO barcodeDTO){
        return barcodeMapper.toDTO(barcodeRepository.save(barcodeMapper.toEntity(barcodeDTO)));
    }

    @Transactional
    public List<BarcodeDTO> createBarcodes(List<BarcodeDTO> barcodeDTOs){
        List<Barcode> barcodes = barcodeDTOs.stream()
                .map(barcodeMapper::toEntity)
                .collect(Collectors.toList());
        List<Barcode> savedBarcodes = barcodeRepository.saveAll(barcodes);
        return savedBarcodes.stream()
                .map(barcodeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteBarcode(String barcode){
        barcodeRepository.deleteByBarcode(barcode);
    }

    @Transactional
    public void deleteAllBarcodesByItemArticle(Long article){
        barcodeRepository.deleteAllByItemArticle(article);
    }

    public List<BarcodeDTO> getBarcodesByItemArticle(Long article){
        List<Barcode> barcodes = barcodeRepository.findByItemArticle(article);
        return barcodes.stream()
                .map(barcodeMapper::toDTO)
                .collect(Collectors.toList());
    }

}
