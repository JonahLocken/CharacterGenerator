package org.example;
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
public class Main {


    public static void main(String[] args) throws IOException, GeneralSecurityException {


        Scanner scanner = new Scanner(System.in);

        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Drive driveService = new Drive.Builder(httpTransport, DriveQuickstart.getDriveService().getJsonFactory(), DriveQuickstart.getCredentials(httpTransport))
                .setApplicationName(DriveQuickstart.getDriveService().getApplicationName())
                .build();
        Docs docsService = new Docs.Builder(httpTransport, DriveQuickstart.getDriveService().getJsonFactory(), DriveQuickstart.getCredentials(httpTransport))
                .setApplicationName(DriveQuickstart.getDriveService().getApplicationName())
                .build();



            // Get character name
            System.out.print("Enter character name: ");
            String name = scanner.nextLine();

            // Get character height
            System.out.print("Enter character height: ");
            String height = scanner.nextLine();

            // Get character weight
            System.out.print("Enter character weight: ");
            String weight = scanner.nextLine();

            // Get character race
            System.out.print("Enter character race: ");
            String race = scanner.nextLine();

            // Get character class
            System.out.print("Enter character class: ");
            String characterClass = scanner.nextLine();

            // Get character subtree
            System.out.print("Enter character subtree: ");
            String subtree = scanner.nextLine();
        File file = DriveUtils.searchForFileByName(driveService, subtree);

        if (file != null) {
            // Fetch and print paragraphs starting with "Level 1:"
            List<String> level1Paragraphs = DocsUtils.getParagraphsStartingWithLevel1(docsService, file.getId());
            for (String paragraph : level1Paragraphs) {
                System.out.println(paragraph);
            }
        } else {
            System.out.println("File not found.");
        }

            // Get character statistics
            int[] statistics = new int[6];
            int pointsRemaining = 11;

            for (int i = 0; i < statistics.length; i++) {
                String statName = "";
                switch (i) {
                    case 0:
                        statName = "Constitution";
                        break;
                    case 1:
                        statName = "Strength";
                        break;
                    case 2:
                        statName = "Dexterity";
                        break;
                    case 3:
                        statName = "Charisma";
                        break;
                    case 4:
                        statName = "Intellect";
                        break;
                    case 5:
                        statName = "Wisdom";
                        break;
                }
                while (true) {
                    System.out.printf("Enter %s (1-3 points, %d points remaining): ", statName, pointsRemaining);
                    int points = scanner.nextInt();
                    if (points > 3 || points < 0 || points > pointsRemaining) {
                        System.out.println("Invalid input, please try again.");
                    } else {
                        pointsRemaining -= points;
                        statistics[i] = points;
                        break;
                    }
                }
            }

            // Display character information
            System.out.println("\nCharacter Information");
            System.out.printf("Name: %s\n", name);
            System.out.printf("Height: %s\n", height);
            System.out.printf("Weight: %s\n", weight);
            System.out.printf("Race: %s\n", race);
            System.out.printf("Class: %s\n", characterClass);
            System.out.printf("Subtree: %s\n", subtree);
            System.out.println("Statistics:");
            System.out.printf("Constitution: %d\n", statistics[0]);
            System.out.printf("Strength: %d\n", statistics[1]);
            System.out.printf("Dexterity: %d\n", statistics[2]);
            System.out.printf("Charisma: %d\n", statistics[3]);
            System.out.printf("Intellect: %d\n", statistics[4]);
            System.out.printf("Wisdom: %d\n", statistics[5]);
    }

}
