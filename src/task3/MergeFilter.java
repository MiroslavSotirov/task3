package task3;

/**
 * *****************************************************************************
 * File: MergeFilter.java
 * <p/>
 * Description:
 * <p/>
 * Merges two Inputs and sorts them by Date.
 * <p/>
 * *****************************************************************************
 */

public class MergeFilter extends MeasurementFramework {

    /**
     * Instantiates a new MergeFilter object.
     */
    public MergeFilter() {
        super(2, 1);
    }

    /**
     * This method writes the given measurement to the output.
     * If a new measurement of the corresponding Input port is available we will return that. Else we return null.
     * <p/>
     *
     * @param measurement the measurement to be outputted
     * @param portID      the port of the measurement
     * @return the next measurement or if none available we will return null
     */
    private Measurement forward(Measurement measurement, int portID) {
        try {
            do {
                writeMeasurementOut(measurement);
                measurement = readMeasurementIn(portID);
            } while (measurement.getId() != 0);
        } catch (EndOfStreamException e) {
            return null;
        }
        return measurement;
    }

    public void run() {
        Measurement measurementA;
        Measurement measurementB;

        try {
            measurementA = readMeasurementIn(0);
            measurementB = readMeasurementIn(1);

            while (true) {

                /**
                 * Here we check which measurement shall be forwarded by comparing the dates of each measurement
                 */
                if (measurementA == null && measurementB == null) {
                    break;
                } else if (measurementA == null) {
                    measurementB = forward(measurementB, 1);
                } else if (measurementB == null) {
                    measurementA = forward(measurementA, 0);
                } else if (measurementA.getMeasurementAsCalendar().compareTo(
                        measurementB.getMeasurementAsCalendar()) <= 0) {
                    measurementA = forward(measurementA, 0);
                } else {
                    measurementB = forward(measurementB, 1);
                }
            }

        } catch (EndOfStreamException e) {
            ClosePorts();
            System.out.print("\n" + this.getName() + "::MergeFilter Exiting;");
        }

    }
}