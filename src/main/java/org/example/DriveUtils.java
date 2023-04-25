package org.example;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class DriveUtils {
    /**
     * Searches for a file in Google Drive based on the given file name.
     *
     * @param drive The Drive object used to access Google Drive.
     * @param fileName The name of the file to search for.
     * @return The File object for the matching file, or null if no matching file is found.
     * @throws IOException If an error occurs while accessing Google Drive.
     */
    public static File searchForFileByName(Drive drive, String fileName) throws IOException, GeneralSecurityException {
        String query = "name='" + fileName + "' and trashed=false";
        List<File> files;
        FileList result = drive.files().list().setQ(query).setSpaces("drive").execute();
        files = result.getFiles();

        if (files.isEmpty()) {
            System.out.println("No matching files found.");
            System.exit(0);
        } else if (files.size() > 1) {
            System.out.println("Multiple matching files found, returning the first one.");
        }

        return files.get(0);

    }


}
