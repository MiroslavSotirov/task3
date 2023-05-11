package task3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 ****************************************************************************************************************
 * File:SinkFilter.java
 * <p/>
 * Description:
 * <p/>
 * This class writes the desired Output in the given order to a file, denoted by the filename.
 * If the filename is <code>null</code> System.out will be used.
 * <p/>
 *****************************************************************************************************************
 */


public class SinkFilter extends MeasurementFramework {

	private int[] orderedIds;
	File file;
	FileWriter fw;
	BufferedWriter bw;

	/**
	 * Set the order of Columns to print
	 *
	 * @param orderedIds
	 *            The order of the Ids
	 * @param fileName to write to or <code>null</code> if System.out is desired.
	 */
	public SinkFilter(int[] orderedIds, String fileName) {
		super(1, 1);
		this.orderedIds = orderedIds;

		if (fileName != null){

			file = new File(fileName);

			try {
				if (!file.exists()) {
					file.createNewFile();
				}
				fw = new FileWriter(file.getAbsoluteFile());
				bw = new BufferedWriter(fw);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void run() {

		/**
		 * Initialize the HashMap for a DataFrame
		 */
		HashMap<Integer, Measurement> outputMap = new HashMap<Integer, Measurement>();
		Measurement m;
		String outputString = "";

		try {

			while (true) {

				Measurement readMeasurement = readMeasurementIn();
				outputMap.put(readMeasurement.getId(), readMeasurement);
				
				// Print the required Measurements in the given order
				if (outputMap.size() == orderedIds.length) {
					for (int i = 0; i < orderedIds.length; i++) {
						int orderedId = orderedIds[i];
						m = outputMap.get(orderedId);
						if (m == null){
							m = outputMap.get(orderedId | (1 << 5));
						}
						outputString+= m.getMeasurementAsString();
						if (i < orderedIds.length -1){
							outputString += ',';
						}
					}
					if (bw == null){
						System.out.println(outputString);
					} else {
						bw.write(outputString);
						bw.newLine();
					}
					outputString = "";
					outputMap.clear();
				}

			}
		} catch (EndOfStreamException | IOException e) {
			ClosePorts();
			System.out.println("\n" + this.getName() + "::Sink Exiting;");
		} finally {
			try {
				if (bw != null){
					bw.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}