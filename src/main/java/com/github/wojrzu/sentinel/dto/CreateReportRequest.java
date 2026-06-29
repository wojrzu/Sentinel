package com.github.wojrzu.sentinel.dto;

import java.time.LocalTime;

public class CreateReportRequest {
    public String title;
    public String description;
    public String reportee;
    LocalTime startTime;
    public int urgency;
    public double latitude;
    public double longitude;
}
