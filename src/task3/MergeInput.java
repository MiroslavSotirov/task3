package task3;

public class MergeInput extends MeasurementFramework {

    public MergeInput() {
        super(2, 1);
    }

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
                if (measurementA == null && measurementB == null) {
                    break;
                } else if (measurementA == null) {
                    measurementB = forward(measurementB, 1);
                } else if (measurementB == null) {
                    measurementA = forward(measurementA, 0);
                } else if (measurementA.getMeasurementCal().compareTo(
                        measurementB.getMeasurementCal()) <= 0) {
                    measurementA = forward(measurementA, 0);
                } else {
                    measurementB = forward(measurementB, 1);
                }
            }

        } catch (EndOfStreamException e) {
            ClosePorts();
            System.out.print("\n" + this.getName() + "::MergeInput Exiting;");
        }

    }
}