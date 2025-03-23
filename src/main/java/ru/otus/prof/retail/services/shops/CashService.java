package ru.otus.prof.retail.services.shops;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.prof.retail.STATUS;
import ru.otus.prof.retail.dto.shop.CashDTO;
import ru.otus.prof.retail.mappers.shop.CashMapper;
import ru.otus.prof.retail.repositories.shops.CashRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CashService {

    @Autowired
    private CashRepository cashRepository;

    @Autowired
    private CashMapper cashMapper;

    @Transactional
    public CashDTO createCash(CashDTO cashDTO) {
        return cashMapper.toDTO(cashRepository.save(cashMapper.toEntity(cashDTO)));
    }

    public Optional<CashDTO> getCashById(Long id) {
        return cashRepository.findById(id).map(cashMapper::toDTO);
    }

    public Optional<CashDTO> getCashByNumberAndShopNumber(Long number, Long shopNumber) {
        return cashRepository.findByNumberAndShopNumber(number, shopNumber).map(cashMapper::toDTO);
    }

    public List<CashDTO> getCashByShopNumber(Long shopNumber) {
        return cashRepository.findByShopNumber(shopNumber).stream()
                .map(cashMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CashDTO updateCash(CashDTO cashDTO) {
        return cashMapper.toDTO(cashRepository.save(cashMapper.toEntity(cashDTO)));
    }

    @Transactional
    public void updateCashStatus(Long id, STATUS status) {
        cashRepository.updateCashStatus(id, status);
    }


}
