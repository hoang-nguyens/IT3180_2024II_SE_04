package repositories;

import models.Fee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDate;

@Repository
public interface FeeRepository extends JpaRepository<Fee, Long> {
    List<Fee> findAll();

    @Query("""
        SELECT f FROM Fee f 
        WHERE f.startDate <= CURRENT_DATE 
          AND (f.endDate > CURRENT_DATE OR f.endDate IS NULL)
    """)
    List<Fee> findAllActiveFees();

    @Query("""
        SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END
        FROM Fee f 
        WHERE f.category = :category 
          AND f.subCategory = :subCategory 
          AND (f.startDate < :newEnd OR :newEnd IS NULL) 
          AND (:newStart < f.endDate OR f.endDate IS NULL)
    """)
    boolean existsByCategoryAndSubCategoryAndTimeOverlap(
            @Param("category") String category,
            @Param("subCategory") String subCategory,
            @Param("newStart") LocalDate newStart,
            @Param("newEnd") LocalDate newEnd
    );

    @Query("""
        SELECT f FROM Fee f 
        WHERE (:category IS NULL OR f.category = :category)
          AND (:subCategory IS NULL OR f.subCategory = :subCategory)
    """)
    List<Fee> findByCategoryAndSubCategory(
            @Param("category") String category,
            @Param("subCategory") String subCategory
    );

    @Query("""
        SELECT f FROM Fee f 
        WHERE (:category IS NULL OR f.category = :category)
          AND (:subCategory IS NULL OR f.subCategory = :subCategory)
          AND f.startDate <= CURRENT_DATE 
          AND (f.endDate > CURRENT_DATE OR f.endDate IS NULL)
    """)
    List<Fee> findByCategoryAndSubCategoryAndIsActive(
            @Param("category") String category,
            @Param("subCategory") String subCategory
    );

    List<Fee> findByCategoryNot(String category);
}