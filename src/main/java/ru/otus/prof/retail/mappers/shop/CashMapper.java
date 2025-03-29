package ru.otus.prof.retail.mappers.shop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.otus.prof.retail.dto.shop.CashDTO;
import ru.otus.prof.retail.dto.shop.CashDeleteRequestDTO;
import ru.otus.prof.retail.dto.shop.CashStatusRequestDTO;
import ru.otus.prof.retail.entities.shops.Cash;
import ru.otus.prof.retail.repositories.shops.ShopRepository;

@Component
public class CashMapper {

    private final ShopRepository shopRepository;

    @Autowired
    public CashMapper(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    public CashDTO toDTO(Cash cash) {
        return new CashDTO(
                cash.getId(),
                cash.getStatus(),
                cash.getNumber(),
                cash.getCreateDate(),
                cash.getUpdateDate(),
                cash.getShop().getNumber()
        );
    }

    public CashStatusRequestDTO toStatusRequestDTO(Cash cash) {
        return new CashStatusRequestDTO(
                cash.getId(),
                cash.getNumber(),
                cash.getShop().getNumber(),
                cash.getStatus()
        );
    }

    public CashDeleteRequestDTO toDeleteRequestDTO(Cash cash) {
        return new CashDeleteRequestDTO(
                cash.getId(),
                cash.getNumber(),
                cash.getShop().getNumber()
        );
    }

    public Cash toEntity(CashDTO cashDTO) {
        Cash cash = new Cash();
        cash.setId(cashDTO.id());
        cash.setStatus(cashDTO.status());
        cash.setNumber(cashDTO.number());
        cash.setCreateDate(cashDTO.createDate());
        cash.setUpdateDate(cashDTO.updateDate());
        shopRepository.findByNumber(cashDTO.shopNumber()).ifPresent(cash::setShop);
        return cash;
    }
}
