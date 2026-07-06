package com.github.wojrzu.sentinel.dto;

import com.github.wojrzu.sentinel.model.Tag;

import java.time.LocalTime;
import java.util.List;

public class CreateReportRequest {
    public String title;
    public String description;
    public String reportee;
    LocalTime startTime;
    public int urgency;
    public double latitude;
    public double longitude;
    public List<Tag> tags;
}
