package ru.otus.prof.retail.mappers.shop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.otus.prof.retail.dto.shop.CashDTO;
import ru.otus.prof.retail.dto.shop.ShopBasicDTO;
import ru.otus.prof.retail.dto.shop.ShopDTO;
import ru.otus.prof.retail.entities.shops.Cash;
import ru.otus.prof.retail.entities.shops.Shop;
import ru.otus.prof.retail.exception.MappingException;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
            logger.warn("Попытка преобразования null Shop в базовый DTO");
            return null;
        }

        logger.debug("Преобразование Shop в базовый DTO (ID: {})", shop.getId());

        try {
            ShopBasicDTO dto = new ShopBasicDTO(
                    shop.getId(),
                    shop.getNumber(),
                    shop.getName(),
                    shop.getAddress()
            );

            logger.trace("Успешное преобразование Shop в базовый DTO: {}", dto);
            return dto;
        } catch (Exception e) {
            String errorMsg = String.format("Ошибка преобразования Shop в базовый DTO (ID: %s)", shop.getId());
            logger.error(errorMsg, e);
            throw new MappingException(errorMsg, e);
        }
    }

    public ShopDTO toDTO(Shop shop) {
        if (shop == null) {
            logger.warn("Попытка преобразования null Shop в DTO");
            return null;
        }

        logger.debug("Преобразование Shop в DTO (ID: {})", shop.getId());

        try {
            List<CashDTO> cashDTOList = Collections.emptyList();

            if (shop.getCashList() != null) {
                cashDTOList = shop.getCashList().stream()
                        .map(cash -> {
                            try {
                                return cashMapper.toDTO(cash);
                            } catch (Exception e) {
                                logger.warn("Ошибка преобразования кассы (ID: {}) в DTO", cash.getId(), e);
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }

            ShopDTO dto = new ShopDTO(
                    shop.getId(),
                    shop.getNumber(),
                    shop.getName(),
                    shop.getAddress(),
                    cashDTOList
            );

            logger.trace("Успешное преобразование Shop в DTO: {}", dto);
            return dto;
        } catch (Exception e) {
            String errorMsg = String.format("Ошибка преобразования Shop в DTO (ID: %s)", shop.getId());
            logger.error(errorMsg, e);
            throw new MappingException(errorMsg, e);
        }
    }

    public Shop toEntity(ShopDTO shopDTO) {
        if (shopDTO == null) {
            logger.warn("Попытка преобразования null ShopDTO в сущность");
            return null;
        }

        logger.debug("Преобразование ShopDTO в сущность (ID: {})", shopDTO.id());

        try {
            Shop shop = new Shop();
            shop.setId(shopDTO.id());
            shop.setNumber(shopDTO.number());
            shop.setName(shopDTO.name());
            shop.setAddress(shopDTO.address());

            List<Cash> cashList = Collections.emptyList();
            if (shopDTO.cashList() != null) {
                cashList = shopDTO.cashList().stream()
                        .map(cashDTO -> {
                            try {
                                return cashMapper.toEntity(cashDTO);
                            } catch (Exception e) {
                                logger.warn("Ошибка преобразования CashDTO (ID: {}) в сущность", cashDTO.id(), e);
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }

            shop.setCashList(cashList);

            logger.trace("Успешное преобразование ShopDTO в сущность: {}", shop);
            return shop;
        } catch (Exception e) {
            String errorMsg = String.format("Ошибка преобразования ShopDTO в сущность (ID: %s)", shopDTO.id());
            logger.error(errorMsg, e);
            throw new MappingException(errorMsg, e);
        }
    }
}