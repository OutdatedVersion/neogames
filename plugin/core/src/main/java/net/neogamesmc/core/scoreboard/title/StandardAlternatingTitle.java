package net.neogamesmc.core.scoreboard.title;

import net.neogamesmc.core.scheduler.Scheduler;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/18/2017 (10:46 PM)
 */
public class StandardAlternatingTitle implements ScoreboardTitle
{

    // in progress

    private static final short REQUIRED_PASSAGE = 4000;

    private String text = "NEOGAMES";

    private byte index, maxIndex = (byte) text.length();

    private boolean stageOne = true;

    private boolean isInProgress = false;

    private long lastCycle = System.currentTimeMillis() - REQUIRED_PASSAGE;

    public StandardAlternatingTitle()
    {
        // NEOGAMES

        Scheduler.timer(() ->
        {
            if (isInProgress)
            {

            }
            else if ((System.currentTimeMillis() - lastCycle) > REQUIRED_PASSAGE) isInProgress = true;
        }, 10);
    }

    @Override
    public String current()
    {
        return "";
    }

    @Override
    public void updateContent(String fresh)
    {

    }

}
