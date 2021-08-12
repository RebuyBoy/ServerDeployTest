package com.rakecounter;

import com.rakecounter.models.CountResult;
import com.rakecounter.models.Stake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    @Autowired
    private HandParser handParser;
    private Map<String, List<String>> userHands = new HashMap<>();


    @GetMapping
    protected String main(Model model) {
        Map<Stake, CountResult> results = new HashMap<>();
        model.addAttribute("result", results);
        return "rakeTable";
    }

    @PostMapping
    public String getFile(Model model, @RequestParam("file") MultipartFile[] file) throws IOException {
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        HandHistoryReader hhr = new HandHistoryReader();
        List<String> hands = hhr.getHandsFromFiles(file);
        Map<Stake, CountResult> results = handParser.parse(hands);
        model.addAttribute("result", results);
        userHands.put(sessionId, hands);
        return "rakeTable";
    }

    @GetMapping("/download")
    public String downloadFile(Model model, HttpServletResponse response) throws IOException {
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        List<String> hands = userHands.get(sessionId);
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
}
