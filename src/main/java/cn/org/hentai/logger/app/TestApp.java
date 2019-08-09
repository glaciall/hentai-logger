package cn.org.hentai.logger.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by matrixy on 2019/8/9.
 */
public class TestApp
{
    static Logger logger = LoggerFactory.getLogger(TestApp.class);

    public static void main(String[] args) throws Exception
    {
        int i = 0;
        while (true)
        {
            i += 1;
            logger.debug("{} - debug log info -----------------------------------", i);
            if (i % 100 == 99) logger.error("{} - error log info ***********************************", i);
            Thread.sleep(10);
        }
    }
}
