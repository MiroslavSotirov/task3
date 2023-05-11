package task3;


public class MeasurementFramework extends FilterFramework {

	public MeasurementFramework(int inputs, int outputs)
	{
		super(inputs, outputs);
	}

	public MeasurementFramework(){
		super(1, 1);
	}

	final int ID_SIZE = 4;
	final int MEASUREMENT_SIZE = 8;

	Measurement readMeasurementIn() throws EndOfStreamException
	{
		return readMeasurementIn(0);
	}

	Measurement readMeasurementIn(int portID) throws EndOfStreamException
	{
		byte data;

		long measurement;
		int id;

		try
		{
			id = 0;

			for (int i = 0; i < ID_SIZE; i++)
			{
				data = ReadFilterInputPort(portID);

				id = id | (data & 0xFF);

				if (i != ID_SIZE - 1)
				{
					id = id << 8;

				} // if

			} // for

			measurement = 0;

			for (int i = 0; i < MEASUREMENT_SIZE; i++)
			{
				data = ReadFilterInputPort(portID);
				measurement = measurement | (data & 0xFF);

				if (i != MEASUREMENT_SIZE - 1)
				{
					measurement = measurement << 8;
				} // if

			} // if

			return new Measurement(id, measurement);
		} catch (Exception e)
		{
			throw new EndOfStreamException("Stream ended unexpectedly.");
		}

	}

	void writeMeasurementOut(Measurement measurement)
	{
		writeMeasurementOut(measurement, 0);
	}

	void writeMeasurementOut(Measurement measurement, int portID)
	{
		byte[] byteId = measurement.getIdAsByteArray();
		byte[] byteMeasurement = measurement.getMeasurementAsByteArray();

		for (byte b : byteId) {
			WriteFilterOutputPort(b, portID);
		}

		for (byte b : byteMeasurement) {
			WriteFilterOutputPort(b, portID);
		}
	}

}
