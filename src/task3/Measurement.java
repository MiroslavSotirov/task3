package task3;

import java.nio.ByteBuffer;
import java.text.DecimalFormatSymbols;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Measurement {

    private int id;
    private long measurement;
    private static final DecimalFormat PRESSURE = new DecimalFormat("00.00000");
    private static final DecimalFormat ALTITUDE = new DecimalFormat("000000.00000");
    private static final SimpleDateFormat TIMESTAMP = new SimpleDateFormat("yyyy MM dd::hh:mm:ss:SSS");
    private static final DecimalFormat TEMPERATURE = new DecimalFormat("000.00000");

    public static final int TIME_ID = 0,
            TEMP_ID = 4,
            PRESSURE_ID = 3,
            ALT_ID = 2,
            WILDPOINT_ID = PRESSURE_ID | (1 << 5);

    public Measurement(int id, long measurement) {
        super();
        this.id = id;
        this.measurement = measurement;
        DecimalFormatSymbols pSeparator = new DecimalFormatSymbols();
        pSeparator.setDecimalSeparator('.');
        TEMPERATURE.setDecimalFormatSymbols(pSeparator);
        ALTITUDE.setDecimalFormatSymbols(pSeparator);
        PRESSURE.setDecimalFormatSymbols(pSeparator);
    }

    public Measurement(int id, double measurement) {
        this(id, Double.doubleToLongBits(measurement));
    }

    public byte[] getMeasurementByteArray() {
        if (id == 0) {
            return ByteBuffer.allocate(8).putLong(this.getMeasurement()).array();
        } else {
            return ByteBuffer.allocate(8).putDouble(this.getMeasurementDouble()).array();
        }
    }

    public long getMeasurement() {
        return measurement;
    }

    public String getMeasurementStr() {
        String represent;
        switch (id){
            case TIME_ID:
                represent = TIMESTAMP.format(getMeasurementCal().getTime());
                break;
            case ALT_ID:
                represent = ALTITUDE.format(getMeasurementDouble());
                break;
            case PRESSURE_ID:
                represent = PRESSURE.format(getMeasurementDouble());
                break;
            case WILDPOINT_ID:
                represent = PRESSURE.format(getMeasurementDouble()) + "*";
                break;
            case TEMP_ID:
                represent = TEMPERATURE.format(getMeasurementDouble());
                break;
            default:
                represent = Double.toString(getMeasurementDouble());
                break;
        }

        return represent;
    }

    public double getMeasurementDouble() {
        return Double.longBitsToDouble(measurement);
    }

    public Calendar getMeasurementCal() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(measurement);
        return calendar;
    }

    public byte[] getIdByte() {
        return ByteBuffer.allocate(4).putInt(this.getId()).array();
    }

    public String toString() {
        return "Measurement ID: " + id + " and val: " + getMeasurementStr();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMeasurement(long measurement) {
        this.measurement = measurement;
    }

}
