package net.neogamesmc.core.scoreboard;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Map;
import java.util.UUID;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/03/2017 (11:32 PM)
 */
public class PlayerSidebar
{

    /**
     * Objective title management instance.
     * <p>
     * Titles are shared across every client connected to this server.
     */
    @Getter
    private static ScoreboardTitle title;

    public static ScoreboardTitle title(ScoreboardTitle title)
    {
        return PlayerSidebar.title = title;
    }

    /**
     * The scoreboard associated with this sidebar.
     */
    @Getter private final Scoreboard scoreboard;

    /**
     * The objective powering this sidebar.
     */
    @Getter private Objective objective;

    /**
     * Lines to the objective displayed on this sidebar.
     */
    private Map<Integer, String> lines;

    /**
     * Class Constructor
     */
    public PlayerSidebar()
    {
        this(Bukkit.getScoreboardManager().getNewScoreboard(), title);
    }

    /**
     * Class Constructor
     *
     * @param scoreboard Scoreboard associated with this
     * @param title The title of the scoreboard
     */
    public PlayerSidebar(@NonNull Scoreboard scoreboard, @NonNull ScoreboardTitle title)
    {
        this.lines = Maps.newHashMap();
        this.scoreboard = scoreboard;

        this.objective = scoreboard.registerNewObjective(UUID.randomUUID().toString().substring(0, 8), "dummy");
        this.objective.setDisplayName(title.current());
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    /**
     * Set text at the desired index.
     *
     * @param line The line number
     * @param text The text
     * @return This sidebar
     */
    public PlayerSidebar set(int line, String text)
    {
        text = duplicationFix(text);
        this.objective.getScore(text).setScore(line);
        this.lines.put(line, text);
        return this;
    }

    /**
     * Replace the line by removing one line then adding one.
     *
     * @param line The line
     * @param text The text
     * @return This sidebar
     */
    public PlayerSidebar replace(int line, String text)
    {
        remove(line);
        set(line, text);
        return this;
    }

    /**
     * Remove an entry from the scoreboard by line number.
     *
     * @param line The line number
     * @return The sidebar
     */
    public PlayerSidebar remove(int line)
    {
        return remove(null, line);
    }

    /**
     * Remove an entry from the scoreboard by the text.
     *
     * @param text The text content
     * @return This sidebar
     */
    public PlayerSidebar remove(String text)
    {
        return remove(text, -1);
    }

    /**
     * Internal method used to process removal requests.
     *
     * @param text Remove by text
     * @param line Remove by line number
     * @return This sidebar
     */
    private PlayerSidebar remove(String text, int line)
    {
        val iterator = lines.entrySet().iterator();

        while (iterator.hasNext())
        {
            val entry = iterator.next();

            if (text != null && !entry.getValue().equals(text))
                continue;

            if (line != -1 && entry.getKey() != line)
                continue;

            scoreboard.resetScores(entry.getValue());
            iterator.remove();
        }

        return this;
    }

    /**
     * Get the text at the provided line number.
     *
     * @param line The line number.
     * @return The text at that line
     */
    public String get(int line)
    {
        return lines.get(line);
    }

    /**
     * Iterate over every line and check whether or not it
     * matches the line being inserted. Adding an empty chat
     * character to allow the duplication to occur.
     *
     * @param textIn The text to check over
     * @return The fixed text
     */
    private String duplicationFix(String textIn)
    {
        boolean unique = false;

        while (!unique)
        {
            unique = true;

            for (String line : lines.values())
            {
                if (line.equals(textIn))
                {
                    textIn += ChatColor.RESET;
                    unique = false;
                }
            }
        }

        return textIn;
    }

}
