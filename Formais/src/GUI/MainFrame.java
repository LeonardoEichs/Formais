package GUI;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import RegularLanguages.RegularLanguage;


public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6315534773769970357L;
	private JList<String> jListMainRL;
	private HashMap<String, RegularLanguage> languages = new HashMap<String, RegularLanguage>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		Toolkit xToolkit = Toolkit.getDefaultToolkit();
		java.lang.reflect.Field awtAppClassNameField;
		try {
			// Set application name
			awtAppClassNameField = xToolkit.getClass().getDeclaredField("awtAppClassName");
			awtAppClassNameField.setAccessible(true);
			awtAppClassNameField.set(xToolkit, "INE5421");
			
			// Set native window theme
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame window = new MainFrame();
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}

	/**
	 * Create the application.
	 */
	public MainFrame() {
		initialize();
	}


	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		this.setTitle("INE5421");
		this.setResizable(false);
		this.setBounds(100, 100, 500, 400);
		this.setMinimumSize(new Dimension(400, 300));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
		JPanel mainPanel = new JPanel();
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
		
		// JButtons:
		
		JButton btnMainAddRL = new JButton("Add RL");
		btnMainAddRL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AddRL(MainFrame.this);
			}
		});
		
		JButton btnMainViewEdit = new JButton("View/Edit");
		//btnMainViewEdit.addActionListener(new ActionListener() {
		//	public void actionPerformed(ActionEvent e) {
		//		if (isLanguageSelected()) {
		//			String idSelected = jListMainRL.getSelectedValue();
		//			new ViewEditFrame(MainFrame.this, languages.get(idSelected));
		//		}
		//	}
		//});
		
		JButton btnMainRemoveRL = new JButton("Remove");
		//btnMainRemoveRL.addActionListener(new ActionListener() {
		//    public void actionPerformed(ActionEvent e) {
		//    	MainFrame.this.removeSelected();
		//    }
		//});
		
		JButton btnMainRenameRL = new JButton("Rename");
		//btnMainRenameRL.addActionListener(new ActionListener() {
		//    public void actionPerformed(ActionEvent e) {
		//    	MainFrame.this.renameSelected();
		//    }
		//});
		
		JButton btnMainClear = new JButton("Clear");
		//btnMainClear.addActionListener(new ActionListener() {
		//    public void actionPerformed(ActionEvent e) {
		//    	MainFrame.this.clearList();
		//    }
		//});
		
		JButton btnMainOperations = new JButton("RL Operations");
		//btnMainOperations.addActionListener(new ActionListener() {
		//    public void actionPerformed(ActionEvent e) {
		//    	new OperationsFrame(MainFrame.this);
		//    }
		//});
		
		JButton btnMainVerifications = new JButton("RL Properties");
		//btnMainVerifications.addActionListener(new ActionListener() {
		//    public void actionPerformed(ActionEvent e) {
		//    	new PropertiesFrame(MainFrame.this);
		//    }
		//});
		
		// Scrollable JList:
		
		jListMainRL = new JList<String>();
		jListMainRL.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		updateJList();
		
		JScrollPane scrollPaneMainRL = new JScrollPane();
		scrollPaneMainRL.setViewportView(jListMainRL);
		
		// Close Window action:
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (MainFrame.this.confirm("Quit") == JOptionPane.YES_OPTION) {
					MainFrame.this.dispose();
				}
			}
		});
		
		// Layout definitions:
		
		GroupLayout gl_mainPanel = new GroupLayout(mainPanel);
		gl_mainPanel.setHorizontalGroup(
			gl_mainPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_mainPanel.createSequentialGroup()
					.addGroup(gl_mainPanel.createParallelGroup(Alignment.LEADING, false)
							.addComponent(btnMainAddRL, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
							.addComponent(btnMainViewEdit, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
							.addComponent(btnMainRenameRL, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
							.addComponent(btnMainRemoveRL, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
							.addComponent(btnMainClear, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE))
					.addContainerGap()
					.addContainerGap()
					.addComponent(scrollPaneMainRL, GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					)
				.addGroup(gl_mainPanel.createSequentialGroup()
					.addGap(46)
					.addComponent(btnMainOperations, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
					.addGap(14)
					.addComponent(btnMainVerifications, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
					.addGap(40))
		);
		gl_mainPanel.setVerticalGroup(
			gl_mainPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_mainPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_mainPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_mainPanel.createSequentialGroup()
							.addComponent(scrollPaneMainRL)
							.addGap(40))
						.addGroup(gl_mainPanel.createSequentialGroup()
							.addComponent(btnMainAddRL, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnMainViewEdit, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
							.addGap(7)
							.addComponent(btnMainRenameRL, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
							.addGap(7)
							.addComponent(btnMainRemoveRL, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
							.addGap(7)
							.addComponent(btnMainClear, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 254, Short.MAX_VALUE)
							.addGroup(gl_mainPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(btnMainOperations, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnMainVerifications, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))))
					.addContainerGap())
		);
		
		mainPanel.setLayout(gl_mainPanel);
		
}

	protected int confirm(String string) {
		return JOptionPane.showConfirmDialog(
				this,
				"Are you sure you want to " + string + "?",
				string + '?',
				JOptionPane.YES_NO_OPTION
		);	
	}

	private void updateJList() {
		this.jListMainRL.setListData(this.getLanguagesNames());
	}

	private String[] getLanguagesNames() {
		return this.languages.keySet()
				.stream()
				.toArray(size -> new String[size]);
	}

	public RegularLanguage getLanguage(String name) {
		return this.languages.get(name);
	}

	public void addToPanel(RegularLanguage rl) {
		this.languages.put(rl.getId(), rl);
		this.updateJList();
		this.jListMainRL.setSelectedValue(rl.getId(), true);	
	}		
			
}