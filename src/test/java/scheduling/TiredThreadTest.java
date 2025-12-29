package scheduling;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class TiredThreadTest {

    @Test
    public void testRun() throws InterruptedException {
        // Arrange
        TiredThread thread = new TiredThread(0, 1.0);
        thread.start();
        AtomicBoolean taskRan = new AtomicBoolean(false);

        // Act
        thread.newTask(() -> taskRan.set(true));
        
        // Wait briefly for task to complete
        Thread.sleep(100);

        // Assert
        assertTrue(taskRan.get(), "Checking if task ran");
        
        // Cleanup
        thread.shutdown();
        thread.join();
    }

    @Test
    public void testShutdown() throws InterruptedException {
        // Arrange
        TiredThread thread = new TiredThread(1, 1.0);
        thread.start();

        // Act
        thread.shutdown();
        thread.join(1000); // Wait for it to die

        // Assert
        assertFalse(thread.isAlive(), "Thread should not be alive after shutdown.");
    }

    @Test
    public void testCompareTo() throws InterruptedException {
        // Arrange
        // Thread 1 gets tired very easily (Factor 100)
        TiredThread t1 = new TiredThread(1, 100.0);
        // Thread 2 gets tired slowly (Factor 0.01)
        TiredThread t2 = new TiredThread(2, 0.01);
        
        t1.start();
        t2.start();

        // Act: Run the same workload on both
        Runnable work = () -> {
            try { Thread.sleep(10); } catch (InterruptedException e) {}
        };
        
        t1.newTask(work);
        t2.newTask(work);
        
        // Wait for work to finish
        Thread.sleep(100);

        // Assert
        // t1 should have significantly higher fatigue than t2.
        // Therefore, t1 > t2. compareTo should return a positive number.
        assertTrue(t1.getFatigue() > t2.getFatigue());
        assertTrue(t1.compareTo(t2) > 0, "High fatigue thread should compare greater than low fatigue thread.");

        // Cleanup
        t1.shutdown();
        t2.shutdown();
    }
}