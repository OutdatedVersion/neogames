package net.neogamesmc.network.task;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/01/2017 (5:22 AM)
 */
public class WriteToFileTask implements Task
{

    /**
     * Used to convert these objects into writable data.
     */
    private static final Gson GSON = new Gson();

    /**
     * The file we're writing to.
     */
    private File file;

    /**
     * The file's name.
     */
    private String name;

    /**
     * The object to write.
     */
    private Object object;

    /**
     * Class Constructor
     *
     * @param name The file's name
     * @param object The thing to write
     */
    public WriteToFileTask(String name, Object object)
    {
        this.name = name;
        this.object = object;
    }

    @Override
    public WriteToFileTask target(String val)
    {
        this.file = new File(val + File.pathSeparator + name);
        return this;
    }

    @Override
    public void execute() throws Exception
    {
        FileUtils.writeStringToFile(file, GSON.toJson(object));
    }

}
