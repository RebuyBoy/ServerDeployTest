package com.rakecounter;

import com.rakecounter.models.Stake;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 MB
        maxFileSize = 1024 * 1024 * 50,      // 50 MB
        maxRequestSize = 1024 * 1024 * 500   // 500 MB
)
@Controller()
@RequestMapping("/")
public class ApplicationController {
    private List<String> hands = new ArrayList<>();

    @GetMapping
    protected String main(Model model) {
        Map<Stake, CountResult> results = new HashMap<>();
        model.addAttribute("result", results);
        return "test";
    }

    @PostMapping
    public String getFile(Model model, @RequestParam("file") MultipartFile[] file) throws IOException {
        HandHistoryReader hhr = new HandHistoryReader();
        hands = hhr.getHandsFromFiles(file);
        RakeCounter rakeCounter = new RakeCounter();
        Map<Stake, CountResult> results = rakeCounter.process(hands);
        model.addAttribute("result", results);

//        Archiver archiver = new Archiver();
//        byte[] archive = archiver.archive(hands);
//        String fileName = "hh.zip";
//        response.setContentType("application/octet-stream");
//        response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
//        ServletOutputStream outputStream = response.getOutputStream();
//        outputStream.write(archive);
//        outputStream.flush();


        return "rakeTable";
    }

    @GetMapping("/download")
    public String downloadFile(Model model, HttpServletResponse response) throws IOException {
        Archiver archiver = new Archiver();
        byte[] archive = archiver.archive(hands);
        String fileName = "hh.zip";
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(archive);
        outputStream.flush();
        return "rakeTable";
    }
//     @PostMapping
//    public String getFile(Model model, @RequestParam("file") MultipartFile[] file) throws IOException {
//        List<String> hands = new ArrayList<>();
//        HandHistoryReader hhr = new HandHistoryReader();
//        for (MultipartFile filePart : file) {
//            InputStream inputStream = filePart.getInputStream();
//            String handsFromFile = hhr.getHandsFromFiles(inputStream);
//            hands.add(handsFromFile);
//        }
//        RakeCounter rakeCounter = new RakeCounter();
//        Map<Stake, CountResult> results = rakeCounter.process(hands);
//        model.addAttribute("result", results);
//        return "test";
//    }
//
}
