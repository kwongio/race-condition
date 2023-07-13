package com.example.stock.repository;

import com.example.stock.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StockRepository extends JpaRepository<Stock, Long> {


    @Query("select s from Stock s where s.id = :id" )
    Stock findByIdWithPessimisticLock(Long id);
}
