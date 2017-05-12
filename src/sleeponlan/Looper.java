package sleeponlan;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Looper implements Runnable {
	
	private AtomicBoolean keepRunning;
	private DatagramSocket socket;
	private int bufSize;
	private int socketPort;
	private String cmd;
	private Commander commander;
	private boolean macSequence;
	private static SolServer server = SolServer.server;
	
	public Looper(int socketPort, int bufSize, boolean macSequence) {
		keepRunning = new AtomicBoolean(true);
		this.socketPort = socketPort;
		this.bufSize = bufSize;
		this.macSequence = macSequence; 
	}
	
	public void createSocket(int port){
		DatagramSocket socket;
		
		try {
			socket = new DatagramSocket(port);
			this.socket = socket;
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		keepRunning.set(false);
		socket.close();
		SolServer.server.changeStartButton(false);
	}
	
	public void sendCommand(String cmd){
		this.cmd = cmd;
	}
	
	public boolean getServerStatus(){
		return keepRunning.get();
	}
	
	@Override
	public void run() {
		String inputMAC;
    	byte[] buf = new byte[bufSize];
    	server.logNewMsg(SolServer.server.getCurrentTime() + "Listening to port " + socketPort + " with " + bufSize + " bytes buffer.");
    	commander = new Commander();

    	while(keepRunning.get()){
    		DatagramPacket packet = new DatagramPacket(buf, buf.length);
    		inputMAC = "";
    		
    		try {
    			socket = null;
    			createSocket(socketPort);
    			socket.receive(packet);
    			if (packet.getLength() < 1){
    				server.logNewMsg(SolServer.server.getCurrentTime() + "Throw error packet.");
    				continue;
    			}
    			
        		inputMAC = MagicPacket.getInputMAC(packet);
        		if (MagicPacket.checkValidPacket(packet, macSequence) && keepRunning.get() != false){
        			server.logNewMsg(SolServer.server.getCurrentTime() + "Correct MAC: " + inputMAC);
        			commander.run(cmd);
        		} else if (keepRunning.get() != false){
        			server.logNewMsg(SolServer.server.getCurrentTime() + "Incorrect MAC: " + inputMAC);
        		}
        		
        		socket.close();
    		} catch (IOException e) {
    			server.logNewMsg(SolServer.server.getCurrentTime() + "Stop listening.");
    		} 
    	}
	}
	
}
