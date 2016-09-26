package core.system;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import core.Constants;
import core.JIDE;

public class ConsoleManager {
	public static String addedText;
	public static int moved;
	private static boolean buildFailed = false, run = false;
	
	private static JIDE jideInstance;
	public static Process pr;
	
	public static void init(JIDE jideInst) {
		jideInstance = jideInst;
		moved = 0;
		addedText = "";
	}
	
	private static void build(String fileName) throws InterruptedException, IOException {
		File f = new File(fileName.substring(0, fileName.length() - 2) + Constants.PROG_EXTENSION);
		
		Runtime rt = Runtime.getRuntime();
		
		pr = rt.exec("gcc " + fileName + " -o " + fileName.substring(0, fileName.length() - 2) + Constants.PROG_EXTENSION);
		
		BufferedReader build_errors = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
		String line;
		String msg = "";
		while ((line = build_errors.readLine()) != null) {
			msg += line;
			msg += "\n";
		}

		buildFailed = false;
		
		if (!msg.equals("")) {
			String optionButtons[] = { "Yes", "No" };
			if(JOptionPane.showOptionDialog(null, "Build Failed:\n" + msg + "\nWould you like to run a previous build?",
					"Build Failed!", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, optionButtons,
					optionButtons[0]) == JOptionPane.YES_OPTION) {
				if(f.exists() && !f.isDirectory()) {
					buildFailed = true;
					run = true;
				} else {
					JOptionPane.showMessageDialog(jideInstance, "No previous builds found.");
					buildFailed = true;
					run = false;
				}
			} else {
				buildFailed = true;
				run = false;
			}
		}

		pr.waitFor();
		pr.destroy();
		
		f = new File(fileName.substring(0, fileName.length() - 2) + Constants.PROG_EXTENSION);
		
		if(f.exists() && !f.isDirectory() && !buildFailed) System.out.println("Built to " + f.getAbsolutePath() + " successfully.");		
	}

	/*
	 * TODO: Figure out issue with stdin handling occuring before stdout
	 * handling
	 */
	private static void runEXE(String fileName) throws IOException, InterruptedException {
		if(buildFailed && !run) return;
		
		pr = Runtime.getRuntime().exec(fileName.substring(0, fileName.length() - 2) + Constants.PROG_EXTENSION);
		JIDE.console.setEditable(true);

		// UNUSED:
		// InputStream stderr = pr.getErrorStream();
		InputStream stdout = pr.getInputStream();

		BufferedReader reader_out = new BufferedReader(new InputStreamReader(stdout));
		// UNUSED:
		// BufferedReader reader_err = new BufferedReader(new
		// InputStreamReader(stderr));
		String line;

		while (pr.isAlive()) {
			while ((line = reader_out.readLine()) != null) {
				StyledDocument doc = JIDE.console.getStyledDocument();
				Style style = JIDE.console.addStyle("OUTPUT_STYLE", null);
				StyleConstants.setForeground(style, Color.BLUE);
				try {
					doc.insertString(doc.getLength(), line + "\n", style);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
			pr.waitFor();
		}

		JIDE.console.setEditable(false);

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
			JIDE.console.setText("");

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
