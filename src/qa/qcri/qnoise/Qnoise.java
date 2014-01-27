/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */
package qa.qcri.qnoise;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import org.apache.commons.cli.*;
import qa.qcri.qnoise.inject.DuplicateInjector;
import qa.qcri.qnoise.inject.InconsistencyInjector;
import qa.qcri.qnoise.inject.MissingInjector;
import qa.qcri.qnoise.inject.OutlierInjector;
import qa.qcri.qnoise.util.Tracer;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Qnoise launcher.
 */
public class Qnoise {
    private static Options options;
    private static PrintStream tracer = System.err;

    public static void main(String[] args) {
        options = createQnoiseOption();
        CommandLineParser parser = new GnuParser();

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

            JsonReader jsonReader =
                new JsonReader(
                    new InputStreamReader(
                        new FileInputStream(fileName),
                        Charset.forName("UTF-8")
                    )
                );

            GsonBuilder gson = new GsonBuilder();
            gson.registerTypeAdapter(NoiseSpec.class, new NoiseSpecDeserializer());
            gson.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
            NoiseSpec spec = gson.create().fromJson(jsonReader, NoiseSpec.class);
            NoiseReport report = new NoiseReport(spec);
            reader =
                new CSVReader(new FileReader(spec.inputFile), spec.csvSeparator);

            DataProfile profile = DataProfile.readData(reader, spec.schema);
            report.addMetric(NoiseReport.Metric.InputRow, profile.getLength());
            switch (spec.noiseType) {
                case Missing:
                    new MissingInjector().inject(spec, profile, report);
                    break;
                case Duplicate:
                    new DuplicateInjector().inject(spec, profile, report);
                    break;
                case Inconsistency:
                    new InconsistencyInjector().inject(spec, profile, report);
                    break;
                case Outlier:
                    new OutlierInjector().inject(spec, profile, report);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown noise type.");
            }

            fileName = line.getOptionValue("o");
            writer = new CSVWriter(
                new FileWriter(fileName),
                spec.csvSeparator,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.NO_ESCAPE_CHARACTER
            );
            profile.writeData(writer);
            report.appendMetric(NoiseReport.Metric.OutputFilePath, fileName);
            report.addMetric(NoiseReport.Metric.OutputRow, profile.getLength());
            report.saveToFile(spec.logFile);
            report.print();

        } catch (MissingOptionException me) {
            printHelp();
        } catch (ParseException ex) {
            ex.printStackTrace();
            printHelp();
        } catch (Exception ex) {
            tracer.println("Exception : " + ex.getMessage());
            ex.printStackTrace();
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
            "qnoise.sh -f <input JSON file> -o <output file>",
            "All the options:",
            options,
            null
        );
    }

    @SuppressWarnings("all")
    private static Options createQnoiseOption() {
        Options options = new Options();
        options.addOption(OptionBuilder.withDescription("Print this message.").create("help"));
        options.addOption(
            OptionBuilder.
                withArgName("file").
                isRequired().
                hasArg().
                withDescription("Input JSON file path.").
                withType(String.class).
                create("f")
        );

        options.addOption(
            OptionBuilder.
                withDescription("Verbose output.").
                create("v")
        );

        options.addOption(
            OptionBuilder.
                withArgName("output file").
                hasArg().
                isRequired().
                withDescription("Output file path.").
                withType(String.class).
                create("o")
        );

        return options;
    }
}
