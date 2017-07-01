package net.neogamesmc.network.task;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/01/2017 (3:45 AM)
 */
public class ReplaceVariablesTask implements Task
{

    /**
     * The file name.
     */
    private String fileName;

    /**
     * What we're reading to replace the variables in.
     */
    private File file;

    /**
     * The variables to replace.
     */
    private Pair<String, Object>[] variables;

    /**
     * Class Constructor
     *
     * @param fileName The file name
     * @param pairs The variables
     */
    public ReplaceVariablesTask(String fileName, Pair<String, Object>... pairs)
    {
        this.fileName = fileName;
        this.variables = pairs;
    }

    /**
     * Set the file that we'll be reading.
     *
     * @param val The value
     * @return Return this for chaining
     */
    @Override
    public ReplaceVariablesTask target(String val)
    {
        this.file = new File(val + File.pathSeparator + fileName);
        return this;
    }

    @Override
    public void execute() throws Exception
    {
        String val = FileUtils.readFileToString(file, "UTF-8");

        for (Pair<String, Object> pair : variables)
            val = val.replaceAll(pair.getLeft(), String.valueOf(pair.getRight()));

        FileUtils.writeStringToFile(file, val, "UTF-8");
    }

}
