package core.system;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.text.DefaultEditorKit;

import core.JIDE;

public class ActionManager {
	private static JIDE jideInstance;
	
	public static void init(JIDE jideInst) {
		jideInstance = jideInst;
	}
	
	public static Action Run = new AbstractAction("Run", new ImageIcon(JIDE.class.getResource("images/run.jpg"))) {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			if (JIDE.currentFile.equals("Untitled")) {
				FileManager.saveFileAs();
			}
			if (!JIDE.currentFile.equals("Untitled")) {
				FileManager.saveFile(JIDE.currentFile);
				jideInstance.setTitle(JIDE.currentFile);
				Terminate.setEnabled(true);
				JIDE.console.setText("");
				ConsoleManager.addedText = "";
				JIDE.console.setEditable(true);
				ConsoleManager.buildAndRun(JIDE.currentFile);
				JIDE.console.setEditable(false);
			}
		}
	};

	public static Action Terminate = new AbstractAction("Terminate", new ImageIcon(JIDE.class.getResource("images/terminate.jpg"))) {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			if (ConsoleManager.pr == null)
				return;
			if (ConsoleManager.pr.isAlive())
				ConsoleManager.pr.destroy();

			if (ConsoleManager.pr.isAlive())
				ConsoleManager.pr.destroyForcibly();

			JIDE.console.setEditable(false);
			JIDE.console.setText("");
			ConsoleManager.addedText = "";
		}
	};

	public static Action New = new AbstractAction("New", new ImageIcon(JIDE.class.getResource("images/new.gif"))) {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			if (JOptionPane.showConfirmDialog(jideInstance.getRootPane(), "Would you like to save " + JIDE.currentFile + "?", "Save",
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				FileManager.saveFile(JIDE.currentFile);
			}

			JIDE.editor.setText("");
			JIDE.currentFile = "Untitled";
			jideInstance.setTitle(JIDE.currentFile);
			jideInstance.changed = false;
			Save.setEnabled(false);
			SaveAs.setEnabled(false);
		}
	};

	public static Action Open = new AbstractAction("Open", new ImageIcon(JIDE.class.getResource("images/open.gif"))) {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			FileManager.saveOld();
			if (JIDE.dialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				FileManager.readInFile(JIDE.dialog.getSelectedFile().getAbsolutePath());
			}

			SaveAs.setEnabled(true);
			
			jideInstance.updateSyntaxHighlighting();
		}
	};

	public static Action Save = new AbstractAction("Save", new ImageIcon(JIDE.class.getResource("images/save.gif"))) {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			if (!JIDE.currentFile.equals("Untitled")) {
				FileManager.saveFile(JIDE.currentFile);
			} else {
				FileManager.saveFileAs();
			}

			jideInstance.setTitle(JIDE.currentFile);
		}
	};

	public static Action SaveAs = new AbstractAction("Save as...") {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			FileManager.saveFileAs();
		}
	};

	public static Action Quit = new AbstractAction("Quit") {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			FileManager.saveOld();
			System.exit(0);
		}
	};

	private static ActionMap m = JIDE.editor.getActionMap();
	public static Action Cut = m.get(DefaultEditorKit.cutAction);
	public static Action Copy = m.get(DefaultEditorKit.copyAction);
	public static Action Paste = m.get(DefaultEditorKit.pasteAction);
}
