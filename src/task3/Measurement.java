package task3;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * ***************************************************************************************************************
 * File:Measurement.java
 * Description:
 * Used to hold measurement tuples.
 *
 * Does all the relevant to represent a measurement in the correct way, according to its id.
 * ****************************************************************************************************************
 */

public class Measurement {

    private int id;
    private long measurement;
    /*
     * Formatting styles used in the output, the decimal separator is set in the constructor
     */
    private static final SimpleDateFormat TimeStampFormat = new SimpleDateFormat("yyyy MM dd::hh:mm:ss:SSS");
    private static final DecimalFormat TEMPERATURE_FORMAT = new DecimalFormat("000.00000");
    private static final DecimalFormat ALTITUDE_FORMAT = new DecimalFormat("000000.00000");
    private static final DecimalFormat PRESSURE_FORMAT = new DecimalFormat("00.00000");

    /*
     * The ids used in the stream
     */
    public static final int ID_TIME = 0,
            ID_VELOCITY = 1,
            ID_ALTITUDE = 2,
            ID_PRESSURE = 3,
            ID_TEMPERATURE = 4,
            ID_ATTITUDE = 5,
            ID_WILDPOINT= ID_PRESSURE | (1 << 5);

    /**
     * Instantiates a Measurement object with the given id and measurement value.
     *
     * @param id The id
     * @param measurement The measurement value
     */
    public Measurement(int id, long measurement) {
        super();
        this.id = id;
        this.measurement = measurement;
        // set the decimal separator to a point to archive locale independence for the output.
        DecimalFormatSymbols pointSep = new DecimalFormatSymbols();
        pointSep.setDecimalSeparator('.');
        TEMPERATURE_FORMAT.setDecimalFormatSymbols(pointSep);
        ALTITUDE_FORMAT.setDecimalFormatSymbols(pointSep);
        PRESSURE_FORMAT.setDecimalFormatSymbols(pointSep);
    }

    /**
     * Instantiates a Measurement object with the given id and measurement value.
     *
     * @param id The id
     * @param measurement The measurement value
     */
    public Measurement(int id, double measurement) {
        this(id, Double.doubleToLongBits(measurement));
    }

    /**
     * Will convert the Measurement to ByteArray.
     * <p/>
     * The conversion will be done depending on the ID according to the specification
     * <p/>
     * ID = 0 will be converted as Timestamp
     * ID = 1 - 5 will be converted as Double
     *
     * @return ByteArray of the Measurement
     */
    public byte[] getMeasurementAsByteArray() {
        if (id == 0) {
            return ByteBuffer.allocate(8).putLong(this.getMeasurement()).array();
        } else {
            return ByteBuffer.allocate(8).putDouble(this.getMeasurementAsDouble()).array();
        }
    }

    public long getMeasurement() {
        return measurement;
    }

    /**
     * This method returns the Measurement as a String according to its id.
     * This method is different from the toString method,
     * as the toString method is used for debugging the the values of the object.
     *
     * This method only returns the value of the measurement.
     *
     * @return String value of the measurement
     */
    public String getMeasurementAsString() {
        String representation;
        switch (id){
            case ID_TIME:
                representation = TimeStampFormat.format(getMeasurementAsCalendar().getTime());
                break;
            case ID_ALTITUDE:
                representation = ALTITUDE_FORMAT.format(getMeasurementAsDouble());
                break;
            case ID_PRESSURE:
                representation = PRESSURE_FORMAT.format(getMeasurementAsDouble());
                break;
            case ID_WILDPOINT:
                representation = PRESSURE_FORMAT.format(getMeasurementAsDouble()) + "*";
                break;
            case ID_TEMPERATURE:
                representation = TEMPERATURE_FORMAT.format(getMeasurementAsDouble());
                break;
            default:
                representation = Double.toString(getMeasurementAsDouble());
                break;
        }

        return representation;
    }

    /**
     * Returns the Measurement converted to double. This is needed for ID = 1-5
     *
     * @return double
     */
    public double getMeasurementAsDouble() {
        return Double.longBitsToDouble(measurement);
    }

    /**
     * Returns the Measurement converted to a Calendar instance. This is needed for ID = 0
     *
     * @return Calendar
     */
    public Calendar getMeasurementAsCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(measurement);
        return calendar;
    }

    /**
     * This method will convert the id to a 4byte long array representation.
     *
     * @return The id as byte[]
     */
    public byte[] getIdAsByteArray() {
        return ByteBuffer.allocate(4).putInt(this.getId()).array();
    }

    /**
     * This method returns a String representation of the current object.
     *
     * This method is used for debugging purposes.
     *
     * @return Object as String
     */
    public String toString() {
        return "Measurement with ID: " + id + " and Value: " + getMeasurementAsString();
    }

    /*
     * We skip obvious comments for getters and setters...
     */

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
