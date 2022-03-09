package com.ndgndg91.ordersimulator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@SpringBootApplication
public class OrderSimulatorApplication {

    private static final Random enumRandom = new Random();
    private static final Random shareRandom = new Random();
    private static final Random priceRandom = new Random();
    private static final Random symbolRandom = new Random();

    private static final int MAX_SHARE = 5000;
    private static final int MIN_SHARE = 50;

    private static final int MAX_PRICE = 100_000;
    private static final int MIN_PRICE = 90_000;

    public static void main(String[] args) {
        SpringApplication.run(OrderSimulatorApplication.class, args);
    }

    private String symbol(int v) {
        switch (v) {
            case 1:
                return "MSFT";
            case 2:
                return "GOOG";
            case 3:
                return "AMZN";
            case 4:
                return "TSLA";
            case 5:
                return "TSM";
            case 6:
                return "FB";
            case 7:
                return "KO";
            default:
                return "AAPL";

        }
    }

    @Bean
    public CommandLineRunner runner() {
        return args -> {
            ExecutorService executor = Executors.newFixedThreadPool(100);
            HttpClient client = HttpClient.newBuilder().executor(executor).build();

            while (true) {
                String orderType = enumRandom.nextInt(2) == 0 ? "ASK" : "BID";
                String priceType = enumRandom.nextInt(20) % 19 == 0 ? "MARKET" : "LIMIT";
                String shares = String.valueOf(shareRandom.nextInt(MAX_SHARE - MIN_SHARE) + MIN_SHARE);
                String price = String.valueOf(priceRandom.nextInt(MAX_PRICE - MIN_PRICE) + MIN_PRICE);
                String symbol = symbol(symbolRandom.nextInt(8));
                HttpRequest post = HttpRequest.newBuilder(URI.create("http://localhost:8080/apis/orders"))
                        .POST(HttpRequest.BodyPublishers.ofString("{\n" +
                                "  \"orderType\": \"" + orderType + "\",\n" +
                                "  \"symbol\": \""+ symbol + "\",\n" +
                                "  \"shares\": " + shares + ",\n" +
                                "  \"priceType\": \"" + priceType + "\",\n" +
                                "  \"price\": " + price + "\n" +
                                "}"))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .build();
                client.sendAsync(post, HttpResponse.BodyHandlers.ofString())
                        .thenApply(response -> {
                            log.info("headers : {}", response.headers());
                            log.info("status code: {}", response.statusCode());
                            return response;
                        })
                        .thenApply(HttpResponse::body)
                        .thenAccept(log::info);
                Thread.sleep(500L);
            }
        };
    }
}
