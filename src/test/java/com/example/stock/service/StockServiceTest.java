package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class StockServiceTest {

    @Autowired
    private StockService stockService;

    @Autowired
    private  StockRepository stockRepository;

    @Test
    public void test1() {

        stockRepository.save(Stock.builder()
                .quantity(100L)
                .productId(1L)
                .build());
        stockService.decrease(1L, 1L);
        Stock stock = stockRepository.findById(1L).orElseThrow();
        Assertions.assertThat(stock.getQuantity()).isEqualTo(99L);
    }

    @Test
    public void 동시에_100개_요청() throws InterruptedException {
        stockRepository.save(Stock.builder()
                .quantity(100L)
                .productId(1L)
                .build());
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decrease(1L, 1L);

                } finally {
                    countDownLatch.countDown();

                }

            });

        }
        countDownLatch.await();
        Stock stock = stockRepository.findById(1L).orElseThrow();
        Assertions.assertThat(0L).isEqualTo(stock.getQuantity());
    }



}