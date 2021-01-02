package org.langwah.hoa;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class HOAWaterRouter extends RouteBuilder {

    @Override
    public void configure() {
        from("file:pdf?noop=true&include=.*\\.pdf").routeId("pdfFileRoute")
                .transform().method("pdfStripper", "simpleExtract")
                .to("stream:out");
        from("file:pdf?noop=true&include=.*\\.xlsx").routeId("xlsFileRoute")
                .transform().method("xlsStripper", "extract")
                .to("stream:out");
    }

}
