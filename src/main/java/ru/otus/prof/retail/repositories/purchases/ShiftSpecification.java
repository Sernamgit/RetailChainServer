package ru.otus.prof.retail.repositories.purchases;

import org.springframework.data.jpa.domain.Specification;
import ru.otus.prof.retail.entities.purchases.Shift;

import java.time.LocalDate;

public class ShiftSpecification {

    public static Specification<Shift> byCloseDate(LocalDate closeDate) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(criteriaBuilder.function("CAST", LocalDate.class, root.get("closeTime")), closeDate));
    }

    public static Specification<Shift> byCloseDateRange(LocalDate startDate, LocalDate endDate) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.between(criteriaBuilder.function("CAST", LocalDate.class, root.get("closeTime")), startDate, endDate));
    }

    public static Specification<Shift> byShopNumber(Long shopNumber) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("shopNumber"), shopNumber);
    }

    public static Specification<Shift> byCashNumber(Long cashNumber) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("cashNumber"), cashNumber);
    }
}
