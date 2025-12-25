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
        loadMatrix(matrix, VectorOrientation.ROW_MAJOR);
    }

    public void loadColumnMajor(double[][] matrix) {
        loadMatrix(matrix, VectorOrientation.COLUMN_MAJOR);
    }

    /*
    * Helper funciton: gets matrix and loads it, replacing current data. Sets orientation as well.
     */
    public void loadMatrix(double[][] matrix, VectorOrientation matrixOrientation){
        SharedVector[] oldVectors = this.vectors;

        acquireAllVectorWriteLocks(vectors);
        try{
            SharedVector[] newVectors = new SharedVector[matrix.length];
            for(int i = 0; i < matrix.length; i++){
                newVectors[i] = new SharedVector(matrix[i], matrixOrientation);
            }

            this.vectors = newVectors;
        }
        finally{
            releaseAllVectorWriteLocks(oldVectors);
        } 
    }

    public double[][] readRowMajor() {
        // TODO: return matrix contents as a row-major double[][]
        try{
            acquireAllVectorReadLocks(vectors);
            if(this.vectors[0].getOrientation() == VectorOrientation.ROW_MAJOR){
                return this.readMatrix();
            }
            return this.readOppositeMatrix();
        }
        finally{
            releaseAllVectorReadLocks(vectors);
        }
    }

    /**
    * Helper Function
    * @return 2 dimension array, each element in array is a column in the matrix
    */
    public double[][] readColumnMajor() {
        try{
            acquireAllVectorReadLocks(vectors);
            if(this.vectors[0].getOrientation() == VectorOrientation.COLUMN_MAJOR){
                return this.readMatrix();
            }
            return this.readOppositeMatrix();
        }
        finally{
            releaseAllVectorReadLocks(vectors);
        }
    }

    /**
     * Helper Function: Regardles of orientation, return the matrix
     * @return two dimension doulbe array
     */
    public double[][] readMatrix(){
        try{
            acquireAllVectorReadLocks(vectors);
            if(this.isEmpty()){
                return new double[0][0];
            }

            double[][] out = new double[vectors.length][];

            for(int i=0; i < vectors.length;i++){
                out[i] = vectors[i].get_vector_as_array();
            }
            
            return out;
        }
        finally{
            releaseAllVectorReadLocks(vectors);
        }
    }

    /**
     * Helper Function: If matrix orientation is COLUMN, return the matrix as ROW (and vice versa). The code is the same, but the field names are for ROW -> COLUMN scenario.
     * @return two dimension double array
     */
    public double[][] readOppositeMatrix(){

        int number_of_rows = vectors[0].length();
        int number_of_cols = vectors.length;

        double[][] out = new double[number_of_rows][];

        for(int i=0; i < number_of_rows;i++){
            double[] new_row = new double[number_of_cols];
            for(int j=0; j<number_of_cols;j++){
                new_row[i] = vectors[j].get(i);
            }
        }
        releaseAllVectorReadLocks(vectors);
        return out;
    }



    public SharedVector get(int index) {
        // TODO: return vector at index
        return vectors[index];
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

    /*
    * Helper function to make the code cleaner
    */
    public boolean isEmpty(){
        return this.length()==0;
    }

    private void acquireAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: acquire read lock for each vector
        for(int i = 0; i<vectors.length;i++){
            vectors[i].readLock();
        }
    }

    private void releaseAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: release read locks
        for(int i = vectors.length -1; i>=0;i--){
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
        for(int i = vectors.length -1; i>=0;i--){
            vectors[i].writeUnlock();
        }
    }
}
