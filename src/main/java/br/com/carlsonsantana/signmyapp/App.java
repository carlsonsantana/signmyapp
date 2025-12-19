package br.com.carlsonsantana.signmyapp;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class App {
    public static void main(String[] args) throws Exception {
        var inputOption = createOption(
            "in",
            "aligned-app.apk",
            "Aligned APK path"
        );
        var outputOption = createOption(
            "out",
            "signed-app.apk",
            "Signed APK path"
        );
        var keystoreOption = createOption(
            "ks",
            "keystore.jks",
            "Keystore path"
        );
        var keystorePasswordOption = createOption(
            "ks-pass",
            "123456",
            "Keystore password"
        );
        var keystoreKeyAliasOption = createOption(
            "ks-key-alias",
            "mykey",
            "Keystore key alias"
        );
        var keystoreKeyPasswordOption = createOption(
            "key-pass",
            "abcdef",
            "Key password"
        );
        var options = new Options();
        options.addOption(keystoreOption);
        options.addOption(inputOption);
        options.addOption(outputOption);
        options.addOption(keystorePasswordOption);
        options.addOption(keystoreKeyAliasOption);
        options.addOption(keystoreKeyPasswordOption);

        var commandLineParser = new DefaultParser();
        try {
            commandLineParser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            new HelpFormatter().printHelp("signmyapp.jar", options);
            System.exit(1);
        }
    }

    private static Option createOption(
        String shortName,
        String argumentName,
        String description
    ) {
        return Option.builder(shortName)
               .argName(argumentName).hasArg()
               .required(true).desc(description).build();
    }
}
