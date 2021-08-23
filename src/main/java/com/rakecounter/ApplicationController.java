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
        CountResult countResult = getTotalResult(results);
        model.addAttribute("result", results);
        model.addAttribute("totals", countResult);
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

    private CountResult getTotalResult(Map<Stake, CountResult> resultMap) {
        CountResult countResult = new CountResult();
        int hands = 0;
        int vPips = 0;
        int allFolds = 0;
        double ggRake = 0;
        double jpRake = 0;
        double profit = 0;
        double JpCount = 0;
        int stakeCount = 0;
        for (Map.Entry<Stake, CountResult> resultEntry : resultMap.entrySet()) {
            Stake key = resultEntry.getKey();
            if (Stake.UNK.equals(key)) {
                continue;
            }
            if (Stake.TOTAL.equals(key)) {
                double value = resultEntry.getValue().getHandsPerHour();
                countResult.setHandsPerHour(value);
                continue;
            }
            CountResult value = resultEntry.getValue();
            stakeCount++;
            hands += value.getNumberOfHands();
            ggRake += value.getGeneralRake();
            jpRake += value.getJackpotRake();
            profit += value.getProfit();
            JpCount += value.getJPCount() * 59 * Stake.getAnteByStake(key);
            vPips += value.getvPip();
            allFolds += value.getAllFolds();
        }

        countResult.setvPip(averageVpip(vPips,allFolds,hands));
        countResult.setGeneralRake(ggRake);
        countResult.setProfit(profit);
        countResult.setJackpotRake(jpRake);
        countResult.setJPCount(JpCount);
        countResult.setNumberOfHands(hands);
        countResult.setCount(stakeCount);
        return countResult;
    }

    private int averageVpip(int vPips,int allFolds,int handsCount ) {
        return vPips*100/(handsCount-allFolds);
    }
}
