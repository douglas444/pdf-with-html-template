package org.example;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {

        URL templateURL = Main.class.getClassLoader().getResource("my-template.html");
        if (templateURL == null) {
            throw new RuntimeException();
        }

        URL fontURL = Main.class.getClassLoader().getResource("flamouse.ttf");
        if (fontURL == null) {
            throw new RuntimeException();
        }

        File templateFile = new File(templateURL.toURI());
        File fontFile = new File(fontURL.toURI());

        Map<String, String> values = new HashMap<>();
        values.put("name", "Anonymous");
        values.put("birthday", LocalDate.of(2024, 2, 12).toString());
        values.put("patience", String.valueOf(100.0));

        String htmlContent = Files.readString(templateFile.toPath());
        htmlContent = replace(htmlContent, values);

        String baseURL = templateFile.getParentFile().toURI().toURL().toString();

        String outputPdf = "my-pdf.pdf";
        try (OutputStream os = new FileOutputStream(outputPdf)) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withUri(outputPdf);
            builder.toStream(os);
            builder.withHtmlContent(htmlContent, baseURL);
            builder.useFont(fontFile, "flamouse");
            builder.run();
        }

    }

    public static String replace(String s, Map<String, String> values) {

        for (Map.Entry<String, String> entry : values.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            s = s.replace("${" + key + "}", value);
        }

        return s;

    }

}