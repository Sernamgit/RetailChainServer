package ru.otus.prof.retail.services.shops;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.prof.retail.dto.shop.ShopBasicDTO;
import ru.otus.prof.retail.dto.shop.ShopDTO;
import ru.otus.prof.retail.entities.shops.Cash;
import ru.otus.prof.retail.entities.shops.Shop;
import ru.otus.prof.retail.mappers.shop.ShopMapper;
import ru.otus.prof.retail.repositories.shops.CashRepository;
import ru.otus.prof.retail.repositories.shops.ShopRepository;
import ru.otus.prof.retail.exception.shop.ShopNotFoundException;
import ru.otus.prof.retail.exception.shop.ShopNumberAlreadyExistsException;
import ru.otus.prof.retail.exception.shop.ShopValidationException;

import java.util.List;
import java.util.Optional;

@Service
public class ShopService {

    private static final Logger logger = LoggerFactory.getLogger(ShopService.class);

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private CashRepository cashRepository;

    @Autowired
    private ShopMapper shopMapper;

    @Transactional
    public ShopDTO createShop(ShopDTO shopDTO) {
        logger.debug("Создание нового магазина: {}", shopDTO);

        if (shopRepository.findByNumber(shopDTO.number()).isPresent()) {
            throw new ShopNumberAlreadyExistsException("Магазин с номером " + shopDTO.number() + " уже существует");
        }
        Shop savedShop = shopRepository.save(shopMapper.toEntity(shopDTO));
        logger.info("Создан новый магазин с ID: {}", savedShop.getId());
        return shopMapper.toDTO(savedShop);
    }

    public Optional<ShopBasicDTO> getShopById(Long id) {
        logger.debug("Получение основной информации о магазине по ID: {}", id);
        return shopRepository.findById(id)
                .map(shop -> {
                    logger.debug("Найден магазин с ID: {}", id);
                    return shopMapper.toBasicDTO(shop);
                });
    }

    public Optional<ShopDTO> getShopByIdWithCash(Long id) {
        logger.debug("Получение информации о магазине с кассами по ID: {}", id);
        return shopRepository.findByIdWithCash(id)
                .map(shop -> {
                    logger.debug("Найден магазин с информацией о кассах для ID: {}", id);
                    return shopMapper.toDTO(shop);
                });
    }

    public Optional<ShopBasicDTO> getShopByNumber(Long number) {
        logger.debug("Получение основной информации о магазине по номеру: {}", number);
        return shopRepository.findByNumber(number)
                .map(shop -> {
                    logger.debug("Найден магазин с номером: {}", number);
                    return shopMapper.toBasicDTO(shop);
                });
    }

    public Optional<ShopDTO> getShopByNumberWithCash(Long number) {
        logger.debug("Получение информации о магазине с кассами по номеру: {}", number);
        return shopRepository.findByNumberWitchCash(number)
                .map(shop -> {
                    logger.debug("Найден магазин с информацией о кассах для номера: {}", number);
                    return shopMapper.toDTO(shop);
                });
    }

    @Transactional
    public ShopDTO updateShop(ShopDTO shopDTO) {
        logger.debug("Обновление магазина: {}", shopDTO);

        if (shopDTO.id() == null && shopDTO.number() == null) {
            throw new ShopValidationException("Для обновления необходимо указать ID или номер магазина");
        }

        Shop shop = shopDTO.id() != null
                ? shopRepository.findById(shopDTO.id())
                .orElseThrow(() -> new ShopNotFoundException("Магазин с ID: " + shopDTO.id() + " не найден"))
                : shopRepository.findByNumber(shopDTO.number())
                .orElseThrow(() -> new ShopNotFoundException("Магазин с номером: " + shopDTO.number() + " не найден"));

        if (shopDTO.name() != null && !shopDTO.name().equals(shop.getName())) {
            logger.debug("Обновление названия магазина с '{}' на '{}'", shop.getName(), shopDTO.name());
            shop.setName(shopDTO.name());
        }

        if (shopDTO.address() != null && !shopDTO.address().equals(shop.getAddress())) {
            logger.debug("Обновление адреса магазина с '{}' на '{}'", shop.getAddress(), shopDTO.address());
            shop.setAddress(shopDTO.address());
        }

        Shop updatedShop = shopRepository.save(shop);
        logger.info("Магазин успешно обновлен с ID: {}", updatedShop.getId());
        return shopMapper.toDTO(updatedShop);
    }

    @Transactional
    public void deleteShop(Long id) {
        logger.info("Удаление магазина с ID: {}", id);

        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Магазин с ID: {} не найден для удаления", id);
                    return new ShopNotFoundException("Магазин с ID: " + id + " не найден");
                });

        logger.debug("Найден магазин для удаления: {}", shop);

        List<Cash> cashList = cashRepository.findByShopNumber(shop.getNumber());
        if (!cashList.isEmpty()) {
            logger.debug("Удаление {} касс для магазина с ID: {}", cashList.size(), id);
            cashRepository.deleteAll(cashList);
        }

        shopRepository.deleteById(id);
        logger.info("Магазин успешно удален с ID: {}", id);
    }
}