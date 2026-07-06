package com.github.wojrzu.sentinel.service;

import com.github.wojrzu.sentinel.model.Client;
import com.github.wojrzu.sentinel.model.Officer;
import com.github.wojrzu.sentinel.model.Report;
import com.github.wojrzu.sentinel.repository.OfficerRepository;
import com.github.wojrzu.sentinel.repository.ReportRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final OfficerRepository officerRepository;


    @Transactional
    public Report createReportAsAdmin(Report report) {
        Report r = new Report();
        r.setTitle(report.getTitle());
        r.setDescription(report.getDescription());
        r.setAddress(report.getAddress());
        r.setReporter(report.getReporter());
        r.setHandlerID(report.getHandlerID());
        r.setReportArrivalTime(report.getReportArrivalTime() != null ? report.getReportArrivalTime() : LocalTime.now());
        r.setReportClosureTime(report.getReportClosureTime());
        r.setState(report.getState() != null ? report.getState() : Report.ReportState.NEW);
        r.setTags(report.getTags());
        return reportRepository.save(r);
    }


    @Transactional
    public Report createReport(Report report, Client client) {
        Report r = new Report();
        r.setTitle(report.getTitle());
        r.setDescription(report.getDescription());
        r.setAddress(report.getAddress());
        r.setReporter(client.getUserId());
        r.setReportArrivalTime(LocalTime.now());
        r.setState(Report.ReportState.NEW);
        r.setTags(report.getTags());

        return reportRepository.save(r);
    }


    @Transactional
    public Report updateReport(Long reportId, Report updated, Client client) {
        Report existing = getById(reportId);
        if (!existing.getReporter().equals(client.getUserId())){
            throw new RuntimeException("You can only edit your own reports");
        }

        if (existing.getState() != Report.ReportState.NEW){
            throw new RuntimeException("Cannot edit report in state: " + existing.getState());
        }

        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setAddress(updated.getAddress());

        return reportRepository.save(existing);
    }


    @Transactional
    public void deleteReport(Long reportId, Client client) {
        Report report = getById(reportId);
        reportRepository.delete(report);
    }


    @Transactional
    public Report claimReport(Long reportId, UUID dispatcherId) {
        Report report = getById(reportId);
        if (report.getState() != Report.ReportState.NEW) {
            throw new RuntimeException("Report is not available");
        }
        report.setHandlerID(dispatcherId);
        report.setState(Report.ReportState.PENDING);
        return reportRepository.save(report);
    }


    @Transactional
    public Report assignOfficer(Long reportId, String officerId, UUID handlerId) {
        Report report = getById(reportId);

        if (report.getState() != Report.ReportState.NEW && report.getState() != Report.ReportState.PENDING)
            throw new RuntimeException("Cannot assign officer in state: " + report.getState());

        Officer officer = officerRepository.findById(UUID.fromString(officerId))
                .orElseThrow(() -> new RuntimeException("Officer not found: " + officerId));

        if (officer.getStatus() != 1)
            throw new RuntimeException("Officer " + officer.getUsername() + " is not available");

        freeAssignedOfficer(report);

        officer.setStatus(2);
        officerRepository.save(officer);

        if (handlerId != null){
            report.setHandlerID(handlerId);
        }

        report.setAssignedOfficer(officer);
        report.setState(Report.ReportState.PENDING);

        return reportRepository.save(report);
    }


    @Transactional
    public Report resolveReport(Long reportId) {
        Report report = getById(reportId);
        freeAssignedOfficer(report);
        report.setState(Report.ReportState.FINISHED);
        report.setReportClosureTime(LocalTime.now());
        return reportRepository.save(report);
    }


    @Transactional
    public Report rejectReport(Long reportId) {
        Report report = getById(reportId);
        freeAssignedOfficer(report);
        report.setState(Report.ReportState.FINISHED);
        report.setReportClosureTime(LocalTime.now());
        return reportRepository.save(report);
    }


    public Report getReport(Long reportId) {
        return getById(reportId); }

    public List<Report> getReportsByUser(Client client) {
        return reportRepository.findByReporter(client.getUserId());
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public List<Report> getByState(Report.ReportState state) {
        return reportRepository.findByState(state);
    }


    private void freeAssignedOfficer(Report report) {
        if (report.getAssignedOfficer() != null) {
            report.getAssignedOfficer().setStatus(1);
            officerRepository.save(report.getAssignedOfficer());
            report.setAssignedOfficer(null);
        }
    }

    private Report getById(Long id) {
        return reportRepository.findById(id).orElseThrow(() -> new RuntimeException("Report not found: " + id));
    }
}