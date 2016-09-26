package core.system;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.swing.JOptionPane;

import core.Constants;
import core.JIDE;

public class ConsoleManager {
	public static String addedText;
	public static int moved;
	
	private static JIDE jideInstance;
	public static Process pr;
	
	public static void init(JIDE jideInst) {
		jideInstance = jideInst;
		moved = 0;
		addedText = "";
	}
	
	/*
	 * TODO: Add Code to delete previous build
	 */
	private static void build(String fileName) throws InterruptedException, IOException {
		Runtime rt = Runtime.getRuntime();
		pr = rt.exec("gcc " + fileName + " -o " + fileName.substring(0, fileName.length() - 2) + Constants.PROG_EXTENSION);

		BufferedReader build_errors = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
		String line;
		String msg = "";
		while ((line = build_errors.readLine()) != null) {
			msg += line;
			msg += "\n";
		}

		if (!msg.equals("")) {
			JOptionPane.showMessageDialog(jideInstance, "Could not compile! Errors: \n" + msg);
		}

		pr.waitFor();
		pr.destroy();
	}

	/*
	 * TODO: Figure out issue with stdin handling occuring before stdout
	 * handling
	 */
	private static void runEXE(String fileName) throws IOException, InterruptedException {
		pr = Runtime.getRuntime().exec(fileName.substring(0, fileName.length() - 2) + Constants.PROG_EXTENSION);
		JIDE.area2.setEditable(true);

		// UNUSED:
		// InputStream stderr = pr.getErrorStream();
		InputStream stdout = pr.getInputStream();

		BufferedReader reader_out = new BufferedReader(new InputStreamReader(stdout));
		// UNUSED:
		// BufferedReader reader_err = new BufferedReader(new
		// InputStreamReader(stderr));
		String line;

		while (pr.isAlive()) {
			while ((line = reader_out.readLine()) != null)
				JIDE.area2.append(line + "\n");
			pr.waitFor();
		}

		JIDE.area2.setEditable(false);

		ActionManager.Terminate.setEnabled(false);
	}

	public static synchronized void sendMsg(String msg) throws IOException {

		OutputStream stdin = pr.getOutputStream();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));

		writer.write(addedText + "\n");
		writer.flush();

		addedText = "";
	}

	public static void buildAndRun(String fileName) {
		try {
			build(fileName);
			addedText = new String();
			JIDE.area2.setText("");

			new Thread() {
				public void run() {
					try {
						runEXE(fileName);
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
					}
				}
			}.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
