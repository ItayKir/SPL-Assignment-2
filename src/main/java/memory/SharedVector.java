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
        try{
            readLock();
            double vector_value = vector[index];
            return vector_value;
        }
        finally{
            readUnlock();
        }
    }

    public int length() {
        // TODO: return vector length
        try{
            readLock();
            int length = vector.length;
            return length;
        }
        finally{
            readUnlock();
        }
    }

    public VectorOrientation getOrientation() {
        // TODO: return vector orientation
        try{
            readLock();
            VectorOrientation return_orientation = this.orientation;
            return return_orientation;
        }
        finally{
            readUnlock();
        }
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
        try{
            writeLock();
            if(orientation == VectorOrientation.COLUMN_MAJOR){
                orientation = VectorOrientation.ROW_MAJOR;
            }
            else{
                orientation = VectorOrientation.COLUMN_MAJOR;
            }
        }
        finally{
            writeUnlock();
        }
    }

    public void add(SharedVector other) {
        // TODO: add two vectors
        if(other == null){
            throw new IllegalArgumentException("Other vector is null");
        }

        try{
            other.readLock();
            if (this.vector.length != other.vector.length) {
                other.readUnlock();
                throw new IllegalArgumentException("Illegal operation: dimensions mismatch");
            }

            writeLock();
            
            
            for(int index=0; index<this.length(); index++){
                vector[index] = this.vector[index] + other.vector[index];
            }
        }
        finally{
            other.readUnlock();
            writeUnlock();
        }
    }

    public void negate() {
        // TODO: negate vector
        try{
            writeLock();

            for(int index=0; index<this.length(); index++){
                vector[index] = (-1) * this.vector[index];
            }
        
        }
        finally{
            writeUnlock();
        }
    }

    public double dot(SharedVector other) {
        // TODO: compute dot product (row · column)
        double sum = 0;
        if(other == null){
            throw new IllegalArgumentException("other vector is null");
        }
        try{
            this.readLock();
            other.readLock();

            for(int index=0; index<this.length(); index++){
                sum += this.vector[index] * other.vector[index]; 
            }
            return sum;
        }
        finally{
            other.readUnlock();
            this.readUnlock();
        }
    }

    public void vecMatMul(SharedMatrix matrix) {
        // TODO: compute row-vector × matrix
        if(matrix == null){
            throw new IllegalArgumentException("Provided Matrix is null");
        }
        try{
            this.writeLock();
            
            double[][] column_matrix = matrix.readColumnMajor();
            double[] new_vector = new double[matrix.length()];

            if(column_matrix.length==0){
                this.vector = new_vector;
                return; // Matrix is empty...
            }

            for(int row=0;row < this.vector.length; row++){
                for(int col=0; col < column_matrix.length; col++){
                    new_vector[col] += this.vector[row] * column_matrix[col][row]; 
                }
            }
            this.vector = new_vector;

        }
        finally{
            this.writeUnlock();
        }
    }

    // Helper Functions:

    public double[] get_vector_as_array(){
        try{
            readLock();

            double[] out = new double[vector.length];
            for(int i=0; i<vector.length;i++){
                out[i] = vector[i];
            }

            return out;
        }
        finally{
            readUnlock();
        }
    }
}
