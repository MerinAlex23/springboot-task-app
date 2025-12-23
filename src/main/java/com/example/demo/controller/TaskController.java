package com.example.demo.controller;

import com.example.demo.dto.TaskRequest;
import com.example.demo.model.TaskEntity;
import com.example.demo.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/tasks", consumes = MediaType.APPLICATION_JSON_VALUE)
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) { this.taskService = taskService; }

    @PostMapping
    public ResponseEntity<String> submit(@Valid @RequestBody TaskRequest request) {
        taskService.processSubmission(request);
        return ResponseEntity.ok("Saved to DB and added to Queue");
    }

    @GetMapping("/{id}")
    public TaskEntity get(@PathVariable Long id) {
        return taskService.getTask(id);
    }
}