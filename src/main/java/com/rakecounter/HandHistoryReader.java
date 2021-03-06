package com.rakecounter;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class HandHistoryReader {
    private final int maxFileSizeThreshold = 1024 * 1024 * 18;
    private final String zipApplication = "application/zip";

    public List<String> getHandsFromFiles(MultipartFile[] filePart) {
        if (filePart == null) {
            throw new NullPointerException();
        }
        StringBuilder hands = new StringBuilder();
        List<String> splittedHands = new ArrayList<>();
        for (MultipartFile multipartFile : filePart) {
            try {
                InputStream inputStream = multipartFile.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (hands.length() >= maxFileSizeThreshold) {
                        if (line.startsWith("Poker Hand")) {
                            splittedHands.add(hands.toString());
                            hands = new StringBuilder();
                        }
                    }
                    hands.append(line).append("\n");
                }
                reader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        splittedHands.add(hands.toString());
        return splittedHands;
    }
}