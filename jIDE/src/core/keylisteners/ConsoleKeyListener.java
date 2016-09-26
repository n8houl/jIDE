package core.keylisteners;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import core.JIDE;
import core.system.ConsoleManager;

public class ConsoleKeyListener implements KeyListener {
	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
			if (!JIDE.console.isEditable())
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
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}
}