package ru.otus.prof.retail.services.product;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.prof.retail.dto.product.PriceDTO;
import ru.otus.prof.retail.entities.product.Price;
import ru.otus.prof.retail.mappers.product.PriceMapper;
import ru.otus.prof.retail.repositories.product.PriceRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PriceService {

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private PriceMapper priceMapper;

    @Transactional
    public PriceDTO createPrice(PriceDTO  priceDTO){
        return priceMapper.toDTO(priceRepository.save(priceMapper.toEntity(priceDTO)));
    }

    @Transactional
    public List<PriceDTO> createPrices(List<PriceDTO> priceDTOs){
        List<Price> prices = priceDTOs.stream()
                .map(priceMapper::toEntity)
                .collect(Collectors.toList());
        List<Price> savedPrices = priceRepository.saveAll(prices);
        return savedPrices.stream()
                .map(priceMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PriceDTO updatePrice(PriceDTO priceDTO){
        return priceMapper.toDTO(priceRepository.save(priceMapper.toEntity(priceDTO)));
    }

    @Transactional
    public void deletePrice(Long id){
        priceRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllPricesByItemArticle(Long article){
        priceRepository.deleteAllByItemArticle(article);
    }

    public List<PriceDTO> getPricesByItemArticle(Long article){
        List<Price> prices = priceRepository.findByItem_Article(article);
        return prices.stream()
                .map(priceMapper::toDTO)
                .collect(Collectors.toList());
    }
}
