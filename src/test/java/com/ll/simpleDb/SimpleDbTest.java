package com.ll.simpleDb;

import org.junit.jupiter.api.*;

import java.util.stream.IntStream;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class SimpleDbTest {
    private static SimpleDb simpleDb;

    @BeforeAll
    public static void beforeAll() {
        simpleDb = new SimpleDb("localhost", "root", "lldj123414", "simpleDb__test");
        simpleDb.setDevMode(true);

        createArticleTable();
    }

    @BeforeEach
    public void beforeEach() {
        truncateArticleTable();
        makeArticleTestData();
    }

    private static void createArticleTable() {
        simpleDb.run("DROP TABLE IF EXISTS article");

        simpleDb.run("""
                CREATE TABLE article (
                    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
                    PRIMARY KEY(id),
                    createdDate DATETIME NOT NULL,
                    modifiedDate DATETIME NOT NULL,
                    title VARCHAR(100) NOT NULL,
                    `body` TEXT NOT NULL,
                    isBlind BIT(1) NOT NULL DEFAULT 0
                )
                """);
    }

    private void makeArticleTestData() {
        IntStream.rangeClosed(1, 6).forEach(no -> {
            boolean isBlind = no > 3;
            String title = "제목%d".formatted(no);
            String body = "내용%d".formatted(no);

            simpleDb.run("""
                    INSERT INTO article
                    SET createdDate = NOW(),
                    modifiedDate = NOW(),
                    title = ?,
                    `body` = ?,
                    isBlind = ?
                    """, title, body, isBlind);
        });
    }

    private void truncateArticleTable() {
        simpleDb.run("TRUNCATE article");
    }

    @Test
    void t1() {

    }

//    @Test
//    @DisplayName("selectRows, Article")
//    public void t015() {
//        Sql sql = simpleDb.genSql();
//        /*
//        == rawSql ==
//        SELECT *
//        FROM article
//        ORDER BY id ASC
//        LIMIT 3
//        */
//        sql.append("SELECT * FROM article ORDER BY id ASC LIMIT 3");
//        List<Article> articleRows = sql.selectRows(Article.class);
//
//        IntStream.range(0, articleRows.size()).forEach(i -> {
//            long id = i + 1;
//
//            Article article = articleRows.get(i);
//
//            assertThat(article.getId()).isEqualTo(id);
//            assertThat(article.getTitle()).isEqualTo("제목%d".formatted(id));
//            assertThat(article.getBody()).isEqualTo("내용%d".formatted(id));
//            assertThat(article.getCreatedDate()).isInstanceOf(LocalDateTime.class);
//            assertThat(article.getCreatedDate()).isNotNull();
//            assertThat(article.getModifiedDate()).isInstanceOf(LocalDateTime.class);
//            assertThat(article.getModifiedDate()).isNotNull();
//            assertThat(article.isBlind()).isEqualTo(false);
//        });
//    }
//
//    @Test
//    @DisplayName("selectRow, Article")
//    public void t016() {
//        Sql sql = simpleDb.genSql();
//        /*
//        == rawSql ==
//        SELECT *
//        FROM article
//        WHERE id = 1
//        */
//        sql.append("SELECT * FROM article WHERE id = 1");
//        Article article = sql.selectRow(Article.class);
//
//        Long id = 1L;
//
//        assertThat(article.getId()).isEqualTo(id);
//        assertThat(article.getTitle()).isEqualTo("제목%d".formatted(id));
//        assertThat(article.getBody()).isEqualTo("내용%d".formatted(id));
//        assertThat(article.getCreatedDate()).isInstanceOf(LocalDateTime.class);
//        assertThat(article.getCreatedDate()).isNotNull();
//        assertThat(article.getModifiedDate()).isInstanceOf(LocalDateTime.class);
//        assertThat(article.getModifiedDate()).isNotNull();
//        assertThat(article.isBlind()).isEqualTo(false);
//    }
//
//    // 테스트 메서드를 정의하고, 테스트 이름을 지정합니다.
//    @Test
//    @DisplayName("use in multi threading")
//    public void t017() throws InterruptedException {
//        // 쓰레드 풀의 크기를 정의합니다.
//        int numberOfThreads = 10;
//
//        // 고정 크기의 쓰레드 풀을 생성합니다.
//        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
//
//        // 성공한 작업의 수를 세는 원자적 카운터를 생성합니다.
//        AtomicInteger successCounter = new AtomicInteger(0);
//
//        // 동시에 실행되는 작업의 수를 세는 데 사용되는 래치를 생성합니다.
//        CountDownLatch latch = new CountDownLatch(numberOfThreads);
//
//        // 각 쓰레드에서 실행될 작업을 정의합니다.
//        Runnable task = () -> {
//            try {
//                // SimpleDB에서 SQL 객체를 생성합니다.
//                Sql sql = simpleDb.genSql();
//
//                // SQL 쿼리를 작성합니다.
//                sql.append("SELECT * FROM article WHERE id = 1");
//
//                // 쿼리를 실행하여 결과를 Article 객체로 매핑합니다.
//                Article article = sql.selectRow(Article.class);
//
//                // 기대하는 Article 객체의 ID를 정의합니다.
//                Long id = 1L;
//
//                // Article 객체의 값이 기대하는 값과 일치하는지 확인하고,
//                // 일치하는 경우 성공 카운터를 증가시킵니다.
//                if (article.getId() == id &&
//                        article.getTitle().equals("제목%d".formatted(id)) &&
//                        article.getBody().equals("내용%d".formatted(id)) &&
//                        article.getCreatedDate() != null &&
//                        article.getModifiedDate() != null &&
//                        !article.isBlind()) {
//                    successCounter.incrementAndGet();
//                }
//            } finally {
//                // 커넥션 종료
//                simpleDb.close();
//                // 작업이 완료되면 래치 카운터를 감소시킵니다.
//                latch.countDown();
//            }
//        };
//
//        // 쓰레드 풀에서 쓰레드를 할당받아 작업을 실행합니다.
//        for (int i = 0; i < numberOfThreads; i++) {
//            executorService.submit(task);
//        }
//
//        // 모든 작업이 완료될 때까지 대기하거나, 최대 10초 동안 대기합니다.
//        latch.await(10, TimeUnit.SECONDS);
//
//        // 쓰레드 풀을 종료시킵니다.
//        executorService.shutdown();
//
//        // 성공 카운터가 쓰레드 수와 동일한지 확인합니다.
//        assertThat(successCounter.get()).isEqualTo(numberOfThreads);
//    }
//
//    @Test
//    @DisplayName("rollback")
//    public void t018() {
//        // SimpleDB에서 SQL 객체를 생성합니다.
//        long oldCount = simpleDb.genSql()
//                .append("SELECT COUNT(*)")
//                .append("FROM article")
//                .selectLong();
//
//        // 트랜잭션을 시작합니다.
//        simpleDb.startTransaction();
//
//        simpleDb.genSql()
//                .append("INSERT INTO article ")
//                .append("(createdDate, modifiedDate, title, body)")
//                .appendIn("VALUES (NOW(), NOW(), ?)", "새 제목", "새 내용")
//                .insert();
//
//        simpleDb.rollback();
//
//        long newCount = simpleDb.genSql()
//                .append("SELECT COUNT(*)")
//                .append("FROM article")
//                .selectLong();
//
//        assertThat(newCount).isEqualTo(oldCount);
//    }
//
//    @Test
//    @DisplayName("commit")
//    public void t019() {
//        // SimpleDB에서 SQL 객체를 생성합니다.
//        long oldCount = simpleDb.genSql()
//                .append("SELECT COUNT(*)")
//                .append("FROM article")
//                .selectLong();
//
//        // 트랜잭션을 시작합니다.
//        simpleDb.startTransaction();
//
//        simpleDb.genSql()
//                .append("INSERT INTO article ")
//                .append("(createdDate, modifiedDate, title, body)")
//                .appendIn("VALUES (NOW(), NOW(), ?)", "새 제목", "새 내용")
//                .insert();
//
//        simpleDb.commit();
//
//        long newCount = simpleDb.genSql()
//                .append("SELECT COUNT(*)")
//                .append("FROM article")
//                .selectLong();
//
//        assertThat(newCount).isEqualTo(oldCount + 1);
//    }
}