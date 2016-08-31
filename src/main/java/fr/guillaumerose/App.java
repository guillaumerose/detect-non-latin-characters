package fr.guillaumerose;

import java.io.File;
import java.io.IOException;

import org.openstreetmap.osmosis.pbf2.v0_6.PbfReader;

public class App {
    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        PbfReader reader = new PbfReader(new File(args[0]), 4);
        try (CsvLogger csvLogger = new CsvLogger()) {
            reader.setSink(new UnicodeDetectorSink(csvLogger));
            reader.run();
        }
        System.out.println("Time: " + (System.currentTimeMillis() - start) + "ms");
    }
}
