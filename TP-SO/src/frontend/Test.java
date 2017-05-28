package frontend;

import backend.*;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class Test {

	public static void main(String[] args) throws Exception {
		
		Queue<Burst> trace = new LinkedList<>();
		String threadsCount = "[";
		String deviceCount = "3";
		trace.add(new Burst(Burst.BurstType.CPU, 3));
		trace.add(new Burst(Burst.BurstType.IO_1, 2));
		trace.add(new Burst(Burst.BurstType.CPU, 1));
		ULT ult1 = new ULT(1, trace);
		trace = new LinkedList<>();
		trace.add(new Burst(Burst.BurstType.DEAD, 1));
		trace.add(new Burst(Burst.BurstType.CPU, 2));
		trace.add(new Burst(Burst.BurstType.IO_2, 3));
		trace.add(new Burst(Burst.BurstType.CPU, 4));
		ULT ult2 = new ULT(2, trace);
		List<ULT> userThreads = new LinkedList<>();
		userThreads.add(ult1);
		userThreads.add(ult2);
		UserScheduler us1 = new UserSchedulerFIFO(userThreads);
		KLT klt1 = new KLT(1, 0, us1);
		threadsCount += "{%22ULT%22:2},";

		trace = new LinkedList<>();
		trace.add(new Burst(Burst.BurstType.DEAD, 2));
		trace.add(new Burst(Burst.BurstType.CPU, 4));
		trace.add(new Burst(Burst.BurstType.IO_1, 1));
		trace.add(new Burst(Burst.BurstType.CPU, 2));
		ult1 = new ULT(1, trace);
		trace = new LinkedList<>();
		trace.add(new Burst(Burst.BurstType.CPU, 2));
		trace.add(new Burst(Burst.BurstType.IO_2, 2));
		trace.add(new Burst(Burst.BurstType.CPU, 1));
		ult2 = new ULT(2, trace);
		userThreads = new LinkedList<>();
		userThreads.add(ult1);
		userThreads.add(ult2);
		us1 = new UserSchedulerFIFO(userThreads);
		KLT klt2 = new KLT(2, 2, us1);
		threadsCount += "{%22ULT%22:2},";
		
		trace = new LinkedList<>();
		trace.add(new Burst(Burst.BurstType.CPU, 2));
		trace.add(new Burst(Burst.BurstType.IO_0, 2));
		trace.add(new Burst(Burst.BurstType.CPU, 2));
		ult1 = new ULT(1, trace);
		userThreads = new LinkedList<>();
		userThreads.add(ult1);
		us1 = new UserSchedulerFIFO(userThreads);
		KLT klt3 = new KLT(3, 1, us1);
		threadsCount += "{%22ULT%22:1}]";
		
		List<KLT> kernelThreads = new LinkedList<>();
		kernelThreads.add(klt1);
		kernelThreads.add(klt2);
		kernelThreads.add(klt3);
		
		KernelScheduler ks = new KernelSchedulerRR(2, kernelThreads, 2);
		
		Gantt gantt = ks.solve();
		/*try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("filename.txt"), "utf-8"))) {
		   writer.write(gantt.getJson());
		} 
		catch (IOException ex) {
			System.out.println("lmao");
		} */
		//String parameters = "asd";
		Desktop.getDesktop().open(new File(createHtmlLauncher("./index.html?gantt=" + gantt.getRunJson() + "&threads=" + threadsCount + "&devices=" + deviceCount + "&ready=" + gantt.getReadyJson() + "&block=" + gantt.getBlockJson())));
		System.out.println(gantt);
		System.out.println(gantt.getBlockJson());
	}
	
	private static String createHtmlLauncher(String targetUrl) throws Exception {          
	    /*String launcherFile = System.getProperty("java.io.tmpdir") + "local_launcher.html";
	    File launcherTempFile = new File(launcherFile); */   
	    try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./webFront/local_launcher.html"), "utf-8"))) {
		   writer.write("<meta http-equiv=\"refresh\" content=\"0; url=" + targetUrl + "\" />");
		} 
		catch (IOException ex) {
			System.out.println("lmao");
		} 

	    return "./webFront/local_launcher.html";        
	}

}
