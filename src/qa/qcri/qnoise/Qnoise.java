/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */
package qa.qcri.qnoise;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.collect.Lists;
import org.apache.commons.cli.*;
import qa.qcri.qnoise.util.Tracer;

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

            if (line.hasOption("v")) {
                Tracer.setVerbose(true);
            }

            String fileName = line.getOptionValue("f");
            if (Files.notExists(Paths.get(fileName))) {
                throw new FileNotFoundException("Input file " + fileName + " does not exist.");
            }
            reader = new CSVReader(new FileReader(fileName));
            header = reader.readNext();
            entries = reader.readAll();

            NoiseSpec spec = NoiseSpec.valueOf(line);
            NoiseReport report = new NoiseReport(spec);

            NoiseGenerator.Type type =
                NoiseGenerator.Type.getGeneratorType(line.getOptionValue("t"));
            switch (type) {
                case Missing:
                    new NoiseGenerator().missingInject(spec, entries, report);
                case Duplicate:
                    new NoiseGenerator().duplicateInject(spec, entries, report);
            }

            fileName = line.getOptionValue("o");
            writer = new CSVWriter(new FileWriter(fileName));
            writer.writeAll(entries);
            writer.flush();
            report.addMetric(NoiseReport.Metric.OutputRow, entries.size());

            report.print();

        } catch (MissingOptionException me) {
            printHelp();
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
            "qnoise.sh -f <input csv file> [OPTIONS]",
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
                withArgName("[row | cell]").
                isRequired().
                hasArg().
                withDescription("Injection data granularity, default value is row.").
                withType(String.class).
                create("g")
        );

        options.addOption(
            OptionBuilder.
                withDescription("Verbose output.").
                create("v")
        );

        options.addOption(
            OptionBuilder.
                withArgName("<Missing value parameter>").
                hasArg().
                withDescription("Inject missing noises.").
                withType(String.class).
                create("m")
        );

        options.addOption(
            OptionBuilder.
                withArgName("<Duplicate value parameter>").
                hasArg().
                withDescription("Inject duplicate noises.").
                withType(String.class).
                create("d")
        );

        options.addOption(
            OptionBuilder.
                withArgName("<Inconsistency parameter>").
                hasArg().
                withDescription("Inject inconsistency noises.").
                withType(String.class).
                create("i")
        );

        options.addOption(
            OptionBuilder.
                withArgName("<Outlier parameter>").
                hasArg().
                withDescription("Inject outlier noises.").
                withType(String.class).
                create("o")
        );

        return options;
    }
}
