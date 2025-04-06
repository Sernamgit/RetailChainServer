package ru.otus.prof.retail.services.purchases;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.prof.retail.dto.purchases.ShiftDTO;
import ru.otus.prof.retail.dto.purchases.ShiftSearchRequest;
import ru.otus.prof.retail.entities.purchases.Shift;
import ru.otus.prof.retail.exception.purchases.ShiftNotFoundException;
import ru.otus.prof.retail.exception.purchases.ShiftValidationException;
import ru.otus.prof.retail.mappers.purchases.ShiftMapper;
import ru.otus.prof.retail.repositories.purchases.ShiftRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShiftService {
    private static final Logger logger = LoggerFactory.getLogger(ShiftService.class);

    private final ShiftRepository shiftRepository;
    private final ShiftMapper shiftMapper;

    @Autowired
    public ShiftService(ShiftRepository shiftRepository, ShiftMapper shiftMapper) {
        this.shiftRepository = shiftRepository;
        this.shiftMapper = shiftMapper;
    }

    //валидация диапазона дат
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            String errorMessage = String.format("Начальная дата (%s) позже конечной (%s)", startDate, endDate);
            logger.error("Ошибка валидации диапазона дат: {}", errorMessage);
            throw new ShiftValidationException(errorMessage);
        }
        logger.debug("Диапазон дат успешно провалидирован: {} - {}", startDate, endDate);
    }

    //все смены
    @Transactional
    public List<ShiftDTO> getAllShiftsByCloseDate(LocalDate date, boolean withPurchases) {
        logger.debug("Получение всех смен по дате закрытия: {}, withPurchases: {}", date, withPurchases);
        List<Shift> shifts = shiftRepository.findShiftByCloseDate(date, withPurchases);

        if (shifts.isEmpty()) {
            String errorMessage = "Смены не найдены за " + date;
            logger.warn(errorMessage);
            throw new ShiftNotFoundException(errorMessage);
        }

        logger.info("Найдено {} смен за дату {}", shifts.size(), date);
        return shifts.stream()
                .map(shift -> shiftMapper.toDTO(shift, withPurchases))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ShiftDTO> getAllShiftsByCloseDateRange(LocalDate startDate, LocalDate endDate, boolean withPurchases) {
        logger.debug("Получение всех смен по диапазону дат: {} - {}, withPurchases: {}", startDate, endDate, withPurchases);
        validateDateRange(startDate, endDate);

        List<Shift> shifts = shiftRepository.findShiftByCloseDateRange(startDate, endDate, withPurchases);

        if (shifts.isEmpty()) {
            String errorMessage = "Смены не найдены в диапазоне " + startDate + " - " + endDate;
            logger.warn(errorMessage);
            throw new ShiftNotFoundException(errorMessage);
        }

        logger.info("Найдено {} смен в диапазоне {} - {}", shifts.size(), startDate, endDate);
        return shifts.stream()
                .map(shift -> shiftMapper.toDTO(shift, withPurchases))
                .collect(Collectors.toList());
    }

    //по магазину
    @Transactional
    public List<ShiftDTO> getShiftsByShopNumberAndCloseDate(Long shopNumber, LocalDate date, boolean withPurchases) {
        logger.debug("Получение смен для магазина {} по дате: {}, withPurchases: {}", shopNumber, date, withPurchases);
        List<Shift> shifts = shiftRepository.findShiftsByShopNumberAndCloseDate(shopNumber, date, withPurchases);

        if (shifts.isEmpty()) {
            String errorMessage = "Смены не найдены для магазина #" + shopNumber + " за " + date;
            logger.warn(errorMessage);
            throw new ShiftNotFoundException(errorMessage);
        }

        logger.info("Найдено {} смен для магазина {} за дату {}", shifts.size(), shopNumber, date);
        return shifts.stream()
                .map(shift -> shiftMapper.toDTO(shift, withPurchases))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ShiftDTO> getShiftsByShopNumberAndCloseRange(Long shopNumber, LocalDate startDate, LocalDate endDate, boolean withPurchases) {
        logger.debug("Получение смен для магазина {} по диапазону дат: {} - {}, withPurchases: {}", shopNumber, startDate, endDate, withPurchases);
        validateDateRange(startDate, endDate);

        List<Shift> shifts = shiftRepository.findShiftsByShopNumberAndCloseDateRange(shopNumber, startDate, endDate, withPurchases);

        if (shifts.isEmpty()) {
            String errorMessage = "Смены не найдены для магазина #" + shopNumber + " в диапазоне " + startDate + " - " + endDate;
            logger.warn(errorMessage);
            throw new ShiftNotFoundException(errorMessage);
        }

        logger.info("Найдено {} смен для магазина {} в диапазоне {} - {}", shifts.size(), shopNumber, startDate, endDate);
        return shifts.stream()
                .map(shift -> shiftMapper.toDTO(shift, withPurchases))
                .collect(Collectors.toList());
    }

    //по кассе
    @Transactional
    public List<ShiftDTO> getShiftsByShopNumberAndCashNumberAndCloseDate(Long shopNumber, Long cashNumber, LocalDate date, boolean withPurchases) {
        logger.debug("Получение смен для магазина {} и кассы {} по дате: {}, withPurchases: {}", shopNumber, cashNumber, date, withPurchases);
        List<Shift> shifts = shiftRepository.findShiftsByShopNumberAndCashNumberAndCloseDate(shopNumber, cashNumber, date, withPurchases);

        if (shifts.isEmpty()) {
            String errorMessage = "Смены не найдены для магазина #" + shopNumber + ", кассы #" + cashNumber + " за " + date;
            logger.warn(errorMessage);
            throw new ShiftNotFoundException(errorMessage);
        }

        logger.info("Найдено {} смен для магазина {} и кассы {} за дату {}", shifts.size(), shopNumber, cashNumber, date);
        return shifts.stream()
                .map(shift -> shiftMapper.toDTO(shift, withPurchases))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ShiftDTO> getShiftsByShopNumberAndCashNumberAndCloseDateRange(Long shopNumber, Long cashNumber, LocalDate startDate, LocalDate endDate, boolean withPurchases) {
        logger.debug("Получение смен для магазина {} и кассы {} по диапазону дат: {} - {}, withPurchases: {}", shopNumber, cashNumber, startDate, endDate, withPurchases);
        validateDateRange(startDate, endDate);

        List<Shift> shifts = shiftRepository.findShiftsByShopNumberAndCashNumberAndCloseDateRange(shopNumber, cashNumber, startDate, endDate, withPurchases);

        if (shifts.isEmpty()) {
            String errorMessage = "Смены не найдены для магазина #" + shopNumber + ", кассы #" + cashNumber + " в диапазоне " + startDate + " - " + endDate;
            logger.warn(errorMessage);
            throw new ShiftNotFoundException(errorMessage);
        }

        logger.info("Найдено {} смен для магазина {} и кассы {} в диапазоне {} - {}", shifts.size(), shopNumber, cashNumber, startDate, endDate);
        return shifts.stream()
                .map(shift -> shiftMapper.toDTO(shift, withPurchases))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ShiftDTO> searchShifts(ShiftSearchRequest request) {
        logger.debug("Поиск смен по параметрам: {}", request);
        if (request == null) {
            String errorMessage = "Запрос не может быть null";
            logger.error(errorMessage);
            throw new ShiftValidationException(errorMessage);
        }

        List<ShiftDTO> result = request.isDateRangeSearch() ? handleDateRangeSearch(request) : handleSingleDateSearch(request);

        if (result.isEmpty()) {
            String errorMessage = "Смены не найдены по заданным критериям";
            logger.warn(errorMessage);
            throw new ShiftNotFoundException(errorMessage);
        }

        logger.info("Найдено {} смен по критериям поиска", result.size());
        return result;
    }

    @Transactional
    public List<ShiftDTO> searchShiftsBatch(List<ShiftSearchRequest> requests) {
        logger.debug("Пакетный поиск смен по {} запросам", requests != null ? requests.size() : 0);
        if (requests == null || requests.isEmpty()) {
            String errorMessage = "Список запросов не может быть пустым";
            logger.error(errorMessage);
            throw new ShiftValidationException(errorMessage);
        }

        List<ShiftDTO> result = requests.stream()
                .flatMap(request -> searchShifts(request).stream())
                .distinct()
                .toList();

        if (result.isEmpty()) {
            String errorMessage = "Смены не найдены ни по одному из критериев";
            logger.warn(errorMessage);
            throw new ShiftNotFoundException(errorMessage);
        }

        logger.info("Пакетный поиск завершен. Найдено {} уникальных смен", result.size());
        return result;
    }

    private List<ShiftDTO> handleDateRangeSearch(ShiftSearchRequest request) {
        logger.debug("Обработка поиска по диапазону дат: {}", request);
        if (request.shopNumber() == null) {
            return getAllShiftsByCloseDateRange(request.startDate(), request.endDate(), request.withPurchases());
        }
        return request.cashNumber() == null
                ? getShiftsByShopNumberAndCloseRange(request.shopNumber(), request.startDate(), request.endDate(), request.withPurchases())
                : getShiftsByShopNumberAndCashNumberAndCloseDateRange(
                request.shopNumber(), request.cashNumber(), request.startDate(), request.endDate(), request.withPurchases()
        );
    }

    private List<ShiftDTO> handleSingleDateSearch(ShiftSearchRequest request) {
        logger.debug("Обработка поиска по одной дате: {}", request);
        if (request.shopNumber() == null) {
            return getAllShiftsByCloseDate(request.date(), request.withPurchases());
        }
        return request.cashNumber() == null
                ? getShiftsByShopNumberAndCloseDate(request.shopNumber(), request.date(), request.withPurchases())
                : getShiftsByShopNumberAndCashNumberAndCloseDate(request.shopNumber(), request.cashNumber(), request.date(), request.withPurchases());
    }
}