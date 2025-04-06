package ru.otus.prof.retail.repositories.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.otus.prof.retail.entities.product.Item;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Modifying
    @Query("DELETE FROM Item i WHERE i.article = :article")
    void deleteByArticle(@Param("article") Long article);

    default List<Long> findMissingArticles(Set<Long> articles) {
        List<Long> existingArticles = findByArticleIn(articles)
                .stream()
                .map(Item::getArticle)
                .collect(Collectors.toList());

        return articles.stream()
                .filter(art -> !existingArticles.contains(art))
                .collect(Collectors.toList());
    }

    List<Item> findByArticleIn(Set<Long> articles);
}
