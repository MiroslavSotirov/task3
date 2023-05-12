package task3;

import java.util.ArrayList;
import java.util.List;

public class PressureWildPoints extends MeasurementFramework {
    private final int id;
    private final int estimateId;
    private final double diversion;
    private List<Measurement> cache = new ArrayList<>();

    public PressureWildPoints(int id, double diversion) {
        super(1, 2);
        this.id = id;
        estimateId = id | (1 << 5);
        this.diversion = diversion;
    }

    private boolean isValid(Measurement previousValidPoint, Measurement measurement) {
        if (measurement.getMeasurementDouble() < 0) {
            return false;
        }
        if (previousValidPoint != null) {
            return Math.abs(measurement.getMeasurementDouble() - previousValidPoint.getMeasurementDouble()) <= diversion;
        }
        return true;
    }

    private Measurement estimate(Measurement previousValid, Measurement nextValid) {
        if (previousValid == null && nextValid == null) {
            throw new NullPointerException("previous valid and next valid are null");
        } else if (previousValid == null) {
            return new Measurement(estimateId, nextValid.getMeasurementDouble());
        } else if (nextValid == null) {
            return new Measurement(estimateId, previousValid.getMeasurementDouble());
        } else {
            return new Measurement(estimateId, (previousValid.getMeasurementDouble() + nextValid.getMeasurementDouble()) / 2);
        }
    }

    private void Cache(Measurement previousValidPoint, Measurement currentMeasurement, int wildPoints){
    	for (Measurement m : cache){
    		if (m.getId() == Measurement.TIME_ID){
                writeMeasurementOut(m, 0);
    			if (wildPoints > 0){
                    writeMeasurementOut(m, 1);
    			}
    		} else if (m.getId() == this.id){
    			Measurement estimated = estimate(previousValidPoint, currentMeasurement);
                writeMeasurementOut(estimated, 0);
                writeMeasurementOut(m, 1);
    			wildPoints--;
    		} else {
                writeMeasurementOut(m);
    		}
    	}
    	
    	if (currentMeasurement != null){
            writeMeasurementOut(currentMeasurement);
    	}

    	cache.clear();
    }
    
    public void run() {
    	Measurement previousValidPoint = null;
    	int wildPoints = 0;
        while (true) {
            try {
                Measurement measurement = readMeasurementIn();

                if (measurement.getId() == this.id){
                	if (isValid(previousValidPoint, measurement)){
                        Cache(previousValidPoint, measurement, wildPoints);
                        previousValidPoint = measurement;
                		wildPoints = 0;
                	} else {
                    	cache.add(measurement);
                    	wildPoints++;
                	}
                } else {
                	cache.add(measurement);
                }


            } catch (EndOfStreamException e) {
                Cache(previousValidPoint, null, wildPoints);
                ClosePorts();
                System.out.print("\n" + this.getName() + "::WildPoints Exiting;");
                break;
            }
        }
    }
}