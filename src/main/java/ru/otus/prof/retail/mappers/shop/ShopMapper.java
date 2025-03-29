package ru.otus.prof.retail.mappers.shop;

import org.hibernate.LazyInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.otus.prof.retail.dto.shop.CashDTO;
import ru.otus.prof.retail.dto.shop.ShopBasicDTO;
import ru.otus.prof.retail.dto.shop.ShopDTO;
import ru.otus.prof.retail.entities.shops.Cash;
import ru.otus.prof.retail.entities.shops.Shop;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ShopMapper {

    private static final Logger logger = LoggerFactory.getLogger(ShopMapper.class);
    private final CashMapper cashMapper;

    public ShopMapper(CashMapper cashMapper) {
        this.cashMapper = cashMapper;
    }

    public ShopBasicDTO toBasicDTO(Shop shop) {
        if (shop == null) {
            logger.debug("Получен null при преобразовании магазина в базовый DTO");
            return null;
        }
        logger.trace("Преобразование магазина в базовый DTO: {}", shop);
        return new ShopBasicDTO(
                shop.getId(),
                shop.getNumber(),
                shop.getName(),
                shop.getAddress()
        );
    }

    public ShopDTO toDTO(Shop shop) {
        if (shop == null) {
            logger.debug("Получен null при преобразовании магазина в DTO");
            return null;
        }

        logger.trace("Преобразование магазина в DTO: {}", shop);
        List<CashDTO> cashDTOList = null;
        try {
            if (shop.getCashList() != null) {
                cashDTOList = shop.getCashList().stream()
                        .map(cashMapper::toDTO)
                        .collect(Collectors.toList());
            }
        } catch (LazyInitializationException e) {
            logger.debug("Ошибка LazyInitializationException при получении списка касс, возвращаем пустой список");
            cashDTOList = Collections.emptyList();
        }
        return new ShopDTO(
                shop.getId(),
                shop.getNumber(),
                shop.getName(),
                shop.getAddress(),
                cashDTOList
        );
    }

    public Shop toEntity(ShopDTO shopDTO) {
        logger.trace("Преобразование DTO магазина в сущность: {}", shopDTO);
        Shop shop = new Shop();
        shop.setId(shopDTO.id());
        shop.setNumber(shopDTO.number());
        shop.setName(shopDTO.name());
        shop.setAddress(shopDTO.address());

        List<Cash> cashList = Collections.emptyList();
        if (shopDTO.cashList() != null) {
            cashList = shopDTO.cashList().stream()
                    .map(cashMapper::toEntity)
                    .collect(Collectors.toList());
        }

        shop.setCashList(cashList);
        return shop;
    }
}