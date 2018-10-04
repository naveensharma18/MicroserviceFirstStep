package com.khoj.stock.stockservice.resource;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/stock")
public class StockResource {

    @Autowired
    RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "fallback", groupKey = "stock" , commandKey = "stock",threadPoolKey = "stockThread")
    @GetMapping("/{username}")
    public List<Quote> getStock(@PathVariable("username") final String userName){
        ResponseEntity<List<String>> responseEntity = restTemplate.exchange("http://db-service/rest/db/" +userName,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<String>>(){});
        List<String> responseEntityBody = responseEntity.getBody();
        return responseEntityBody
                .stream()
                .map(quote->{
                        Stock stock = getStockPrice(quote);
                       return new Quote(quote,stock.getQuote().getPrice());
                        })
                .collect(Collectors.toList());
    }

    private Stock getStockPrice(String quote) {
        try {
            return YahooFinance.get(quote);
        } catch (IOException e) {
            e.printStackTrace();
            return new Stock(quote);
        }
    }

    public List<Quote> fallback(String userName){
        List<Quote> quotes = Arrays.asList(new Quote("Fall Back init",new BigDecimal(0)));
        return quotes;
    }


    private class Quote {
        private String quote;
        private BigDecimal price;

        public Quote(String quote, BigDecimal price) {

            this.quote = quote;
            this.price = price;
        }

        public String getQuote() {
            return quote;
        }

        public void setQuote(String quote) {
            this.quote = quote;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }
}
