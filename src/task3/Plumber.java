package task3;

/**
 * ***************************************************************************************************************
 * File:Plumber.java
 * <p/>
 * Description:
 * <p/>
 * This class instantiates and connects all of our Filters to create the output.
 * <p/>
 * ****************************************************************************************************************
 */
public class Plumber {
    public static void main(String argv[]) {

        SourceFilter sourceFilterA = new SourceFilter("SubSetA.dat");
        SourceFilter sourceFilterB = new SourceFilter("SubSetB.dat");
        MergeFilter mergeFilter = new MergeFilter();
        LessThanFilter lessThan10KFilter = new LessThanFilter(2, 10000);
        PressureWildPointsFilter wildpointsFilter = new PressureWildPointsFilter(3, 10);

        SinkFilter sinkFilter = new SinkFilter(new int[]{0, 1, 2, 3, 4, 5}, "OutputC.dat");
        SinkFilter wildpointsSinkFilter = new SinkFilter(new int[]{0, 3}, "PressureWildPoints.dat");
        SinkFilter less10kSinkFilter = new SinkFilter(new int[]{0, 1, 2, 3, 4, 5}, "LessThan10K.dat");

        // connect the filters to each other
        sinkFilter.Connect(wildpointsFilter, 0, 0);
        wildpointsSinkFilter.Connect(wildpointsFilter, 0, 1);
        wildpointsFilter.Connect(lessThan10KFilter, 0, 0);
        less10kSinkFilter.Connect(lessThan10KFilter, 0, 1);
        lessThan10KFilter.Connect(mergeFilter, 0, 0);
        mergeFilter.Connect(sourceFilterA, 0, 0);
        mergeFilter.Connect(sourceFilterB, 1, 0);

        // start the filters
        sourceFilterA.start();
        sourceFilterB.start();
        mergeFilter.start();
        lessThan10KFilter.start();
        wildpointsFilter.start();
        sinkFilter.start();
        wildpointsSinkFilter.start();
        less10kSinkFilter.start();

    }
}