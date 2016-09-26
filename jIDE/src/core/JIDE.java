package core;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
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

public class JIDE extends JFrame {

	private static final long serialVersionUID = 1L;

	public static JTextArea area = new JTextArea(20, 120);
	public static JTextArea area2 = new JTextArea(20, 60);
	public static JFileChooser dialog = new JFileChooser(System.getProperty("user.dir"));
	public static String currentFile = "Untitled";
	public boolean changed = false, saved = false;

	final ConsoleKeyListener keyListener;

	private final int OS = System.getProperty("os.name").toLowerCase().contains("windows") ? Constants.WINDOWS
			: (System.getProperty("os.name").toLowerCase().contains("mac") ? Constants.MAC : Constants.LINUX);
	
	/* TEMPORARY */
	
	public JIDE() {
		Constants.init(OS);
		
		ActionManager.init(this);
		FileManager.init(this);
		
		area.setFont(new Font("Monospaced", Font.PLAIN, 12));
		area2.setFont(new Font("Monospaced", Font.PLAIN, 12));
		area2.setEditable(false);

		keyListener = new ConsoleKeyListener();

		area2.addKeyListener(keyListener);
		area.addKeyListener(k);

		JScrollPane scroll = new JScrollPane(area, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		JScrollPane scroll2 = new JScrollPane(area2, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(scroll, BorderLayout.CENTER);
		add(scroll2, BorderLayout.SOUTH);

		JMenuBar jmb = new JMenuBar();
		setJMenuBar(jmb);
		JMenu file = new JMenu("File");
		JMenu edit = new JMenu("Edit");
		jmb.add(file);
		jmb.add(edit);

		file.add(ActionManager.New);
		file.add(ActionManager.Open);
		file.add(ActionManager.Save);
		file.add(ActionManager.SaveAs);
		file.add(ActionManager.Quit);

		file.addSeparator();

		for (int i = 0; i < 4; i++) {
			file.getItem(i).setIcon(null);
		}

		edit.add(ActionManager.Cut);
		edit.add(ActionManager.Copy);
		edit.add(ActionManager.Paste);

		edit.getItem(0).setText("Cut");
		edit.getItem(1).setText("Copy");
		edit.getItem(2).setText("Paste");

		JToolBar tool = new JToolBar();
		tool.setFloatable(false);
		add(tool, BorderLayout.NORTH);

		tool.add(ActionManager.New);
		tool.add(ActionManager.Open);
		tool.add(ActionManager.Save);

		tool.addSeparator();

		ActionManager.Terminate.setEnabled(false);

		JButton run = tool.add(ActionManager.Run);
		run.setToolTipText("Run Program");
		JButton terminate = tool.add(ActionManager.Terminate);
		terminate.setToolTipText("Terminate Program");

		tool.addSeparator();

		JButton cut = tool.add(ActionManager.Cut), cop = tool.add(ActionManager.Copy), pas = tool.add(ActionManager.Paste);

		cut.setText(null);
		cut.setToolTipText("Cut");
		cut.setIcon(new ImageIcon(JIDE.class.getResource("images/cut.gif")));

		cop.setText(null);
		cop.setToolTipText("Copy");
		cop.setIcon(new ImageIcon(JIDE.class.getResource("images/open.gif")));

		pas.setText(null);
		pas.setToolTipText("Paste");
		pas.setIcon(new ImageIcon(JIDE.class.getResource("images/paste.gif")));

		ActionManager.Save.setEnabled(false);
		ActionManager.SaveAs.setEnabled(false);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (!changed)
					System.exit(0);
				String optionButtons[] = { "Yes", "No" };
				int result = JOptionPane.showOptionDialog(null, "Would you like to save your work before you exit?",
						"TextEditor", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, optionButtons,
						optionButtons[0]);
				if (result == JOptionPane.YES_OPTION) {
					if (currentFile.equals("Untitled")) {
						FileManager.saveFileAs();
					} else {
						FileManager.saveFile(currentFile);
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
			if (e.getKeyCode() == KeyEvent.VK_S && ((e.getModifiers() & ((OS == Constants.WINDOWS || OS == Constants.LINUX)
					? KeyEvent.CTRL_MASK : Toolkit.getDefaultToolkit().getMenuShortcutKeyMask())) != 0)) {
				changed = false;
				ActionManager.Save.setEnabled(false);
				ActionManager.SaveAs.setEnabled(false);
				if (currentFile.equals("Untitled"))
					FileManager.saveFileAs();
				else
					FileManager.saveFile(currentFile);
				title = currentFile;
			} else {
				changed = true;
				ActionManager.Save.setEnabled(true);
				ActionManager.SaveAs.setEnabled(true);
				title = "*" + currentFile;
			}

			setTitle(title);
		}
	};

	

	

	

	public static void main(String args[]) {
		new JIDE();
	}

	class ConsoleKeyListener implements KeyListener {
		@Override
		public void keyPressed(KeyEvent arg0) {
			if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
				if (!area2.isEditable())
					return;
				ConsoleManager.moved = 0;
				try {
					ConsoleManager.sendMsg(ConsoleManager.addedText);
				} catch (IOException e) {
					e.printStackTrace();
				}

				ConsoleManager.addedText = "";
			} else if (arg0.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				if (ConsoleManager.moved == 0)
					ConsoleManager.addedText = ConsoleManager.addedText.substring(0, ConsoleManager.addedText.length() - 1);
				else {
					String first = ConsoleManager.addedText.substring(0, ConsoleManager.addedText.length() + ConsoleManager.moved);
					String second = ConsoleManager.addedText.substring(ConsoleManager.addedText.length() + ConsoleManager.moved, ConsoleManager.addedText.length());
					first = first.substring(0, first.length() - 1);
					ConsoleManager.addedText = first + second;
				}
			} else if (arg0.getKeyCode() == KeyEvent.VK_LEFT) {
				ConsoleManager.moved -= 1;
			} else if (arg0.getKeyCode() == KeyEvent.VK_RIGHT) {
				ConsoleManager.moved += 1;
				if (ConsoleManager.moved == 0)
					ConsoleManager.moved = 0;
			} else {
				if (ConsoleManager.moved == 0)
					ConsoleManager.addedText += arg0.getKeyChar();
				else {
					String first = ConsoleManager.addedText.substring(0, ConsoleManager.addedText.length() + ConsoleManager.moved);
					String second = ConsoleManager.addedText.substring(ConsoleManager.addedText.length() + ConsoleManager.moved, ConsoleManager.addedText.length());
					ConsoleManager.addedText = first + arg0.getKeyChar() + second;
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
		}
	}
}
