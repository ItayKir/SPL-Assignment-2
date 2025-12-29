package spl.lae;
import java.io.IOException;
import java.text.ParseException;

import parser.*;
import parser.OutputWriter.ResultMatrix;

public class Main {
    public static void main(String[] args) throws IOException {
      // TODO: main
      InputParser parser = new InputParser();
      try{
        ComputationNode rootNode = parser.parse("example.json");
        int numThreads = 4;
        LinearAlgebraEngine engine = new LinearAlgebraEngine(numThreads);
        ComputationNode result = engine.run(rootNode);
        OutputWriter.write(result.getMatrix(), "itay_test.json");
      }
      catch(ParseException e){
        OutputWriter.write(e.getMessage(), "itay_test.json");
      }
    }
}