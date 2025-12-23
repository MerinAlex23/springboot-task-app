package com.example.demo.scheduler;

import com.example.demo.dto.TaskRequest;
import com.example.demo.service.TaskService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class QueueProcessor {
    private final TaskService taskService;
    private final RestTemplate restTemplate = new RestTemplate();

    public QueueProcessor(TaskService taskService) { this.taskService = taskService; }

  @Async
@Scheduled(fixedDelay = 10000)
public void run() {
    TaskRequest request = taskService.taskQueue.poll();
    if (request != null) {
        System.out.println("Queue found a task! Processing: " + request.getName() + " on thread: " + Thread.currentThread().getName());
        try {
            restTemplate.postForObject("https://example.com/api", request, String.class);
            System.out.println("Successfully posted " + request.getName());
        } catch (Exception e) {
            System.err.println("Async post failed for " + request.getName() + " but app is still healthy.");
        }
    } else {
        System.out.println("Queue is empty, waiting for next cycle...");
    }
}
}


