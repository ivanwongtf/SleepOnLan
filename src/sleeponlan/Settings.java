package sleeponlan;

import java.awt.*;
import java.awt.event.*;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public abstract class Settings extends JDialog {
	
	protected static final long serialVersionUID = 1L;
	protected JPanel panel;
	
	public Settings(){
		super(SolServer.server, "Settings", true);
		setVisible(false);
		setResizable(false);
		setDefaultCloseOperation(ServerSetting.HIDE_ON_CLOSE);
	}
	
	public void openWindow(boolean running){
		if (running)
			JOptionPane.showMessageDialog(SolServer.server, "Please stop listening before changing any settings.", "Server is running!", JOptionPane.WARNING_MESSAGE);
		else
			setVisible(true);
	}
}

class ServerSetting extends Settings implements ActionListener {
	
	// Port
	private static final long serialVersionUID = 1L;
	private JLabel sSettingPort = new JLabel("Port to listen: ");
	private JTextField sPort;
	
	// Buffer
	private JLabel sSettingBufSize = new JLabel("Buffer size: ");
	private JTextField sBufSize;
	
	// Start Settings
	private JLabel toListenOnStart = new JLabel("Start listening when open: ");
	private ButtonGroup listenOnStart;
	private JRadioButton listenOnStart_yes, listenOnStart_no;
	
	// Network Interface
	private JLabel sSettingInfce = new JLabel("Network Interface: ");
	private JComboBox<String> netInterfaceList;
	
	// Other Fields
	private String PORT = "port", BUFSIZE = "bufSize";
	private Preferences prefs = Preferences.userNodeForPackage(this.getClass());
	private InfoCollector collector = new InfoCollector();
	private JButton apply, cancel;
	

	public ServerSetting(){
		super();
		setTitle("Server settings");
		Container container = getContentPane();
		setLayout(new BorderLayout());
		
		// Port
		sPort = new JTextField(5);
		sPort.setDocument(new JTextFieldLimit(5));
		sPort.setText(String.valueOf(prefs.getInt(PORT, 9)));
		
		// Buffer
		sBufSize = new JTextField(10);
		sBufSize.setDocument(new JTextFieldLimit(10));
		sBufSize.setText(String.valueOf(prefs.getInt(BUFSIZE, 144)));

		// Start Settings
		listenOnStart_yes = new JRadioButton("Yes");
		listenOnStart_yes.setActionCommand("listenOnStart_yes");
		listenOnStart_no = new JRadioButton("No");
		listenOnStart_no.setActionCommand("listenOnStart_no");
		listenOnStart = new ButtonGroup();
		listenOnStart.add(listenOnStart_yes);
		listenOnStart.add(listenOnStart_no);
		listenOnStart_yes.setSelected(prefs.getBoolean("listenOnStart", true));
		listenOnStart_no.setSelected(!prefs.getBoolean("listenOnStart", true));
		JPanel listenOnStartPanel = new JPanel();
		listenOnStartPanel.add(listenOnStart_yes);
		listenOnStartPanel.add(listenOnStart_no);
		
		// Apply and Cancel
		apply = new JButton("Apply");
		apply.setActionCommand("apply");
		cancel = new JButton("Cancel");
		cancel.setActionCommand("cancel");

		// settings form
		JPanel settingForm = new JPanel(new GridLayout(0, 2, 0, 5));
		settingForm.setBorder(new EmptyBorder(10, 10, 5, 10));
		settingForm.add(sSettingPort);
		settingForm.add(sPort);
		settingForm.add(sSettingBufSize);	
		settingForm.add(sBufSize);
		settingForm.add(toListenOnStart);
		settingForm.add(listenOnStartPanel);
		container.add(settingForm, BorderLayout.NORTH);

		// Network Interface selection
		netInterfaceList = new JComboBox<String>(collector.getInfceList());
		try {
			netInterfaceList.setSelectedIndex(collector.getIndexOfInfce());
		} catch (IllegalArgumentException e) {
			
			int indexOfValidInfceinList = collector.initIndexOfInfce();
			if (indexOfValidInfceinList < 0){
				JOptionPane.showMessageDialog(null, "<html><body><p style = \"font-size: 16px\">No Network interface is online!</p><br>"
						+ "<p style = \"font-size: 11px\">Please try to collect to any network before running this program.</p></body></html>", "Cannot initialize!", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			} else {
				netInterfaceList.setSelectedIndex(indexOfValidInfceinList);
				collector.sendIndexOfInfce(indexOfValidInfceinList);
			}
			
		}
		JPanel networkInfcePanel = new JPanel(new GridLayout(0, 1));
		networkInfcePanel.setBorder(new EmptyBorder(0, 10, 5, 10));
		networkInfcePanel.add(sSettingInfce);
		networkInfcePanel.add(netInterfaceList);
		container.add(networkInfcePanel, BorderLayout.CENTER);
		
		// apply and cancel
		JPanel savePanel = new JPanel();
		savePanel.add(apply);
		savePanel.add(cancel);
		apply.addActionListener(this);
		cancel.addActionListener(this);
		container.add(savePanel, BorderLayout.SOUTH);
		
		pack();
		setLocationRelativeTo(SolServer.server);
	}

	public int getPort(){
		return prefs.getInt(PORT, 9);
	}

	public int getBufSize(){
		return prefs.getInt(BUFSIZE, 144);
	}
	
	public boolean toListenOnStart(){
		return prefs.getBoolean("listenOnStart", true);
	}

	public void actionPerformed (ActionEvent e){
		String cmd = e.getActionCommand();

		if (cmd.equals("apply")){
			prefs.putInt(PORT, Integer.parseInt(sPort.getText()));
			prefs.putInt(BUFSIZE, Integer.parseInt(sBufSize.getText()));
			prefs.putBoolean("listenOnStart", listenOnStart_yes.isSelected());
			collector.sendIndexOfInfce(netInterfaceList.getSelectedIndex());
			SolServer.server.updateInfo();
		}
		setVisible(false);
	}
}

class CommandSetting extends Settings implements ActionListener {
	
	// GUI related
	private static final long serialVersionUID = 1L;
	private JLabel macSelectLabel = new JLabel("Call at detecting: ");
	private JLabel notice = new JLabel("Enter one command in one line.");
	private JRadioButton normal, reverse;
	private ButtonGroup mac;
	private JTextArea cmdArea = new JTextArea(8, 30);
	private JButton apply, cancel;
	
	// other fields
	private Preferences prefs = Preferences.userNodeForPackage(this.getClass());
	private String CMD = "command", nullMsg = "Please enter command here.";
	
	public CommandSetting(){
		super();
		setTitle("Command settings");
		//setSize(400, 270); 
		Container container = getContentPane();
        setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        
        // mac select
        JPanel macSelectPanel = new JPanel();
        macSelectPanel.add(macSelectLabel);
        mac = new ButtonGroup();
        normal = new JRadioButton("Normal MAC");
        normal.setActionCommand("setNormalMAC");
        mac.add(normal);
        normal.addActionListener(this);
        macSelectPanel.add(normal);     
        reverse = new JRadioButton("Reversed MAC");
        reverse.setActionCommand("setReversedMAC");
        reverse.addActionListener(this);
        mac.add(reverse);
        macSelectPanel.add(reverse);
        normal.setSelected(prefs.getBoolean("macSequence", false));
        reverse.setSelected(!prefs.getBoolean("macSequence", false));
        macSelectPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(macSelectPanel);
        
        // command text area
        cmdArea.setText(prefs.get(CMD, nullMsg));
        cmdArea.setLineWrap(true);
        cmdArea.setWrapStyleWord(true);
        Border border = BorderFactory.createLineBorder(Color.GRAY);
        cmdArea.setBorder(BorderFactory.createCompoundBorder(border, 
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        notice.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(notice);
        cmdArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(cmdArea);
        
        // apply or cancel
        JPanel savePanel = new JPanel();
        apply = new JButton("Apply");
        apply.setActionCommand("apply");
        apply.addActionListener(this);
        savePanel.add(apply);
        cancel = new JButton("Cancel");
        cancel.setActionCommand("cancel");
        cancel.addActionListener(this);
        savePanel.add(cancel);
        container.add(savePanel, Component.CENTER_ALIGNMENT);
		
        pack();
        setLocationRelativeTo(SolServer.server);
	}
	
	public void actionPerformed (ActionEvent e){
		String cmd = e.getActionCommand();
		String cmdAreaCommand = cmdArea.getText();
		
		if (cmd.equals("apply")){
			if (cmdAreaCommand.equals("")){
				prefs.put(CMD, nullMsg);
			} else 
				prefs.put(CMD, cmdAreaCommand);
			cmdArea.setText(prefs.get(CMD, nullMsg));
			prefs.putBoolean("macSequence", normal.isSelected());
			normal.setSelected(prefs.getBoolean("macSequence", false));
			reverse.setSelected(!prefs.getBoolean("macSequence", false));
			setVisible(false);
		} else if (cmd.equals("cancel")){
			setVisible(false);
		}
	}
	
	public String getCommand(){
		return prefs.get(CMD, "");
	}
	
	public boolean getMacSequence(){
		return prefs.getBoolean("macSequence", false);
	}
}

class JTextFieldLimit extends PlainDocument {
	private static final long serialVersionUID = 1L;
	private int limit;

	JTextFieldLimit(int limit) {
	 super();
	 this.limit = limit;
	 }

	public void insertString(int offset, String  str, AttributeSet attr) throws BadLocationException {
	  if (str == null) return;

	  if ((getLength() + str.length()) <= limit) {
	    super.insertString(offset, str, attr);
	  }
	}
}