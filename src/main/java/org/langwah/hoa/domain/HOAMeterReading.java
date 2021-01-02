package org.langwah.hoa.domain;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

@Data
@Builder
public class HOAMeterReading {
    private Date from;
    private Date to;
    private String meter;
    private int days;
    private long consumption;
    private long current;
    private long previous;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HOAMeterReading that = (HOAMeterReading) o;
        return from.equals(that.from) && to.equals(that.to) && meter.equals(that.meter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, meter);
    }
}
