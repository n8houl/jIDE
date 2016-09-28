package core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import core.listeners.ConsoleKeyListener;
import core.system.ActionManager;
import core.system.FileManager;

public class JIDE extends JFrame {

	private static final long serialVersionUID = 1L;

	public static JTextPane editor = new JTextPane();
	public static JTextPane console = new JTextPane();
	public static JFileChooser dialog = new JFileChooser(System.getProperty("user.dir"));
	public static String currentFile = "Untitled";
	public boolean changed = false, saved = false;

	private final ConsoleKeyListener consoleKeyListener;

	public static String workspaceDir;
	
	private final KeyListener saveKeyListener = new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
			String title = "";
			if (e.getKeyCode() == KeyEvent.VK_S
					&& ((e.getModifiers() & ((OS == Constants.WINDOWS || OS == Constants.LINUX) ? KeyEvent.CTRL_MASK
							: Toolkit.getDefaultToolkit().getMenuShortcutKeyMask())) != 0)) {
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

	private final int OS = System.getProperty("os.name").toLowerCase().contains("windows") ? Constants.WINDOWS
			: (System.getProperty("os.name").toLowerCase().contains("mac") ? Constants.MAC : Constants.LINUX);

	public JIDE(String workspaceDir) {
		JIDE.workspaceDir = workspaceDir;
		System.out.println(JIDE.workspaceDir);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}

		Constants.init(OS);

		ActionManager.init(this);
		FileManager.init(this);

		editor.setFont(new Font("Monospaced", Font.PLAIN, 12));
		console.setFont(new Font("Courier", Font.PLAIN, 12));
		console.setEditable(false);
		
		console.setForeground(new Color(0, 153, 0));
		console.setBackground(new Color(210, 210, 210));
		
		editor.getStyledDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateSyntaxHighlighting();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateSyntaxHighlighting();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {}		
		});
		
		consoleKeyListener = new ConsoleKeyListener();

		console.addKeyListener(consoleKeyListener);
		editor.addKeyListener(saveKeyListener);

		JScrollPane scroll = new JScrollPane(editor, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		JScrollPane scroll2 = new JScrollPane(console, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setPreferredSize(new Dimension(720, 320));
		scroll2.setPreferredSize(new Dimension(720, 160));

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

		JButton cut = tool.add(ActionManager.Cut), cop = tool.add(ActionManager.Copy),
				pas = tool.add(ActionManager.Paste);

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
	
	public synchronized void updateSyntaxHighlighting() {
		Pattern patternKeyword = Pattern.compile("\\(?\\s*(int|float|char|return|double|void)\\**\\s*\\)?");
		Pattern patternPrint = Pattern.compile("print");
		Pattern patternInclude = Pattern.compile("#include\\s*(<|\").*(\"|>)");
		Pattern patternString = Pattern.compile("\".*\"");
		Runnable syntaxMatcher = new Runnable() {
			public void run() {
				try {
					SimpleAttributeSet defaultSet = new SimpleAttributeSet();
					editor.getStyledDocument().setCharacterAttributes(0, editor.getStyledDocument().getLength(), defaultSet, true);
					
					Matcher matcher = patternKeyword.matcher(editor.getStyledDocument().getText(0, editor.getStyledDocument().getLength()));
					SimpleAttributeSet keywordSet = new SimpleAttributeSet();
					StyleConstants.setForeground(keywordSet, Color.BLUE);
					StyleConstants.setBold(keywordSet, true);
					
					SimpleAttributeSet printSet = new SimpleAttributeSet();
					StyleConstants.setForeground(printSet, Color.BLACK);
					StyleConstants.setBold(printSet, false);
					
					SimpleAttributeSet stringSet = new SimpleAttributeSet();
					StyleConstants.setForeground(stringSet, Color.RED);
					StyleConstants.setBold(stringSet, false);
					
					SimpleAttributeSet includeSet = new SimpleAttributeSet();
					StyleConstants.setForeground(includeSet, new Color(204, 102, 0));
					StyleConstants.setBold(includeSet, false);
					
					while(matcher.find()) {
						editor.getStyledDocument().setCharacterAttributes(matcher.start(1), matcher.end(1) - matcher.start(1), keywordSet, false);
					}
					
					matcher = patternPrint.matcher(editor.getStyledDocument().getText(0, editor.getStyledDocument().getLength()));
					while(matcher.find()) {
						editor.getStyledDocument().setCharacterAttributes(matcher.start(), matcher.end()-matcher.start(), printSet, false);
					}
					
					matcher = patternInclude.matcher(editor.getStyledDocument().getText(0, editor.getStyledDocument().getLength()));
					while(matcher.find()) {
						editor.getStyledDocument().setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), includeSet, false);
					}
					
					matcher = patternString.matcher(editor.getStyledDocument().getText(0, editor.getStyledDocument().getLength()));
					while(matcher.find()) {
						editor.getStyledDocument().setCharacterAttributes(matcher.start(), matcher.end()-matcher.start(), stringSet, false);
					}
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		};
		
		SwingUtilities.invokeLater(syntaxMatcher);
	}
}
