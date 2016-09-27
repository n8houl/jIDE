package core.system;

import java.awt.Toolkit;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import core.JIDE;

public class FileManager {
	static JIDE jideInstance;
	
	public static void init(JIDE jideInst) {
		jideInstance = jideInst;
	}
	
	public static void saveFileAs() {
		if (JIDE.dialog.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			saveFile(JIDE.dialog.getSelectedFile().getAbsolutePath());
		}
	}

	public static void saveOld() {
		if (jideInstance.changed) {
			if (JOptionPane.showConfirmDialog(jideInstance, "Would you like to save " + JIDE.currentFile + "?", "Save",
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				saveFile(JIDE.currentFile);
			}
		}
	}

	public static void readInFile(String fileName) {
		try {
			FileReader r = new FileReader(fileName);
			JIDE.editor.read(r, null);
			r.close();
			jideInstance.getContentPane();
			JIDE.currentFile = fileName;
			jideInstance.setTitle(JIDE.currentFile);
			jideInstance.changed = false;
		} catch (IOException e) {
			Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(jideInstance, "Editor can't find the file: " + fileName);
		}
	}

	public static void saveFile(String fileName) {
		try {
			FileWriter w = new FileWriter(fileName);
			JIDE.editor.write(w);
			w.close();
			JIDE.currentFile = fileName;

			ActionManager.Save.setEnabled(false);
			jideInstance.changed = false;
			jideInstance.saved = true;
		} catch (IOException e) {
		}
	}
}
