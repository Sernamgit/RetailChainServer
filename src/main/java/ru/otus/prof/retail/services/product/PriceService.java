package ru.otus.prof.retail.services.product;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.prof.retail.entities.product.Price;
import ru.otus.prof.retail.repositories.product.PriceRepository;

import java.util.List;

@Service
public class PriceService {

    @Autowired
    private PriceRepository priceRepository;

    @Transactional
    public Price createPrice(Price price){
        return priceRepository.save(price);
    }

    @Transactional
    public List<Price> createPrices(List<Price> prices){
        return priceRepository.saveAll(prices);
    }

    @Transactional
    public Price updatePrice(Price price){
        return priceRepository.save(price);
    }

    @Transactional
    public void deletePrice(Long id){
        priceRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllPricesByItemArticle(Long article){
        priceRepository.deleteAllByItemArticle(article);
    }

    public List<Price> getPricesByItemArticle(Long article){
        return priceRepository.findByItem_Article(article);
    }


}
