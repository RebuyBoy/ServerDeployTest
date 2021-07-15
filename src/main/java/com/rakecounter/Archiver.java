package com.rakecounter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Archiver {
    public byte[] archive(List<String> hands) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        String fileName = "hh";
        String extension = ".txt";
        String currentFilename = fileName + extension;
        int parts = 0;
        for (String hand : hands) {
            byte[] bytes = hand.getBytes(StandardCharsets.UTF_8);
            ZipEntry zipEntry = new ZipEntry(currentFilename);
            parts++;
            currentFilename = fileName + "-" + parts + extension;
            zos.putNextEntry(zipEntry);
            zos.write(bytes);
        }
        zos.close();
        return baos.toByteArray();
    }

}
