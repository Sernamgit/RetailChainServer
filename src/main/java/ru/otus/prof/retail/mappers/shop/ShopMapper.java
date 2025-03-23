package ru.otus.prof.retail.mappers.shop;

import org.springframework.stereotype.Component;
import ru.otus.prof.retail.dto.shop.CashDTO;
import ru.otus.prof.retail.dto.shop.ShopDTO;
import ru.otus.prof.retail.entities.shops.Cash;
import ru.otus.prof.retail.entities.shops.Shop;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ShopMapper {

    private final CashMapper cashMapper;

    public ShopMapper(CashMapper cashMapper) {
        this.cashMapper = cashMapper;
    }

    public ShopDTO toDTO(Shop shop) {
        if (shop == null) {
            return null;
        }

        List<CashDTO> cashDTOList = Collections.emptyList();
        if (shop.getCashList() != null) {
            cashDTOList = shop.getCashList().stream()
                    .map(cashMapper::toDTO)
                    .collect(Collectors.toList());
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
