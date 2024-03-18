package com.ritzjucy.technicaldrawingsbackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TechnicaldrawingsbackendApplication
{

	public static void main(String[] args)
	{
		SpringApplication.run(TechnicaldrawingsbackendApplication.class, args);
	}

	@Bean
	public ObjectMapper objectMapper()
	{
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		return mapper;
	}

}
