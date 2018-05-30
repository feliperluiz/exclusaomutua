package tokenring;

import java.io.*;
import java.net.*;

public class Server implements Runnable
{
    Socket socket=null;
    static ServerSocket ss;
    Server(Socket newSocket)
    {
        this.socket=newSocket;        
    }
    public static void main(String args[]) throws IOException
    {
        ss=new ServerSocket(7000);
        System.out.println("Server Started");
        while(true)
        {
            Socket s = ss.accept();
            Server es = new Server(s);
            Thread t = new Thread(es);
            t.start();
        }
    }
    public void run()
    {
        try {        
            BufferedReader in = new BufferedReader(new 
InputStreamReader(socket.getInputStream()));
            while(true)
            {
                System.out.println(in.readLine());
            }
        }
        catch(Exception e)
        {}
    }
}