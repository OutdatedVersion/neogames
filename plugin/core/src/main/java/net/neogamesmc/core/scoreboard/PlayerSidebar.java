package net.neogamesmc.core.scoreboard;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import net.neogamesmc.core.scoreboard.mod.ScoreboardModifier;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/03/2017 (11:32 PM)
 */
public class PlayerSidebar
{

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
     *
     */
    private String[] stagingLines = new String[15];

    /**
     *
     */
    private byte counter;

    /**
     * A set of modifiers being used with this scoreboard.
     */
    @Getter
    private Set<ScoreboardModifier> activeModifiers;

    /**
     * Class Constructor
     */
    public PlayerSidebar()
    {
        this(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    /**
     * Class Constructor
     *
     * @param scoreboard Scoreboard associated with this
     */
    public PlayerSidebar(@NonNull Scoreboard scoreboard)
    {
        this.lines = Maps.newHashMap();
        this.scoreboard = scoreboard;

        this.objective = scoreboard.registerNewObjective(UUID.randomUUID().toString().substring(0, 8), ScoreboardConstants.DUMMY_OBJECTIVE);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public PlayerSidebar add(String line)
    {
        checkState((counter + 1) <= ScoreboardConstants.MAX_LINES, "You've reached the line cap on this scoreboard.");

        stagingLines[counter++] = line;
        return this;
    }

    public PlayerSidebar blank()
    {
        return add(" ");
    }

    /**
     * Send out the lines currently in-queue to be sent out.
     *
     * @return This sidebar
     */
    public PlayerSidebar draw()
    {
        // Entries are added top-to-bottom; To display lines 1 to (entry count)
        // we need to set them bottom-to-top
        ArrayUtils.reverse(stagingLines);

        // Allow duplicate lines
        duplicationFix();

        byte score = 1;

        for (String entry : stagingLines)
            if (entry != null)
                objective.getScore(entry).setScore(score++);

        return this;
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
        this.objective.getScore(text).setScore(line);
        this.lines.put(line, text);
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
     * matches a currently existing line. If it does an empty chat
     * character is added to allow the "duplication" to occur.
     */
    private void duplicationFix()
    {
        for (byte i = 0; i < stagingLines.length; i++)
        {
            String working = stagingLines[i];

            if (working == null)
                continue;

            boolean unique = false;

            while (!unique)
            {
                unique = true;

                for (String entry : stagingLines)
                {
                    if (entry != null)
                    {
                        if (entry.equals(working))
                        {
                            working += ChatColor.RESET;
                            unique = false;
                        }
                    }
                }
            }

            stagingLines[i] = working;
        }
    }

    /**
     * Perform cleanup on this sidebar instance.
     */
    public void cleanup()
    {
        if (activeModifiers != null)
            activeModifiers.forEach(modifier -> modifier.end(scoreboard));
    }

    public PlayerSidebar registerModifier(ScoreboardModifier modifier)
    {
        if (activeModifiers == null)
            activeModifiers = Sets.newHashSet();

        modifier.start(scoreboard);
        activeModifiers.add(modifier);
        return this;
    }

    public PlayerSidebar removeModifier(ScoreboardModifier modifier)
    {
        modifierCheck();

        modifier.end(scoreboard);
        activeModifiers.remove(modifier);
        return this;
    }

    public PlayerSidebar registerWith(PlayerSidebarManager manager, Player player)
    {
        manager.add(player, this);
        return this;
    }

    public Optional<ScoreboardModifier> modifier(Class<? extends ScoreboardModifier> clazz)
    {
        modifierCheck();
        return activeModifiers.stream().filter(mod -> mod.getClass().equals(clazz)).findFirst();
    }

    /**
     * Verify that we have initiated our modifier set.
     */
    private void modifierCheck()
    {
        checkNotNull(activeModifiers, "Modifiers have yet to be initialized.");
    }

}
