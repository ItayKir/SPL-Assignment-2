package spl.lae;

import parser.*;
import memory.*;
import scheduling.*;

import java.util.List;


public class LinearAlgebraEngine {

    private SharedMatrix leftMatrix = new SharedMatrix();
    private SharedMatrix rightMatrix = new SharedMatrix();
    private TiredExecutor executor;

    public LinearAlgebraEngine(int numThreads) {
        // TODO: create executor with given thread count
        this.executor = new TiredExecutor(numThreads);
    }

    public ComputationNode run(ComputationNode computationRoot) {
        // TODO: resolve computation tree step by step until final matrix is produced
        try{
            computationRoot.associativeNesting();
            while(computationRoot.getNodeType() != ComputationNodeType.MATRIX){
                ComputationNode node = computationRoot.findResolvable();
                loadAndCompute(node);
                node.resolve(leftMatrix.readRowMajor());
            }

            return computationRoot;
        }
        finally{
            try{
                System.out.print(this.getWorkerReport());
                executor.shutdown();
            }
            catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
    }

    public void loadAndCompute(ComputationNode node) {
        // TODO: load operand matrices
        // TODO: create compute tasks & submit tasks to executor
        List<ComputationNode> children = node.getChildren();

        java.util.concurrent.atomic.AtomicReference<String> error = new java.util.concurrent.atomic.AtomicReference<>(null);
        // error is defined like this so the first task which throws an error will update it using CAS, then other threads will not run their task
        // and the error message that we will write to the file (in main), will be that first error 

        leftMatrix.loadRowMajor(children.get(0).getMatrix()); //left matrix

        List<Runnable> tasksToRun = null;

        switch(node.getNodeType()){
            case ComputationNodeType.NEGATE:
                tasksToRun = createNegateTasks();
                break;
            case ComputationNodeType.TRANSPOSE:
                tasksToRun = createTransposeTasks();
                break;
            case ComputationNodeType.ADD:
                rightMatrix.loadRowMajor(children.get(1).getMatrix()); //right matrix
                tasksToRun = createAddTasks();
                break;
            case ComputationNodeType.MULTIPLY:
                rightMatrix.loadRowMajor(children.get(1).getMatrix());//right matrix
                tasksToRun = createMultiplyTasks();
                break;               
        }
        
        List<Runnable> tasksToRunWrapped = executor.createArrayListRunnables();

        if(tasksToRun != null){
            for(Runnable task: tasksToRun){
                tasksToRunWrapped.add(() -> {
                    if(error.get() != null)
                        return;
                    try{
                        task.run();
                    }
                    catch(Exception e){
                        error.compareAndSet(null, e.getMessage());
                    }
                });
            }
        }

        executor.submitAll(tasksToRunWrapped);

        if(error.get() != null){
            throw new RuntimeException(error.get());
        }
    }

    public List<Runnable> createAddTasks() {
        // TODO: return tasks that perform row-wise addition
        List<Runnable> runList = executor.createArrayListRunnables();
        for(int row =0; row < leftMatrix.length();row++){
            int i = row;
            runList.add(() -> {
                leftMatrix.get(i).add(rightMatrix.get(i));
            });
        }
        return runList;
    }

    public List<Runnable> createMultiplyTasks() {
        // TODO: return tasks that perform row × matrix multiplication
        List<Runnable> runList = executor.createArrayListRunnables();
        for(int row =0; row < leftMatrix.length();row++){
            int i = row;
            runList.add(() -> {
                leftMatrix.get(i).vecMatMul(rightMatrix);
            });
        }
        return runList;
    }

    public List<Runnable> createNegateTasks() {
        // TODO: return tasks that negate rows
        List<Runnable> runList = executor.createArrayListRunnables();
        for(int row =0; row < leftMatrix.length();row++){
            int i = row;
            runList.add(() -> {
                leftMatrix.get(i).negate();
            });
        }
        return runList;
    }

    public List<Runnable> createTransposeTasks() {
        // TODO: return tasks that transpose rows
        List<Runnable> runList = executor.createArrayListRunnables();
        for(int row =0; row < leftMatrix.length();row++){
            int i = row;
            runList.add(() -> {
                leftMatrix.get(i).transpose();
            });
        }
        return runList;
    }

    public String getWorkerReport() {
        // TODO: return summary of worker activity
        return executor.getWorkerReport();
    }
}
