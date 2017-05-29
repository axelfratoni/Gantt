package frontend;

import backend.*;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class Test {

	public static String threadsCount;
	public static void main(String[] args) throws Exception {
		String deviceCount = "3";
		if (args == null || args.length != 1) {
			System.out.println("Invalid nubmer of arguments: exactly one required (input file path)");
			return;
		}
		Gantt gantt;
		try {
			gantt = InputParser.build(args[0]).solve();
		} catch (Exception e) {
			System.err.println("Error durante la ejecución:");
			System.err.println(e.getMessage());
			return;
		}
		Desktop.getDesktop().open(new File(createHtmlLauncher("./index.html?gantt=" + gantt.getRunJson() + "&threads=" + threadsCount + "&devices=" + deviceCount + "&ready=" + gantt.getReadyJson() + "&block=" + gantt.getBlockJson())));
		System.out.println(gantt);
	}
	
	private static String createHtmlLauncher(String targetUrl) throws Exception {          
	    try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./webFront/local_launcher.html"), "utf-8"))) {
		   writer.write("<meta http-equiv=\"refresh\" content=\"0; url=" + targetUrl + "\" />");
		} 
		catch (IOException ex) {
			System.err.println("Error reading input");
		} 

	    return "./webFront/local_launcher.html";        
	}

}
