package com.fw.listenup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.fw.listenup.config.DataSourceConfig;

@SpringBootApplication
@Import(DataSourceConfig.class)
public class ListenupApplication {

	public static void main(String[] args) {
		SpringApplication.run(ListenupApplication.class, args);
	}

}
