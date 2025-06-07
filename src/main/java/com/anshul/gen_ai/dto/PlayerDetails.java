package com.anshul.gen_ai.dto;

import java.time.LocalDate;
import java.util.List;

public record PlayerDetails(
        String name,
        LocalDate careerStartDate,
        LocalDate careerEnd,
        Integer age,
        String team,
        Integer rating,
        List<String> achievements
) {}
