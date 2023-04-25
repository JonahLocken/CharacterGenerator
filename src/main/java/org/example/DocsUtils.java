package org.example;

import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.model.Document;
import com.google.api.services.docs.v1.model.ParagraphElement;
import com.google.api.services.docs.v1.model.StructuralElement;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DocsUtils {

    public static List<String> getParagraphsStartingWithLevel1(Docs docs, String documentId) throws IOException, IOException {
        Document document = docs.documents().get(documentId).execute();
        List<StructuralElement> elements = document.getBody().getContent();
        List<String> level1Paragraphs = new ArrayList<>();

        Pattern pattern = Pattern.compile("^Level 1 ");

        for (StructuralElement element : elements) {
            if (element.getParagraph() != null) {
                StringBuilder paragraphText = new StringBuilder();
                for (ParagraphElement textElement : element.getParagraph().getElements()) {
                    if (textElement.getTextRun() != null) {
                        paragraphText.append(textElement.getTextRun().getContent());
                    }
                }
                String paragraphString = paragraphText.toString();
                if (pattern.matcher(paragraphString).find()) {
                    level1Paragraphs.add(paragraphString);
                }
            }
        }
        return level1Paragraphs;
    }
}
