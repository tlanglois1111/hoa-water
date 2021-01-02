package org.langwah.hoa.documents;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;
import org.langwah.hoa.domain.HOAMeterReading;
import org.langwah.hoa.service.WaterBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component("pdfStripper")
@Data
public class PDFStripper {
    private SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
    private WaterBillService waterBillService;

    @Autowired
    public PDFStripper(WaterBillService waterBillService) {
        this.waterBillService = waterBillService;
    }

    public void simpleExtract(File file) throws IOException {
        PDDocument document = PDDocument.load(file);
        AccessPermission ap = document.getCurrentAccessPermission();
        if (!ap.canExtractContent()) {
            throw new IOException("You do not have permission to extract text");
        }

        PDFTextStripper stripper = new PDFTextStripper();

        // This example uses sorting, but in some cases it is more useful to switch it off,
        // e.g. in some files with columns where the PDF content stream respects the
        // column order.
        stripper.setSortByPosition(true);

        //for (int p = 1; p <= document.getNumberOfPages(); ++p) {

        // Set the page interval to extract. If you don't, then all pages would be extracted.
        stripper.setStartPage(1);
        stripper.setEndPage(1);

        // let the magic happen
        String text = stripper.getText(document);

        // do some nice output with a header
        Stream<String> stream = Arrays.stream(text.trim().split( "\n" ));
        List<String> waterUsage = stream.filter(s -> s.startsWith("WATER  "))
                .collect(Collectors.toList());
        waterUsage.forEach(s->
                {
                    log.info(s);
                    String[] parts = s.split("\\s+");
                    try {
                        HOAMeterReading meterReading = HOAMeterReading.builder()
                                .from(df.parse(parts[1]))
                                .to(df.parse(parts[3]))
                                .days(Integer.parseInt(parts[4]))
                                .meter(parts[5])
                                .current(Long.parseLong(parts[6]))
                                .previous(Long.parseLong(parts[7]))
                                .consumption(Long.parseLong(parts[8]))
                                .build();

                        waterBillService.addHOAReading(meterReading);
                    } catch (ParseException e) {
                        log.error(e.getMessage());
                    }
                });

        document.close();
    }

}
