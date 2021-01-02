package org.langwah.hoa.documents;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.langwah.hoa.domain.PrivateMeterReading;
import org.langwah.hoa.service.WaterBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;

import static org.apache.poi.ss.usermodel.CellType.NUMERIC;

@Component("xlsStripper")
@Slf4j
public class XLSStripper {
    DateTimeFormatter df = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("MMM").toFormatter();
    private WaterBillService waterBillService;

    @Autowired
    public XLSStripper(WaterBillService waterBillService) {
        this.waterBillService = waterBillService;
    }

    public void extract(File file) {
        try {
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(file));
            XSSFSheet sheet = wb.getSheetAt(0);
            XSSFRow row;
            XSSFCell cell;

            int rows; // No of rows
            rows = sheet.getPhysicalNumberOfRows();

            List<Month> months = new ArrayList<>();
            YearMonth yearMonth = YearMonth.now();
            for(int r = 0; r < rows; r++) {
                row = sheet.getRow(r);
                if(row != null) {
                    // do we have the month yet
                    if (months.size() == 0) {
                        for (int i=3; i<row.getPhysicalNumberOfCells(); i++) {
                            cell = row.getCell((short)i);
                            if (cell != null && !"".equals(cell.getStringCellValue())) {
                                TemporalAccessor accessor = df.parse(cell.getStringCellValue());
                                Month month = Month.from(accessor);
                                months.add(month);
                            }
                        }
                    } else {
                        long previous;
                        long current = 0;
                        for (int i=3; i<row.getPhysicalNumberOfCells(); i++) {
                            cell = row.getCell((short) i);
                            if (cell != null && cell.getCellType().equals(NUMERIC)) {

                                // turn into local dates
                                int month = months.get(i-3).getValue();
                                int year = (yearMonth.getMonthValue() < month)?yearMonth.getYear()-1:yearMonth.getYear();
                                YearMonth readingYearMonth = YearMonth.of(year, month);
                                LocalDate from = LocalDate.of(year, month, 1);
                                LocalDate to = LocalDate.of(year, month, readingYearMonth.lengthOfMonth());
                                previous = current;
                                current = (int) row.getCell((short) i).getNumericCellValue();
                                PrivateMeterReading privateMeterReading = PrivateMeterReading.builder()
                                        .meter(row.getCell((short) 1).getRawValue())
                                        .address(row.getCell((short) 2).getStringCellValue())
                                        .consumption(current - previous)
                                        .from(java.sql.Date.valueOf(from))
                                        .to(java.sql.Date.valueOf(to))
                                        .previous(previous)
                                        .current(current)
                                        .build();


                                waterBillService.addPrivateReading(privateMeterReading);
                            }
                        }
                    }
                }
            }
        } catch(Exception ioe) {
            ioe.printStackTrace();
        }
    }
}
