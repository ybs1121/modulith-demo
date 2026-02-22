package com.toy.modulithdemo.promotion;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Promotion p where p.id = :id")
    Optional<Promotion> findByIdWithPessimisticLock(@Param("id") Long id); // 비관적인 락

    @Lock(LockModeType.OPTIMISTIC)
    @Query("select p from Promotion p where p.id = :id")
    Optional<Promotion> findByIdWithOptimisticLock(@Param("id") Long id); // 낙관적인 락
}
