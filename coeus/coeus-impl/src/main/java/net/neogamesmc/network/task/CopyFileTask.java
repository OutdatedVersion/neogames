package net.neogamesmc.network.task;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Arrays;

import static org.pmw.tinylog.Logger.debug;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/01/2017 (3:18 AM)
 */
public class CopyFileTask implements Task
{

    /**
     * The target directory.
     */
    public File targetDirectory;

    /**
     * The files to copy.
     */
    public File[] copyThese;

    /**
     * Class Constructor
     *
     * @param copyThese The files to copy
     */
    public CopyFileTask(File... copyThese)
    {
        this.copyThese = copyThese;
    }

    /**
     * Class Constructor
     *
     * @param copyThese The files to copy
     */
    public CopyFileTask(String... copyThese)
    {
        this.copyThese = Arrays.stream(copyThese).map(File::new).filter(File::exists).toArray(File[]::new);
    }

    /**
     * Updates the target directory
     *
     * @param target The target directory
     * @return This task, for chaining
     */
    @Override
    public CopyFileTask target(String target)
    {
        this.targetDirectory = new File(target);
        return this;
    }

    @Override
    public void execute() throws Exception
    {
        for (File file : copyThese)
        {
            FileUtils.copyFileToDirectory(file, targetDirectory);
            debug("[File Move Task] Copying {} to {}", file.getAbsolutePath(), targetDirectory.getAbsolutePath());
        }
    }

}
