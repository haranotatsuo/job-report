package com.example.demo.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.model.User;
import com.example.demo.service.ReportService;
import com.example.demo.service.UserService;

@Controller
public class ReportsController {

    @Autowired
    private UserService userService;

    @Autowired
    private ReportService reportService;

    // カレンダー表示
    @GetMapping("/reports")
    public String viewReports(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());

        if (user.getRole().equals("ROLE_USER")) {
            model.addAttribute("reports", reportService.findByUser(user));
        } else if (user.getRole().equals("ROLE_STAFF")) {
            model.addAttribute("reports", reportService.findAll());
        }

        // ログイン中のユーザー情報もJSに渡す用に追加
        model.addAttribute("currentUserId", user.getId());
        model.addAttribute("currentUserRole", user.getRole());

        return "reports"; // templates/reports.html を表示
    }

    // リスト表示
    @GetMapping("/reports/list")
    public String showReportList(Model model, Principal principal) {
        // 必要であれば、ここにも同様のロジックを追加
        return "reportList";
    }
}
