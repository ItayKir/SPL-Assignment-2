package memory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
//import java.lang.reflect.Field;

public class SharedVectorTest {
    @Test
    public void testAdd(){
        double[] vecData1 = {10.0, 20.0, 30.0};
        double[] vecData2 = {4.0, 5.0, 6.0};

        SharedVector v1 = new SharedVector(vecData1, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(vecData2, VectorOrientation.ROW_MAJOR);

        v1.add(v2);
        double[] expectedResult= {14.0,25.0,36.0};

        assertArrayEquals(expectedResult, v1.get_vector_as_array(), 0.000001);
    }

    @Test
    public void testGet(){
        double[] vecData1 = {10.0, 20.0, 30.0};

        SharedVector v1 = new SharedVector(vecData1, VectorOrientation.ROW_MAJOR);
        double value2 = v1.get(1);
        double expectedResult = 20.0;

        assertEquals(expectedResult,value2);
    }

    @Test
    public void testLength(){
        double[] vecData1 = {10.0, 20.0, 30.0};

        SharedVector v1 = new SharedVector(vecData1, VectorOrientation.ROW_MAJOR);
        double vecLength = v1.length();
        double expectedResult = 3;

        assertEquals(expectedResult,vecLength);
    }

    @Test
    public void testGetOrientation(){
        double[] vecData1 = {10.0, 20.0, 30.0};
        double[] vecData2 = {4.0, 5.0, 6.0};

        SharedVector v1 = new SharedVector(vecData1, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(vecData2, VectorOrientation.COLUMN_MAJOR);

        VectorOrientation vecOren1 = v1.getOrientation();
        VectorOrientation vecOren2 = v2.getOrientation();

        VectorOrientation expectedResult1 = VectorOrientation.ROW_MAJOR;
        VectorOrientation expectedResult2 = VectorOrientation.COLUMN_MAJOR;

        assertEquals(expectedResult1,vecOren1);
        assertEquals(expectedResult2,vecOren2);
    }

    @Test
    public void testTranspose(){
        double[] vecData1 = {10.0, 20.0, 30.0};
        double[] vecData2 = {4.0, 5.0, 6.0};

        SharedVector v1 = new SharedVector(vecData1, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(vecData2, VectorOrientation.COLUMN_MAJOR);

        v1.transpose();
        v2.transpose();

        VectorOrientation expectedResult1 = VectorOrientation.COLUMN_MAJOR;
        VectorOrientation expectedResult2 = VectorOrientation.ROW_MAJOR;

        assertEquals(expectedResult1,v1.getOrientation());
        assertEquals(expectedResult2,v2.getOrientation());
    }

    @Test
    public void testNegeate(){
        double[] vecData1 = {10.0, 20.0, 30.0};
        double[] vecData2 = {4.0, 5.0, 6.0};

        SharedVector v1 = new SharedVector(vecData1, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(vecData2, VectorOrientation.COLUMN_MAJOR);

        v1.negate();
        v2.negate();

        double[] vecResultData1 = {-10.0, -20.0, -30.0};
        double[] vecResultData2 = {-4.0, -5.0, -6.0};

        SharedVector expectedResult1 = new SharedVector(vecResultData1, VectorOrientation.ROW_MAJOR);
        SharedVector expectedResult2 = new SharedVector(vecResultData2, VectorOrientation.COLUMN_MAJOR);


        assertArrayEquals(expectedResult1.get_vector_as_array(),v1.get_vector_as_array());
        assertArrayEquals(expectedResult2.get_vector_as_array(),v2.get_vector_as_array());
    } 

    @Test
    public void testDot(){
        double[] vecData1 = {10.0, 20.0, 30.0};
        double[] vecData2 = {4.0, 5.0, 6.0};

        SharedVector v1 = new SharedVector(vecData1, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(vecData2, VectorOrientation.COLUMN_MAJOR);

        double result=v1.dot(v2);

        double expectedResult = 320;


        assertEquals(expectedResult,result);
    }   

    @Test
    public void testVecMatMul(){
        double[] vecData1 = {10.0, 20.0};

        double[] vecMatrixData1 = {4.0, 5.0, 6.0};
        double[] vecMatrixData2 = {3.0, 8.0, 5.0};
        
        double[][] matrixVectors = new double[2][];
        matrixVectors[0]=vecMatrixData1;
        matrixVectors[1]=vecMatrixData2;

        //1
        SharedVector vector1 = new SharedVector(vecData1, VectorOrientation.ROW_MAJOR);
        SharedMatrix matrix1 = new SharedMatrix(matrixVectors);

        //2
        SharedVector vector2 = new SharedVector(vecData1, VectorOrientation.COLUMN_MAJOR);
        SharedMatrix matrix2 = new SharedMatrix(matrixVectors);

        double[] vecData2 = {10.0, 20.0, 30.0};
        
        //3
        SharedVector vector3 = new SharedVector(vecData2, VectorOrientation.ROW_MAJOR);
        SharedMatrix matrix3 = new SharedMatrix(matrixVectors);

        
        double[] vecResultData1 = {100.0, 210.0, 160.0};

        vector1.vecMatMul(matrix1);

        //Check result
        assertArrayEquals(vecResultData1,vector1.get_vector_as_array());
        //Not allowed column major
        assertThrows(IllegalArgumentException.class,() -> {vector2.vecMatMul(matrix2);});
        // Mismatch (1x3)(2x3) not allwoed
        assertThrows(IllegalArgumentException.class,() -> {vector3.vecMatMul(matrix3);});

        assertArrayEquals(vecResultData1,vector1.get_vector_as_array());

        // TODO: Check for vecMatMul row-vector x column major matrix
    }     

    @Test
    public void testVecMatMulWithColumnMatrix() throws Exception {
        // 1. Arrange
        double[][] data = {
            {1.0, 2.0}, // Initially Row 0, will become Column 0
            {3.0, 4.0}  // Initially Row 1, will become Column 1
        };
        SharedMatrix mat = new SharedMatrix(data);

        //access the private 'orientation' field in SharedVector
        java.lang.reflect.Field orientationField = SharedVector.class.getDeclaredField("orientation");
        orientationField.setAccessible(true); // Allow access to private field

        for (int i = 0; i < mat.length(); i++) {
            SharedVector v = mat.get(i);
            // Set the private field value manually
            orientationField.set(v, VectorOrientation.COLUMN_MAJOR);
        }

        // Verify that matrix is column major
        assertEquals(VectorOrientation.COLUMN_MAJOR, mat.getOrientation(), 
            "Matrix should report COLUMN_MAJOR");

        // 3. Act
        // Input Vector: [10.0, 20.0]
        SharedVector rowVector = new SharedVector(new double[]{10.0, 20.0}, VectorOrientation.ROW_MAJOR);
        
        // Multiply: [10, 20] * Columns
        rowVector.vecMatMul(mat);

        // 4. Assert
        double[] expected = {50.0, 110.0};
        assertArrayEquals(expected, rowVector.get_vector_as_array(), 0.0001, 
            "Vector should be dot-producted against the columns forced by reflection");
    }
}
