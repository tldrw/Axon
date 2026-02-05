package org.example.utils;

import java.io.File;
import java.io.PrintWriter;

public class Utils {

    public static int getOsType() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.startsWith("windows")) {
            return 1;
        } else if (osName.startsWith("mac")) {
            return 2;
        } else if (osName.startsWith("linux")) {
            return 3;
        } else {
            return 4;
        }
    }

    public static String getTempReqName(String filename) {
        String tempDir = System.getProperty("java.io.tmpdir");
        return tempDir + File.separator + filename;
    }

    public static String makeBatFile(String filename, String content) {
        String tempDir = System.getProperty("java.io.tmpdir");
        File batFile = new File(tempDir + File.separator + filename);

        try {
            if (!batFile.exists()) {
                batFile.createNewFile();
            }
            batFile.deleteOnExit();
            PrintWriter pw = new PrintWriter(batFile);
            pw.print(content);
            pw.close();
            return batFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "Fail";
        }
    }
}
