package spl.lae;
import java.io.IOException;

import parser.*;


public class Main {
    public static void main(String[] args) throws IOException {
      // TODO: main
      if(args.length < 3){
        System.err.println("Requied {number of threads} {path/to/input/file} {path/to/output/file}");
        return;
      }
      InputParser parser = new InputParser();
      int numThreads = Integer.parseInt(args[0]);
      String path_to_input_file = args[1];
      String path_to_output_file = args[2];
      
      try{
        LinearAlgebraEngine engine = new LinearAlgebraEngine(numThreads);

        ComputationNode rootNode = parser.parse(path_to_input_file);
        ComputationNode result = engine.run(rootNode);
        OutputWriter.write(result.getMatrix(), path_to_output_file);
      }
      catch(Exception e){
        OutputWriter.write(e.getMessage(), path_to_output_file);
      }
    }
}