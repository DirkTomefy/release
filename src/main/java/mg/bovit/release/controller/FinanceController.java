package mg.bovit.release.controller;

import mg.bovit.release.dto.FinancialStatsDTO;
import mg.bovit.release.service.FinancialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;

@Controller
@RequestMapping("/finance")
public class FinanceController {

    @Autowired
    private FinancialService financialService;

    @GetMapping
    public String financePage(Model model) {
        return "finance/dashboard";
    }

    @GetMapping("/data")
    @ResponseBody
    public FinancialStatsDTO getFinanceData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        return financialService.getFinancialStats(dateDebut, dateFin);
    }
}