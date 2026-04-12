package com.nebulaparfums.nebula_parfums;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@SpringBootApplication
public class NebulaParfumsApplication {

	public static void main(String[] args) {
		SpringApplication.run(NebulaParfumsApplication.class, args);
	}

}
