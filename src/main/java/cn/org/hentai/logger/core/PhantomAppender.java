package cn.org.hentai.logger.core;

import org.apache.log4j.Layout;
import org.apache.log4j.WriterAppender;

import java.io.OutputStream;

/**
 * Created by matrixy on 2019/8/8.
 */
public class PhantomAppender extends WriterAppender
{
    public PhantomAppender()
    {
        super();
    }

    public PhantomAppender(Layout layout, OutputStream os)
    {
        super(layout, os);
    }

    public void activateOptions()
    {
        new LogDistributeServer().start();
        this.setWriter(this.createWriter(new DistributeOutputStream()));
        super.activateOptions();
    }
}
