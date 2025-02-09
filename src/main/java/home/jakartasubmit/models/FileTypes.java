package home.jakartasubmit.models;

public enum FileTypes {
    ZIP(".zip"),
    PPTX(".pptx"),
    XLSX(".xlsx"),
    PDF(".pdf");

    private final String extension;

    // Constructor to set the file extension
    FileTypes(String extension) {
        this.extension = extension;
    }

    // Method to get the file extension
    public String getExtension() {
        return extension;
    }

    // A utility method to check if a given file path matches any valid file type
    public static boolean isValidFileType(String filePath) {
        for (FileTypes fileType : FileTypes.values()) {
            if (filePath.toLowerCase().endsWith(fileType.getExtension())) {
                return true;
            }
        }
        return false;
    }
}
