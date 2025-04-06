package ru.otus.prof.retail.mappers.shop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.otus.prof.retail.dto.shop.CashDTO;
import ru.otus.prof.retail.dto.shop.CashDeleteRequestDTO;
import ru.otus.prof.retail.dto.shop.CashStatusRequestDTO;
import ru.otus.prof.retail.entities.shops.Cash;
import ru.otus.prof.retail.entities.shops.Shop;
import ru.otus.prof.retail.exception.MappingException;
import ru.otus.prof.retail.exception.shop.ShopNotFoundException;
import ru.otus.prof.retail.repositories.shops.ShopRepository;


@Component
public class CashMapper {
    private static final Logger logger = LoggerFactory.getLogger(CashMapper.class);
    private final ShopRepository shopRepository;

    public CashMapper(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    public CashDTO toDTO(Cash cash) {
        if (cash == null) {
            logger.warn("Попытка преобразования null Cash в DTO");
            return null;
        }

        logger.debug("Преобразование Cash в DTO (ID: {})", cash.getId());

        try {
            if (cash.getShop() == null) {
                throw new MappingException("Касса должна быть привязана к магазину");
            }

            CashDTO dto = new CashDTO(
                    cash.getId(),
                    cash.getStatus(),
                    cash.getNumber(),
                    cash.getCreateDate(),
                    cash.getUpdateDate(),
                    cash.getShop().getNumber()
            );

            logger.trace("Успешное преобразование Cash в DTO: {}", dto);
            return dto;
        } catch (Exception e) {
            String errorMsg = String.format("Ошибка преобразования Cash в DTO (ID: %s)", cash.getId());
            logger.error(errorMsg, e);
            throw new MappingException(errorMsg, e);
        }
    }

    public CashStatusRequestDTO toStatusRequestDTO(Cash cash) {
        if (cash == null) {
            logger.warn("Попытка создания запроса на изменение статуса для null Cash");
            return null;
        }

        logger.debug("Создание запроса на изменение статуса для кассы (ID: {})", cash.getId());

        try {
            if (cash.getShop() == null) {
                throw new MappingException("Касса должна быть привязана к магазину");
            }

            CashStatusRequestDTO dto = new CashStatusRequestDTO(
                    cash.getId(),
                    cash.getNumber(),
                    cash.getShop().getNumber(),
                    cash.getStatus()
            );

            logger.trace("Успешное создание запроса на изменение статуса: {}", dto);
            return dto;
        } catch (Exception e) {
            String errorMsg = String.format("Ошибка создания запроса на изменение статуса (ID: %s)", cash.getId());
            logger.error(errorMsg, e);
            throw new MappingException(errorMsg, e);
        }
    }

    public CashDeleteRequestDTO toDeleteRequestDTO(Cash cash) {
        if (cash == null) {
            logger.warn("Попытка создания запроса на удаление для null Cash");
            return null;
        }

        logger.debug("Создание запроса на удаление кассы (ID: {})", cash.getId());

        try {
            if (cash.getShop() == null) {
                throw new MappingException("Касса должна быть привязана к магазину");
            }

            CashDeleteRequestDTO dto = new CashDeleteRequestDTO(
                    cash.getId(),
                    cash.getNumber(),
                    cash.getShop().getNumber()
            );

            logger.trace("Успешное создание запроса на удаление: {}", dto);
            return dto;
        } catch (Exception e) {
            String errorMsg = String.format("Ошибка создания запроса на удаление (ID: %s)", cash.getId());
            logger.error(errorMsg, e);
            throw new MappingException(errorMsg, e);
        }
    }

    public Cash toEntity(CashDTO cashDTO) {
        if (cashDTO == null) {
            logger.warn("Попытка преобразования null CashDTO в сущность");
            return null;
        }

        logger.debug("Преобразование CashDTO в сущность (ID: {})", cashDTO.id());

        try {
            Cash cash = new Cash();
            cash.setId(cashDTO.id());
            cash.setStatus(cashDTO.status());
            cash.setNumber(cashDTO.number());
            cash.setCreateDate(cashDTO.createDate());
            cash.setUpdateDate(cashDTO.updateDate());

            Shop shop = shopRepository.findByNumber(cashDTO.shopNumber())
                    .orElseThrow(() -> new ShopNotFoundException("Магазин с номером " + cashDTO.shopNumber() + " не найден"));
            cash.setShop(shop);

            logger.trace("Успешное преобразование CashDTO в сущность: {}", cash);
            return cash;
        } catch (ShopNotFoundException e) {
            throw e;
        } catch (Exception e) {
            String errorMsg = String.format("Ошибка преобразования CashDTO в сущность (ID: %s)", cashDTO.id());
            logger.error(errorMsg, e);
            throw new MappingException(errorMsg, e);
        }
    }
}