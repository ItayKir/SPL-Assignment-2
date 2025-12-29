package spl.lae;

import parser.*;
import memory.*;
import scheduling.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

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
                node.resolve(leftMatrix.readMatrix());
            }

            return computationRoot;
        }
        finally{
            try{
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
        Iterator<ComputationNode> iter = node.getChildren().iterator();
        leftMatrix.loadRowMajor(iter.next().getMatrix());
        switch(node.getNodeType()){
            case ComputationNodeType.NEGATE:
                executor.submitAll(createNegateTasks());
                break;
            case ComputationNodeType.TRANSPOSE:
                executor.submitAll(createTransposeTasks());
                break;
            case ComputationNodeType.ADD:
                rightMatrix.loadRowMajor(iter.next().getMatrix());
                executor.submitAll(createAddTasks());
                break;
            case ComputationNodeType.MULTIPLY:
                rightMatrix.loadRowMajor(iter.next().getMatrix());
                executor.submitAll(createMultiplyTasks());
                break;               
        }
    }

    public List<Runnable> createAddTasks() {
        // TODO: return tasks that perform row-wise addition
        List<Runnable> runList = new LinkedList<Runnable>();
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
        List<Runnable> runList = new LinkedList<Runnable>();
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
        List<Runnable> runList = new LinkedList<Runnable>();
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
        List<Runnable> runList = new LinkedList<Runnable>();
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
