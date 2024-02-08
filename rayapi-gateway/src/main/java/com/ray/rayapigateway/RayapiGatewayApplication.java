package com.ray.rayapigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RayapiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(RayapiGatewayApplication.class, args);
    }


//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route("path_route", r -> r.path("/baidu")
//                        .uri("https://www.baidu.com"))
//                .route("host_route", r -> r.path("/yupiicu")
//                        .uri("http://yupi.icu"))
//                .build();
//    }

}
