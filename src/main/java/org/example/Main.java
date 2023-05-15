package org.example;
import com.google.api.services.docs.v1.model.Document;
import com.google.api.services.docs.v1.model.StructuralElement;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import io.javalin.Javalin;

public class Main {
    public static void main(String[] args) throws IOException, GeneralSecurityException {

        Drive driveService;
        Docs docsService;
        DriveQuickstart driveQuickstart = new DriveQuickstart();

        try {
            driveService = DriveQuickstart.getDriveService();
            docsService = new Docs.Builder(driveQuickstart.getHttpTransport(), driveQuickstart.getJsonFactory(), driveService.getRequestFactory().getInitializer())
                    .setApplicationName(DriveQuickstart.APPLICATION_NAME)
                    .build();
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return;  // exit if a failure occurs
        }


        Javalin app = Javalin.create(config -> {
            config.enableCorsForAllOrigins();
        }).start(7000);

        app.post("/CharGen", ctx -> {
            String name = ctx.formParam("name");
            String height = ctx.formParam("height");
            String weight = ctx.formParam("weight");
            String race = ctx.formParam("race");
            String characterClass = ctx.formParam("characterClass");
            String subtree = ctx.formParam("subtree");

            File fileRace = DriveUtils.searchForFileByName(driveService, "Races");
            String raceInformation = null;
            if (fileRace != null) {
                raceInformation = DocsUtils.getRaceInformation(docsService, fileRace.getId(), race);
            }

            File fileClass = DriveUtils.searchForFileByName(driveService, characterClass + " Information");
            List<String> classInformation = null;
            if (fileClass != null) {
                classInformation = DocsUtils.getParagraphsMatchingPattern(docsService, fileClass.getId(), "");
            }

            File fileSubtree = DriveUtils.searchForFileByName(driveService, subtree);
            List<String> subtreeInformation = null;
            if (fileSubtree != null) {
                subtreeInformation = DocsUtils.getParagraphsMatchingPattern(docsService, fileSubtree.getId(), "Level 1 ");
            }

            int[] statistics = new int[6];
            int pointsRemaining = 11;

            for (int i = 0; i < statistics.length; i++) {
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

                String statValue = ctx.formParam(statName);
                if (statValue != null) {
                    int points = Integer.parseInt(statValue);
                    pointsRemaining -= points;
                    statistics[i] = points;
                }
            }

            Character character = new Character(name, height, weight, race, raceInformation, characterClass, classInformation, subtree, subtreeInformation, statistics);
            ctx.json(character);
        });
    }
}
