package sleeponlan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.PatternSyntaxException;

public class Commander {
	private String[] cmds;
	
	public Commander(){

	}
	
	private void cmdSplit(String cmd){
		
		try {
			cmds = cmd.split("\\n+");
		} catch (PatternSyntaxException e) {
			e.printStackTrace();
		}
		
	}
	
	public void run(String cmd){    
	    cmdSplit(cmd);
	    
	    for (String command : cmds) {
	    	SolServer.server.logNewMsg(SolServer.server.getCurrentTime() + "Run Command: " + command);
	    	Thread thread = new Thread(new Runnable(){

	    		@Override
	    		public void run(){
	    			try {
		    			Process p = Runtime.getRuntime().exec(command);
		    			p.waitFor();
		    			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		    			String line = "";
		    			while ((line = reader.readLine()) != null) {
		    				SolServer.server.logNewMsg("CMD: " + line);
		    			}
	    			} catch (IOException | InterruptedException e) {
	    				e.printStackTrace();
	    			}
	    		}
	    	});
	    	thread.start();
	    }
	}
}
