/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */
package qa.qcri.qnoise;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.collect.Lists;
import org.apache.commons.cli.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Qnoise launcher.
 */
public class Qnoise {
    private static Options options;
    private static PrintStream tracer = System.err;

    public static void main(String[] args) {
        options = createQnoiseOption();
        CommandLineParser parser = new GnuParser();
        List<String[]> entries = Lists.newArrayList();
        String[] header = null;
        CSVReader reader = null;
        CSVWriter writer = null;
        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("f")) {
                String fileName = line.getOptionValue("f");
                if (Files.notExists(Paths.get(fileName))) {
                    throw new FileNotFoundException("Input file " + fileName + " does not exist.");
                }
                reader = new CSVReader(new FileReader(fileName));
                header = reader.readNext();
                entries = reader.readAll();
            }

            double perc = Double.parseDouble(line.getOptionValue("p"));
            NoiseModal modal = NoiseModal.getNoiseModal(line.getOptionValue("m"));
            NoiseGranularity granularity =
                NoiseGranularity.getNoiseGranularity(line.getOptionValue("g"));
            NoiseSpec spec =
                new NoiseSpec.NoiseSpecBuilder()
                    .modal(modal)
                    .granularity(granularity)
                    .perc(perc)
                    .build();

            NoiseReport report = new NoiseReport(spec);

            new NoiseGenerator().nullInject(spec, entries, report);

            if (line.hasOption("o")) {
                String fileName = line.getOptionValue("o");
                writer = new CSVWriter(new FileWriter(fileName));
                writer.writeAll(entries);
                writer.flush();
                report.addMetric(NoiseReport.Metric.OutputRow, entries.size());
            }

            report.print();

        } catch (ParseException ex) {
            ex.printStackTrace();
            printHelp();
        } catch (Exception ex) {
            tracer.println("Exception : " + ex.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }

                if (writer != null) {
                    writer.close();
                }
            } catch (Exception ex) {}
        }
    }

    private static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(
            100,
            "qnoise.sh -f <input csv file> -o <output csv file> [OPTIONS]",
            "All the options:",
            options,
            null
        );
    }

    private static Options createQnoiseOption() {
        Options options = new Options();
        options.addOption(OptionBuilder.withDescription("Print this message.").create("help"));
        options.addOption(
            OptionBuilder.
                withArgName("file").
                isRequired().
                hasArg().
                withDescription("Input with CSV file.").
                withType(String.class).
                create("f")
        );

        options.addOption(
            OptionBuilder.
                withArgName("percentage").
                isRequired().
                hasArg().
                withDescription("Injection data percentage.").
                withType(Double.class).
                create("p")
        );

        options.addOption(
            OptionBuilder.
                withArgName("[row | cell]").
                isRequired().
                hasArg().
                withDescription("Injection data granularity, default value is row.").
                withType(String.class).
                create("g")
        );

        options.addOption(
            OptionBuilder.
                withArgName("[random | norm]").
                isRequired().
                hasArg().
                withDescription("Noise distribution modal, default value is random.").
                withType(String.class).
                create("m")
        );
        /*
        options.addOption(
            OptionBuilder.
                    withArgName("url").
                    hasArg().
                    withDescription(
                            "Input database JDBC url, e.g. " +
                                    "jdbc:postgresql://localhost/test?usr=myUser&password=myPassword").
                    create("url")
        );
        */
        options.addOption(
            OptionBuilder.
                withArgName("file").
                isRequired().
                hasArg().
                withDescription("Output to CSV file.").
                create("o")
        );

        return options;
    }
}
