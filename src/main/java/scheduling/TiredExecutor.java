package scheduling;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TiredExecutor {

    private final TiredThread[] workers;
    private final PriorityBlockingQueue<TiredThread> idleMinHeap = new PriorityBlockingQueue<>();
    private final AtomicInteger inFlight = new AtomicInteger(0);

    public TiredExecutor(int numThreads) {
        // TODO
        this.workers = new TiredThread[numThreads];
        for(int threadID=0; threadID<numThreads;threadID++){
            TiredThread thread = new TiredThread(threadID, calculateRandomFatigueFactor());

            this.workers[threadID] = thread;
            this.idleMinHeap.add(thread);
            thread.start();
        }
    }

    /**
     * Helper function: calculates random fatigue factor between lowerValue(0.5) and upperValue(1.5) 
     * @return double
     */
    private double calculateRandomFatigueFactor(){
        double upperValue = 1.5;
        double lowerValue = 0.5; // I would have set it at as const fields in the class but I think it is not allowed :)
        return Math.random() * (upperValue - lowerValue) + lowerValue ;
    }

    public void submit(Runnable task) {
        // TODO
        try{
            inFlight.incrementAndGet();
            TiredThread thread = idleMinHeap.take();

            Runnable run_task = () -> {
                try{
                    task.run();
                }
                finally{
                    idleMinHeap.add(thread);
                    if(inFlight.decrementAndGet() == 0){
                        synchronized (this){
                            this.notifyAll();
                        }
                    }
                }
            };
            
            thread.newTask(run_task);
        }
        catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }
        
    }

    public void submitAll(Iterable<Runnable> tasks) {
        // TODO: submit tasks one by one and wait until all finish
        for (Runnable task: tasks){
                    submit(task);
            }
            synchronized (this){
                while(inFlight.get()>0){
                    try{
                        wait();
                    }
                    catch(InterruptedException e){
                        Thread.currentThread().interrupt();
                    }
                }
            }
    }

    public void shutdown() throws InterruptedException {
        // TODO
        for(TiredThread worker: workers){
            worker.shutdown();
        }
    }

    public synchronized String getWorkerReport() {
        // TODO: return readable statistics for each worker
        String out="";
        for(TiredThread worker: workers){
            int workerID = worker.getWorkerId();
            double workerFatigue = worker.getFatigue();
            long WorkerTimeUsed = worker.getTimeUsed();
            long WorkerTimeIdle = worker.getTimeIdle();
            if(out != ""){ //Only adding a new line if the string is not empty (no need to add a new line before first worker)
                out += System.lineSeparator();
            }
            out = out + "Worker #" + workerID + ": {Fatigue: "+ workerFatigue + ", Time Used: "+WorkerTimeUsed+ ", Time Idle: "+WorkerTimeIdle+"}";
        }
        return out;
    }

}
