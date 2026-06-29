package com.github.wojrzu.sentinel.controller;

import com.github.wojrzu.sentinel.model.*;
import com.github.wojrzu.sentinel.service.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@AllArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/admin")
    public ResponseEntity<Report> createAsAdmin(@RequestBody Report report, @AuthenticationPrincipal User caller) {
        if (!(caller instanceof Admin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(reportService.createReportAsAdmin(report));
    }

    @PostMapping
    public ResponseEntity<Report> create(@RequestBody Report report, @AuthenticationPrincipal User caller) {
        if (!(caller instanceof Client client)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(reportService.createReport(report, client));
    }

    @PutMapping("/{reportId}")
    public ResponseEntity<Report> update(@PathVariable Long reportId, @RequestBody Report report, @AuthenticationPrincipal User caller) {
        if (!(caller instanceof Client client)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(reportService.updateReport(reportId, report, client));
    }

    @GetMapping("/my")
    public ResponseEntity<List<Report>> getMyReports(@AuthenticationPrincipal User caller) {
        if (!(caller instanceof Client client)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();}

        return ResponseEntity.ok(reportService.getReportsByUser(client));
    }

    @GetMapping
    public ResponseEntity<List<Report>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<Report> getReport(@PathVariable Long reportId) {
        return ResponseEntity.ok(reportService.getReport(reportId));
    }

    @PostMapping("/{reportId}/claim")
    public ResponseEntity<Report> claim(@PathVariable Long reportId, @AuthenticationPrincipal User caller) {
        if (!(caller instanceof Dispatcher dispatcher)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(reportService.claimReport(reportId, dispatcher.getUserId()));
    }

    @PostMapping("/{reportId}/assign")
    public ResponseEntity<Report> assignOfficer(@PathVariable Long reportId, @RequestBody Map<String, String> body, @AuthenticationPrincipal User caller) {
        if (!(caller instanceof Dispatcher) && !(caller instanceof Admin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(reportService.assignOfficer(reportId, body.get("officerId"), caller.getUserId()));
    }

    @PostMapping("/{reportId}/resolve")
    public ResponseEntity<Report> resolve(@PathVariable Long reportId, @AuthenticationPrincipal User caller) {
        if (!(caller instanceof Admin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(reportService.resolveReport(reportId));
    }

    @PostMapping("/{reportId}/reject")
    public ResponseEntity<Report> reject(@PathVariable Long reportId, @AuthenticationPrincipal User caller) {
        if (!(caller instanceof Dispatcher) && !(caller instanceof Admin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(reportService.rejectReport(reportId));
    }
}