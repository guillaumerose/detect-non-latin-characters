package fr.guillaumerose;

import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;

import static java.util.stream.Collectors.*;

public class CsvLogger implements Closeable {
    private final CSVPrinter printer;

    public CsvLogger() {
        try {
            printer = new CSVPrinter(new FileWriter("results.csv"), CSVFormat.DEFAULT.withDelimiter(';'));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Element changeset(long changeset) {
        try {
            return Jsoup.connect("http://www.openstreetmap.org/api/0.6/changeset/" + changeset).get()
                    .select("changeset").first();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void log(Entity entity, String... strings) {
        try {
            printer.print(entity.getType());
            printer.print(entity.getId());
            for (int i = 0; i < strings.length; i++) {
                printer.print(strings[i]);
            }
            printer.print(entity.getUser().getName());
            Map<String, String> tags = changeset(entity.getChangesetId())
                    .select("tag").stream().collect(toMap(e -> e.attr("k"), e -> e.attr("v")));
            printer.print(tags.get("created_by"));
            printer.print(tags.get("comment"));
            printer.println();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        printer.flush();
        printer.close();
    }
}