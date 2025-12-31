package lae;

import memory.SharedMatrix;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import parser.ComputationNode;
import parser.ComputationNodeType;
import spl.lae.LinearAlgebraEngine;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LinearAlgebraEngineTest {
 
    private LinearAlgebraEngine engine; 
    private SharedMatrix leftMatrixRef;
    private SharedMatrix rightMatrixRef;

    @BeforeEach
    public void setUp() throws Exception {
        engine = new LinearAlgebraEngine(2);
        
        Field leftField = LinearAlgebraEngine.class.getDeclaredField("leftMatrix");
        leftField.setAccessible(true);
        leftMatrixRef = (SharedMatrix) leftField.get(engine);

        Field rightField = LinearAlgebraEngine.class.getDeclaredField("rightMatrix");
        rightField.setAccessible(true);
        rightMatrixRef = (SharedMatrix) rightField.get(engine);
    }

    @Test
    public void testConstructor() {
        assertNotNull(engine);
        assertNotNull(leftMatrixRef);
        assertNotNull(rightMatrixRef);
    }

    @Test
    public void testCreateAddTasks() {
        double[][] dataA = {{1.0, 2.0}};
        double[][] dataB = {{3.0, 4.0}};
        leftMatrixRef.loadRowMajor(dataA);
        rightMatrixRef.loadRowMajor(dataB);

        List<Runnable> tasks = engine.createAddTasks();

        tasks.forEach(Runnable::run);

        double[][] result = leftMatrixRef.readRowMajor();
        assertEquals(4.0, result[0][0]); // 1+3
        assertEquals(6.0, result[0][1]); // 2+4
    }

    @Test
    public void testCreateMultiplyTasks() {
        // 1*3 + 2*4 = 11
        double[][] dataLeft = {{1.0, 2.0}};
        double[][] dataRight = {{3.0}, {4.0}};
        
        leftMatrixRef.loadRowMajor(dataLeft);
        rightMatrixRef.loadRowMajor(dataRight);

        List<Runnable> tasks = engine.createMultiplyTasks();
        tasks.forEach(Runnable::run);

        double[][] result = leftMatrixRef.readRowMajor();
        assertEquals(11.0, result[0][0]);
    }

    @Test
    public void testCreateNegateTasks() {
        double[][] data = {{1.0, -2.0}};
        leftMatrixRef.loadRowMajor(data);

        List<Runnable> tasks = engine.createNegateTasks();
        tasks.forEach(Runnable::run);

        double[][] result = leftMatrixRef.readRowMajor();
        assertEquals(-1.0, result[0][0]);
        assertEquals(2.0, result[0][1]);
    }

    @Test
    public void testCreateTransposeTasks() {
        double[][] data = {{1.0, 2.0}};
        leftMatrixRef.loadRowMajor(data);
        List<Runnable> tasks = engine.createTransposeTasks();
        tasks.forEach(Runnable::run);

        double[][] result = leftMatrixRef.readRowMajor();
        assertEquals(2, result.length); // 2 rows
        assertEquals(1, result[0].length); // 1 col
        assertEquals(1.0, result[0][0]);
        assertEquals(2.0, result[1][0]);
    }

    @Test
    public void testLoadAndCompute() {
        // Test ADD operation via loadAndCompute
        double[][] d1 = {{10.0}};
        double[][] d2 = {{20.0}};
        ComputationNode node1 = new ComputationNode(d1);
        ComputationNode node2 = new ComputationNode(d2);
        ComputationNode rootOp = new ComputationNode(ComputationNodeType.ADD, List.of(node1, node2));

        engine.loadAndCompute(rootOp);

        double[][] result = leftMatrixRef.readRowMajor();
        assertEquals(30.0, result[0][0]);
    }

    @Test
    public void testRun() {
        double[][] d1 = {{10.0}};
        double[][] d2 = {{20.0}};
        ComputationNode node1 = new ComputationNode(d1);
        ComputationNode node2 = new ComputationNode(d2);
        ComputationNode root = new ComputationNode(ComputationNodeType.ADD, List.of(node1, node2));

        ComputationNode resultNode = engine.run(root);

        double[][] resMatrix = resultNode.getMatrix();
        assertEquals(30.0, resMatrix[0][0]);
    }
}