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
        writeLock();
        try{
            other.readLock();
            try{
                if (this.vector.length != other.length()) {
                    throw new IllegalArgumentException("Illegal operation: dimensions mismatch");
                }

                for(int index=0; index<this.vector.length; index++){
                    vector[index] = this.vector[index] + other.vector[index];
                }
            }
            finally{
                other.readUnlock();
            }
        }
        finally{
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

        this.writeLock();

        try{
            if(this.orientation==VectorOrientation.COLUMN_MAJOR){
                throw new IllegalArgumentException("This vector is COLUMN Major. Row is required.");
            }

            int matrixLength = matrix.length();
            if(matrixLength == 0){
                return;
            }        
            
            double[] new_vector;
            
            if(matrix.getOrientation() == VectorOrientation.COLUMN_MAJOR){
                if(vector.length != matrix.get(0).length()){
                    throw new IllegalArgumentException("Illegal operation: dimensions mismatch");
                }

                new_vector = new double[matrixLength];
                for(int i =0; i<matrixLength; i++){
                    SharedVector matrixColumn = matrix.get(i);
                    new_vector[i] = this.dot(matrixColumn);
                }
            }
            else{
                if(vector.length != matrixLength){
                    throw new IllegalArgumentException("Illegal operation: dimensions mismatch");
                }

                new_vector = new double[matrix.get(0).length()];
                for(int i=0; i<matrixLength; i++){
                    double vectorValue = this.get(i);
                    if(vectorValue==0) 
                        continue; // 0 vectors dot anything is 0...

                    SharedVector row = matrix.get(i);
                    row.readLock();
                    int rowLength = row.length();
                    try{
                        for(int j=0; j < rowLength; j++){
                            new_vector[j] += vectorValue * row.get(j);
                        }
                    }
                    finally{
                        row.readUnlock();
                    }

                }

            }
            this.vector = new_vector;

        }
        finally{
            this.writeUnlock();
        }
    }


/**
 * Helper function: returning a snapshot of the vector as double[]
 * @return double[] array
 * @Note assuming readLock or acquireAllVectorReadLocks was ran
 */
    public double[] get_vector_as_array(){
        double[] out = new double[vector.length];
        for(int i=0; i<vector.length;i++){
            out[i] = vector[i];
        }

        return out;
    }
}
