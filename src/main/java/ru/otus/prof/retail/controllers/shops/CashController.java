package ru.otus.prof.retail.controllers.shops;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.prof.retail.STATUS;
import ru.otus.prof.retail.dto.shop.CashDTO;
import ru.otus.prof.retail.dto.shop.CashDeleteRequestDTO;
import ru.otus.prof.retail.dto.shop.CashStatusRequestDTO;
import ru.otus.prof.retail.services.shops.CashService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cash")
public class CashController {

    private final CashService cashService;

    @Autowired
    public CashController(CashService cashService) {
        this.cashService = cashService;
    }

    @PostMapping
    public ResponseEntity<CashDTO> createCash(@RequestBody CashDTO cashDTO) {
        if (cashDTO.status() == null || cashDTO.number() == null || cashDTO.shopNumber() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(cashService.createCash(cashDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CashDTO> getCashById(@PathVariable Long id) {
        return cashService.getCashById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{shopNumber}/{number}")
    public ResponseEntity<CashDTO> getCashByNumberAndShopNumber(@PathVariable Long shopNumber, @PathVariable Long number) {
        return cashService.getCashByNumberAndShopNumber(number, shopNumber)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/shop/{shopNumber}")
    public ResponseEntity<List<CashDTO>> getCashByShopNumber(@PathVariable Long shopNumber) {
        List<CashDTO> cashList = cashService.getCashByShopNumber(shopNumber);
        return ResponseEntity.ok(cashList);
    }

    @PutMapping("/status")
    public ResponseEntity<Void> updateCashStatus(@RequestBody CashStatusRequestDTO request) {
        if (request.id() != null) {
            cashService.updateCashStatus(request.id(), request.status());
            return ResponseEntity.ok().build();
        } else if (request.number() != null && request.shopNumber() != null) {
            cashService.getCashByNumberAndShopNumber(request.number(), request.shopNumber())
                    .ifPresent(cash -> cashService.updateCashStatus(cash.id(), request.status()));
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCashById(@PathVariable Long id) {
        cashService.updateCashStatus(id, STATUS.DELETED);
        return ResponseEntity.noContent().build();
    }

    //TODO ne rabotaet
    @DeleteMapping("/number/{shopNumber}/{number}")
    public ResponseEntity<Void> deleteCashByNumber(@PathVariable Long shopNumber, @PathVariable Long number) {
        cashService.getCashByNumberAndShopNumber(number, shopNumber)
                .ifPresent(cash -> cashService.updateCashStatus(cash.id(), STATUS.DELETED));
        return ResponseEntity.noContent().build();
    }

    //TODO ne rabotaet
    @DeleteMapping
    public ResponseEntity<Void> deleteCash(@RequestBody CashDeleteRequestDTO request) {
        if (request.id() != null) {
            cashService.updateCashStatus(request.id(), STATUS.DELETED);
            return ResponseEntity.noContent().build();
        } else if (request.number() != null && request.shopNumber() != null) {
            cashService.getCashByNumberAndShopNumber(request.number(), request.shopNumber())
                    .ifPresent(cash -> cashService.updateCashStatus(cash.id(), STATUS.DELETED));
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }
}