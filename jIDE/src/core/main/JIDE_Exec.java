package core.main;

import core.JIDE;

public class JIDE_Exec {
	public static void main(String args[]) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new JIDE();
			}
		});
	}
}
