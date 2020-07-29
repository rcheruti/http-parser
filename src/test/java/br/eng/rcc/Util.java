package br.eng.rcc;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Util {

    public static String contents(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get(Util.class.getClassLoader().getResource(fileName).toURI())));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

}