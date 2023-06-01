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

    // Method to retrieve paragraphs from a Google Docs document that match a given pattern
    public static List<String> getParagraphsMatchingPattern(Docs docs, String documentId, String regexPattern) throws IOException {
        // Request the document from Google Docs
        Document document = docs.documents().get(documentId).execute();

        // Get the content elements of the document
        List<StructuralElement> elements = document.getBody().getContent();
        List<String> matchedParagraphs = new ArrayList<>();

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(regexPattern);

        // Loop over each structural element in the document
        for (StructuralElement element : elements) {
            // Check if the element is a paragraph
            if (element.getParagraph() != null) {
                StringBuilder paragraphText = new StringBuilder();

                // Loop over each element in the paragraph
                for (ParagraphElement textElement : element.getParagraph().getElements()) {
                    // Check if the element is a text run
                    if (textElement.getTextRun() != null) {
                        // Append the content of the text run to the paragraph text
                        paragraphText.append(textElement.getTextRun().getContent());
                    }
                }

                // If the paragraph text matches the pattern, add it to the list
                String paragraphString = paragraphText.toString();
                if (pattern.matcher(paragraphString).find()) {
                    matchedParagraphs.add(paragraphString);
                }
            }
        }

        // Return the list of matched paragraphs
        return matchedParagraphs;
    }

    // List of race names used in the Divide as of 5/17
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

    // List of primary race names, used to identify if a race is a sub-race or not
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
    // Method to retrieve race information from a Google Docs document
    public static String getRaceInformation(Docs docs, String documentId, String inputRaceName) throws IOException {
        // Request the document from Google Docs
        Document document = docs.documents().get(documentId).execute();

        // Get the content elements of the document
        List<StructuralElement> elements = document.getBody().getContent();

        // Determine the full race name by searching the RACE_NAMES array
        String raceName = Arrays.stream(RACE_NAMES)
                .filter(fullRaceName -> fullRaceName.toLowerCase().startsWith(inputRaceName.toLowerCase()))
                .findFirst()
                .orElse(inputRaceName);

        // Determine the primary race name by searching the PRIMARY_RACE_NAMES array
        String primaryRaceName = Arrays.stream(PRIMARY_RACE_NAMES)
                .filter(fullRaceName -> raceName.contains(fullRaceName))
                .findFirst()
                .orElse(null);

        // Create a Pattern object for the race name
        Pattern racePattern = Pattern.compile("(?i)^" + Pattern.quote(raceName));

        // Create a Pattern object for the primary race name (if one exists)
        Pattern primaryRacePattern = primaryRaceName != null ? Pattern.compile("(?i)^" + Pattern.quote(primaryRaceName)) : null;

        // Create a Pattern object for the end of the race information
        Pattern endPattern = Pattern.compile("(?i)^(" + String.join("|", Arrays.stream(RACE_NAMES).map(Pattern::quote).collect(Collectors.joining("|"))) + ")");

        StringBuilder raceInformation = new StringBuilder();

        // Flags to determine if we're currently capturing race or primary race information
        boolean isCapturing = false;
        boolean isCapturingPrimary = primaryRacePattern != null;

        // Loop over each structural element in the document
        for (StructuralElement element : elements) {
            if (element.getParagraph() != null) {
                StringBuilder paragraphText = new StringBuilder();
                for (ParagraphElement paragraphElement : element.getParagraph().getElements()) {
                    if (paragraphElement.getTextRun() != null) {
                        paragraphText.append(paragraphElement.getTextRun().getContent());
                    }
                }

                // If we've found the primary race information, start capturing
                if (primaryRacePattern != null && primaryRacePattern.matcher(paragraphText).find()) {
                    isCapturingPrimary = true;
                }
                // If we've reached the end of the primary race information, stop capturing
                else if (isCapturingPrimary && endPattern.matcher(paragraphText).find()) {
                    isCapturingPrimary = false;
                }

                // If we've found the race information, start capturing
                if (racePattern.matcher(paragraphText).find()) {
                    isCapturing = true;
                }
                // If we've reached the end of the race information, stop capturing and break the loop
                else if (isCapturing && endPattern.matcher(paragraphText).find()) {
                    break;
                }

                // If we're capturing, add the paragraph text to the race information
                if (isCapturingPrimary || isCapturing) {
                    raceInformation.append(paragraphText).append("\n");
                }
            }
        }

        // Return the race information as a string
        return raceInformation.toString();
    }
}
