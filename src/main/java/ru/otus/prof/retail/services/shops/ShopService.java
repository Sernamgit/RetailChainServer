package ru.otus.prof.retail.services.shops;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.prof.retail.dto.shop.ShopDTO;
import ru.otus.prof.retail.entities.shops.Cash;
import ru.otus.prof.retail.entities.shops.Shop;
import ru.otus.prof.retail.mappers.shop.ShopMapper;
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

    @Autowired
    private ShopMapper shopMapper;

    // Создание магазина (все поля обязательны, кроме касс)
    @Transactional
    public ShopDTO createShop(ShopDTO shopDTO) {
        return shopMapper.toDTO(shopRepository.save(shopMapper.toEntity(shopDTO)));
    }

    //получение магазина по номеру без касс
    public Optional<ShopDTO> getShopByNumber(Long number) {
        return shopRepository.findByNumber(number).map(shopMapper::toDTO);
    }

    //получение магазина по номеру с кассами
    public Optional<ShopDTO> getShopByNumberWithCash(Long number) {
        return shopRepository.findByNumberWitchCash(number).map(shopMapper::toDTO);
    }

    @Transactional
    public ShopDTO updateShop(ShopDTO shopDTO) {
        return shopMapper.toDTO(shopRepository.save(shopMapper.toEntity(shopDTO)));
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
