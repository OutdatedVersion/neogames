package net.neogamesmc.network.task;

import org.apache.commons.io.FileUtils;

import java.io.File;

import static org.pmw.tinylog.Logger.debug;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/01/2017 (3:36 AM)
 */
public class CopyDirectoryTask implements Task
{

    /**
     * The source directory.
     */
    private File source, target;

    /**
     * Create the files from their absolute paths.
     *
     * @param source Source directory
     */
    public CopyDirectoryTask(String source)
    {
        this(new File(source));
    }

    /**
     * Class Constructor
     *
     * @param source Source directory
     */
    public CopyDirectoryTask(File source)
    {
        this.source = source;
    }

    /**
     * Update the target directory.
     *
     * @param target The target directory
     * @return This task, for chaining
     */
    @Override
    public CopyDirectoryTask target(String target)
    {
        this.target = new File(target);
        return this;
    }

    @Override
    public void execute() throws Exception
    {
        FileUtils.copyDirectoryToDirectory(source, target);
        debug("[Copy File Task] Copying {} to {}", source.getAbsolutePath(), target.getAbsolutePath());
    }

}
