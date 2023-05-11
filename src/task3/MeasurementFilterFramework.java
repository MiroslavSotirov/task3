package task3;

/**
 * ***************************************************************************************************************
 * File:MeasurementFilterFramework.java
 * <p/>
 * Description:
 * <p/>
 * This Class extends the FilterFramework to be able to read and write Measurements to the pipes.
 * <p/>
 * ****************************************************************************************************************
 */
public class MeasurementFilterFramework extends FilterFramework {

	public MeasurementFilterFramework(int inputs, int outputs) 
	{
		super(inputs, outputs);
	}

	public MeasurementFilterFramework(){
		super(1, 1);
	}

	final int MEASUREMENT_LENGTH = 8;   // This is the length of all measurements (including time) in bytes
	final int ID_LENGTH = 4;            // This is the length of IDs in the byte stream

	Measurement readMeasurementFromInput() throws EndOfStreamException
	{
		return readMeasurementFromInput(0);
	}

	Measurement readMeasurementFromInput(int portID) throws EndOfStreamException
	{
		byte databyte;                // This is the data byte read from the stream

		long measurement;             // This is the word used to store all measurements
		int id;                       // This is the measurement id

		try 
		{
			id = 0;

			for (int i = 0; i < ID_LENGTH; i++)
			{
				databyte = ReadFilterInputPort(portID); // This is where we read the byte from the stream...

				id = id | (databyte & 0xFF);            // We append the byte on to ID...

				if (i != ID_LENGTH - 1)                 // If this is not the last byte, then slide the
				{                                       // previously appended byte to the left by one byte
					id = id << 8;                       // to make room for the next byte we append to the ID

				} // if

			} // for

			measurement = 0;

			for (int i = 0; i < MEASUREMENT_LENGTH; i++)
			{
				databyte = ReadFilterInputPort(portID);
				measurement = measurement | (databyte & 0xFF);  // We append the byte on to measurement...

				if (i != MEASUREMENT_LENGTH - 1)                // If this is not the last byte, then slide the
				{                                               // previously appended byte to the left by one byte
					measurement = measurement << 8;             // to make room for the next byte we append to the
				} // if

			} // if

			return new Measurement(id, measurement);
		} catch (Exception e) 
		{
			throw new EndOfStreamException("Stream ended unexpectedly.");
		}

	}

	void writeMeasurementToOutput(Measurement measurement)
	{
		writeMeasurementToOutput(measurement, 0);
	}

	void writeMeasurementToOutput(Measurement measurement, int portID)
	{
		byte[] bytesID = measurement.getIdAsByteArray();
		byte[] bytesMeasurement = measurement.getMeasurementAsByteArray();

		for (byte b : bytesID) {
			WriteFilterOutputPort(b, portID);
		}

		for (byte b : bytesMeasurement) {
			WriteFilterOutputPort(b, portID);
		}
	}

}
