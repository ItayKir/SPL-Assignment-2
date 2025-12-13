package memory;

public class SharedMatrix {

    private volatile SharedVector[] vectors = {}; // underlying vectors

    public SharedMatrix() {
        // TODO: initialize empty matrix
        vectors = new SharedVector[0];
    }

    public SharedMatrix(double[][] matrix) {
        // TODO: construct matrix as row-major SharedVectors
        loadRowMajor(matrix);
    }

    public void loadRowMajor(double[][] matrix) {
        // TODO: replace internal data with new row-major matrix
        acquireAllVectorWriteLocks(vectors);

        vectors = new SharedVector[matrix.length];
        for(int i = 0; i < matrix.length; i++){
            vectors[i] = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
        }

        releaseAllVectorWriteLocks(vectors);
    }

    public void loadColumnMajor(double[][] matrix) {
        acquireAllVectorWriteLocks(vectors);

        vectors = new SharedVector[matrix.length];
        for(int i = 0; i < matrix.length; i++){
            vectors[i] = new SharedVector(matrix[i], VectorOrientation.COLUMN_MAJOR);
        }
        
        releaseAllVectorWriteLocks(vectors);
    }

    public double[][] readRowMajor() {
        // TODO: return matrix contents as a row-major double[][]
        double[][] out = new double[vectors.length][];
        acquireAllVectorReadLocks(vectors);
        for(int i=0; i < vectors.length;i++){
            out[i] = vectors[i].get_vector_as_array();
        }
        releaseAllVectorReadLocks(vectors);
        return out;
    }

    public SharedVector get(int index) {
        // TODO: return vector at index
        vectors[index].readLock();
        SharedVector vec = vectors[index];
        vectors[index].readUnlock();
        return vec;
    }

    public int length() {
        // TODO: return number of stored vectors
        return vectors.length;
    }

    public VectorOrientation getOrientation() {
        // TODO: return orientation
        // Assuming all vecotrs inside the matrix are of the same orientation
        acquireAllVectorReadLocks(vectors);
        VectorOrientation orientation = vectors[0].getOrientation();
        releaseAllVectorReadLocks(vectors);
        return orientation;
    }

    private void acquireAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: acquire read lock for each vector
        for(int i = 0; i<vectors.length;i++){
            vectors[i].readLock();
        }
    }

    private void releaseAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: release read locks
        for(int i = 0; i<vectors.length;i++){
            vectors[i].readUnlock();
        }
    }

    private void acquireAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: acquire write lock for each vector
        for(int i = 0; i<vectors.length;i++){
            vectors[i].writeLock();
        }
    }

    private void releaseAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: release write locks
        for(int i = 0; i<vectors.length;i++){
            vectors[i].writeUnlock();
        }
    }
}
