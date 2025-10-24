package ru.booking.reserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ReserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReserverApplication.class, args);
    }

}
