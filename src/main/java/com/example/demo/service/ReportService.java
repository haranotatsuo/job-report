package com.example.demo.service;

import java.util.List;

import com.example.demo.model.Report;
import com.example.demo.model.User;

public interface ReportService {
    List<Report> findByUser(User user);
    List<Report> findAll();
}
