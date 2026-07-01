package dev.sprintmc.sprintkb;

public class VersionComparator {
    public static boolean isNewer(String current, String latest) {
        try {
            current = current.replaceAll("[^0-9.]", "");
            latest = latest.replaceAll("[^0-9.]", "");
            String[] currentParts = current.split("\\.");
            String[] latestParts = latest.split("\\.");
            
            int length = Math.max(currentParts.length, latestParts.length);
            for (int i = 0; i < length; i++) {
                int c = i < currentParts.length && !currentParts[i].isEmpty() ? Integer.parseInt(currentParts[i]) : 0;
                int l = i < latestParts.length && !latestParts[i].isEmpty() ? Integer.parseInt(latestParts[i]) : 0;
                if (l > c) return true;
                if (l < c) return false;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
