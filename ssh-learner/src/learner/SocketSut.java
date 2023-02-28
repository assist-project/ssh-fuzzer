package learner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketSut implements Sut {
	private PrintWriter sockout;
	private BufferedReader sockin;

	public SocketSut(Socket sock) {
		try {
			// Create socket out (no buffering) and in 
			sockout = new PrintWriter(sock.getOutputStream(), true);
			sockin = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String sendInput(String input) {
		try {	
			// Send input to SUT
			sockout.println(input);
			
			return sockin.readLine();
		} 
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void reset() {
		// Perform a reset on the SUT: empty input list on wrapper and send reset signal
		sockout.println("reset");
		
		// Check if reset succeeded. Note: this is also needed because not receiving after reset will immediately continue
		// to sending Input, allowing the possibility for the client to receive "reset INPUT" in one string. Reading in between
		// will force a break since reading is blocking.
		try {
			String line = sockin.readLine();
			if (!line.toString().equals("resetok")) {
				throw new MapperException(String.format("Reset did not succeed. On sending reset expected %s but got %s", "resetok", line ));
			}
		} catch (IOException e) {
			System.out.println("RESET NOT OK");
			e.printStackTrace();
			throw new MapperException("Reset could not be sent");
		}
	}
}
