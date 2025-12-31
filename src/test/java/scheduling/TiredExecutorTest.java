package scheduling;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TiredExecutorTest {

    @Test
    public void testConstructor() {
        TiredExecutor executor = new TiredExecutor(5);
        // If no exception is thrown, basic initialization passed.
        // We can verify report to ensure workers exist.
        String report = executor.getWorkerReport();
        assertTrue(report.contains("Worker #4"), "Executor should initialize 5 workers (0-4).");
        try { executor.shutdown(); } catch (InterruptedException e) {}
    }

    @Test
    public void testSubmit() throws InterruptedException {
        // Arrange
        TiredExecutor executor = new TiredExecutor(2);
        AtomicInteger counter = new AtomicInteger(0);

        // Act
        executor.submit(counter::incrementAndGet);
        
        // Allow time for execution (since submit is void and non-blocking relative to main)
        Thread.sleep(100);

        // Assert
        assertEquals(1, counter.get(), "Task submitted should increment counter.");
        executor.shutdown();
    }

    @Test
    public void testSubmitAll() throws InterruptedException {
        // Arrange
        TiredExecutor executor = new TiredExecutor(4);
        int taskCount = 50;
        AtomicInteger counter = new AtomicInteger(0);
        List<Runnable> tasks = new ArrayList<>();

        for (int i = 0; i < taskCount; i++) {
            tasks.add(counter::incrementAndGet);
        }

        // Act
        // submitAll is blocking, so we expect all tasks to be done when it returns
        executor.submitAll(tasks);

        // Assert
        assertEquals(taskCount, counter.get(), "All tasks in the list should have been executed.");
        executor.shutdown();
    }

}