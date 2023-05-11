package task3;

import java.util.ArrayList;
import java.util.List;

/**
 * ***************************************************************************************************************
 * File:LessThanFilter.java
 * <p/>
 * Description:
 * <p/>
 * The filter will check the measurement with the given id. If the measurement is above the given limit it will
 * be deleted from the DataFrame.
 * <p/>
 * ****************************************************************************************************************
 */

public class LessThanFilter extends MeasurementFramework {
    private final int id;
    private final double limit;

    private boolean lessThanLimit = false;
    private List<Measurement> frame = new ArrayList<Measurement>();

    /**
     * Instantiates a new LessThanFilter object.
     *
     * @param id    The id of the measurement
     * @param limit The upper bound value for the measurement with the given id
     */
    public LessThanFilter(int id, double limit) {
        super(1, 2);
        this.id = id;
        this.limit = limit;
    }

    public void run() {

        while (true) {
            try {
                Measurement measurement = readMeasurementIn();

                if (measurement.getId() == 0) {
                    int port = 0;
                    if (lessThanLimit) {
                        port = 1;
                    }
                    for (Measurement m : frame) {
                        writeMeasurementOut(m, port);
                    }
                    frame.clear();
                } else if (measurement.getId() == id) {
                    if (measurement.getMeasurementAsDouble() < limit) {
                        lessThanLimit = true;
                    } else {
                        lessThanLimit = false;
                    }
                }
                frame.add(measurement);

            } catch (EndOfStreamException e) {
            	if (!frame.isEmpty()){
                    int port = 0;
                    if (lessThanLimit) {
                        port = 1;
                    }
                    for (Measurement m : frame) {
                        writeMeasurementOut(m, port);
                    }
                    frame.clear();
            	}
                ClosePorts();
                System.out.print("\n" + this.getName() + "::LessThanFilter Exiting;");
                break;
            }
        }

    }
}