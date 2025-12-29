package memory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;


public class SharedMatrixTest {
    
    @Test
    public void ConstructorTest(){
        //Checking SharedMatrix
        SharedMatrix emptyMatrix1 = new SharedMatrix();

        double[][] emptyVecotrs = new double[0][];

        SharedMatrix emptyMatrix2 = new SharedMatrix(emptyVecotrs);

        assertArrayEquals(emptyMatrix2.readRowMajor(), emptyMatrix1.readRowMajor());

        
    }

    @Test
    public void readRowMajorTest() throws Exception{
        double[][] matrixData = {
            {1.0, 2.0}, 
            {3.0, 4.0}, 
            {5.0, 6.0}
        };
        SharedMatrix matrix1 = new SharedMatrix(matrixData);

        assertArrayEquals(matrixData, matrix1.readRowMajor());

        // Checking when matrix is column major
        SharedMatrix matrix2 = new SharedMatrix(matrixData);
        // access the private 'orientation' field in SharedVector
        java.lang.reflect.Field orientationField = SharedVector.class.getDeclaredField("orientation");
        orientationField.setAccessible(true); // Allow access to private field

        for (int i = 0; i < matrix2.length(); i++) {
            SharedVector v = matrix2.get(i);
            // Set the private field value manually
            orientationField.set(v, VectorOrientation.COLUMN_MAJOR);
        }

        assertEquals(VectorOrientation.COLUMN_MAJOR, matrix2.getOrientation(), 
            "Matrix should report COLUMN_MAJOR");

        double[][] transMatrixData = {
            {1.0 ,3.0, 5.0},
            {2.0, 4.0, 6.0}
        };

        assertArrayEquals(transMatrixData, matrix2.readRowMajor());
    }

    @Test
    public void readMatrixTest(){ //Helper function
        double[][] matrixData = {
            {1.0, 2.0}, 
            {3.0, 4.0}, 
            {5.0, 6.0}
        };
        SharedMatrix matrix = new SharedMatrix(matrixData);

        assertArrayEquals(matrixData, matrix.readMatrix());

        // Checking when matrix is column major
    }

    @Test
    public void readOppositeMatrixTest(){ //Helper function
        double[][] matrixData = {
            {1.0, 2.0}, 
            {3.0, 4.0}, 
            {5.0, 6.0}
        };
        SharedMatrix matrix = new SharedMatrix(matrixData);

        double[][] transMatrixData = {
            {1.0 ,3.0, 5.0},
            {2.0, 4.0, 6.0}
        };

        assertArrayEquals(transMatrixData, matrix.readOppositeMatrix());
    }
    
    @Test
    public void otherFunctionsTest(){
        double[][] matrixData = {
            {1.0, 2.0}, 
            {3.0, 4.0}, 
            {5.0, 6.0}
        };

        SharedMatrix matrix = new SharedMatrix();

        //Testing isEmpty
        assertEquals(true, matrix.isEmpty());

        //Testing isEmpty, Orientation, lentgth and get
        matrix.loadRowMajor(matrixData);
        assertEquals(false, matrix.isEmpty());
        assertEquals(3, matrix.length());
        assertArrayEquals(new double[]{1.0, 2.0}, matrix.get(0).get_vector_as_array());
    }

    @Test
    public void loadRowMajorTest(){
        double[][] matrixData = {
            {1.0, 2.0}, 
            {3.0, 4.0}, 
            {5.0, 6.0}
        };

        SharedMatrix matrix = new SharedMatrix(matrixData);

        assertArrayEquals(matrixData, matrix.readRowMajor());

        double[][] newMatrixData ={
            {1.0, 67.0}
        };
        matrix.loadRowMajor(newMatrixData);

        assertArrayEquals(newMatrixData, matrix.readRowMajor());

    }

}
