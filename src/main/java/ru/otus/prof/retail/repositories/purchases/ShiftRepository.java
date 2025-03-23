package ru.otus.prof.retail.repositories.purchases;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.otus.prof.retail.entities.purchases.Shift;

import java.time.LocalDate;

import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long>, JpaSpecificationExecutor<Shift> {

    @Query("SELECT DISTINCT s FROM Shift s LEFT JOIN FETCH s.purchases p LEFT JOIN FETCH p.positions")
    List<Shift> findAllWithPositions(Specification<Shift> spec);

    @Query("SELECT s FROM Shift s")
    List<Shift> findAllWithoutPositions(Specification<Shift> spec);

    default List<Shift> findShiftByCloseDate(LocalDate closeDate, boolean withPositions) {
        Specification<Shift> spec = ShiftSpecification.byCloseDate(closeDate);
        return withPositions ? findAllWithPositions(spec) : findAllWithoutPositions(spec);
    }

    default List<Shift> findShiftsByShopNumberAndCloseDate(Long shopNumber, LocalDate closeDate, boolean withPositions) {
        Specification<Shift> spec = ShiftSpecification.byShopNumber(shopNumber)
                .and(ShiftSpecification.byCloseDate(closeDate));
        return withPositions ? findAllWithPositions(spec) : findAllWithoutPositions(spec);
    }

    default List<Shift> findShiftsByShopNumberAndCashNumberAndCloseDate(Long shopNumber, Long cashNumber, LocalDate closeDate, boolean withPositions) {
        Specification<Shift> spec = ShiftSpecification.byShopNumber(shopNumber)
                .and(ShiftSpecification.byCashNumber(cashNumber))
                .and(ShiftSpecification.byCloseDate(closeDate));
        return withPositions ? findAllWithPositions(spec) : findAllWithoutPositions(spec);
    }

    default List<Shift> findShiftByCloseDateRange(LocalDate startDate, LocalDate endDate, boolean withPositions) {
        Specification<Shift> spec = ShiftSpecification.byCloseDateRange(startDate, endDate);
        return withPositions ? findAllWithPositions(spec) : findAllWithoutPositions(spec);
    }

    default List<Shift> findShiftsByShopNumberAndCloseDateRange(Long shopNumber, LocalDate startDate, LocalDate endDate, boolean withPositions) {
        Specification<Shift> spec = ShiftSpecification.byShopNumber(shopNumber)
                .and(ShiftSpecification.byCloseDateRange(startDate, endDate));
        return withPositions ? findAllWithPositions(spec) : findAllWithoutPositions(spec);
    }

    default List<Shift> findShiftsByShopNumberAndCashNumberAndCloseDateRange(Long shopNumber, Long cashNumber, LocalDate startDate, LocalDate endDate, boolean withPositions) {
        Specification<Shift> spec = ShiftSpecification.byShopNumber(shopNumber)
                .and(ShiftSpecification.byCashNumber(cashNumber))
                .and(ShiftSpecification.byCloseDateRange(startDate, endDate));
        return withPositions ? findAllWithPositions(spec) : findAllWithoutPositions(spec);
    }

}
