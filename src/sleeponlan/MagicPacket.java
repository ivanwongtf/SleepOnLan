package sleeponlan;

import java.net.DatagramPacket;
import java.util.regex.*;

public class MagicPacket {
	
	private MagicPacket(){
		
	}

	public static boolean checkValidPacket(DatagramPacket packet, boolean macSequence) {
    	byte[] data;
    	boolean correct = false;
    	int packetLength = packet.getLength();
    	InfoCollector collector = new InfoCollector();
    	collector.collect();
    	String localMAC;
    	if (macSequence)
    		localMAC = collector.getMAC();
    	else
    		localMAC = collector.getRMAC();
    	String inputMAC = getInputMAC(packet);
    	final String WOLHEAD = "FF:FF:FF:FF:FF:FF";
    	final int WOL_LENGTH = 108;
    	
    	if (packetLength > WOL_LENGTH)
    		return false;
    	else if (packetLength > 0){
    		data = packet.getData();
    		if (hexConvert(data, true).substring(0, 16).equals(WOLHEAD))
    			correct = false;
    		else if (checkValidMAC(inputMAC) == false)
    			correct = false;
    		else if (!localMAC.equals(inputMAC))
    			correct = false;
    		else
    			correct = true;
    	}
    	return correct;
    }
    
    public static boolean checkValidMAC(String mac){
    	final Pattern macPat = Pattern.compile("((([0-9a-fA-F]){2}[-:]){5}([0-9a-fA-F]){2})");
    	Matcher macMatcher = macPat.matcher(mac);
    	
    	if (macMatcher.find())
    		return true;
    	else
    		return false;
    }
    
    public static String getInputMAC(DatagramPacket packet){
    	byte[] data;
    	byte[] mac = new byte[6];
    	int j=0;
    	data = packet.getData();
    	for (int i=6; i<12; i++){
    		mac[j] = data[i];
    		j++;
    	}
    	return hexConvert(mac, true);
    }
    
    public static String hexConvert (byte[] data, boolean notReverse){
    	StringBuilder packetData = new StringBuilder();
    	if (notReverse){
	        for (int i=0; i < data.length; i++) {
	            packetData.append(String.format("%02X", data[i]));
	            if (i < data.length-1)
	            	packetData.append(":");
	        }
    	} else {
    		for (int i=data.length-1; i >= 0; i--) {
	            packetData.append(String.format("%02X", data[i]));
	            if (i > 0)
	            	packetData.append(":");
	        }
    	}
        return packetData.toString();
    }
}
