package com.example.demo.service;

import com.example.demo.dto.TaskRequest;
import com.example.demo.model.TaskEntity;
import com.example.demo.repository.TaskRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue; 

@Service
public class TaskService {

    private final TaskRepository repository;

    public final Queue<TaskRequest> taskQueue = new ConcurrentLinkedQueue<>();

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public void processSubmission(TaskRequest request) {
        TaskEntity entity = new TaskEntity(null, request.getName(), request.getDescription());
        repository.save(entity);
        
        taskQueue.add(request);
    }

    @Cacheable(value = "tasks", key = "#id")
    public TaskEntity getTask(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
    }
}