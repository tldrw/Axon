package org.example.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CommandExecutor {

    public static void executeCommand(String command, String workingDirectory) throws IOException {
        
        // 预处理工作目录
        File workDir = null;
        String workDirPath = null;
        if (workingDirectory != null && !workingDirectory.trim().isEmpty()) {
            workDir = new File(workingDirectory);
            if (workDir.exists() && workDir.isDirectory()) {
                workDirPath = workDir.getAbsolutePath();
            }
        }

        List<String> arrayList = new ArrayList<>();
        int osType = Utils.getOsType();

        if (osType == 1) {
            // Windows
            String finalCmd;
            if (workDirPath != null) {
                finalCmd = String.format("cd /d \"%s\" && %s", workDirPath, command);
            } else {
                finalCmd = command;
            }
            
            String batContent = String.format("@echo off\n%s\ncmd /k", finalCmd);
            String batFilePath = Utils.makeBatFile("cmd4axon.bat", batContent);
            
            if (!"Fail".equals(batFilePath)) {
                arrayList.add("cmd.exe");
                arrayList.add("/c");
                arrayList.add("start");
                arrayList.add(batFilePath);
            } else {
                throw new IOException("Failed to create bat file");
            }
            
        } else if (osType == 2) {
            // macOS
            String shellCmd;
            if (workDirPath != null) {
                shellCmd = String.format("cd \"%s\" && %s", workDirPath, command);
            } else {
                shellCmd = command;
            }

            String escapedCmd = shellCmd.replace("\"", "\\\"");
            
            arrayList.add("osascript");
            arrayList.add("-e");
            String appleScript = "tell application \"Terminal\"\n" +
                                 "    activate\n" +
                                 "    do script \"" + escapedCmd + "\"\n" +
                                 "end tell";
            arrayList.add(appleScript);
            
        } else if (osType == 3) {
            // Linux
            String shellCmd;
            if (workDirPath != null) {
                shellCmd = String.format("cd \"%s\" && %s; bash", workDirPath, command);
            } else {
                shellCmd = String.format("%s; bash", command);
            }
            
            arrayList.add("gnome-terminal");
            arrayList.add("--");
            arrayList.add("bash");
            arrayList.add("-c");
            arrayList.add(shellCmd);
            
        } else {
            // Unknown
            String shellCmd;
            if (workDirPath != null) {
                shellCmd = String.format("cd \"%s\" && %s", workDirPath, command);
            } else {
                shellCmd = command;
            }
            
            arrayList.add("/bin/bash");
            arrayList.add("-c");
            arrayList.add(shellCmd);
        }

        if (!arrayList.isEmpty()) {
            ProcessBuilder processBuilder = new ProcessBuilder(arrayList);
            if (workDir != null) {
                processBuilder.directory(workDir);
            }
            Process process = processBuilder.start();
            
            try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = input.readLine()) != null) {
                    System.out.println(line);
                }
            }
        }
    }
}
