package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @Size(min = 10, message = "Description must be at least 10 characters")
    private String description;
}