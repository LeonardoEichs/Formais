package GUI;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

import RegularLanguages.RegularLanguage;
import RegularLanguages.RegularLanguage.InputType;

public class ViewEdit extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5093560403504907089L;
	
	private MainFrame mainFrame;
	private JScrollPane viewEditRE;
	private JScrollPane viewEditRG;
	private JScrollPane viewEditFA;
	private JTextArea txtaViewEditRE;
	private JTextArea txtaViewEditRG;
	private JTextArea txtaViewEditFA;
	private JTabbedPane viewEditTabbedPane;
	
	private RegularLanguage language = null;
	
	static final String strRE = "Regular Expression";
	static final String strFA = "Finite Automata";
	static final String strRG = "Regular Grammar";
	


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
	public ViewEdit(MainFrame f, RegularLanguage rl) {
		try {
			this.mainFrame = f;
			this.language = rl;
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
		if (this.language == null ) {
			this.setTitle("View and Edit Regular Languages");
		} else {
			this.setTitle("View and Edit - " + this.language.toString());
		}
		this.setResizable(false);
		this.setBounds(100, 100, 500, 500);
		this.setMinimumSize(new Dimension(475, 400));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Tabbed scrollable JEditorPanes:
		
		viewEditTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		txtaViewEditRE = new JTextArea();
		viewEditRE = new JScrollPane(txtaViewEditRE);
		viewEditTabbedPane.addTab(strRE, null, viewEditRE, null);
	
		txtaViewEditFA = new JTextArea();
		txtaViewEditFA.setFont(new Font("monospaced", Font.PLAIN, 14));
		txtaViewEditFA.setEditable(false);
		viewEditFA = new JScrollPane(txtaViewEditFA);
		viewEditTabbedPane.addTab(strFA, null, viewEditFA, null);
		
		txtaViewEditRG = new JTextArea();
		viewEditRG = new JScrollPane(txtaViewEditRG);
		viewEditTabbedPane.addTab(strRG, null, viewEditRG, null);
		
		
		enableTextPane();
		
		// JButtons:
		
		JButton btnViewEditSave = new JButton("Save");
		btnViewEditSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String input = getPaneText(); // Gets text from pane
				String type;
				RegularLanguage rl; // Gets RL object
				if (input == null) {
					rl = ViewEdit.this.language.getFA();
					type = strFA;
				}
				else {
					rl = RegularLanguage.validate(input);
					if(rl == null) { // If type is not valid
						JOptionPane.showMessageDialog(ViewEdit.this, "Invalid input!");
						return;
					}
					if (rl.getType() == InputType.RG) {
						type = strRG;
					} else {
						type = strRE;
					}
				}
				rl.setId(language.toString());
				
				int answer = JOptionPane.showConfirmDialog(
						ViewEdit.this,
						"Replace '" + language.toString()+ "' by this new " + type + "?",
						"Overwrite?",
						JOptionPane.YES_NO_OPTION
				);
				if (answer != JOptionPane.YES_OPTION) {
					return;
				}
				// add Regular Language to Main Panel
				ViewEdit.this.mainFrame.addToPanel(rl);
				ViewEdit.this.exit();
				
			}
		});
		
		JButton btnViewEditCancel = new JButton("Cancel");
		btnViewEditCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ViewEdit.this.exit();
			}
		});
		btnViewEditCancel.setVerticalAlignment(SwingConstants.BOTTOM);
		btnViewEditCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ViewEdit.this.exit();
			}
		});
		
		// Close Window action:

		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				ViewEdit.this.exit();
			}
		});

		// Layout definitions:
		
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(viewEditTabbedPane)
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addComponent(btnViewEditCancel, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnViewEditSave, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(12)
					.addComponent(viewEditTabbedPane, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
					.addGap(12)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnViewEditSave, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnViewEditCancel, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		getContentPane().setLayout(groupLayout);
	}
	
//	private void setRadioButtonListeners() {
//	}
	
	
	private void enableTextPane() {
		RegularLanguage.InputType type = language.getType();
		if (type.equals(RegularLanguage.InputType.RG)) {
			viewEditTabbedPane.setSelectedComponent(viewEditRG);
			viewEditTabbedPane.setEnabledAt(0, false);
		} else if (type.equals(RegularLanguage.InputType.RE)) {
			viewEditTabbedPane.setSelectedComponent(viewEditRE);
			txtaViewEditRE.setText(language.getRE().getDefinition());
			txtaViewEditRE.setCaretPosition(0);
		} else {
			viewEditTabbedPane.setSelectedComponent(viewEditFA);
			viewEditTabbedPane.setEnabledAt(0, false);
		}
		if (language.getRG() == null) {
			viewEditTabbedPane.setEnabledAt(2, false);
		} else {
			txtaViewEditRG.setText(language.getRG().getDefinition());
		}
		txtaViewEditRG.setCaretPosition(0);
		txtaViewEditFA.setText(language.getFA().getDefinition());
		txtaViewEditFA.setCaretPosition(0);
	}
	private String getPaneText() {
		int selectedTabIndex = viewEditTabbedPane.getSelectedIndex();
		String selectedTab = viewEditTabbedPane.getTitleAt(selectedTabIndex);
		if (selectedTab.equals(strRG)) {
			return txtaViewEditRG.getText();
		} else if (selectedTab.equals(strRE)) {
			return txtaViewEditRE.getText();
		} else {
			return null;
		}
	}

}