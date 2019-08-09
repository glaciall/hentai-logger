package cn.org.hentai.logger.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by matrixy on 2019/8/8.
 */
public class LogDistributeServer extends Thread
{
    Logger logger = LoggerFactory.getLogger(LogDistributeServer.class);

    public LogDistributeServer()
    {
        this.setName("log-distribute-server");
    }

    public void run()
    {
        ServerSocket server = null;
        try
        {
            int port = 1122;

            server = new ServerSocket();
            server.bind(new InetSocketAddress("0.0.0.0", port));
            logger.info("LogDistributeServer started at: {}", port);

            while (!this.isInterrupted())
            {
                Socket conn = server.accept();
                conn.setSoTimeout(1000);
                conn.setSendBufferSize(81920);
                LogDistributeManager.getInstance().subscribe(conn);
            }
        }
        catch(Exception e)
        {
            logger.error("accept connection error", e);
        }
    }
}
