package task3;

import java.util.ArrayList;
import java.util.List;

public class LowerThan extends MeasurementFramework {
    private final int id;
    private final double limit;

    private boolean lowLimit = false;
    private List<Measurement> frame = new ArrayList<Measurement>();

    public LowerThan(int id, double limit) {
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
                    if (lowLimit) {
                        port = 1;
                    }
                    for (Measurement m : frame) {
                        writeMeasurementOut(m, port);
                    }
                    frame.clear();
                } else if (measurement.getId() == id) {
                    if (measurement.getMeasurementDouble() < limit) {
                        lowLimit = true;
                    } else {
                        lowLimit = false;
                    }
                }
                frame.add(measurement);

            } catch (EndOfStreamException e) {
            	if (!frame.isEmpty()){
                    int port = 0;
                    if (lowLimit) {
                        port = 1;
                    }
                    for (Measurement m : frame) {
                        writeMeasurementOut(m, port);
                    }
                    frame.clear();
            	}
                ClosePorts();
                System.out.print("\n" + this.getName() + "::LowerThan Exiting;");
                break;
            }
        }

    }
}