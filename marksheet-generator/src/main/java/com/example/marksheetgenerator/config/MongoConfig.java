package com.example.marksheetgenerator.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Configuration
public class MongoConfig {

    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = Arrays.asList(
                new LocalDateToDateConverter(),
                new DateToLocalDateConverter());
        return new MongoCustomConversions(converters);
    }

    static class LocalDateToDateConverter implements Converter<LocalDate, Date> {
        @Override
        public Date convert(LocalDate source) {
            // Convert LocalDate to Date at start of day in IST
            return Date.from(source.atStartOfDay(ZoneId.of("Asia/Kolkata")).toInstant());
        }
    }

    static class DateToLocalDateConverter implements Converter<Date, LocalDate> {
        @Override
        public LocalDate convert(Date source) {
            // Convert Date to LocalDate using IST timezone
            return source.toInstant().atZone(ZoneId.of("Asia/Kolkata")).toLocalDate();
        }
    }
}
