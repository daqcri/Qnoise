/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */
package qa.qcri.qnoise;

import org.apache.commons.cli.*;

import java.io.FileNotFoundException;
import java.io.PrintStream;
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

        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("f")) {
                String fileName = line.getOptionValue("f");
                if (Files.notExists(Paths.get(fileName))) {
                    throw new FileNotFoundException("Input file " + fileName + " does not exist.");
                }

            }
        } catch (ParseException ex) {
            printHelp();
        } catch (Exception ex) {
            tracer.println("Exception : " + ex.getMessage());
        }
    }

    private static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(
            100,
            "qnoise.sh [OPTIONS]",
            "Qnoise - Data Noise Generator",
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
                    withArgName("url").
                    hasArg().
                    withDescription(
                            "Input database JDBC url, e.g. " +
                                    "jdbc:postgresql://localhost/test?usr=myUser&password=myPassword").
                    create("url")
        );

        options.addOption(
            OptionBuilder.
                withArgName("file").
                hasArg().
                withDescription("Output to CSV file.").
                create("o")
        );

        return options;
    }
}
