package home.jakartasubmit.models;

public class Files {
    public Files(){}

    public static String formatFileName(String savedFileName){
        int underscoreIndex = savedFileName.indexOf("_");

        if (underscoreIndex != -1) {
            return savedFileName.substring(underscoreIndex + 1); // Extract original filename
        }

        return savedFileName; // Return as-is if no underscore found
    }
}
