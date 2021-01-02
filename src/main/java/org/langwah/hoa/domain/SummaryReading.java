package org.langwah.hoa.domain;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Builder
@Slf4j
public class SummaryReading implements Reading {
    private Date readingDate;
    private String readingName;
    private long reading;

    @Builder.Default
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public String toCsvRow() {
        String result =  Stream.of(df.format(readingDate), readingName, ""+reading)
                .map(value -> value.replaceAll("\"", "\"\""))
                .map(value -> Stream.of("\"", ",").anyMatch(value::contains) ? "\"" + value + "\"" : value)
                .collect(Collectors.joining(","));
        log.info("result: {}", result);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SummaryReading that = (SummaryReading) o;
        return readingDate.equals(that.readingDate) && readingName.equals(that.readingName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(readingDate, readingName);
    }
}
