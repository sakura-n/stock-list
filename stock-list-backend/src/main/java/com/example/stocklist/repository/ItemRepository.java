package com.example.stocklist.repository;

import com.example.stocklist.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i WHERE " +
            "(:keyword IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(i.category) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:category IS NULL OR i.category = :category)")
    List<Item> search(@Param("keyword") String keyword, @Param("category") String category);

    @Query("SELECT DISTINCT i.category FROM Item i WHERE i.category IS NOT NULL ORDER BY i.category")
    List<String> findDistinctCategories();
}
