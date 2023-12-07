package com.assignment.crypto.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CryptoValidator {

    @Value("${crypto.accepted.tickers}")
    private final List<String> supportedCryptos;

    public boolean isCryptoAccepted(String ticker){
        return supportedCryptos.contains(ticker);
    }
}
