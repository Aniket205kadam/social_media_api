package dev.aniket.Instagram_api.utility;

public class UtilityClass {
    public static String getFileExtension(String originalFilename, long size) {
        // find the last index of dot character
        int lastIndexOfDot = originalFilename.lastIndexOf(".");

        if (lastIndexOfDot > 0 && lastIndexOfDot < (size - 1)) {
            return originalFilename.substring(lastIndexOfDot + 1);
        }
        return null;
    }
}
