package ru.otus.prof.retail.services.shops;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.prof.retail.STATUS;
import ru.otus.prof.retail.entities.shops.Cash;
import ru.otus.prof.retail.repositories.shops.CashRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CashService {

    @Autowired
    private CashRepository cashRepository;

    @Transactional
    public Cash createCash(Cash cash){
        return cashRepository.save(cash);
    }

    public Optional<Cash> getCashById(Long id){
        return cashRepository.findById(id);
    }

    public Optional<Cash> getCashByNumberAndShopNumber(Long number, Long shopNumber){
        return cashRepository.findByNumberAndShopNumber(number, shopNumber);
    }

    public List<Cash> getCashByShopNumber(Long shopNumber){
        return cashRepository.findByShopNumber(shopNumber);
    }

    @Transactional
    public Cash updateCash(Cash cash){
        return cashRepository.save(cash);
    }

    @Transactional
    public void updateCashStatus(Long id, STATUS status){
        cashRepository.updateCashStatus(id, status);
    }


}
