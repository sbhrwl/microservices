package com.example.items.service;

import com.example.items.model.Item;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvService {

    public List<Item> parse(byte[] fileBytes) {
        List<Item> items = new ArrayList<>();

        try (
            Reader reader = new InputStreamReader(new ByteArrayInputStream(fileBytes));
            CSVParser parser = CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .parse(reader)
        ) {
            for (CSVRecord record : parser) {
                Item item = new Item();
                item.setName(record.get("name"));
                item.setAge(Integer.parseInt(record.get("age")));
                item.setCity(record.get("city"));
                items.add(item);
            }
        } catch (Exception e) {
            throw new RuntimeException("CSV parsing failed", e);
        }

        return items;
    }
}