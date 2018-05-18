package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import RegularLanguages.RegularLanguage;

public class AddRL extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -830547183942403759L;
	private MainFrame mainFrame;

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
	public AddRL(MainFrame mainFrame) {
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
		this.setTitle("Add Regular Language");
		this.setResizable(false);
		this.setBounds(100, 100, 500, 500);
		this.setMinimumSize(new Dimension(450, 300));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel mainPanel = new JPanel();
		
		// JLabels:
		
		JLabel lblAddRLName = new JLabel("Name :");
		JLabel lblAddRLAdd = new JLabel("Regular Grammar or a Regular Expression below:");
		
		// RL name input:

		JTextField txtAddRLName = new JTextField();
		
		// Scrollable RL input box:
		
		JTextArea txtaAddRL = new JTextArea();
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(txtaAddRL);


		
		// JButons:
		
		JButton btnAddRLCancel = new JButton("Cancel");
		btnAddRLCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				AddRL.this.exit();
			}
		});
				
		JButton btnAddRLAdd = new JButton("Add");
		btnAddRLAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String name = txtAddRLName.getText();
				if(name.isEmpty() || name.contains("[") || name.contains("]")) {
					JOptionPane.showMessageDialog(AddRL.this, "Invalid name");
					return;
				}
				else if (AddRL.this.mainFrame.getLanguage(name) != null) {
					int answer = JOptionPane.showConfirmDialog(
							AddRL.this,
							'"' + name + "\" already exists!\nOverwrite?",
							"Overwrite?",
							JOptionPane.YES_NO_OPTION
					);
					if (answer != JOptionPane.YES_OPTION) {
						return;
					}
				}
				String input = txtaAddRL.getText();
				RegularLanguage rl = RegularLanguage.validate(input);
				if(rl == null) {
					JOptionPane.showMessageDialog(AddRL.this, "Invalid input!");
					return;
				}
				rl.setId(name);
				AddRL.this.mainFrame.addToPanel(rl);
				AddRL.this.exit();
			}
		});

		this.getContentPane().setLayout(new BorderLayout(0, 0));
		
		// Close Window action:
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				AddRL.this.exit();
			}
		});
		
		// Layout definitions:
		
		GroupLayout gl_mainPanel = new GroupLayout(mainPanel);
		gl_mainPanel.setHorizontalGroup(
			gl_mainPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_mainPanel.createSequentialGroup()
					.addGap(13)
					.addGroup(gl_mainPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblAddRLName, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
						.addComponent(txtAddRLName, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
						.addComponent(lblAddRLAdd, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
						.addGroup(Alignment.LEADING, gl_mainPanel.createSequentialGroup()
							.addComponent(scrollPane)
							.addPreferredGap(ComponentPlacement.RELATED))
						.addGroup(gl_mainPanel.createSequentialGroup()
							.addComponent(btnAddRLCancel, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnAddRLAdd, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)))
					.addGap(13))
		);
		gl_mainPanel.setVerticalGroup(
			gl_mainPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_mainPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblAddRLName)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtAddRLName, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblAddRLAdd)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_mainPanel.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_mainPanel.createSequentialGroup()
							.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnAddRLCancel, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
						.addComponent(btnAddRLAdd, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		
		
		mainPanel.setLayout(gl_mainPanel);
		
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
	}
}