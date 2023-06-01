package org.example;
import com.google.api.services.docs.v1.model.Document;
import com.google.api.services.docs.v1.model.StructuralElement;
import com.google.api.services.drive.Drive;


import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import io.javalin.Javalin;


public class Main {
    public static void main(String[] args) throws IOException, GeneralSecurityException {

        // Declare services for Google Drive and Google Docs
        Drive driveService;
        Docs docsService;
        DriveQuickstart driveQuickstart = new DriveQuickstart();

        // Try to initialize Google Drive and Google Docs services
        try {
            driveService = DriveQuickstart.getDriveService();
            docsService = new Docs.Builder(driveQuickstart.getHttpTransport(), driveQuickstart.getJsonFactory(), driveService.getRequestFactory().getInitializer())
                    .setApplicationName(DriveQuickstart.APPLICATION_NAME)
                    .build();
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return;  // exit if a failure occurs
        }

        // Set up Javalin web server
        Javalin app = Javalin.create(config -> {
            config.enableCorsForAllOrigins();
        }).start(7000);

        // Define POST endpoint for character generation
        app.post("/CharGen", ctx -> {
            // Collect form parameters
            String name = ctx.formParam("name");
            String height = ctx.formParam("height");
            String weight = ctx.formParam("weight");
            String race = ctx.formParam("race");
            String characterClass = ctx.formParam("characterClass");
            String subtree = ctx.formParam("subtree");

            // Search for race information in Google Docs
            File fileRace = DriveUtils.searchForFileByName(driveService, "Races");
            String raceInformation = null;
            if (fileRace != null) {
                raceInformation = DocsUtils.getRaceInformation(docsService, fileRace.getId(), race);
            }

            // Search for class information in Google Docs
            File fileClass = DriveUtils.searchForFileByName(driveService, characterClass + " Information");
            List<String> classInformation = null;
            if (fileClass != null) {
                classInformation = DocsUtils.getParagraphsMatchingPattern(docsService, fileClass.getId(), "");
            }

            // Search for subtree information in Google Docs
            File fileSubtree = DriveUtils.searchForFileByName(driveService, subtree);
            List<String> subtreeInformation = null;
            if (fileSubtree != null) {
                subtreeInformation = DocsUtils.getParagraphsMatchingPattern(docsService, fileSubtree.getId(), "Level 1 ");
            }

            // Gather character statistics
            int[] statistics = new int[6];
            int pointsRemaining = 11;
            for (int i = 0; i < statistics.length; i++) {
                // Convert stat index to stat name
                String statName = "";
                switch (i) {
                    case 0:
                        statName = "constitution";
                        break;
                    case 1:
                        statName = "strength";
                        break;
                    case 2:
                        statName = "dexterity";
                        break;
                    case 3:
                        statName = "charisma";
                        break;
                    case 4:
                        statName = "intellect";
                        break;
                    case 5:
                        statName = "wisdom";
                        break;
                }

                // Get stat value from form
                String statValue = ctx.formParam(statName);
                if (statValue != null) {
                    int points = Integer.parseInt(statValue);
                    pointsRemaining -= points;
                    statistics[i] = points;
                }
            }

            // Create new character with collected data
            Character character = new Character(name, height, weight, race, raceInformation, characterClass, classInformation, subtree, subtreeInformation, statistics);

            // Convert the character to a String representation
            String characterString = character.toString();

            // Create a File and write the character to it
            java.io.File characterFile = new java.io.File("C:\\Users\\Jonah\\Documents\\CSD480\\CharacterStorage\\"+name+".txt");
            try (FileWriter writer = new FileWriter(characterFile)) {
                writer.write(characterString);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Send the File to the client
            try {
                Path path = Paths.get("C:\\Users\\Jonah\\Documents\\CSD480\\CharacterStorage\\" + name + ".txt");
                InputStream is = Files.newInputStream(path);
                ctx.result(is);
                ctx.contentType("application/octet-stream");  // This tells the client to download the file
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
