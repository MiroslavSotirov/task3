package task3;

public class Plumber {
    public static void main(String argv[]) {

        SourceFilter sourceFilterA = new SourceFilter("SubSetA.dat");
        SourceFilter sourceFilterB = new SourceFilter("SubSetB.dat");
        MergeInput mergeInput = new MergeInput();
        LowerThan lowerThan10 = new LowerThan(2, 10000);
        PressureWildPoints pressureWildPoints = new PressureWildPoints(3, 10);

        SinkFilter sinkFilter = new SinkFilter(new int[]{0, 1, 2, 3, 4, 5}, "OutputC.dat");
        SinkFilter wildpointsSinkFilter = new SinkFilter(new int[]{0, 3}, "PressureWildPoints.dat");
        SinkFilter lower10SinkFilter = new SinkFilter(new int[]{0, 1, 2, 3, 4, 5}, "LowerThan10.dat");

        sinkFilter.Connect(pressureWildPoints, 0, 0);
        wildpointsSinkFilter.Connect(pressureWildPoints, 0, 1);
        pressureWildPoints.Connect(lowerThan10, 0, 0);
        lower10SinkFilter.Connect(lowerThan10, 0, 1);
        lowerThan10.Connect(mergeInput, 0, 0);
        mergeInput.Connect(sourceFilterA, 0, 0);
        mergeInput.Connect(sourceFilterB, 1, 0);

        sourceFilterA.start();
        sourceFilterB.start();
        mergeInput.start();
        lowerThan10.start();
        pressureWildPoints.start();
        sinkFilter.start();
        wildpointsSinkFilter.start();
        lower10SinkFilter.start();

    }
}