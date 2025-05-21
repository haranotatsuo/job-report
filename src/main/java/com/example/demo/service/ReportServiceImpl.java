package com.example.demo.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.demo.model.Report;
import com.example.demo.model.User;
import com.example.demo.repository.ReportRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

    private final ReportRepository reportRepository;

    @Override
    public List<Report> findByUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("ユーザー情報が null です");
        }

        logger.debug("ユーザーID {} のレポート一覧を取得", user.getId());
        return reportRepository.findByUser(user);
    }

    @Override
    public List<Report> findAll() {
        logger.debug("全てのレポートを取得");
        return reportRepository.findAll();
    }

    // --- 参考：ページング対応メソッド（必要時に有効化） ---
    // public Page<Report> findByUser(User user, Pageable pageable) {
    //     if (user == null) {
    //         throw new IllegalArgumentException("ユーザー情報が null です");
    //     }
    //     return reportRepository.findByUser(user, pageable);
    // }
}
