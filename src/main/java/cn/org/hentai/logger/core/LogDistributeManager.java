package cn.org.hentai.logger.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by matrixy on 2019/8/8.
 */
public class LogDistributeManager extends Thread
{
    static Logger logger = LoggerFactory.getLogger(LogDistributeManager.class);

    Object lock = new Object();
    LinkedList<Socket> subscribers = new LinkedList();
    LinkedBlockingQueue<byte[]> messages = new LinkedBlockingQueue<>();

    public void subscribe(Socket connection)
    {
        synchronized (lock)
        {
            subscribers.addLast(connection);
        }
        System.err.println("Connected: " + connection.getInetAddress());
    }

    public void distribute(byte[] block)
    {
        try
        {
            messages.put(block);
        }
        catch(Exception e) { e.printStackTrace(new PrintStream(System.err)); }
    }

    private void distribte0(byte[] block)
    {
        Socket[] connections = null;
        synchronized (lock)
        {
            connections = subscribers.toArray(new Socket[0]);
        }
        for (Socket conn : connections)
        {
            try
            {
                conn.getOutputStream().write(block);
                conn.getOutputStream().flush();
            }
            catch(Exception ex)
            {
                synchronized (lock)
                {
                    try { conn.close(); } catch(Exception e) { }
                    subscribers.remove(conn);
                    System.err.println("Closed: " + conn.getInetAddress());
                }
            }
        }
    }

    public void run()
    {
        while (!this.isInterrupted())
        {
            try
            {
                byte[] msg = messages.take();
                distribte0(msg);
            }
            catch(Exception e)
            {
                e.printStackTrace(new PrintStream(System.err));
                logger.error("distribute error", e);
            }
        }
    }

    private static volatile LogDistributeManager instance;
    private LogDistributeManager()
    {
        this.setName("log-distributor");
    }

    public static LogDistributeManager getInstance()
    {
        if (instance == null)
        {
            synchronized (LogDistributeManager.class)
            {
                if (instance == null)
                {
                    instance = new LogDistributeManager();
                    instance.start();
                }
            }
        }
        return instance;
    }
}
