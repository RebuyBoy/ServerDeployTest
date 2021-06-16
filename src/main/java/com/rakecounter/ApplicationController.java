package com.rakecounter;

import com.rakecounter.models.Stake;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.annotation.MultipartConfig;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 MB
        maxFileSize = 1024 * 1024 * 10,      // 10 MB
        maxRequestSize = 1024 * 1024 * 100   // 100 MB
)
@Controller()
@RequestMapping("/")
public class ApplicationController {
    @GetMapping
    protected String main(Model model) {
        CountResult result = new CountResult();
        model.addAttribute("result", result);
        return "test";
    }

    @PostMapping
    public String getFile(Model model, @RequestParam("file") MultipartFile[] file) throws IOException {
        List<String> hands = new ArrayList<>();
        HandHistoryReader hhr = new HandHistoryReader();
        for (MultipartFile filePart : file) {
            InputStream inputStream = filePart.getInputStream();
            String handsFromFile = hhr.getHandsFromFiles(inputStream);
            hands.add(handsFromFile);
        }
        RakeCounter rakeCounter = new RakeCounter();
        Map<Stake, CountResult> results = rakeCounter.process(hands);
        model.addAttribute("result", results);
        return "test";
    }
}