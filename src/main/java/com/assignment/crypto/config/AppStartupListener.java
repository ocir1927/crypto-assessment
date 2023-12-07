package com.assignment.crypto.config;

import com.assignment.crypto.service.CryptoPriceCSVLoader;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Log4j2
public class AppStartupListener implements ApplicationListener<ContextRefreshedEvent> {

    private final CryptoPriceCSVLoader cryptoPriceCSVLoader;

    public AppStartupListener(CryptoPriceCSVLoader cryptoPriceCSVLoader) {
        this.cryptoPriceCSVLoader = cryptoPriceCSVLoader;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        try {
            cryptoPriceCSVLoader.loadFromCSV("prices/BTC_values.csv");
            cryptoPriceCSVLoader.loadFromCSV("prices/DOGE_values.csv");
            cryptoPriceCSVLoader.loadFromCSV("prices/ETH_values.csv");
            cryptoPriceCSVLoader.loadFromCSV("prices/LTC_values.csv");
            cryptoPriceCSVLoader.loadFromCSV("prices/XRP_values.csv");
        } catch (IOException e) {
            log.error("There was a problem loading CSV values ", e.getCause());
            e.printStackTrace();
        }

    }
}
