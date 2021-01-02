package org.langwah.hoa.service;

import lombok.extern.slf4j.Slf4j;
import org.langwah.hoa.domain.HOAMeterReading;
import org.langwah.hoa.domain.PrivateMeterReading;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class WaterBillService {
    private Map<String, Set<HOAMeterReading>> hoaBillMap = new HashMap();
    private Map<String, Set<PrivateMeterReading>> privateBillMap = new HashMap();

    public void addHOAReading(HOAMeterReading meterReading) {
        if (!hoaBillMap.containsKey(meterReading.getMeter())) {
            hoaBillMap.put(meterReading.getMeter(), new HashSet<>());
        }
        hoaBillMap.get(meterReading.getMeter()).add(meterReading);
    }

    public void addPrivateReading(PrivateMeterReading meterReading) {
        if (!privateBillMap.containsKey(meterReading.getMeter())) {
            privateBillMap.put(meterReading.getMeter(), new HashSet<>());
        }
        privateBillMap.get(meterReading.getMeter()).add(meterReading);
    }

    public void takeADump() {
        log.info("hoa: {}", hoaBillMap.entrySet().size());
        log.info("homes: {}", privateBillMap.entrySet().size());
    }
}
