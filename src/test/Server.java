package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable{
	
	private static ArrayList<Server> list = new ArrayList<Server>();
	
	Socket mSocket;
	BufferedWriter bw;
	BufferedReader br;
	
	Server(Socket socket){
		mSocket = socket;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException{
		ServerSocket ss = new ServerSocket(9023);
		System.out.println(ss.getLocalSocketAddress());
		while(true){
			Server s = new Server(ss.accept());
			list.add(s);
			s.start();
		}
	}

	@Override
	public void run() {
		boolean running = true;
		while(running){
			try {
				String message = br.readLine();
				if(message!=null){
					for(Server server:Server.list){
						if(server!=this){
							server.sendMessage(message);
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				running = false;
				try {
					mSocket.close();
					list.remove(this);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					list.remove(this);
				}
			}
		}
		
	}
	
	public void sendMessage(String s){
		synchronized (bw) {
			try {
				bw.write(s+"\n");
				System.out.println(s);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void start(){
		new Thread(this).start();
	}
	
	@Override
	public void finalize(){
		try {
			mSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		list.remove(this);
	}
	
}
