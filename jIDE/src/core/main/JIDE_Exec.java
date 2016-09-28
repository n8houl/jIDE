package core.main;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import core.JIDE;

public class JIDE_Exec {
	public static void main(String args[]) {
		/*JFileChooser dialog = new JFileChooser(System.getProperty("user.dir"));
		dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		dialog.setDialogTitle("Choose Workspace Directory");
		
		if(dialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
		String workspaceDir = dialog.getSelectedFile().getAbsolutePath();
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new JIDE(workspaceDir);
			}
		});
		*/
		
        JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));

        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setFileFilter( new FileFilter(){

            @Override
            public boolean accept(File f) {
                return f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Any folder";
            }

        });

        fc.setDialogType(JFileChooser.SAVE_DIALOG);
        fc.setApproveButtonText("Select");
        fc.setVisible(true);
        
        if(fc.showDialog(null, "Select") == JFileChooser.APPROVE_OPTION) {
        	String workspaceDir = fc.getSelectedFile().getAbsolutePath();
        	SwingUtilities.invokeLater(new Runnable() {
        		public void run() {
        			new JIDE(workspaceDir);
        		}
        	});
        } else {
        	System.exit(0);
        }
	}
}
