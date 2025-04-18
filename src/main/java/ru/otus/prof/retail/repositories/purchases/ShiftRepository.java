package ru.otus.prof.retail.repositories.purchases;

import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.otus.prof.retail.entities.purchases.Shift;

import java.time.LocalDate;

import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long>, JpaSpecificationExecutor<Shift> {

    private List<Shift> findAllWithPurchases(Specification<Shift> spec) {
        return findAll((root, query, cb) -> {
            root.fetch("purchases", JoinType.LEFT).fetch("positions", JoinType.LEFT);
            return spec.toPredicate(root, query, cb);
        });
    }

    private List<Shift> findAllWithoutPurchases(Specification<Shift> spec) {
        return findAll((root, query, cb) -> {
            query.distinct(true);
            return spec.toPredicate(root, query, cb);
        });
    }

    default List<Shift> findShiftByCloseDate(LocalDate closeDate, boolean withPositions) {
        Specification<Shift> spec = ShiftSpecification.byCloseDate(closeDate);
        return withPositions ? findAllWithPurchases(spec) : findAllWithoutPurchases(spec);
    }

    default List<Shift> findShiftsByShopNumberAndCloseDate(Long shopNumber, LocalDate closeDate, boolean withPositions) {
        Specification<Shift> spec = ShiftSpecification.byCloseDate(closeDate)
                .and(ShiftSpecification.byShopNumber(shopNumber));
        return withPositions ? findAllWithPurchases(spec) : findAllWithoutPurchases(spec);
    }

    default List<Shift> findShiftsByShopNumberAndCashNumberAndCloseDate(Long shopNumber, Long cashNumber, LocalDate closeDate, boolean withPositions) {
        Specification<Shift> spec = ShiftSpecification.byShopNumber(shopNumber)
                .and(ShiftSpecification.byCashNumber(cashNumber))
                .and(ShiftSpecification.byCloseDate(closeDate));
        return withPositions ? findAllWithPurchases(spec) : findAllWithoutPurchases(spec);
    }

    default List<Shift> findShiftByCloseDateRange(LocalDate startDate, LocalDate endDate, boolean withPositions) {
        Specification<Shift> spec = ShiftSpecification.byCloseDateRange(startDate, endDate);
        return withPositions ? findAllWithPurchases(spec) : findAllWithoutPurchases(spec);
    }

    default List<Shift> findShiftsByShopNumberAndCloseDateRange(Long shopNumber, LocalDate startDate, LocalDate endDate, boolean withPositions) {
        Specification<Shift> spec = ShiftSpecification.byShopNumber(shopNumber)
                .and(ShiftSpecification.byCloseDateRange(startDate, endDate));
        return withPositions ? findAllWithPurchases(spec) : findAllWithoutPurchases(spec);
    }

    default List<Shift> findShiftsByShopNumberAndCashNumberAndCloseDateRange(Long shopNumber, Long cashNumber, LocalDate startDate, LocalDate endDate, boolean withPositions) {
        Specification<Shift> spec = ShiftSpecification.byShopNumber(shopNumber)
                .and(ShiftSpecification.byCashNumber(cashNumber))
                .and(ShiftSpecification.byCloseDateRange(startDate, endDate));
        return withPositions ? findAllWithPurchases(spec) : findAllWithoutPurchases(spec);
    }

}
