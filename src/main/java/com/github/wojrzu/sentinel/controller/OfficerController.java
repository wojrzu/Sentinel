package com.github.wojrzu.sentinel.controller;

import com.github.wojrzu.sentinel.model.Officer;
import com.github.wojrzu.sentinel.model.Report;
import com.github.wojrzu.sentinel.model.User;
import com.github.wojrzu.sentinel.repository.OfficerRepository;
import com.github.wojrzu.sentinel.repository.ReportRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/officer")
@AllArgsConstructor
public class OfficerController {

    private final OfficerRepository officerRepository;
    private final ReportRepository reportRepository;

    @GetMapping("/available")
    public ResponseEntity<List<Officer>> getAvailable() {
        return ResponseEntity.ok(officerRepository.findByStatus(1));
    }

    @PatchMapping("/status")
    public ResponseEntity<Void> updateStatus(@RequestBody Map<String, Integer> body, @AuthenticationPrincipal User caller) {
        if (!(caller instanceof Officer officer)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Integer status = body.get("status");
        if (status == null || status < 0 || status > 1) {
            return ResponseEntity.badRequest().build();
        }

        if (officer.getStatus() == 2) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        officer.setStatus(status);
        officerRepository.save(officer);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/assigned")
    public ResponseEntity<Report> getAssigned(@AuthenticationPrincipal User caller) {
        if (!(caller instanceof Officer officer)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return reportRepository.findAll().stream()
                .filter(r -> r.getAssignedOfficer() != null
                        && r.getAssignedOfficer().getUserId().equals(officer.getUserId())
                        && (r.getState() == Report.ReportState.PENDING
                        || r.getState() == Report.ReportState.ACTIVE))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping("/assigned/confirm")
    public ResponseEntity<Report> confirmAssigned(@AuthenticationPrincipal User caller) {
        if (!(caller instanceof Officer officer)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Report report = reportRepository.findAll().stream()
                .filter(r -> r.getAssignedOfficer() != null
                        && r.getAssignedOfficer().getUserId().equals(officer.getUserId())
                        && r.getState() == Report.ReportState.PENDING)
                .findFirst()
                .orElse(null);

        if (report == null) {
            return ResponseEntity.notFound().build();
        }

        report.setState(Report.ReportState.ACTIVE);
        reportRepository.save(report);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/assigned/complete")
    public ResponseEntity<Report> completeAssigned(@AuthenticationPrincipal User caller) {
        if (!(caller instanceof Officer officer)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Report report = reportRepository.findAll().stream()
                .filter(r -> r.getAssignedOfficer() != null
                        && r.getAssignedOfficer().getUserId().equals(officer.getUserId())
                        && (r.getState() == Report.ReportState.ACTIVE
                        || r.getState() == Report.ReportState.PENDING))
                .findFirst()
                .orElse(null);

        if (report == null) {
            return ResponseEntity.notFound().build();
        }

        officer.setStatus(1);
        officerRepository.save(officer);

        report.setAssignedOfficer(null);
        report.setState(Report.ReportState.FINISHED);
        report.setReportClosureTime(LocalTime.now());
        reportRepository.save(report);

        return ResponseEntity.ok(report);
    }
}