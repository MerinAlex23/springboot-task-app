package com.example.demo.service;

import com.example.demo.dto.TaskRequest;
import com.example.demo.model.TaskEntity;
import com.example.demo.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskService taskService;

    private TaskRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleRequest = new TaskRequest();
        sampleRequest.setName("Interview Task");
        sampleRequest.setDescription("Prepare README and Tests");
    }

    @Test
    void processSubmission_ShouldSaveToDbAndAddToQueue() {
        // Arrange
        // We don't need to specify what save returns for this test, but we can verify it's called.

        // Act
        taskService.processSubmission(sampleRequest);

        // Assert
        // 1. Verify repository.save() was called exactly once
        verify(repository, times(1)).save(any(TaskEntity.class));

        // 2. Verify the task was added to the queue
        assertFalse(taskService.taskQueue.isEmpty(), "Queue should not be empty");
        assertEquals("Interview Task", taskService.taskQueue.peek().getName());
    }

    @Test
    void getTask_ShouldReturnTaskWhenIdExists() {
        // Arrange
        Long taskId = 1L;
        TaskEntity mockEntity = new TaskEntity(taskId, "Cached Task", "Description");
        when(repository.findById(taskId)).thenReturn(Optional.of(mockEntity));

        // Act
        TaskEntity result = taskService.getTask(taskId);

        // Assert
        assertNotNull(result);
        assertEquals(taskId, result.getId());
        assertEquals("Cached Task", result.getName());
    }

    @Test
    void getTask_ShouldThrowExceptionWhenIdDoesNotExist() {
        // Arrange
        Long taskId = 99L;
        when(repository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> taskService.getTask(taskId));
    }
}