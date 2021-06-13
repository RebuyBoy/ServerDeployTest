package com.rakecounter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HandHistoryReader {

    public String getHandsFromFiles(InputStream inputStream) {
        StringBuilder hands = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                hands.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hands.toString();
    }
}
