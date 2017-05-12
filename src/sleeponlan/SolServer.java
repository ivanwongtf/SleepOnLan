package sleeponlan;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

public class SolServer extends JFrame implements ActionListener {
	
	// SolServer field
	private static final long serialVersionUID = 1L;
	static SolServer server;
	private Looper looper;
	private ServerSetting sSettings;
	private CommandSetting cSettings;
	private InfoCollector collector = new InfoCollector();
	
	// TrayIcon related
	private TrayIcon trayIcon;
	private final boolean traySupport = SystemTray.isSupported();
	private SystemTray tray = null;
	private URL trayIconUrl = SolServer.class.getResource("/icon_tray.gif");
	private URL taskIconUrl = SolServer.class.getResource("/icon_taskbar.gif");
	private final ImageIcon aboutImage = new ImageIcon(SolServer.class.getResource("/icon_about.gif"));
	private final Image trayImage = Toolkit.getDefaultToolkit().getImage(trayIconUrl);
	private final Image taskImage = Toolkit.getDefaultToolkit().getImage(taskIconUrl);
	private ArrayList<Image> icons;
	
	// GUI related
	private String localIP, localMAC, localRMAC;
	private JTextField showIP = new JTextField(), showMAC = new JTextField(), showRMAC = new JTextField();
	private JTextArea showLog = new JTextArea("Welcome to SolServer!\n");
	private JButton serverControl = new JButton("Start");
	private JMenuBar menubar;
	private JMenu setting, help;
	private JMenuItem serverSetting, cmdSetting, info;	
	
	// Time and Date related
	private Date dNow;
	private SimpleDateFormat date = new SimpleDateFormat ("[yyyy:MM:dd - HH:mm:ss] ");
	
	public SolServer(){
		super();
		setTitle("SolServer");
		setLayout(new BorderLayout());
		this.setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // set trays
        if (traySupport){
	    	tray = SystemTray.getSystemTray();
	    	trayIcon = new TrayIcon(trayImage, "SolServer GUI", null);
	    	trayIcon.setImageAutoSize(true);
	    	trayIcon.addMouseListener(new MouseListener(){
	    		   public void mouseEntered(MouseEvent e){}
	    		   public void mouseExited(MouseEvent e){}
	    		   public void mouseReleased(MouseEvent e){}
	    		   public void mousePressed(MouseEvent e){}
	    		   public void mouseClicked(MouseEvent e) {
	    			   if (e.getButton() == MouseEvent.BUTTON1){
		    			   tray.remove(trayIcon);
		    			   setVisible(true);
		    			   setState(JFrame.NORMAL);
	    			   }
	    		   }
	    	   });
	    } else {
	    	JOptionPane.showMessageDialog(null, "SystemTray not support!");
	    }
        
        // set icons
        icons = new ArrayList<Image>();
		icons.add(trayImage);
		icons.add(taskImage);
		setIconImages(icons);
		
		// add Windows Listener
		addWindowListener(new WindowListener(){
        	
    		public void windowClosed(WindowEvent e) {}
    	    public void windowOpened(WindowEvent e) {}
    	    public void windowIconified(WindowEvent e) {
    	    	if (traySupport) {
        	    	   setVisible(false); 
        	    	   try {
        	    		   tray.add(trayIcon);
        	    	   } catch (AWTException error) {
        	    		   JOptionPane.showMessageDialog(null, "Cannot add TrayIcon!"); 
        	    	   }
        	    }
    	    }
    	    public void windowDeiconified(WindowEvent e) {}
    	    public void windowActivated(WindowEvent e) {}
    	    public void windowDeactivated(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {}
		});
		
		// create settings object
		sSettings = new ServerSetting();
		cSettings = new CommandSetting();
	}
	
	public void updateInfo(){
		collector.collect();
		localIP = collector.getIP();
    	localMAC = collector.getMAC();
    	localRMAC = collector.getRMAC();
		showIP.setText(localIP);
    	showMAC.setText(localMAC);
    	showRMAC.setText(localRMAC);
	}
	
	public void initMainFrame(){
		
		// initialize menu bar
		menubar = new JMenuBar();
    	
    	setting = new JMenu("Settings");
    	serverSetting = new JMenuItem("Server settings");
    	serverSetting.setActionCommand("sSettings");
    	serverSetting.addActionListener(this);
    	cmdSetting = new JMenuItem("Command settings");
    	cmdSetting.setActionCommand("cSettings");
    	cmdSetting.addActionListener(this);
    	setting.add(serverSetting);
    	setting.add(cmdSetting);
    	
    	help = new JMenu("Help");
    	info = new JMenuItem("About");
    	info.setActionCommand("info");
    	info.addActionListener(this);
    	help.add(info);
    	menubar.add(setting);
    	menubar.add(help);
    	setJMenuBar(menubar);
		
		// initialize MAC, IP information
		JPanel info = new JPanel(new GridBagLayout());
		info.setBorder(new EmptyBorder(10, 10, 5, 5));
		updateInfo();
		
		GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 5 ,0);
		info.add(new JLabel("Current IP: "), c);
		
		c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 10 ,0);
		info.add(showIP, c);
		
		c.gridx = 0;
        c.gridy = 1;
		info.add(new JLabel("Current MAC: "), c);
		
		c.gridx = 1;
        c.gridy = 1;
		info.add(showMAC, c);
		
		c.gridx = 0;
        c.gridy = 2;
		info.add(new JLabel("Reversed MAC: "), c);
		
		c.gridx = 1;
        c.gridy = 2;
		info.add(showRMAC, c);
		
		// initialize message box
		showLog.setLineWrap(true);
		showLog.setWrapStyleWord(true);
		showLog.setEditable(false);
		
		JScrollPane areaScrollPane = new JScrollPane(showLog);
        areaScrollPane.setVerticalScrollBarPolicy(
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        areaScrollPane.setViewportView(showLog);
        DefaultCaret caret = (DefaultCaret) showLog.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		// initialize server control button
		serverControl.setActionCommand("serverControl");
		serverControl.addActionListener(this);
		
		// add all component into main frame
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(info, BorderLayout.WEST);
		this.add(topPanel, BorderLayout.NORTH);
		this.add(areaScrollPane, BorderLayout.CENTER);
		this.add(serverControl, BorderLayout.SOUTH);
		
		// set main frame location and show it when finish initializing.
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	public void startServer(){
    	looper = new Looper(sSettings.getPort(), sSettings.getBufSize(), cSettings.getMacSequence());
		looper.sendCommand(cSettings.getCommand());
		changeStartButton(true);
		Thread t = new Thread(looper);
		t.start();
    }
	
	public void changeStartButton(boolean start){
		if (start == false)
			serverControl.setText("Start");
		else
			serverControl.setText("Stop");
	}
	
	public void logNewMsg(String message){
		final String newline = "\n";
		showLog.append(message + newline);
	}
    
    public static JFrame getMainFrame(){
    	return server;
    }
    
    public String getCurrentTime(){
    	dNow = new Date();
    	return date.format(dNow);
    }
	
    public void actionPerformed(ActionEvent e){
    	String cmd = e.getActionCommand();
		if (cmd.equals("serverControl")){
			if (looper == null) {
				startServer();
			} else {
				looper.stop();
				looper = null;
			}
		} else if (cmd.equals("sSettings")){
			sSettings.setLocationRelativeTo(this);
			sSettings.openWindow(looper != null);
		} else if (cmd.equals("cSettings")){
			cSettings.setLocationRelativeTo(this);
			cSettings.openWindow(looper != null);
		} else if (cmd.equals("info")){
			String msg = "<html><h1>SolServer</h1><p>A software performs preseted commands " + 
						 "when receiving correct WOL packets.<br>Version: 0.4.1<br>" + 
						 "Author: Ivan Wong<br>Website: <a href=\"http://www.justdoevil.info\">" + 
						 "http://www.justdoevil.info</a></p></html>";
			JOptionPane.showMessageDialog(this, msg, "About SolServer", JOptionPane.INFORMATION_MESSAGE, aboutImage);
		}
    }
    
    public static void main(String[] args){
    	
    	try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	SwingWorker<String, Object> initialization = new SwingWorker<String, Object>(){
    		
    		private URL trayIconUrl = SolServer.class.getResource("/icon_tray.gif");
    		private final Image trayImage = Toolkit.getDefaultToolkit().getImage(trayIconUrl);
    		private JDialog dialog = new JDialog();
    		private JLabel label = new JLabel("SolServer is initializing:");
    		private JProgressBar progessBar = new JProgressBar();
    		private Container container = dialog.getContentPane();
    		private JPanel panel = new JPanel(new GridLayout(0, 1, 0, 5));
    		
    		private void showInitBox(){
    			dialog.setIconImage(trayImage);
    			dialog.setTitle("Initializing...");
    			dialog.setVisible(true);
    			dialog.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    			dialog.setLocationRelativeTo(null);
    			panel.setBorder(new EmptyBorder(10, 10, 10, 10));
    			
    			panel.add(label);
    			progessBar.setIndeterminate(true);
    			panel.add(progessBar);
    			container.add(panel);
    			dialog.pack();
    		}
    		
    		@Override
    		protected String doInBackground(){
    			showInitBox();
    			server = new SolServer();
    			server.initMainFrame();
    	    	if (server.sSettings.toListenOnStart()){
    	    		server.startServer();
    	    	}

    			return null;
    		}
    		
    		@Override
    		protected void done(){
    			dialog.dispose();
    		}
    	};
    	
    	initialization.execute();

    }
}