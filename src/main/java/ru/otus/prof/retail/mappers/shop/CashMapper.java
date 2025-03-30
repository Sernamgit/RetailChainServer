package ru.otus.prof.retail.mappers.shop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.otus.prof.retail.dto.shop.CashDTO;
import ru.otus.prof.retail.dto.shop.CashDeleteRequestDTO;
import ru.otus.prof.retail.dto.shop.CashStatusRequestDTO;
import ru.otus.prof.retail.entities.shops.Cash;
import ru.otus.prof.retail.repositories.shops.ShopRepository;

@Component
public class CashMapper {

    private static final Logger logger = LoggerFactory.getLogger(CashMapper.class);
    private final ShopRepository shopRepository;

    @Autowired
    public CashMapper(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    public CashDTO toDTO(Cash cash) {
        logger.debug("Преобразование сущности Cash в DTO для кассы с ID: {}", cash.getId());
        CashDTO dto = new CashDTO(
                cash.getId(),
                cash.getStatus(),
                cash.getNumber(),
                cash.getCreateDate(),
                cash.getUpdateDate(),
                cash.getShop().getNumber()
        );
        logger.trace("Результат преобразования в DTO: {}", dto);
        return dto;
    }

    public CashStatusRequestDTO toStatusRequestDTO(Cash cash) {
        logger.debug("Создание запроса на изменение статуса для кассы с ID: {}", cash.getId());
        CashStatusRequestDTO dto = new CashStatusRequestDTO(
                cash.getId(),
                cash.getNumber(),
                cash.getShop().getNumber(),
                cash.getStatus()
        );
        logger.trace("Создан запрос на изменение статуса: {}", dto);
        return dto;
    }

    public CashDeleteRequestDTO toDeleteRequestDTO(Cash cash) {
        logger.debug("Создание запроса на удаление для кассы с ID: {}", cash.getId());
        CashDeleteRequestDTO dto = new CashDeleteRequestDTO(
                cash.getId(),
                cash.getNumber(),
                cash.getShop().getNumber()
        );
        logger.trace("Создан запрос на удаление: {}", dto);
        return dto;
    }

    public Cash toEntity(CashDTO cashDTO) {
        logger.debug("Преобразование DTO в сущность Cash для кассы с ID: {}", cashDTO.id());
        Cash cash = new Cash();
        cash.setId(cashDTO.id());
        cash.setStatus(cashDTO.status());
        cash.setNumber(cashDTO.number());
        cash.setCreateDate(cashDTO.createDate());
        cash.setUpdateDate(cashDTO.updateDate());

        shopRepository.findByNumber(cashDTO.shopNumber())
                .ifPresentOrElse(
                        shop -> {
                            cash.setShop(shop);
                            logger.debug("Найден магазин с номером {} для кассы", cashDTO.shopNumber());
                        },
                        () -> logger.warn("Магазин с номером {} не найден для кассы", cashDTO.shopNumber())
                );

        logger.trace("Результат преобразования в сущность: {}", cash);
        return cash;
    }
}