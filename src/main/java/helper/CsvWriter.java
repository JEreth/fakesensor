package helper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * CSV Helper functions
 */
public class CsvWriter {

    private static final char DEFAULT_SEPARATOR = ';';

    public void writLine(String sensorId, HashMap<String, String> values) {
        try {
            boolean first = true;
            StringBuilder sb = new StringBuilder();

            // generate writer
            File csvFile = new File(sensorId + ".csv");

            // generate header line - if file does not exist
            if (!csvFile.exists()) {
                for (String key : values.keySet()) {
                    if (!first) {
                        sb.append(DEFAULT_SEPARATOR);
                    }
                    sb.append(followCsvFormat(key));
                    first = false;
                }
                sb.append("\n");
                first = true;
            }

            // load file
            FileWriter writer = null;
            Writer w = new FileWriter(csvFile, true);

            // add value line
            for (Map.Entry<String, String> valueEntry : values.entrySet()) {
                String value = valueEntry.getValue();
                if (!first) {
                    sb.append(DEFAULT_SEPARATOR);
                }
                sb.append(followCsvFormat(value));

                first = false;
            }
            sb.append("\n");
            w.append(sb.toString());
            w.flush();
            w.close();
            System.out.println("WRITE: " + sb.toString());
        } catch (IOException e) {
            e.printStackTrace(); // cant write file
        }
    }

    //https://tools.ietf.org/html/rfc4180
    private static String followCsvFormat(String value) {

        String result = value;
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;

    }
}
