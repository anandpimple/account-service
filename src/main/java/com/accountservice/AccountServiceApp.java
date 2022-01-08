package com.accountservice;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static springfox.documentation.builders.PathSelectors.regex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class AccountServiceApp {
    private static final Logger logger = LoggerFactory.getLogger(AccountServiceApp.class);

    public static void main(String[] args) {
        logger.info("Starting car api service");
        SpringApplication.run(AccountServiceApp.class, args);
    }

    @Bean
    public Docket serviceDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("internal")
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(and(regex("/.*"),
                not(regex("/error.*")),
                not(regex("/version.*")),
                not(regex("/manage.*"))))
            .build()
            .pathMapping("/")
            .apiInfo(metadata());
    }

    private ApiInfo metadata() {
        return new ApiInfoBuilder()
            .title("Account service API")
            .description("Api's to manage account related activities")
            .build();
    }
}
