package net.neogamesmc.lobby;

import com.destroystokyo.paper.Title;
import com.google.inject.Injector;
import lombok.val;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.minecraft.server.v1_11_R1.EnumItemSlot;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.payload.QueuePlayersForGroupPayload;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.core.bukkit.Plugin;
import net.neogamesmc.core.command.api.CommandHandler;
import net.neogamesmc.core.hotbar.HotbarHandler;
import net.neogamesmc.core.hotbar.HotbarItem;
import net.neogamesmc.core.inventory.ItemBuilder;
import net.neogamesmc.core.issue.Issues;
import net.neogamesmc.core.npc.NPCManager;
import net.neogamesmc.core.npc.NPCType;
import net.neogamesmc.core.scheduler.Scheduler;
import net.neogamesmc.core.scoreboard.PlayerSidebarManager;
import net.neogamesmc.core.text.Colors;
import net.neogamesmc.lobby.news.News;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

import static net.md_5.bungee.api.ChatColor.*;
import static net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention.NONE;
import static net.neogamesmc.core.npc.NPCType.GO_TO_GAME;
import static net.neogamesmc.core.npc.NPCType.TEAM_MEMBER;
import static net.neogamesmc.core.text.Colors.bold;
import static org.bukkit.Material.BOW;
import static org.bukkit.Material.COMPASS;

/**
 * Startup function(s) for a main lobby.
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/19/2017 (3:30 AM)
 */
public class Lobby extends Plugin
{

    /**
     * The beginning of the role.
     */
    private static final Title.Builder BUILDER = new Title.Builder()
            .title(new ComponentBuilder("Neo").bold(true).color(YELLOW).append("Games").color(GOLD).create())
            .fadeIn(35)
            .stay(70);

    /**
     * Where players are sent when joining.
     */
    private Location spawnLocation;

    /**
     * Local instance
     */
    private Database database;

    /**
     * Maintain our lobby scoreboard.
     */
    private LobbyScoreboard scoreboard;

    @Override
    public void enable(Injector injector)
    {
        // Forcefully start database first
        this.database = register(Database.class);

        get(RedisHandler.class).subscribe(RedisChannel.DEFAULT);
        register(News.class);
        register(HotbarHandler.class);

        register(PlayerSidebarManager.class);
        scoreboard = register(LobbyScoreboard.class);

        loadCore();
        setupCommands();
        registerAsListener();

        // Manually add lobby command
        get(CommandHandler.class).registerObject(register(FlyCommand.class));

        val lobby = Bukkit.getWorld("lobby");
        this.spawnLocation = new Location(lobby, 10.5, 64, 5.5, 162.6f, 0f);
        lobby.loadChunk(-1, 0);
        lobby.loadChunk(-1, -1);


        val manager = register(NPCManager.class).scoreboard(get(PlayerSidebarManager.class));

        val blastoff = manager.createNewNPC(GO_TO_GAME, "Blast Off", new Location(lobby, 3.5, 63, 1.5, -60.8f, 0));
        blastoff.equipment.put(EnumItemSlot.MAINHAND, new ItemStack(Material.TNT));
        blastoff.equipment.put(EnumItemSlot.HEAD, new ItemStack(Material.GOLD_HELMET));
        blastoff.setSkin("eyJ0aW1lc3RhbXAiOjE0OTk3NTU4NjgxMzcsInByb2ZpbGVJZCI6ImIwZDczMmZlMDBmNzQwN2U5ZTdmNzQ2MzAxY2Q5OGNhIiwicHJvZmlsZU5hbWUiOiJPUHBscyIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWJlZDljZmEyZWVkMWMzMzUyZWZkZWVmNjZhNjRjOTk1YmFiYmVlYTZjZDNiMzBjZTk3ZmQ0MzkxMjRlMSJ9fX0=", "T34I1QSSk/KvMqM0VsD/8fSwdjMOF/FWMAiLT16kClbyWpohBfNT6L1tblI3/0BkD038cB14c5ib6Wy+jS+swKkS9Ko70YRgiyBuqwduhniY8UWqOLu7W8Nsjl3acNi9uvN0V4QZw90DokG1k/FgstQzNjlJjKshQfo4sothzvJmbLnX1szumFY8mgtWRZZmDwihpzppkgcvifi4Txv29oapHqUXyjBGbpioY2Zr+w80x+i8sn2B6vIm3/cnpXDSCm8nffETfOGZY4UvTvRy4ihg2+geBOUOUSABuSrpwZ3SfwmKdMw2uMLu4eT9thfcde7l4Q/gVitfgoVYWnWkGnVd8/ifFr9nUrhxL01EUwlA/WETJJT+DOqI/W6DUqLV1x3Rw06A7vHvX/9qGBnlXXHS5wgKu1eMop3qXKJ2D/DO/yCuRp0IRYbIlapv1w88h+wujEKakBl3zG0WNzpfCXVDkEZB00IngJzt5v6ELWnVdiqr6Ake6BEJnzWrv+XSfnVua+y5IBroTo5F3ZascqLFmjkRfZgjFv2cbeKimaDFhDq6SJeJYamaFXf8wQwlSotAzBzsXcesAluFokJe32DCWBwKAO9gfVmaJorYunm2L9vs9wSXL5C6mq0SltfM+8G7IE4MyY0Qq2OUYn5URxqV+Tv5LsDwF1N3j/n83tE=");
        blastoff.lineSet(1, GREEN + "Join " + bold(RED) + "Blast Off");
        // blastoff.lineSet(2, GRAY + "No One Is Playing");
        blastoff.data("group", "blastoff");

        val chunkRunner = manager.createNewNPC(GO_TO_GAME, "Chunk Runner", new Location(lobby, -1.147, 63.06250, 2.5, -80, 0));
        chunkRunner.setSkin("eyJ0aW1lc3RhbXAiOjE0OTk3NTU5MTU4NzcsInByb2ZpbGVJZCI6IjQzYTgzNzNkNjQyOTQ1MTBhOWFhYjMwZjViM2NlYmIzIiwicHJvZmlsZU5hbWUiOiJTa3VsbENsaWVudFNraW42Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8xZWM1ZGFhNWM4NjM5YjI0MjIyMTIyMjlhMjk1MTY4MjU0NTJjYjUwMzc2YTVkOWYwNGFjMTYzYTU2ZGIxYzcifX19", "Z7Yz+eTbNRwsmG4b0XStdaYpM6RIUi/v3VfXY2g+DwingtdUDadwwl4qXRzjsvGmXrYbqWUZtc5bbh8cPTSkL/7zU4bzbmAcLYOQGXZA4tmRZjPMxMdDs6KkRKMbI24gciOmKC77jKerOXT70E0s2auSD9gnyubWRRWxE+hshoGVs5jISyhMxi5Vv09kmsAvecEb71wrftE4L1RNZz5blgNBu8nE/ZNQ9MSOgIWHybA4Aosj2jPbdAwdy+hHrISuaDw5J9PrYljUzy3Vw78r+Thg/XuawRLcXTgZjAvyh93WSI1wn8Lb1uCF7Nty9ypVtzqRPeWctJcIpvkj5LiUJmnGnGqEe6zdScF3TOH7z5YcIytZz9+DXsBMyBidSVljxmk2unEDE3oiNJklsPTZ5/m/iC4p1DDD+CvC5jzbhyaMnn40e7JiU+4kisURvNIBpUYdcjxNHFT2iEJfeGvWI62p0L0BZl3ODvdRuNO6YxoHzY/C2HXA4Qh15WB7tfLjffcHGe16SWMsd7YKNe/Zb45+eYFldI81kOvczbRhlX9qp4n3cJxJ5fiFQiz3ZtChGj3DlrcWN5/fTkCQKP8zIEU/M9aqzdqF/iZyt1e+/wK/GmTPVRUKHBuW/DRzffUks1DmP8Hb9n/yjoNCDl9iILcbYkzNX4L5m8s7FZ9gyKw=");
        chunkRunner.lineSet(1, GREEN + "Join " + bold(DARK_GREEN) + "Chunk Runner");
        // chunkRunner.lineSet(2, GRAY + "No One Is Playing");
        chunkRunner.data("group", "chunkrunner");

        val bowplinko = manager.createNewNPC(GO_TO_GAME, "Bowplinko", new Location(lobby, -0.5, 63.06, -2.5, -48.3f, 0f));
        bowplinko.equipment.put(EnumItemSlot.MAINHAND, new ItemStack(BOW));
        bowplinko.setSkin("eyJ0aW1lc3RhbXAiOjE0OTk3NTU5NDcxMjEsInByb2ZpbGVJZCI6ImRhNzQ2NWVkMjljYjRkZTA5MzRkOTIwMTc0NDkxMzU1IiwicHJvZmlsZU5hbWUiOiJJc2F5bGEiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ViNDljOWNjM2U5YmNhMjljZWMwMWQwODMyOWMyYTM3ZTVlYmYzY2ZhOWJiMjgzMGEzMzJiMjczNzVhOSJ9fX0=", "gm5IgpFXiSzOpQPedsEU3E0r1E15w8MmBUY4oX1tb2B6J7xPwCBHTuorBe+9RQAzY1yTXaKN1dp+xBBzAgfQ+o6eQkEct/A3r/o0tA794BmIWf7WW7HP+euT8sadvOXZRsS8/v78Rbh31jPNUCtpNjCX+sYP0aBMc+M63h864Ivnkyjb6xJsWTWC/ptBWnautDp3YTdV/YIxH53Oi+XttpiFZK9b9+rRQoo4ZG4KOKkmXHD7/fLbglu9OaTLFcEdExkiocz6jDnrrn1360n5dembowSmc3GJkGibkcIjv/Fw3phIJZkpcSL5Q0hHg9Lh1UMI6WVJaV1i9LIDT5BR03Q5cPvkcis4w6o1nZhlP3ykXxheMHpzSn9KkKh5GPsqLu7q4q8eDxKj7ISZkv9HoSqz52/9fhVK76B0Wy4fyPa77S8bUGraPOnaQLTYDI07ENC6T5Bwq/4fEkqW20gemk5jZmIzVq4jWXMsnHbPJ5q6OMDkDnjYWAf93Gcu2jzEkRaYHQ5xAmts3IjGoQm0XZwpfGVfXaKfCCgJe2ePe0vcx0VVE44ODaSbDzbQZPOR+MxcHM0wTe2+0iuJdC65pnB3LsAbSoLDY7MSeg/vcwMhf1gIgeV8cQ55PYynnSFoIjyskK6w/8ourYAacr+aLE0pspyG7Alzl1W93lUWfLM=");
        bowplinko.lineSet(1, GREEN + "Join " + bold(DARK_PURPLE) + "Bowplinko");
        // bowplinko.lineSet(2, GRAY + "No One Is Playing");
        bowplinko.data("group", "bowplinko");


        // TEAM

        val ben = manager.createNewNPC(TEAM_MEMBER, "OutdatedVersion", new Location(spawnLocation.getWorld(), 27.54, 65, -33.5, 24.8f, 12.6f));
        ben.data("quote", "ice is chewy");
        ben.lineSet(1, ChatColor.YELLOW + "" + net.md_5.bungee.api.ChatColor.BOLD + "NPC " + net.md_5.bungee.api.ChatColor.RESET + org.bukkit.ChatColor.YELLOW + " " + ben.getName());
        ben.setSkin("eyJ0aW1lc3RhbXAiOjE0OTk2Njk5NjY0OTIsInByb2ZpbGVJZCI6IjAzYzMzN2NkN2JlMDQ2OTRiOWIwZTJmZDAzZjU3MjU4IiwicHJvZmlsZU5hbWUiOiJPdXRkYXRlZFZlcnNpb24iLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmM0NmExYjU5ZTNhYmU0YWMyNDdjYWY5ZWE1ZGQzOWY4ZmZiOWQ1NTBkOGRkMzY0NjEwNWU5NGI5OTljIn0sIkNBUEUiOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81YzNjYTdlZTJhNDk4ZjFiNWQyNThkNWZhOTI3ZTYzZTQzMzE0M2FkZDU1MzhjZjYzYjZhOWI3OGFlNzM1In19fQ==", "nC99fqmbJe8ZuI1Pz4SN6u5bdBW1NEiAsJeZGLR4EjUA43tslrz7fDQmV2wIZRMVpTMM2WH+qd8pp6nmJNp7M7420NmhAtATGdIirM5srN8WBnbtRByJ13m2nbOCxN1p5kziFUvY2KlsC5Sk/RGrIz98S0ZH2jt+vVWPbxkNX1fRNIQmyPePd4Bv18UFANBowK/pbb17cb7DTDPp8MlunbLpqkRWmSLrlclf1jfSR5CpgkMD0jyPQvVxDdEy7RcLrBQK7XT5syjuYat84nFCXEGBMlAfXdTB1D+a+iW2Lt1YZCYR7EuXgO6JHnWzqi0xxC3xJ5CJGFbz2HdYGm9llQuRxgJsJ2KfUv+LoeNDbUXsvjguSdxgiAGbK0uaxGVDG0vnLN7rCVunwUR4HTUrOCGG1mBgjGAMzE5lz2+aSCmb3hpqDpHMSb5I02pefDoM1N60C+WcMTbTLoljvNRPeHQ6ZjFz1MW9gymKTXHlRgy4bAhkzqZywBs2dXnlqL1dNBGTYH4FoGctTZkxn5X8nGaIgcGAxwbjOMaHC5LjkcrdQD0RrebVC5M2wI6AXkOi5rNQqR2aQzA+TeP54sYNGhanN/PUORtYIAcU+LX1J5wVHrq07uxWcnj0s+nUQDCoHo5eT2UELOwzKq0LSo36BxW9O/gvNg9aBohTegvwRUQ=");

        val nokoa = manager.createNewNPC(TEAM_MEMBER, "Nokoa", new Location(spawnLocation.getWorld(), 27.5, 64, -29, 42.4f, 15f));
        nokoa.data("quote", "I like trains! Do you like trains?");
        nokoa.lineSet(1, ChatColor.YELLOW + "" + net.md_5.bungee.api.ChatColor.BOLD + "NPC " + net.md_5.bungee.api.ChatColor.RESET + org.bukkit.ChatColor.YELLOW + " " + nokoa.getName());
        nokoa.setSkin("eyJ0aW1lc3RhbXAiOjE0OTk1ODMxNDIzNjUsInByb2ZpbGVJZCI6IjQ0MTJiM2I0ZTAwMDQ4OTViZTFlMThiNThkNDJjYzFkIiwicHJvZmlsZU5hbWUiOiJOb2tvYSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTMzNzYwYWY1ZWNkZDIwODJlMTk0ZTc0ODYyMTg4ZmE3MjI1Yjc3M2ViYjNkN2ZkNmRiNDQyZmZiYWJiYWUifX19", "ABh1gNaUFCnVtLtb9JObQtJ9ctRMhbT7b6AEcubU3q7y36N+eij3Q+XGuHfgv5e03lgd1lX3nNxOrxbt5NvBFh7ZwEDUqlaQMFqdMskDPqTRKR89CEd6vMzXgIvJQF+eZ/lNlHXCurSn8MuP1afhPlvm8bu4W/W1rk/XHCDmsKYkssChNMN9pvLxnxS3Mx/jcuMuJJfjgjxCWl2IrbtiOjrtXwMVqvhQy37N2eGIfC1FPKzdp5G8sIdrd4+/0RzrkC+jTA/8Uirdm8FZuu/xIIq9yB72Sw1EjKUl9JSJkn5OEOmtquVVCpoYavoNdhpRhWkUbMejdhMTmkzFQB2fsIgKuKXGXSCTIMuauZ3hRIVbCKWsvZIyelmtwzd5pH9ml/7GQvlOzQ02jMwagFNg7ydUBpTf4mIOT3sM5xEwV1Uyfg8aSIDauRTA1E76JuI9xKxOCTThg4GSylZyhiKiiAtezTypc/bIHam5nXlLFAg8H8zdWH5Irluc36eSOfhJlKvHzDQOyspXrFKt5k1oLSgoyH628qwR3dMclXVSpj09HGVMSWjhKaq2RFVvpUbjAMz05YGqwN18qBPXVBL1Gy3j+dmsnfFdu8OaUI4Qp6Gf7IlfCU86FAGWm4PnXLviNa4zK2dyRp8tFnc52LmhZ5zn/kLbkLyzFKsA1Q+Xj7w=");

        val neo = manager.createNewNPC(TEAM_MEMBER, "NeoMc_", new Location(spawnLocation.getWorld(), -6.5, 64, -26.5, -30.3f, 0));
        neo.data("quote", "Command blocks make the world spin");
        neo.lineSet(1, ChatColor.YELLOW + "" + net.md_5.bungee.api.ChatColor.BOLD + "NPC " + net.md_5.bungee.api.ChatColor.RESET + org.bukkit.ChatColor.YELLOW + " " + neo.getName());
        neo.setSkin("eyJ0aW1lc3RhbXAiOjE0OTk1ODMwNTU4OTQsInByb2ZpbGVJZCI6IjY3MWRhNjhlNTAwZjRlZDRiYWQ1YmFlYmU2ZjAzMzM3IiwicHJvZmlsZU5hbWUiOiJOZW9NY18iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2JjYzhkZWM5OGRhNDRkOWFkZGE2MjIyMTM1ZDZiY2YzNjVlYWI3OGZhN2RjZTc5Yzg4M2Q1OGZlYWYifSwiQ0FQRSI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQzYTUxZDM0YjA3NmY5YWRhNTU1ZGNhNTYyMjA2YmQ5NDJlNDZhM2M0ZDVmODNjMmMyOWU1YjljM2Q3ZGJjYiJ9fX0=", "bZigeHg+9P0hogAqbuDXOJ8r3nc0ha/bC2/BvS+Hf4/UITmVJsxAvB4YJ9+WniC1rLaYW2SrNL8W8zkUFopF5H51dOVS6G1KNd/Lkj2PL8Wjfbz1aMiyTZf1srwNDnTDj9R7hHqjJDmSCNvxfQnS6isicyPB7XQFYgVzb44nnev1qyejzLkAWy9DVIbwpW7jYjMvoqvRIhNzjUeHsTOBIhoJ1GllBzboKyGkvqfUqzX0vACCUyKtJh645tPWtuuG9BwNkZ+AH38fweShxDd3fRGFE7eI+lvUE8oxCG9MVSWKqng96XFFHL8BQM+qDvD+1myCkalJspBL2FaXz0KBwNYIOeppo6jGfitv3oV02aCbwjbGGPEtxkCYm6Ar7jmHzZHmAEDVTeZEZt1LL7XEH9pgxlaGyPzhQ68yqEqr2wfblbaJIASUc2zR55Ada/2nvq4U8h+PPDTAg5WMMsSb9h+o+/FiwMBEtQXbN4FIasa5H2QEbo6+6E04qqqjTULr02ExU16eSJo1DGlaD/wXpHJCuSh8EmGxQ26Y0K0DONgofBNs1hcSTvIAD7C3fTarUDWrUb/gO5VZZ/RCqjoVQ3ycJSf/c5I7ODD7NEuWiPnHhTGiG+MyhSAIc9b6JGGUVpT1UhgOD+0n+4cSZc0+57Kf7VzBcIsEQRDA4oS37X8=");
    }

    @Override
    public void disable()
    {
        get(Database.class).release();

        spawnLocation.getWorld().getEntities().stream().filter(entity -> entity.hasMetadata("send-server")).forEach(Entity::remove);
    }

    /**
     * Creates an instance of the provided class.
     * <p>
     * If it happens to be a descendant of a {@link Listener}
     * we'll automatically register it with Bukkit as well.
     *
     * @param clazz The class to register
     */
    public <T> T register(final Class<T> clazz)
    {
        try
        {
            T obj = get(clazz);

            // auto-register event listeners
            if (obj instanceof Listener)
                getServer().getPluginManager().registerEvents((Listener) obj, this);

            return obj;
        }
        catch (Exception ex)
        {
            Issues.handle("Class Registration", ex);
        }

        throw new RuntimeException("An unknown issue occurred during injection.");
    }

    @EventHandler ( priority = EventPriority.LOWEST )
    public void movePlayer(PlayerJoinEvent event)
    {
        event.setJoinMessage(null);
        event.getPlayer().teleport(spawnLocation);

        new HotbarItem(event.getPlayer(), new ItemBuilder(COMPASS).name(bold(DARK_GREEN) + "Game Selection").build())
                .location(0)
                .action(this::openNavigationMenu, Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK)
                .add(get(HotbarHandler.class));

        scoreboard.create(event.getPlayer());

        event.getPlayer().sendTitle(BUILDER.subtitle(new ComponentBuilder("Welcome back, ")
                .append(event.getPlayer().getName()).color(DARK_GREEN).append("!", NONE).create()).build());
    }

    @EventHandler
    public void cleanup(PlayerQuitEvent event)
    {
        scoreboard.destroy(event.getPlayer());
    }

    @EventHandler
    public void disallowDrop(PlayerDropItemEvent event)
    {
        event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
    }

    @EventHandler
    public void disallowMovement(InventoryClickEvent event)
    {
        event.setCancelled(event.getWhoClicked().getGameMode() != GameMode.CREATIVE);
    }

    @EventHandler
    public void disallowMovement(PlayerSwapHandItemsEvent event)
    {
        event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event)
    {
        event.setCancelled(event.getEntity().hasMetadata("send-server") || event.getEntityType() == EntityType.PLAYER);
    }

    @EventHandler
    public void removeQuitMessages(PlayerQuitEvent event)
    {
        event.setQuitMessage(null);
    }

    @EventHandler
    public void disallowBreaking(BlockBreakEvent event)
    {
        event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
    }

    @EventHandler
    public void disallowPlacing(BlockPlaceEvent event)
    {
        event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
    }

    @EventHandler
    public void disallowHunger(FoodLevelChangeEvent event)
    {
        event.setCancelled(true);
    }

    @EventHandler
    public void disallowDamage(EntityDamageEvent event)
    {
        if (event.getEntityType() == EntityType.PLAYER)
        {
            event.setCancelled(true);

            if (event.getCause() == EntityDamageEvent.DamageCause.VOID)
            {
                Player player = ((Player) event.getEntity());

                player.setVelocity(new Vector(0, 0, 0));
                player.teleport(spawnLocation);
            }
        }
    }

    @EventHandler
    public void disallowDamage(EntityDamageByEntityEvent event)
    {
        if (event.getEntityType() == EntityType.PLAYER)
            event.setCancelled(true);
    }

    @EventHandler
    public void disallowWeatherUpdate(WeatherChangeEvent event)
    {
        event.setCancelled(event.toWeatherState());
    }

    @EventHandler
    public void npcHandler(PlayerInteractEntityEvent event)
    {
        if (!event.getRightClicked().hasMetadata("send-server"))
            return;

        event.setCancelled(true);
        sendTo(event.getPlayer(), event.getRightClicked().getMetadata("send-server").get(0).asString());
    }

    @EventHandler
    public void keepEntityFromCatchingOnFire(EntityCombustEvent event)
    {
        event.setCancelled(true);
    }

    public void openNavigationMenu(Player player)
    {
        Inventory inventory = Bukkit.createInventory(null, 27, bold(DARK_GREEN) + "Join Game");

        ItemBuilder chunkRunnerItemBuilder = new ItemBuilder(Material.GRASS);
        chunkRunnerItemBuilder.name(Colors.bold(GREEN) + "Chunk Runner");
        chunkRunnerItemBuilder.lore(ChatColor.DARK_GRAY + "Parkour/Challenge", "", ChatColor.GRAY + "Run along a parkour course that", ChatColor.GRAY + "generates in one direction and", ChatColor.GRAY + "crumbles away behind you at an", ChatColor.GRAY + "increasing speed!", "", ChatColor.GRAY + "Developer: " + ChatColor.GOLD + "NeoMc", ChatColor.GRAY + "Credit: " + ChatColor.BLUE + "iWacky & FantomLX", ChatColor.GRAY + "Supports: " + ChatColor.YELLOW + "1 - 24 Players");


        inventory.setItem(11, chunkRunnerItemBuilder.build());
        inventory.setItem(2, glass(13));
        inventory.setItem(20, glass(13));

        ItemBuilder blastOffItemBuilder = new ItemBuilder(Material.FIREBALL);
        blastOffItemBuilder.name(Colors.bold(net.md_5.bungee.api.ChatColor.RED) + "Blast Off");
        blastOffItemBuilder.lore(ChatColor.DARK_GRAY + "Mini-game/PvP", "", ChatColor.GRAY + "Use your arsenal of exploding weapons", ChatColor.GRAY + "and tons of powerups to blast apart", ChatColor.GRAY + "the map! Be the last player standing", ChatColor.GRAY + "to win!", "", ChatColor.GRAY + "Developer: " + ChatColor.GOLD + "NeoMc", ChatColor.GRAY + "Credit: " + ChatColor.BLUE + "iWacky, Falcinspire, Dennisbuilds,", ChatColor.BLUE + "ItsZender, Jayjo, Corey977, JacobRuby,", ChatColor.BLUE + "Team Dracolyte & StainMine", ChatColor.GRAY + "Supports: " + ChatColor.YELLOW + "2 - 12 Players");

        inventory.setItem(13, blastOffItemBuilder.build());
        inventory.setItem(4, glass(1));
        inventory.setItem(22, glass(1));

        ItemBuilder bowplinkoItemBuilder = new ItemBuilder(Material.BOW);
        bowplinkoItemBuilder.name(Colors.bold(net.md_5.bungee.api.ChatColor.DARK_PURPLE) + "Bowplinko");
        bowplinkoItemBuilder.lore(ChatColor.DARK_GRAY + "Mini-game/Archery", "", ChatColor.GRAY + "A fast-paced archery war between", ChatColor.GRAY + "two teams, but with a twist.", ChatColor.GRAY + "If you get hit, you fall down", ChatColor.GRAY + "a plinko board!", "", ChatColor.GRAY + "Developer: " + ChatColor.GOLD + "NeoMc", ChatColor.GRAY + "Credit: " + ChatColor.BLUE + "iWacky", ChatColor.GRAY + "Supports: " + ChatColor.YELLOW + "2 - 24 Players");

        inventory.setItem(15, bowplinkoItemBuilder.build());
        inventory.setItem(6, glass(10));
        inventory.setItem(24, glass(10));

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();

        if (inventory.getName() == null)
            return;

        if (inventory.getName().equals(bold(DARK_GREEN) + "Join Game"))
        {
            event.setCancelled(true);
            player.closeInventory();

            switch (event.getSlot())
            {
                case 11:
                    sendTo(player, "chunkrunner");
                    break;
                case 13:
                    sendTo(player, "blastoff");
                    break;
                case 15:
                    sendTo(player, "bowplinko");
                    break;
            }
        }
    }

    private void sendTo(Player player, String group)
    {
        new QueuePlayersForGroupPayload(group, player.getUniqueId().toString()).publish(get(RedisHandler.class));
    }

    /**
     * Grab the text for the "Join so and so players now" message on the selector.
     *
     * @param group The group this is for
     * @return The text
     */
    private String currentlyPlaying(String group)
    {
        return ChatColor.GREEN + "" + ChatColor.UNDERLINE + "Join" + ChatColor.GOLD + " 0" + ChatColor.GREEN + " people now!";
    }

    /**
     * @param data The color
     * @return The glass
     */
    private static ItemStack glass(int data)
    {
        return new ItemBuilder(Material.STAINED_GLASS_PANE).byteData((byte) data).name(" ").build();
    }

}