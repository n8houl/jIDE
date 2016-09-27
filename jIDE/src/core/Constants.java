package core;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

public class Constants {
	public static final int WINDOWS = 0;
	public static final int MAC = 1;
	public static final int LINUX = 2;
	
	public static int CTRL_CMD_KEY_MASK;
	public static String PROG_EXTENSION;
	
	public static void init(int OS) {
		switch(OS) {
		case MAC:
			CTRL_CMD_KEY_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
			PROG_EXTENSION = "";
			break;
		case WINDOWS:
			CTRL_CMD_KEY_MASK = KeyEvent.CTRL_MASK;
			PROG_EXTENSION = ".exe";
			break;
		case LINUX:
			CTRL_CMD_KEY_MASK = KeyEvent.CTRL_MASK;
			PROG_EXTENSION = "";
			break;
		default:
			break;
		}
	}
}
