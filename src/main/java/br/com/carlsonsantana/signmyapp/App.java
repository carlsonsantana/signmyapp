package br.com.carlsonsantana.signmyapp;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collections;

import com.android.apksig.ApkSigner;
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
            var commandLine = commandLineParser.parse(options, args);
            var inputApk = new File(commandLine.getOptionValue("in"));
            var outputApk = new File(commandLine.getOptionValue("out"));
            var keystoreFile = new File(commandLine.getOptionValue("ks"));
            var storePassword = commandLine.getOptionValue("ks-pass");
            var keyAlias = commandLine.getOptionValue("ks-key-alias");
            var keyPassword = commandLine.getOptionValue("key-pass");

            sign(
                inputApk,
                outputApk,
                keystoreFile,
                storePassword,
                keyAlias,
                keyPassword
            );
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

    private static void sign(
        File inputApk,
        File outputApk,
        File keystoreFile,
        String storePassword,
        String keyAlias,
        String keyPassword
    ) throws Exception {
        var keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (var is = new FileInputStream(keystoreFile)) {
            keyStore.load(is, storePassword.toCharArray());
        }

        var privateKey = (PrivateKey) keyStore.getKey(
            keyAlias,
            keyPassword.toCharArray()
        );
        var certificate = (X509Certificate) keyStore.getCertificate(keyAlias);

        var signerConfig = new ApkSigner.SignerConfig.Builder(
            "CERT",
            privateKey,
            Collections.singletonList(certificate)
        ).build();

        var builder =
            new ApkSigner.Builder(Collections.singletonList(signerConfig))
            .setInputApk(inputApk)
            .setOutputApk(outputApk)
            // Enabling all schemes for maximum compatibility
            .setV1SigningEnabled(true)
            .setV2SigningEnabled(true)
            .setV3SigningEnabled(true);

        builder.build().sign();

        System.out.println(
            "APK signed successfully: " +
            outputApk.getAbsolutePath()
        );
    }
}
