package net.neogamesmc.core.scoreboard.title;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/03/2017 (11:52 PM)
 */
public interface ScoreboardTitle
{

    /**
     * Grab the current text this scoreboard displays.
     *
     * @return The text to display
     */
    String current();

    /**
     * Update the current text of the associated objective's title.
     *
     * @param fresh The new text
     */
    void updateContent(String fresh);

}
