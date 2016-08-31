package fr.guillaumerose;

import java.lang.Character.UnicodeScript;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import static java.lang.Character.UnicodeScript.*;

public class UnicodeDetectorSink implements Sink {
    private final AtomicLong counter = new AtomicLong(0);
    private final CsvLogger csvLogger;

    public UnicodeDetectorSink(CsvLogger csvLogger) {
        this.csvLogger = csvLogger;
    }

    @Override
    public void release() {}

    @Override
    public void complete() {}

    @Override
    public void initialize(Map<String, Object> metaData) {}

    @Override
    public void process(EntityContainer entityContainer) {
        Collection<Tag> tags = entityContainer.getEntity().getTags();
        for (Tag tag : tags) {
            if (tag.getKey().equals("name")) {
                UnicodeScript unicode = weirdUnicode(tag.getValue());
                if (unicode != null) {
                    csvLogger.log(entityContainer.getEntity(), unicode.toString(), tag.getValue());
                }
                break;
            }
        }
        long value = counter.incrementAndGet();
        if (value % 10000000 == 0) {
            System.out.println("Processed: " + value);
        }
    }

    public static UnicodeScript weirdUnicode(String s) {
        for (int i = 0; i < s.length();) {
            int codepoint = s.codePointAt(i);
            i += Character.charCount(codepoint);
            UnicodeScript unicode = Character.UnicodeScript.of(codepoint);
            if (unicode != LATIN && unicode != COMMON && unicode != INHERITED) {
                return unicode;
            }
        }
        return null;
    }
}
