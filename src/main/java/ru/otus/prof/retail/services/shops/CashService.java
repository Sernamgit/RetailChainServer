// CashService.java
package ru.otus.prof.retail.services.shops;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.prof.retail.STATUS;
import ru.otus.prof.retail.dto.shop.CashDTO;
import ru.otus.prof.retail.entities.shops.Cash;
import ru.otus.prof.retail.exception.shop.CashNotFoundException;
import ru.otus.prof.retail.exception.shop.CashNumberAlreadyExistsException;
import ru.otus.prof.retail.exception.shop.CashValidationException;
import ru.otus.prof.retail.mappers.shop.CashMapper;
import ru.otus.prof.retail.repositories.shops.CashRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CashService {

    private static final Logger logger = LoggerFactory.getLogger(CashService.class);

    @Autowired
    private CashRepository cashRepository;

    @Autowired
    private CashMapper cashMapper;

    @Transactional
    public CashDTO createCash(CashDTO cashDTO) {
        logger.info("Создание новой кассы с номером: {} для магазина: {}", cashDTO.number(), cashDTO.shopNumber());

        if (cashRepository.findByNumberAndShopNumber(cashDTO.number(), cashDTO.shopNumber()).isPresent()) {
            throw new CashNumberAlreadyExistsException("Касса с номером " + cashDTO.number() + " уже существует в магазине " + cashDTO.shopNumber());
        }

        CashDTO createdCash = cashMapper.toDTO(cashRepository.save(cashMapper.toEntity(cashDTO)));
        logger.debug("Создана касса: {}", createdCash);
        return createdCash;
    }

    public Optional<CashDTO> getCashById(Long id) {
        logger.debug("Получение кассы по ID: {}", id);
        Optional<CashDTO> result = cashRepository.findById(id).map(cashMapper::toDTO);
        if (result.isEmpty()) {
            throw new CashNotFoundException("Касса с ID " + id + " не найдена");
        }
        return result;
    }

    public Optional<CashDTO> getCashByNumberAndShopNumber(Long number, Long shopNumber) {
        logger.debug("Поиск кассы по номеру: {} и номеру магазина: {}", number, shopNumber);
        Optional<CashDTO> result = cashRepository.findByNumberAndShopNumber(number, shopNumber).map(cashMapper::toDTO);
        if (result.isEmpty()) {
            throw new CashNotFoundException("Касса с номером " + number + " в магазине " + shopNumber + " не найдена");
        }
        return result;
    }

    @Transactional
    public List<CashDTO> getCashByShopNumber(Long shopNumber) {
        logger.debug("Получение всех касс для магазина с номером: {}", shopNumber);

        List<CashDTO> result = cashRepository.findByShopNumber(shopNumber).stream()
                .map(cashMapper::toDTO)
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            throw new CashNotFoundException("Кассы для магазина с номером " + shopNumber + " не найдены");
        }

        logger.debug("Найдено {} касс для магазина {}", result.size(), shopNumber);
        return result;
    }


    @Transactional
    public void updateCashStatus(Long id, STATUS status) {
        logger.info("Обновление статуса кассы с ID {} на {}", id, status);

        if (id == null || status == null) {
            throw new CashValidationException("ID и статус кассы не могут быть null");
        }

        Cash cash = cashRepository.findById(id)
                .orElseThrow(() -> new CashNotFoundException("Касса с ID " + id + " не найдена"));

        if (cash.getStatus() == status) {
            logger.warn("Статус кассы с ID {} уже установлен в {}", id, status);
            return;
        }

        cashRepository.updateCashStatus(id, status);
        logger.debug("Статус кассы с ID {} успешно обновлен на {}", id, status);
    }
}