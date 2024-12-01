package com.freighthub.core.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClusterService {

  public void getTheClusters(){
    try {
      // Path to the Python script
      String pythonScriptPath = "python_file_eke_path_eka_denna/main.py";

      // Points to pass to Python script
      String[] points = {
          "9.233596991805017,77.4682290519898",
          "8.984098180403793,77.92416168168963",
          "9.748302471861006,78.14938141443291",
          "9.206486022999377,78.24276520605818",

          "6.267781185855658,80.98843978940378",
          "6.124905616418271,80.6128204663429",
          "6.433337950386934,80.74673692065156",

          "7.668345467225762,79.94650444323919",
          "7.8010434972578935,81.44571548049963",
          "7.396347821062869,81.45551424544904",

          "7.574460850545271,81.68415209426871",
          "7.286205512353567,80.33192253124949",
          "7.415781858024183,79.91384189340782",

          "9.177188964146808,80.42337767741049",
          "9.199759369044074,80.28292871313555",
          "9.099793834045775,80.7826657255557",
          "13.999361832853586,78.64376623764481",
          "20.2119380042884, 77.41329749789597"
      };


      // Build command with arguments
      ProcessBuilder pb = new ProcessBuilder();
      pb.command("python", pythonScriptPath);
      for (String point : points) {
        pb.command().add(point);
      }

      // Start the process
      Process process = pb.start();

      // Capture Python script's stdout
      BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

      // Capture Python script's stderr
      BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

      // Read and print Python script's output
      System.out.println("Output from Python script:");
      String line;
      while ((line = stdInput.readLine()) != null) {
        System.out.println(line);
      }

      // Read and print Python script's errors (if any)
      System.out.println("Errors from Python script (if any):");
      while ((line = stdError.readLine()) != null) {
        System.err.println(line);
      }

      // Wait for the process to complete and get the exit code
      int exitCode = process.waitFor();
      System.out.println("Python script exited with code: " + exitCode);
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
