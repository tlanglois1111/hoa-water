package org.langwah.hoa.service;

import lombok.extern.slf4j.Slf4j;
import org.langwah.hoa.domain.HOAMeterReading;
import org.langwah.hoa.domain.PrivateMeterReading;
import org.langwah.hoa.domain.Reading;
import org.langwah.hoa.domain.SummaryReading;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WaterBillService {
    private Map<Date, Set<Reading>> hoaBillMap = new HashMap();
    private Map<Date, Set<Reading>> privateBillMap = new HashMap();

    public void addHOAReading(HOAMeterReading meterReading) {
        if (!hoaBillMap.containsKey(meterReading.getReadingDate())) {
            hoaBillMap.put(meterReading.getReadingDate(), new HashSet<>());
        }
        hoaBillMap.get(meterReading.getReadingDate()).add(meterReading);
    }

    public void addPrivateReading(PrivateMeterReading meterReading) {
        if (!privateBillMap.containsKey(meterReading.getReadingDate())) {
            privateBillMap.put(meterReading.getReadingDate(), new HashSet<>());
        }
        privateBillMap.get(meterReading.getReadingDate()).add(meterReading);
    }

    public List<SummaryReading> takeADump() {
        log.info("hoa: {}", hoaBillMap.entrySet().size());
        log.info("homes: {}", privateBillMap.entrySet().size());

        List<SummaryReading> summaryReadings = hoaBillMap.keySet().stream()
                .map(r -> {
                    long usage = hoaBillMap.get(r).stream().filter(f -> f.getReadingName().equals("11935695")).mapToLong(o -> o.getReading()).sum();
                    return SummaryReading.builder()
                            .readingDate(r)
                            .reading(usage*748) // ccf to gallons
                            .readingName("hoa-11935695")
                            .build();
                })
                .collect(Collectors.toList());

        summaryReadings.addAll(
                hoaBillMap.keySet().stream()
                        .map(r -> {
                            long usage = hoaBillMap.get(r).stream().filter(f -> f.getReadingName().equals("70193337")).mapToLong(o -> o.getReading()).sum();
                            return SummaryReading.builder()
                                    .readingDate(r)
                                    .reading(usage*748) // ccf to gallons
                                    .readingName("hoa-70193337")
                                    .build();
                        })
                        .collect(Collectors.toList()));

        summaryReadings.addAll(
                privateBillMap.keySet().stream()
                        .map(r -> {
                            long usage = privateBillMap.get(r).stream().mapToLong(o -> o.getReading()).sum();
                            return SummaryReading.builder()
                                    .readingDate(r)
                                    .reading(usage)
                                    .readingName("privateReadings")
                                    .build();
                        })
                        .collect(Collectors.toList())
        );
        return  summaryReadings;
    }
}
