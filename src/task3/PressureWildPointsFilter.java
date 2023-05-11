package task3;

import java.util.ArrayList;
import java.util.List;

/**
 ****************************************************************************************************************
 * File:PressureWildPointsFilter.java
 * <p/>
 * Description:
 * <p/>
 * This Filter filters pressure wildpoints in the data. The wildpoints are replaced by extrapolated values.
 * The original value of the wildpoint is send to a separate pipe together with the timestamp.
 * <p/>
 * A wild point is any pressure data that varies more than 10PSI between samples
 * and/or is negative.For wild points encountered in the stream, extrapolate a replacement value
 * by using the last known valid measurement and the next valid measurement in the stream.
 * Extrapolate the replacement value by computing the average of the last valid measurement and
 * the next valid measurement in the stream. If a wild point occurs at the beginning of the stream,
 * replace it with the first valid value; if a wild point occurs at the end of the stream, replace it with
 * the last valid value.
 * <p/>
 * Required Input:
 * One input port with at least the measurement data for the pressure, otherwise it will stall.
 * <p/>
 * Output:
 * Two output ports, the first for the normal data flow with the extrapolated values and
 * the second for storing the wildpoints, preceded by its timestamp.
 * The extrapolated values have the bit 5 set in the id (givenId | (1 << 5))
 *****************************************************************************************************************
 */

public class PressureWildPointsFilter extends MeasurementFramework {
    private final int id;
    private final int extrapolateId;
    private final double deviation;

    /**
     * The cache of the filter, it's used to store the data with the exception of
     * the valid pressure measurements. Any pressure measurement in this list will be extrapolated.
     */
    private List<Measurement> cache = new ArrayList<>();

    /**
     * Instantiates a new PressureWildPointsFilter object.
     * It needs one input and supplies two output ports: <br>
     * 0 for the normal data <br>
     * 1 for the wildpoints <br>
     *
     * @param id        The id of the pressure data
     * @param deviation The maximum deviation for valid measurements
     */
    public PressureWildPointsFilter(int id, double deviation) {
        super(1, 2);
        this.id = id;
        extrapolateId = id | (1 << 5);
        this.deviation = deviation;
    }

    /**
     * A pressure measurement is invalid if it either is negative
     * or the deviation to the last valid value is more then specified.
     *
     * @param measurement The measurement that shall be checked
     * @return True if the measurement is valid, false otherwise
     */
    private boolean isValid(Measurement lastValidPoint, Measurement measurement) {
        if (measurement.getMeasurementDouble() < 0) {
            return false;
        } // if
        if (lastValidPoint != null) {
            return Math.abs(measurement.getMeasurementDouble() - lastValidPoint.getMeasurementDouble()) <= deviation;
        } // if
        return true;
    } // isValid

    /**
     * Extrapolates between the last and next valid measurement.
     * If either of the measurements is non-existent, the other is choosen.
     * At least one of the measurements must be existent.
     *
     * @param lastValid the last valid measurement
     * @param nextValid the next valid measurement
     * @return The extrapolated measurement
     */
    private Measurement extrapolate(Measurement lastValid, Measurement nextValid) {
        if (lastValid == null && nextValid == null) {
            throw new NullPointerException("lastValid and nextValid are null");
        } else if (lastValid == null) {
            return new Measurement(extrapolateId, nextValid.getMeasurementDouble());
        } else if (nextValid == null) {
            return new Measurement(extrapolateId, lastValid.getMeasurementDouble());
        } else {
            return new Measurement(extrapolateId, (lastValid.getMeasurementDouble() + nextValid.getMeasurementDouble()) / 2);
        } // if
    } // extrapolate
    
    /**
     * Processes the cache, extrapolating all pressure points with the {@link #lastValidPoint} and
     * given next valid point. <br><br>
     * <em>Clears the cache after forwarding it!</em>
     * @param validMeasurement next valid measurement
     */
    private void processCache(Measurement lastValidPoint, Measurement validMeasurement, int wildPoints){
    	for (Measurement m : cache){
    		if (m.getId() == Measurement.TIME_ID){
                writeMeasurementOut(m, 0);
    			if (wildPoints > 0){
                    writeMeasurementOut(m, 1);
    			}
    		} else if (m.getId() == this.id){
    			// if a pressure point is in the cache it's always extrapolating
    			Measurement extrapolated = extrapolate(lastValidPoint, validMeasurement);
                writeMeasurementOut(extrapolated, 0);
                writeMeasurementOut(m, 1);
    			wildPoints--;
    		} else {
                writeMeasurementOut(m);
    		}

    	} // for: cache
    	
    	if (validMeasurement != null){
            writeMeasurementOut(validMeasurement);
    	} // if
    	
    	cache.clear();
    } // processCache
    
    public void run() {
    	/*
    	 * Iterates over the input steam and reads non-pressure measurements into a cache to retain 
    	 * them if the pressure value is invalid.
    	 * If a non-valid pressure is found it's also added to the cache.
    	 * If a valid measurement is found the current cache is forwarded to the output streams
    	 * though the processCache procedure. 
    	 * 
    	 * All non-valid pressures must be added to the cache.
    	 * Valid pressures must not be added to the cache.
    	 */
    	Measurement lastValidPoint = null;
    	// if an invalid point is found we're in the state extrapolating
    	int wildPoints = 0;
        while (true) {
            try {
                Measurement measurement = readMeasurementIn();

                if (measurement.getId() == this.id){
                	if (isValid(lastValidPoint, measurement)){
                		// flushing the cache with the current valid point
                		processCache(lastValidPoint, measurement, wildPoints);
                		lastValidPoint = measurement;
                		wildPoints = 0;
                	} else {
                		// cache the wild point
                    	cache.add(measurement);
                    	wildPoints++;
                	} // if
                } else {
                	// cache the remaining data
                	cache.add(measurement);
                } // if


            } catch (EndOfStreamException e) {
            	/*
            	 * Flush the remaining cache
            	 */
            	processCache(lastValidPoint, null, wildPoints);
                ClosePorts();
                System.out.print("\n" + this.getName() + "::WildPoints Exiting;");
                break;
            } // try
        }
    } // run

} // PressureWildPointsFilter