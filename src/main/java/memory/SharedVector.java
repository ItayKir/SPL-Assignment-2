package memory;

import java.util.concurrent.locks.ReadWriteLock;

public class SharedVector {

    private double[] vector;
    private VectorOrientation orientation;
    private ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    public SharedVector(double[] vector, VectorOrientation orientation) {
        this.vector = vector;
        this.orientation = orientation;
    }

    public double get(int index) {
        // TODO: return element at index (read-locked)
        if(index < 0 || index >= vector.length){
            throw new ArrayIndexOutOfBoundsException("index out of bounds");
        }
        readLock();
        double vector_value = vector[index];
        readUnlock();
        return vector_value;
    }

    public int length() {
        // TODO: return vector length
        readLock();
        int length = vector.length;
        readUnlock();
        return length;
    }

    public VectorOrientation getOrientation() {
        // TODO: return vector orientation
        readLock();
        VectorOrientation return_orientation = this.orientation;
        readUnlock();
        return return_orientation;
    }

    public void writeLock() {
        // TODO: acquire write lock
        lock.writeLock().lock();
    }

    public void writeUnlock() {
        // TODO: release write lock
        lock.writeLock().unlock();
    }

    public void readLock() {
        // TODO: acquire read lock
        lock.readLock().lock();
    }

    public void readUnlock() {
        // TODO: release read lock
        lock.readLock().unlock();
    }

    public void transpose() {
        // TODO: transpose vector
        writeLock();
        if(orientation == VectorOrientation.COLUMN_MAJOR){
            orientation = VectorOrientation.ROW_MAJOR;
        }
        else{
            orientation = VectorOrientation.COLUMN_MAJOR;
        }
        writeUnlock();
    }

    public void add(SharedVector other) {
        // TODO: add two vectors
        if(other == null){
            throw new IllegalArgumentException("Other vector is null");
        }

        other.readLock();
        if (this.vector.length != other.vector.length) {
            other.readUnlock();
            throw new IllegalArgumentException("Illegal operation: dimensions mismatch");
        }

        writeLock();
        
        
        for(int index=0; index<this.length(); index++){
            vector[index] = this.vector[index] + other.vector[index];
        }
        
        other.readUnlock();
        writeUnlock();
    }

    public void negate() {
        // TODO: negate vector
        writeLock();

        for(int index=0; index<this.length(); index++){
            vector[index] = (-1) * this.vector[index];
        }
        
        writeUnlock();
    }

    public double dot(SharedVector other) {
        // TODO: compute dot product (row · column)
        double sum = 0;
        this.readLock();
        other.readLock();
        for(int index=0; index<this.length(); index++){
            sum += this.vector[index] * other.vector[index]; 
        }
        other.readUnlock();
        this.readUnlock();
        return sum;
    }

    public void vecMatMul(SharedMatrix matrix) {
        // TODO: compute row-vector × matrix
        // add validation - matrix must be column_major!
        double[] new_vector = new double[matrix.length()];
        this.writeLock();
        for(int index = 0; index < matrix.length(); index++){
            new_vector[index] = this.dot(matrix.get(index));
        }
        this.vector = new_vector;
        this.writeUnlock();
    }

    // Helper Functions:

    public double[] get_vector_as_array(){
        double[] out = new double[vector.length];
        readLock();

        for(int i=0; i<vector.length;i++){
            out[i] = vector[i];
        }
        
        readUnlock();
        return out;
    }
}
