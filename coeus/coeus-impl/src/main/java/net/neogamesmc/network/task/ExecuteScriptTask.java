package net.neogamesmc.network.task;

import java.io.File;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/01/2017 (4:13 AM)
 */
public class ExecuteScriptTask implements Task
{

    /**
     * The file's name.
     */
    private String fileName;

    /**
     * The script to execute.
     */
    private File file;

    /**
     * Class Constructor
     *
     * @param fileName Path
     */
    public ExecuteScriptTask(String fileName)
    {
        this.fileName = fileName;
    }

    @Override
    public ExecuteScriptTask target(String val)
    {
        this.file = new File(val + File.pathSeparator + fileName);
        return this;
    }

    @Override
    public void execute() throws Exception
    {
        Runtime.getRuntime().exec(new String[] { "/bin/sh", file.getAbsolutePath() });
    }

}
