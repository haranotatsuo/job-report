package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Report;
import com.example.demo.model.User;

public interface ReportRepository extends JpaRepository<Report, Long> {
	List<Report> findByUser(User user);
	List<Report> findByTargetUser(User user); // ← これがユーザー用ページ用
}
