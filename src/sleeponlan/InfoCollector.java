package sleeponlan;

import java.net.*;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

public class InfoCollector {
	private Enumeration<InetAddress> localInetAddress;
	private NetworkInterface network;
	private String ipAddress;
	private String localMAC, rLocalMAC;
	private Enumeration<NetworkInterface> netInterface;
	private Vector<String> netInterfaceList;
	private Vector<Integer> netInfceIndexList;
	private Preferences prefs = Preferences.userNodeForPackage(this.getClass());
	
	public InfoCollector() {
		
		if (prefs.getInt("indexOfInfce", -1) == -1)
			prefs.putInt("indexOfInfce", findUpIndex());
			
		try{
			netInterface = NetworkInterface.getNetworkInterfaces();
			netInterfaceList = new Vector<String>();
			netInfceIndexList = new Vector<Integer>();
			while (netInterface.hasMoreElements()){
				NetworkInterface element = netInterface.nextElement();
				if (isInvalidInfce(element)){
	                continue;
				} else {
					netInterfaceList.addElement(element.getDisplayName() + "(" + getIPByInfce(element) + ")");
					netInfceIndexList.addElement(element.getIndex());
				}
			}
		} catch (SocketException e){
			e.printStackTrace();
		}
	}
	
	private int findUpIndex(){ // this method can find any one valid network interface and returns its index.
		int index = -1;
		try {
			netInterface = NetworkInterface.getNetworkInterfaces();
			while (netInterface.hasMoreElements()){
				NetworkInterface element = netInterface.nextElement();
				if (isInvalidInfce(element)){
	                continue;
				} else {
					index = element.getIndex();
					break;
	            }
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return index;
	}
	
	private boolean isInvalidInfce(NetworkInterface infce){ // check whether a network interface is valid.
		boolean isInvalidInfce = false;
		
		try {
			isInvalidInfce = (infce == null || infce.isLoopback() || infce.isPointToPoint() || infce.isVirtual() || infce.isUp() == false);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "<html><body><p style = \"font-size: 16px\">No Network interface is online!</p><br>"
					+ "<p style = \"font-size: 11px\">Please try to collect to any network before running this program.</p></body></html>", "Cannot initialize!", JOptionPane.ERROR_MESSAGE);
		}
		
		return isInvalidInfce;
	}
	
	private String getIPByInfce(NetworkInterface infce){
		List<InterfaceAddress> listOfIP = infce.getInterfaceAddresses();
		return listOfIP.toString();
	}
	
	public void collect(){	// this method listens to a network given network interface and gets its information.
		try {
			
			network = NetworkInterface.getByIndex(prefs.getInt("indexOfInfce", findUpIndex()));
			
			if (isInvalidInfce(network)){  // Check whether stored choice of interface is valid of not, if not valid, get other valid.
				int index = findUpIndex();
				network = NetworkInterface.getByIndex(index);
				prefs.putInt("indexOfInfce", index);
			}
			
			localMAC = MagicPacket.hexConvert(network.getHardwareAddress(), true);
			rLocalMAC = MagicPacket.hexConvert(network.getHardwareAddress(), false);
			localInetAddress = network.getInetAddresses();
			ipAddress = localInetAddress.nextElement().getHostAddress();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "<html><body><p style = \"font-size: 16px\">Cannot listen to any network interface!</p><br>"
					+ "<p style = \"font-size: 11px\">Cannot find any valid network interface or it is unable to be listened.</p></body></html>", "Cannot initialize!", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
	
	public int initIndexOfInfce(){
		int validIndex = findUpIndex();
		
		if (validIndex < 0) // return -1 as there is no valid interface obtained.
			return -1;
		
		return netInfceIndexList.indexOf(validIndex);
	}
	
	public void sendIndexOfInfce(int index){
		prefs.putInt("indexOfInfce", netInfceIndexList.get(index));
		prefs.putInt("indexOfindexOfInfce", index);
	}
	
	public int getIndexOfInfce(){
		int validIndex = findUpIndex();
		
		if (validIndex < 0) // return -1 as there is no valid interface obtained.
			return -1;
		
		return prefs.getInt("indexOfindexOfInfce", netInfceIndexList.indexOf(validIndex));
	}
	
	public Vector<String> getInfceList(){
		return netInterfaceList;
	}
	
	
	public String getIP(){
		return ipAddress;
	}
	
	public String getMAC(){
		return localMAC;
	}
	
	public String getRMAC(){
		return rLocalMAC;
	}
}
