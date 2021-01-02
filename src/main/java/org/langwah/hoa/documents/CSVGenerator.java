package org.langwah.hoa.documents;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.langwah.hoa.domain.PrivateMeterReading;
import org.langwah.hoa.domain.SummaryReading;
import org.langwah.hoa.service.WaterBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.poi.ss.usermodel.CellType.NUMERIC;

@Component("csvGenerator")
@Slf4j
public class CSVGenerator {
    DateTimeFormatter df = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("MMM").toFormatter();
    private WaterBillService waterBillService;

    @Autowired
    public CSVGenerator(WaterBillService waterBillService) {
        this.waterBillService = waterBillService;
    }

    public void generate(File file) {
        try {
            List<SummaryReading> summaryReadings = waterBillService.takeADump();
            if (!file.canWrite()) {
               log.error("file is not writable");
            }
            try (PrintWriter pw = new PrintWriter(file)) {
                summaryReadings.stream()
                        .map(SummaryReading::toCsvRow)
                        .forEach(pw::println);
            }
         } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
