package net.neogamesmc.core.scoreboard;

import lombok.AllArgsConstructor;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/03/2017 (11:57 PM)
 */
@AllArgsConstructor
public class StaticTitle implements ScoreboardTitle
{

    /**
     * The content to display on the title.
     */
    private String content;

    /**
     * {@inheritDoc}
     */
    @Override
    public String current()
    {
        return content;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateContent(String fresh)
    {
        // objective.setDisplayName(this.content = fresh);
    }

}
