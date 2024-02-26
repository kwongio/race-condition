### 레이스 컨디션이란
공유 자원에 대해 여러개의 쓰레드 또는 프로세스가 동시에 접근해 실행순서에 따라 결과값이 변하는 문제를 말한다.

---



테스트 코드
```java
    @Test
    public void 동시에_100개_요청() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
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
        Assertions.assertThat(stock.getQuantity()).isEqualTo(0L);
        System.out.println(stock.getQuantity());
    }
```

### 동시성 문제가 발생하는 코드
```java
    public void decrease(Long id, Long quantity) {
        Stock stock = stockRepository.findById(id).orElseThrow();
        stock.decrease(quantity);
        stockRepository.saveAndFlush(stock);
    }
```


### 트랜잭션을 적용한 코드  
- 트랜잭션을 사용하면 동시성 문제가 해결되지 않는다.  
- 실제 데이터베이스에 업데이트 되기 전에 다른 스레드가 실행되면서 갱신되기 전의 값을 가져가기 때문이다.
```java
    @Trasactional
    public void decrease(Long id, Long quantity) {
        Stock stock = stockRepository.findById(id).orElseThrow();
        stock.decrease(quantity);
        stockRepository.saveAndFlush(stock);
    }
```

### sychronized 키워드를 사용해 동시성 문제 해결
```java
    public synchronized void decrease(Long id, Long quantity) {
        Stock stock = stockRepository.findById(id).orElseThrow();
        stock.decrease(quantity);
        stockRepository.saveAndFlush(stock);
    }
```
### Sychronized의 문제점
서버가 한 대 일때는 문제가 없지면 서버가 여러대가 되면 데이터의 접근을 여러대가 할 수 있기 때문에 동시성 문제가 다시 발생한다.






