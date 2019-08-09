package cn.org.hentai.logger.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by matrixy on 2019/8/8.
 */
public class DistributeOutputStream extends OutputStream
{
    private static final int BUFFER_SIZE = 8192;

    private ByteArrayOutputStream buffer;

    public DistributeOutputStream()
    {
        this.buffer = new ByteArrayOutputStream(BUFFER_SIZE);
    }

    @Override
    public void write(int b) throws IOException
    {
        if (this.buffer.size() >= BUFFER_SIZE) this.flush();
        else if ((b & 0xff) == '\n')
        {
            synchronized (this)
            {
                this.buffer.write(b);
            }
            this.flush();
            return;
        }
        synchronized (this)
        {
            this.buffer.write(b);
        }
    }

    @Override
    public void flush() throws IOException
    {
        super.flush();
        byte[] block = null;
        synchronized (this)
        {
            block = this.buffer.toByteArray();
            this.buffer.reset();
        }
        LogDistributeManager.getInstance().distribute(block);
    }

    @Override
    public void close() throws IOException
    {
        super.close();
    }
}
