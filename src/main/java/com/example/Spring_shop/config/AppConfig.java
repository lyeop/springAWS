package com.example.Spring_shop.config;

import com.siot.IamportRestClient.IamportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    String apiKey = "2006460505804620";
    String secretKey = "7iAVxkQS1mhvwke1mZa15cfIDuJoft1Joixrc0EKS1LRAIhjjEAby1oIkQp3neTbmcL31jomnDeyhxAO";

    @Bean
    public IamportClient iamportClient() {
        return new IamportClient(apiKey, secretKey);
    }


}
