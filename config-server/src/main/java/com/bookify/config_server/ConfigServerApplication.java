package com.bookify.config_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
// import org.springframework.cloud.client.discovery.EnableDiscoveryClient; // Gerekli olmayabilir

@SpringBootApplication
@EnableConfigServer
@EnableEurekaServer
// @EnableDiscoveryClient
public class ConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigServerApplication.class, args);
	}
}