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
import qa.qcri.qnoise.inject.InjectorFactory;
import qa.qcri.qnoise.internal.*;
import qa.qcri.qnoise.util.NoiseJsonAdapter;
import qa.qcri.qnoise.util.NoiseJsonAdapterDeserializer;
import qa.qcri.qnoise.util.Tracer;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

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

            final String inputFileName = line.getOptionValue("f");
            if (Files.notExists(Paths.get(inputFileName))) {
                throw new FileNotFoundException("Input file " + inputFileName + " does not exist.");
            }

            JsonReader jsonReader =
                new JsonReader(
                    new InputStreamReader(
                        new FileInputStream(inputFileName),
                        Charset.forName("UTF-8")
                    )
                );

            GsonBuilder gson = new GsonBuilder();
            gson.registerTypeAdapter(NoiseJsonAdapter.class, new NoiseJsonAdapterDeserializer());
            gson.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
            NoiseJsonAdapter adapter = gson.create().fromJson(jsonReader, NoiseJsonAdapter.class);

            reader =
                new CSVReader(new FileReader(adapter.getInputFile()), adapter.getCsvSeparator());
            DataProfile profile = DataProfile.readData(reader, adapter.getSchema());

            final String outputFileName = line.getOptionValue("o");
            writer = new CSVWriter(
                new FileWriter(outputFileName),
                adapter.getCsvSeparator(),
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.NO_ESCAPE_CHARACTER
            );

            for (NoiseSpec spec : adapter.getSpecs()) {
                new DataProcess(
                    new NoiseContextBuilder()
                        .profile(profile)
                        .spec(spec)
                        .build()
                    ).process(InjectorFactory.createInjector(spec.noiseType))
                    .after(new IAction<NoiseContext>() {
                        @Override
                        public void act(
                            NoiseContext context,
                            HashMap<String, Object> extras
                        ) {
                            context.report.appendMetric(
                                NoiseReport.Metric.InputFilePath,
                                inputFileName
                            );

                            context.report.appendMetric(
                                NoiseReport.Metric.OutputFilePath,
                                outputFileName
                            );
                            context.report.saveToFile(context.spec.logFile);
                            context.report.print();
                        }
                    });
            }

            profile.writeData(writer);
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
