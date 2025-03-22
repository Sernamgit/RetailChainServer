package ru.otus.prof.retail.services.shops;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.prof.retail.entities.shops.Cash;
import ru.otus.prof.retail.entities.shops.Shop;
import ru.otus.prof.retail.repositories.shops.CashRepository;
import ru.otus.prof.retail.repositories.shops.ShopRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ShopService {

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private CashRepository cashRepository;

    // Создание магазина (все поля обязательны, кроме касс)
    @Transactional
    public Shop createShop(Shop shop) {
        return shopRepository.save(shop);
    }

    //получение магазина по номеру без касс
    public Optional<Shop> getShopByNumber(Long number) {
        return shopRepository.findByNumber(number);
    }

    //получение магазина по номеру с кассами
    public Optional<Shop> getShopByNumberWithCash(Long number) {
        return shopRepository.findByNumberWitchCash(number);
    }

    //Обновление магазина без касс
    @Transactional
    public Shop updateShop(Shop shop) {
        return shopRepository.save(shop);
    }

    @Transactional
    public void deleteShop(Long id) {
        Optional<Shop> shopOpt = shopRepository.findById(id);
        if (shopOpt.isPresent()) {
            Shop shop = shopOpt.get();
            List<Cash> cashList = cashRepository.findByShopNumber(shop.getNumber());
            if (!cashList.isEmpty()) {
                shopRepository.deleteCashByShopNumber(shop.getNumber());
            }
            shopRepository.deleteShopById(id);
        }
    }
}
