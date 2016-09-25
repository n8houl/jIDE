package core;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.text.DefaultEditorKit;

public class JIDE extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private JTextArea area = new JTextArea(20, 120);
	private JTextArea area2 = new JTextArea(20, 60);
	private JFileChooser dialog = new JFileChooser(System.getProperty("user.dir"));
	private String currentFile = "Untitled";
	private boolean changed = false, saved = false;
	
	final ConsoleKeyListener keyListener;
	private Process pr;
	
	public JIDE() {
		area.setFont(new Font("Monospaced", Font.PLAIN, 12));
		area2.setFont(new Font("Monospaced", Font.PLAIN, 12));
		area2.setEditable(false);
		
		keyListener = new ConsoleKeyListener();
		
		area2.addKeyListener(keyListener);
		area.addKeyListener(k);

		
		JScrollPane scroll = new JScrollPane(area, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		JScrollPane scroll2 = new JScrollPane(area2, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(scroll, BorderLayout.CENTER);
		add(scroll2, BorderLayout.SOUTH);
		
		JMenuBar jmb = new JMenuBar();
		setJMenuBar(jmb);
		JMenu file = new JMenu("File");
		JMenu edit = new JMenu("Edit");
		jmb.add(file);
		jmb.add(edit);
		
		file.add(New);
		file.add(Open);
		file.add(Save);
		file.add(SaveAs);
		file.add(Quit);
		
		
		file.addSeparator();
		
		for(int i = 0; i < 4; i++) {
			file.getItem(i).setIcon(null);
		}
		
		edit.add(Cut);
		edit.add(Copy);
		edit.add(Paste);
		
		edit.getItem(0).setText("Cut");
		edit.getItem(1).setText("Copy");
		edit.getItem(2).setText("Paste");
		
		JToolBar tool = new JToolBar();
		tool.setFloatable(false);
		add(tool, BorderLayout.NORTH);
		
		tool.add(New);
		tool.add(Open);
		tool.add(Save);
		
		tool.addSeparator();
		
		Terminate.setEnabled(false);
		
		JButton run = tool.add(Run);
		run.setToolTipText("Run Program");
		JButton terminate = tool.add(Terminate);
		terminate.setToolTipText("Terminate Program");
		
		tool.addSeparator();
		
		JButton cut = tool.add(Cut), cop = tool.add(Copy), pas = tool.add(Paste);
		
		cut.setText(null);
		cut.setToolTipText("Cut");
		cut.setIcon(new ImageIcon(JIDE.class.getResource("images/cut.gif")));
		
		cop.setText(null);
		cop.setToolTipText("Copy");
		cop.setIcon(new ImageIcon(JIDE.class.getResource("images/open.gif")));
		
		pas.setText(null);
		pas.setToolTipText("Paste");
		pas.setIcon(new ImageIcon(JIDE.class.getResource("images/paste.gif")));
		
		Save.setEnabled(false);
		SaveAs.setEnabled(false);
				
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(!changed) System.exit(0);
				String optionButtons[] = { "Yes", "No" };
				int result = JOptionPane.showOptionDialog(null, "Would you like to save your work before you exit?", "TextEditor", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, optionButtons, optionButtons[0]);
				if(result == JOptionPane.YES_OPTION) {
					if(currentFile.equals("Untitled")) {
						saveFileAs();
					} else {
						saveFile(currentFile);
					}
				}
				
				System.exit(0);
			}
		});
		
		pack();
		setTitle(currentFile);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private KeyListener k = new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
			String title = "";
			if(e.getKeyCode() == KeyEvent.VK_S && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
				changed = false;
				Save.setEnabled(false);
				SaveAs.setEnabled(false);
				if(currentFile.equals("Untitled"))
					saveFileAs();
				else
					saveFile(currentFile);
				title = currentFile;
			} else {
				changed = true;
				Save.setEnabled(true);
				SaveAs.setEnabled(true);
				title = "*" + currentFile;
			}
			
			setTitle(title);
		}
	};
	
	Action Run = new AbstractAction("Run", new ImageIcon(JIDE.class.getResource("images/run.jpg"))) {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			if(currentFile.equals("Untitled")) {
				saveFileAs();
			}
			if(saved || !currentFile.equals("Untitled")) {
				Terminate.setEnabled(true);
				area2.setText("");
				addedText = "";
				area2.setEditable(true);
				buildAndRun(currentFile);
				area2.setEditable(false);
			}
		}
	};
	
	Action Terminate = new AbstractAction("Terminate", new ImageIcon(JIDE.class.getResource("images/terminate.jpg"))) {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			if(pr == null) return;
			if(pr.isAlive()) pr.destroy();
			
			if(pr.isAlive()) pr.destroyForcibly();
			
			area2.setEditable(false);
			area2.setText("");
			addedText = "";
		}
	};
	
	Action New = new AbstractAction("New", new ImageIcon(JIDE.class.getResource("images/new.gif"))) {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			if(JOptionPane.showConfirmDialog(rootPane, "Would you like to save " + currentFile + "?", "Save", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				saveFile(currentFile);
			}
			
			area.setText("");
			currentFile = "Untitled";
			setTitle(currentFile);
			changed = false;
			Save.setEnabled(false);
			SaveAs.setEnabled(false);
		}
	};
	
	Action Open = new AbstractAction("Open", new ImageIcon(JIDE.class.getResource("images/open.gif"))) {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			saveOld();
			if(dialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				readInFile(dialog.getSelectedFile().getAbsolutePath());
			}
			
			SaveAs.setEnabled(true);
		}
	};
	
	Action Save = new AbstractAction("Save", new ImageIcon(JIDE.class.getResource("images/save.gif"))) {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			if(!currentFile.equals("Untitled")) {
				saveFile(currentFile);
			} else {
				saveFileAs();
			}
			
			setTitle(currentFile);			
		}
	};
	
	Action SaveAs = new AbstractAction("Save as...") {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			saveFileAs();
		}
	};
	
	Action Quit = new AbstractAction("Quit") {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			saveOld();
			System.exit(0);
		}
	};
	
	ActionMap m = area.getActionMap();
	Action Cut = m.get(DefaultEditorKit.cutAction);
	Action Copy = m.get(DefaultEditorKit.copyAction);
	Action Paste = m.get(DefaultEditorKit.pasteAction);
	
	private void saveFileAs() {
		if(dialog.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			saveFile(dialog.getSelectedFile().getAbsolutePath());
		}
	}
	
	private void saveOld() {
		if(changed) {
			if(JOptionPane.showConfirmDialog(this, "Would you like to save " + currentFile + "?", "Save", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				saveFile(currentFile);
			}
		}
	}
	
	private void readInFile(String fileName) {
		try {
			FileReader r = new FileReader(fileName);
			area.read(r, null);
			r.close();getContentPane();
			currentFile = fileName;
			setTitle(currentFile);
			changed = false;
		} catch (IOException e) {
			Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(this, "Editor can't find the file: " + fileName);
		}
	}
	
	private void saveFile(String fileName) {
		try {
			FileWriter w = new FileWriter(fileName);
			area.write(w);
			w.close();
			currentFile = fileName;
	
			Save.setEnabled(false);
			changed = false;
			saved = true;
		} catch (IOException e) {
		}
	}

	private String addedText;
	private int moved;
	
	/*
	 * TODO: Add Code to delete previous build
	 * */
	private void build(String fileName) throws InterruptedException, IOException {
		Runtime rt = Runtime.getRuntime();
		pr = rt.exec("gcc "+ fileName + " -o " + fileName.substring(0, fileName.length() - 2) + ".exe");
		
		BufferedReader build_errors = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
		String line;
		String msg = "";
		while((line = build_errors.readLine()) != null) {
			msg += line;
			msg += "\n";
		}
		
		if(!msg.equals("")) {
			JOptionPane.showMessageDialog(this, "Could not compile! Errors: \n" + msg);
		}
		
		pr.waitFor();
		pr.destroy();
	}
	
	/*
	 * TODO: Figure out issue with stdin handling occuring before stdout handling
	 * */
	private void runEXE(String fileName) throws IOException, InterruptedException {
		pr = Runtime.getRuntime().exec(fileName.substring(0, fileName.length() - 2) + ".exe");
		area2.setEditable(true);
		
		//UNUSED:
		//InputStream stderr = pr.getErrorStream();
		InputStream stdout = pr.getInputStream();
		
		BufferedReader reader_out = new BufferedReader(new InputStreamReader(stdout));
		//UNUSED: 
		//BufferedReader reader_err = new BufferedReader(new InputStreamReader(stderr));
		String line;
		
		while(pr.isAlive()) {
			while((line = reader_out.readLine()) != null) 
				area2.append(line + "\n");
			pr.waitFor();
		}
		
		area2.setEditable(false);
		
		Terminate.setEnabled(false);
	}
	
	private synchronized void sendMsg(String msg) throws IOException {
		
		OutputStream stdin = pr.getOutputStream();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));
		
		writer.write(addedText + "\n");
		writer.flush();
		
		addedText = "";
	}
	
	public void buildAndRun(String fileName) {
		try {	
			build(fileName);
			addedText = new String();
			area2.setText("");
			
			new Thread() {
				public void run() {
					try {
						runEXE(fileName);
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
					}
				}
			}.start();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		new JIDE();
	}
	
	class ConsoleKeyListener implements KeyListener {
		@Override
		public void keyPressed(KeyEvent arg0) {
			if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {
				moved = 0;
				try {
					sendMsg(addedText);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				addedText = "";
			} else if(arg0.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				if(moved == 0) addedText = addedText.substring(0, addedText.length()-1);
				else {
					String first = addedText.substring(0, addedText.length()+moved);
					String second = addedText.substring(addedText.length()+moved, addedText.length());
					first = first.substring(0, first.length()-1);
					addedText = first + second;
				}
			} else if(arg0.getKeyCode() == KeyEvent.VK_LEFT) {
				moved -= 1;
			} else if(arg0.getKeyCode() == KeyEvent.VK_RIGHT) {
				moved += 1;
				if(moved == 0) moved = 0;
			}
			else {
				if(moved == 0)addedText += arg0.getKeyChar();
				else {
					String first = addedText.substring(0, addedText.length()+moved);
					String second = addedText.substring(addedText.length()+moved, addedText.length());
					addedText = first + arg0.getKeyChar() + second;
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent arg0) {}

		@Override
		public void keyTyped(KeyEvent arg0) {}
	}
}
