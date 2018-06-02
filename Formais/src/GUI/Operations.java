package GUI;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;

import RegularLanguages.FADeterminize;
import RegularLanguages.FAMinimizer;
import RegularLanguages.FiniteAutomata;
import RegularLanguages.RegularLanguage;
import RegularLanguages.RegularLanguage.InputType;

public class Operations extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3017670684464829627L;

	private JComboBox<RegularLanguage> cbOpRL1;
	private JComboBox<RegularLanguage> cbOpRL2;
	private MainFrame mainFrame = null;
	
	/**
	 * Exit back to main frame
	 */
	public void exit() {
		mainFrame.setVisible(true);
		this.dispose();
	}

	/**
	 * Create the application.
	 */
	public Operations(MainFrame mainFrame) {
		try {
			this.mainFrame = mainFrame;
			initialize();
			this.setVisible(true);
			mainFrame.setVisible(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		this.setTitle("Regular Languages Operations");
		this.setResizable(false);
		this.setBounds(100, 200, 750, 190);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel operationsFramePanel = new JPanel();
		this.getContentPane().add(operationsFramePanel, BorderLayout.CENTER);
		
		// JComboBoxes:
		cbOpRL1 = new JComboBox<RegularLanguage>();
		cbOpRL2 = new JComboBox<RegularLanguage>();
		
		JComboBox<String> cbOpOperations = new JComboBox<String>();
		cbOpOperations.addItem("Intersection");
		cbOpOperations.addItem("Complement");
		cbOpOperations.addItem("Difference");
		cbOpOperations.addItem("Reverse");
		cbOpOperations.addItem("Union");
		cbOpOperations.addItem("Concatenation");
		cbOpOperations.addItem("ClosurePlus");
		cbOpOperations.addItem("ClosureStar");
		cbOpOperations.addItem("Minimize FA");
		cbOpOperations.addItem("Determinize FA");
		cbOpOperations.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	String selected = String.valueOf(cbOpOperations.getSelectedItem());
		    	if (selected.equals("Union") || selected.equals("Intersection") || selected.equals("Difference") || selected.equals("Concatenation")) {
		    		cbOpRL2.setEnabled(true);
		    	} else {
		    		cbOpRL2.setEnabled(false);
		    	}
		    }
		});
		
		// JLabels:
		
		JLabel lbOpSelectRL1 = new JLabel("RL1");
		JLabel lbOpSelectOp = new JLabel("Operation");
		JLabel lbOpSelectRL2 = new JLabel("RL2");
		
		// JButtons:
		
		JButton btnOpCancel = new JButton("Cancel");
		btnOpCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Operations.this.exit();
			}
		});
		
		JButton btnOpSave = new JButton("Save");
		btnOpSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RegularLanguage rl1 = (RegularLanguage) cbOpRL1.getSelectedItem();
				RegularLanguage rl2 = (RegularLanguage) cbOpRL2.getSelectedItem();
				String operation = String.valueOf(cbOpOperations.getSelectedItem());
				if (saveOperation(operation, rl1, rl2)) {
					Operations.this.exit();
				}
			}
		});
		
		
		// Close Window action:
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Operations.this.exit();
			}
		});
	
		// Layout definitions:
		
		GroupLayout gl_operationsFramePanel = new GroupLayout(operationsFramePanel);
		gl_operationsFramePanel.setHorizontalGroup(
			gl_operationsFramePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_operationsFramePanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_operationsFramePanel.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_operationsFramePanel.createSequentialGroup()
							.addGroup(gl_operationsFramePanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_operationsFramePanel.createSequentialGroup()
									.addComponent(cbOpRL1, 0, 283, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.UNRELATED))
								.addGroup(gl_operationsFramePanel.createSequentialGroup()
									.addGap(6)
									.addComponent(lbOpSelectRL1)
									.addGap(41)))
							.addGroup(gl_operationsFramePanel.createParallelGroup(Alignment.LEADING)
								.addComponent(lbOpSelectOp)
								.addComponent(cbOpOperations, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_operationsFramePanel.createParallelGroup(Alignment.LEADING)
								.addComponent(lbOpSelectRL2)
								.addComponent(cbOpRL2, 0, 283, Short.MAX_VALUE)))
						.addGroup(gl_operationsFramePanel.createSequentialGroup()
							.addComponent(btnOpCancel, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnOpSave, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_operationsFramePanel.setVerticalGroup(
			gl_operationsFramePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_operationsFramePanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_operationsFramePanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lbOpSelectRL1)
						.addComponent(lbOpSelectOp)
						.addComponent(lbOpSelectRL2))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_operationsFramePanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_operationsFramePanel.createParallelGroup(Alignment.BASELINE)
							.addComponent(cbOpRL1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(cbOpOperations, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(cbOpRL2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 66, Short.MAX_VALUE)
					.addGroup(gl_operationsFramePanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnOpCancel, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnOpSave, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		operationsFramePanel.setLayout(gl_operationsFramePanel);
		this.populateComboBoxes();
	}
		
	// Save new language based on selections
	private boolean saveOperation(String operation, RegularLanguage rl1, RegularLanguage rl2) {
		if (rl1 == null) {
			return false;
		}
		if (operation.equals("Union")) {
			if (rl2 == null) {
				return false;
			}
			RegularLanguage newL = rl1.getFA().union(rl2.getFA());
			newL.setId("[ [" + rl1.getId() + "] \u222A [" + rl2.getId() + "] ]");
			mainFrame.addToPanel(newL);
			return true;
		} else if (operation.equals("Complement")) {
			RegularLanguage newL = rl1.getFA().complement();
			newL.setId("[ [" + rl1.getId() + "]\u2201 ]");
			mainFrame.addToPanel(newL);
			return true;
		} else if (operation.equals("Reverse")) {
			RegularLanguage newL = rl1.getFA().reverse();
			newL.setId("[ [" + rl1.getId() + "]r ]");
			mainFrame.addToPanel(newL);
			return true;
		} else if (operation.equals("Concatenation")) {
			if (rl2 == null) {
				return false;
			}
			if (rl1.getType() != InputType.RG) {
				return false;
			}
			if (rl2.getType() != InputType.RG) {
				return false;
			}
			RegularLanguage newL = rl1.getRG().concatenation(rl2.getRG());
			newL.setId("[ [" + rl1.getId() + "] \u22C5 [" + rl2.getId() + "] ]");
			mainFrame.addToPanel(newL);
			return true;
		} else if (operation.equals("ClosurePlus")) {
			if (rl1.getType() != InputType.RG) {
				return false;
			}
			RegularLanguage newL = rl1.getRG().closurePlus();
			newL.setId("[ [" + rl1.getId() + "]+ ]");
			mainFrame.addToPanel(newL);
			return true;
		} else if (operation.equals("ClosureStar")) {
			if (rl1.getType() != InputType.RG) {
				return false;
			}
			RegularLanguage newL = rl1.getRG().closureStar();
			newL.setId("[ [" + rl1.getId() + "]* ]");
			mainFrame.addToPanel(newL);
			return true;

		} else if (operation.equals("Intersection")) {
			if (rl2 == null) {
				return false;
			}
			RegularLanguage newL = rl1.getFA().intersection(rl2.getFA());
			newL.setId("[ [" + rl1.getId() + "] \u2229 [" + rl2.getId() + "] ]");
			mainFrame.addToPanel(newL);
		} else if (operation.equals("Difference")) {
			if (rl2 == null) {
				return false;
			}
			RegularLanguage newL = rl1.getFA().difference(rl2.getFA());
			newL.setId("[ [" + rl1.getId() + "] - [" + rl2.getId() + "] ]");
			mainFrame.addToPanel(newL);
		} else if (operation.equals("Minimize FA")) {
			FAMinimizer faMinimizer = new FAMinimizer();
			RegularLanguage newL = faMinimizer.minimize(rl1.getFA());
			newL.setId("[ [" + rl1.getId() + "]min ]");
			mainFrame.addToPanel(newL);
		} else if (operation.equals("Determinize FA")) {
			FADeterminize faDeterminizer = new FADeterminize();
			RegularLanguage newL = faDeterminizer.determinizeAutomata(rl1.getFA());
			newL.setId("[ [" + rl1.getId() + "]det ]");
			mainFrame.addToPanel(newL);
		} else {
			return false;
		}
		
		return true;
		
	}


	// Populate combo boxes with regular languages from the list
	private void populateComboBoxes() {
		HashMap<String, RegularLanguage> languages = mainFrame.getLanguages();
		for (String id : languages.keySet()) {
			cbOpRL1.addItem(languages.get(id));
			cbOpRL2.addItem(languages.get(id));
		}
	}
}
