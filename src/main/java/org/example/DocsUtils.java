package org.example;

import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.model.Document;
import com.google.api.services.docs.v1.model.ParagraphElement;
import com.google.api.services.docs.v1.model.StructuralElement;
import com.google.api.services.docs.v1.model.TextRun;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DocsUtils {

    public static List<String> getParagraphsMatchingPattern(Docs docs, String documentId, String regexPattern) throws IOException {
        Document document = docs.documents().get(documentId).execute();
        List<StructuralElement> elements = document.getBody().getContent();
        List<String> matchedParagraphs = new ArrayList<>();

        Pattern pattern = Pattern.compile(regexPattern);

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
                    matchedParagraphs.add(paragraphString);
                }
            }
        }
        return matchedParagraphs;
    }

    private static final String[] RACE_NAMES = {
            "Amphallion", "Amaldon", "Arthryx", "Draekyn", "Mor' ish Subrace: Esiish", "Mor' ish Subrace: Oshssi",
            "Mor' ish Subrace: Rreshk", "Golariin", "Mastodon", "Saygren", "Skrallion", "Sperys Subrace: Aevys",
            "Sperys Subrace: Falkys", "Sperys Subrace: Nhevys", "Sperys Subrace: Olaerys", "Fen", "Gobrilden", "Haalfer",
            "Dwarf Subrace: Vayl Dwarf", "Dwarf Subrace: Mountain Dwarf", "Dwarf Subrace: Embr Dwarf", "Human",
            "Ikarian Subrace: Hyrikar", "Ikarian Subrace: Whidikar", "Ikarian Subrace: Vesikar", "Satyr", "Ork",
            "Etheral Subrace: Embryn", "Etheral Subrace: Iceryn", "Etheral Subrace: Erthyn", "Teraaz Subrace: Bograaz",
            "Teraaz Subrace: Floraaz", "Teraaz Subrace: Silvaraaz", "The Undead Subrace: Darkborn", "The Undead Subrace: Dark Undead",
            "The Mor' ish", "The Sperys", "The Humanoids", "The Dwarves", "The Ikarian", "The Etheral", "The Teraaz", "The Voidlings", "The Undead"
    };

    private static final String[] PRIMARY_RACE_NAMES = {
            "The Mor' ish",
            "The Sperys",
            "The Humanoids",
            "The Dwarves",
            "The Ikarian",
            "The Etheral",
            "The Teraaz",
            "The Voidlings",
            "The Undead"
    };
    public static String getRaceInformation(Docs docs, String documentId, String inputRaceName) throws IOException {
        Document document = docs.documents().get(documentId).execute();
        List<StructuralElement> elements = document.getBody().getContent();

        // Find the full race name
        String raceName = inputRaceName;
        String primaryRaceName = null;
        for (String fullRaceName : RACE_NAMES) {
            if (fullRaceName.toLowerCase().startsWith(inputRaceName.toLowerCase())) {
                raceName = fullRaceName;
                break;
            }
        }

        // Find the primary race name
        for (String fullRaceName : PRIMARY_RACE_NAMES) {
            if (raceName.contains(fullRaceName)) {
                primaryRaceName = fullRaceName;
                break;
            }
        }

        Pattern racePattern = Pattern.compile("(?i)^" + Pattern.quote(raceName));
        Pattern primaryRacePattern = primaryRaceName != null ? Pattern.compile("(?i)^" + Pattern.quote(primaryRaceName)) : null;
        Pattern endPattern = Pattern.compile("(?i)^(" + String.join("|", Arrays.stream(RACE_NAMES).map(Pattern::quote).collect(Collectors.joining("|"))) + ")");

        StringBuilder raceInformation = new StringBuilder();
        boolean isCapturing = false;
        boolean isCapturingPrimary = primaryRacePattern != null;

        for (StructuralElement element : elements) {
            if (element.getParagraph() != null) {
                StringBuilder paragraphText = new StringBuilder();
                for (ParagraphElement paragraphElement : element.getParagraph().getElements()) {
                    if (paragraphElement.getTextRun() != null) {
                        paragraphText.append(paragraphElement.getTextRun().getContent());
                    }
                }

                if (primaryRacePattern != null && primaryRacePattern.matcher(paragraphText).find()) {
                    isCapturingPrimary = true;
                } else if (isCapturingPrimary && endPattern.matcher(paragraphText).find()) {
                    isCapturingPrimary = false;
                }

                if (racePattern.matcher(paragraphText).find()) {
                    isCapturing = true;
                } else if (isCapturing && endPattern.matcher(paragraphText).find()) {
                    break;
                }

                if (isCapturingPrimary) {
                    raceInformation.append(paragraphText).append("\n");
                } else if (isCapturing) {
                    raceInformation.append(paragraphText).append("\n");
                }
            }
        }
        return raceInformation.toString();
    }
}
