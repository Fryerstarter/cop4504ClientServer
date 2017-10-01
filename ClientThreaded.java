import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;
import java.util.concurrent.*;

public class ClientThreaded
{
	public static void main(String[] args)
	{
		IPInfo ip = new IPInfo();
		
		ip.setPort(9090);
		int numJobs = 10;
		if(args.length ==1 )
		{
			ip.setIP(args[0]);
			numJobs = 10;
		}else if(args.length == 2)
		{
			ip.setIP(args[0]);
			numJobs = Integer.parseInt(args[1]);
		}else
		{
			ip.setIP("73.104.15.60");
			numJobs = 100;
		}
		ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();
		queue.add("ThreadName,DelayPoint1,DelayPoint2");
		Thread printThread = new Thread(new Printer(queue));
		printThread.start();
		Thread[] threads = new Thread[numJobs];
		
		for(int i = 0; i < numJobs; i++)
		{
			Thread newThread = new Thread(new ThreadProcess(queue));
			newThread.setName("Thread"+i);
			threads[i] = newThread;
			threads[i].start();
		}
		
		
	}
	
}

class ThreadProcess implements Runnable
{
	ConcurrentLinkedQueue<String> queue;
	
	ThreadProcess(ConcurrentLinkedQueue<String> queue)
	{
		this.queue = queue;
	}
	
	public void run()
	{
		this.Connect();
	}
	
	public void Connect()
	{
		
		try
		{
			IPInfo ip = new IPInfo();
			InputStreamReader stdInReader = new InputStreamReader(System.in);
			BufferedReader stdIn = new BufferedReader(stdInReader);
			long startTime = 0, finishTime = 0, delay1 = 0, delay2 = 0;

			//make connection
			//System.out.println("Making connection....");
			System.out.println("Setting up " + Thread.currentThread().getName());
			Socket socket = new Socket(ip.getIP(), ip.getPort());
			
			//setup for socket toServer
			BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			//setup for socket fromServer
			PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
			
			
			String userInput = "";
			String serverOutput = "";
			
			//userInput = stdIn.readLine();
			ArrayList<String> firstCom = new ArrayList<String>();
			ArrayList<String> secCom = new ArrayList<String>();
			startTime = System.currentTimeMillis();
			
			//toServer.println("B");
			firstCom = toServer(fromServer, toServer, "B");
			
			finishTime = System.currentTimeMillis();
			delay1 = finishTime - startTime;
			
			startTime = System.currentTimeMillis();
			
			secCom = toServer(fromServer, toServer, "C");
			finishTime = System.currentTimeMillis();
			delay2 = finishTime - startTime;
			System.out.println("Time added.");
			String passer = Thread.currentThread().getName() + "," + delay1 + "," + delay2;
			//System.out.println(passer);
			queue.add(passer);
			fromServer.close();
			toServer.close();
			socket.close();
		
		}catch(IOException e)
		{
			System.out.println(e.toString());
			//fromServer.close();
			//toServer.close();
			//socket.close();
			
		}
		
		
		
			
	//END CONNECT
	}
	
	public ArrayList<String> toServer(BufferedReader fromServer, PrintWriter toServer, String input)
	{
		String serverOutput;
		ArrayList<String> inputList = new ArrayList<String>();
		try{
			
			toServer.println(input);
			while(!fromServer.ready())
				{
				}
			
			do{
				serverOutput = fromServer.readLine();
				
				inputList.add(serverOutput);
			}while(!serverOutput.equals("-2"));
			
			if(inputList.get(0).equals("-1"))
			{
				System.out.println("Error on input.");
			}
		}catch(IOException e)
		{
			
		}
		return inputList;
	}
	
	
	
	
}

class IPInfo
{
	private static String ip;
	private static int port;
	
	
	public IPInfo()
	{
	}
	
	
	public IPInfo(String ip)
	{
		this.ip = ip;
	}
	
	public IPInfo(int port)
	{
		this.port = port;
	}
	
	public IPInfo(String ip, int port)
	{
		this.ip = ip;
		this.port = port;
	}
	
	public String getIP(){return ip;}
	
	public void setIP(String ip) {this.ip = ip;}
	
	public int getPort() {return port;}
	
	public void setPort(int port) {this.port = port;}
	
	
	
}

class Printer implements Runnable
{
	ConcurrentLinkedQueue<String> queue;
	
	Printer(ConcurrentLinkedQueue<String> queue)
	{
		this.queue = queue;
	}
	
	public void run()
	{
		String str;
		while(true){
			
			
			
			try{
				Thread.sleep(50);
				FileWriter fw = new FileWriter("TestData.csv", true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw);
				while((str = queue.poll())!= null)
				{
					System.out.println("Printing...");
					System.out.println(str);
					out.println(str);
				}
				out.close();
				}catch(Exception e)
				{
					System.out.println("Error saving to testdata");
					
				}
			
		}
	}
}