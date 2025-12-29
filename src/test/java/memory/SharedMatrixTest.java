package memory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class SharedMatrixTest {
    
    @Test
    public void ConstructorTest(){
        //Checking SharedMatrix
        SharedMatrix emptyMatrix1 = new SharedMatrix();

        double[][] emptyVecotrs = new double[0][];

        SharedMatrix emptyMatrix2 = new SharedMatrix(emptyVecotrs);

        assertArrayEquals(emptyMatrix2.readRowMajor(), emptyMatrix1.readRowMajor());

        // Testing loadRowMajor when overriding exisitng matrix
        double[][] matrixData = {
            {1.0, 2.0}, 
            {3.0, 4.0}, 
            {5.0, 6.0}
        };

        SharedMatrix matrix1 = new SharedMatrix(matrixData);

        emptyMatrix2.loadRowMajor(matrixData);

        assertArrayEquals(emptyMatrix2.readRowMajor(), emptyMatrix1.readRowMajor());



    }

    @Test
    public void readRowMajorTest(){
        double[][] matrixData = {
            {1.0, 2.0}, 
            {3.0, 4.0}, 
            {5.0, 6.0}
        };
        SharedMatrix matrix = new SharedMatrix(matrixData);

        assertArrayEquals(matrixData, matrix.readRowMajor());

        // Checking 
    }
}
