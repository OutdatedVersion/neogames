package net.neogamesmc.lobby;

import com.destroystokyo.paper.Title;
import com.google.inject.Injector;
import lombok.val;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.minecraft.server.v1_12_R1.EnumItemSlot;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.payload.QueuePlayersForGroupPayload;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.core.bukkit.Plugin;
import net.neogamesmc.core.command.api.CommandHandler;
import net.neogamesmc.core.hotbar.HotbarHandler;
import net.neogamesmc.core.hotbar.HotbarItem;
import net.neogamesmc.core.inventory.ItemBuilder;
import net.neogamesmc.core.npc.NPCManager;
import net.neogamesmc.core.scoreboard.PlayerSidebarManager;
import net.neogamesmc.core.message.Colors;
import net.neogamesmc.lobby.news.News;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import static net.md_5.bungee.api.ChatColor.*;
import static net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention.NONE;
import static net.neogamesmc.core.npc.NPCType.GO_TO_GAME;
import static net.neogamesmc.core.npc.NPCType.TEAM_MEMBER;
import static net.neogamesmc.core.message.Colors.bold;
import static org.bukkit.Material.BOW;
import static org.bukkit.Material.COMPASS;
import static org.bukkit.Material.NETHER_STAR;

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
        blastoff.skinSet("eyJ0aW1lc3RhbXAiOjE0OTk3NTU4NjgxMzcsInByb2ZpbGVJZCI6ImIwZDczMmZlMDBmNzQwN2U5ZTdmNzQ2MzAxY2Q5OGNhIiwicHJvZmlsZU5hbWUiOiJPUHBscyIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWJlZDljZmEyZWVkMWMzMzUyZWZkZWVmNjZhNjRjOTk1YmFiYmVlYTZjZDNiMzBjZTk3ZmQ0MzkxMjRlMSJ9fX0=", "T34I1QSSk/KvMqM0VsD/8fSwdjMOF/FWMAiLT16kClbyWpohBfNT6L1tblI3/0BkD038cB14c5ib6Wy+jS+swKkS9Ko70YRgiyBuqwduhniY8UWqOLu7W8Nsjl3acNi9uvN0V4QZw90DokG1k/FgstQzNjlJjKshQfo4sothzvJmbLnX1szumFY8mgtWRZZmDwihpzppkgcvifi4Txv29oapHqUXyjBGbpioY2Zr+w80x+i8sn2B6vIm3/cnpXDSCm8nffETfOGZY4UvTvRy4ihg2+geBOUOUSABuSrpwZ3SfwmKdMw2uMLu4eT9thfcde7l4Q/gVitfgoVYWnWkGnVd8/ifFr9nUrhxL01EUwlA/WETJJT+DOqI/W6DUqLV1x3Rw06A7vHvX/9qGBnlXXHS5wgKu1eMop3qXKJ2D/DO/yCuRp0IRYbIlapv1w88h+wujEKakBl3zG0WNzpfCXVDkEZB00IngJzt5v6ELWnVdiqr6Ake6BEJnzWrv+XSfnVua+y5IBroTo5F3ZascqLFmjkRfZgjFv2cbeKimaDFhDq6SJeJYamaFXf8wQwlSotAzBzsXcesAluFokJe32DCWBwKAO9gfVmaJorYunm2L9vs9wSXL5C6mq0SltfM+8G7IE4MyY0Qq2OUYn5URxqV+Tv5LsDwF1N3j/n83tE=");
        blastoff.lineSet(1, GREEN + "Join " + bold(RED) + "Blast Off");
        // blastoff.lineSet(2, GRAY + "No One Is Playing");
        blastoff.data("group", "blastoff");

        val chunkRunner = manager.createNewNPC(GO_TO_GAME, "Chunk Runner", new Location(lobby, -1.147, 63.06250, 2.5, -80, 0));
        chunkRunner.skinSet("eyJ0aW1lc3RhbXAiOjE0OTk3NTU5MTU4NzcsInByb2ZpbGVJZCI6IjQzYTgzNzNkNjQyOTQ1MTBhOWFhYjMwZjViM2NlYmIzIiwicHJvZmlsZU5hbWUiOiJTa3VsbENsaWVudFNraW42Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8xZWM1ZGFhNWM4NjM5YjI0MjIyMTIyMjlhMjk1MTY4MjU0NTJjYjUwMzc2YTVkOWYwNGFjMTYzYTU2ZGIxYzcifX19", "Z7Yz+eTbNRwsmG4b0XStdaYpM6RIUi/v3VfXY2g+DwingtdUDadwwl4qXRzjsvGmXrYbqWUZtc5bbh8cPTSkL/7zU4bzbmAcLYOQGXZA4tmRZjPMxMdDs6KkRKMbI24gciOmKC77jKerOXT70E0s2auSD9gnyubWRRWxE+hshoGVs5jISyhMxi5Vv09kmsAvecEb71wrftE4L1RNZz5blgNBu8nE/ZNQ9MSOgIWHybA4Aosj2jPbdAwdy+hHrISuaDw5J9PrYljUzy3Vw78r+Thg/XuawRLcXTgZjAvyh93WSI1wn8Lb1uCF7Nty9ypVtzqRPeWctJcIpvkj5LiUJmnGnGqEe6zdScF3TOH7z5YcIytZz9+DXsBMyBidSVljxmk2unEDE3oiNJklsPTZ5/m/iC4p1DDD+CvC5jzbhyaMnn40e7JiU+4kisURvNIBpUYdcjxNHFT2iEJfeGvWI62p0L0BZl3ODvdRuNO6YxoHzY/C2HXA4Qh15WB7tfLjffcHGe16SWMsd7YKNe/Zb45+eYFldI81kOvczbRhlX9qp4n3cJxJ5fiFQiz3ZtChGj3DlrcWN5/fTkCQKP8zIEU/M9aqzdqF/iZyt1e+/wK/GmTPVRUKHBuW/DRzffUks1DmP8Hb9n/yjoNCDl9iILcbYkzNX4L5m8s7FZ9gyKw=");
        chunkRunner.lineSet(1, GREEN + "Join " + bold(DARK_GREEN) + "Chunk Runner");
        // chunkRunner.lineSet(2, GRAY + "No One Is Playing");
        chunkRunner.data("group", "chunkrunner");

        val bowplinko = manager.createNewNPC(GO_TO_GAME, "Bowplinko", new Location(lobby, -0.5, 63.06, -2.5, -48.3f, 0f));
        bowplinko.equipment.put(EnumItemSlot.MAINHAND, new ItemStack(BOW));
        bowplinko.skinSet("eyJ0aW1lc3RhbXAiOjE0OTk3NTU5NDcxMjEsInByb2ZpbGVJZCI6ImRhNzQ2NWVkMjljYjRkZTA5MzRkOTIwMTc0NDkxMzU1IiwicHJvZmlsZU5hbWUiOiJJc2F5bGEiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ViNDljOWNjM2U5YmNhMjljZWMwMWQwODMyOWMyYTM3ZTVlYmYzY2ZhOWJiMjgzMGEzMzJiMjczNzVhOSJ9fX0=", "gm5IgpFXiSzOpQPedsEU3E0r1E15w8MmBUY4oX1tb2B6J7xPwCBHTuorBe+9RQAzY1yTXaKN1dp+xBBzAgfQ+o6eQkEct/A3r/o0tA794BmIWf7WW7HP+euT8sadvOXZRsS8/v78Rbh31jPNUCtpNjCX+sYP0aBMc+M63h864Ivnkyjb6xJsWTWC/ptBWnautDp3YTdV/YIxH53Oi+XttpiFZK9b9+rRQoo4ZG4KOKkmXHD7/fLbglu9OaTLFcEdExkiocz6jDnrrn1360n5dembowSmc3GJkGibkcIjv/Fw3phIJZkpcSL5Q0hHg9Lh1UMI6WVJaV1i9LIDT5BR03Q5cPvkcis4w6o1nZhlP3ykXxheMHpzSn9KkKh5GPsqLu7q4q8eDxKj7ISZkv9HoSqz52/9fhVK76B0Wy4fyPa77S8bUGraPOnaQLTYDI07ENC6T5Bwq/4fEkqW20gemk5jZmIzVq4jWXMsnHbPJ5q6OMDkDnjYWAf93Gcu2jzEkRaYHQ5xAmts3IjGoQm0XZwpfGVfXaKfCCgJe2ePe0vcx0VVE44ODaSbDzbQZPOR+MxcHM0wTe2+0iuJdC65pnB3LsAbSoLDY7MSeg/vcwMhf1gIgeV8cQ55PYynnSFoIjyskK6w/8ourYAacr+aLE0pspyG7Alzl1W93lUWfLM=");
        bowplinko.lineSet(1, GREEN + "Join " + bold(DARK_PURPLE) + "Bowplinko");
        // bowplinko.lineSet(2, GRAY + "No One Is Playing");
        bowplinko.data("group", "bowplinko");

        val mariokart = manager.createNewNPC(GO_TO_GAME, "Mario Kart", new Location(lobby, 3.67, 63, 5, -106.4f, -2.6f));
        mariokart.skinSet("eyJ0aW1lc3RhbXAiOjE1MDEwNDc3MTc2NDksInByb2ZpbGVJZCI6IjNkOWNmOTZiN2MyNzRiZWVhZDFiOWQ0NTM3NTRjYjc2IiwicHJvZmlsZU5hbWUiOiJOaWtha2EiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzUzZmU1MzE0NzgzY2IyZjg5ZGRjZWE0NDk2MjI5ZWVmNGViNDg2ODYyODJjZDEzOTNkZWZlN2MxOTQ5In19fQ==", "MHBwNl74jscR0q+ibHi1wYzAKJG7poZtWtZUJryoVsCRjrXTGQGH9XtbwoTJaHRB+dtVrIa2AYbeuj9vrxhq5DifU58F2Fx+pmSGenJ1TWF4/9B+NbKyYnUtoQlAkKQTOY+8A6XuhmpeSAQiV4lnkGsFmUyrhk66u14VBkVdrPY+3t6dINYB5aTYJCrxo7mVP8EiJPM9lc29Bu6gQx8fUSvhvLTTohYn2D6Qd2w+mYEJQHVizcxiSi/OauJ870kpzIbtt8iJEJ+JdN8TJvsd3e2hwiXYgLQLklSzRnrZdQZBUJSwOmDI+44830efJSLT4Pd4J3bHzIfh+2wbAlQXNLDrqb0Scbuf7IGBh/0+Znw9xD8wGas4U9SunL7nEV0juSunhhJW5P1HXgRSSVt9qqD0kycQ9saX5TUpWMfaT4H1NklIlBuQL6LIogA3Xmh89oEqv3ElnR9e3R5yVSR5AR/3IPeIyCb5lvRzlsRP6pc0nwXAsw1mWvDKBVklOe2mjLEdlLYsEkjDhAibU2HR4EUhcNwf/sm9VM+eyBN1UusWURsaTig9XYnjGqUyCHw2tj7E5KpD5ZK7HnxdnKMWKL3o8jqVuIsbk50SWlEj3Tve6UPj7m3pmZ+0wWhbq685hDf4+au7UPWsjQEQj2MWaLDhJRlTDmPJFFiAqkfy7/g=");
        mariokart.lineSet(1, GREEN + "Join " + bold(DARK_AQUA) + "Mario Kart");
        // bowplinko.lineSet(2, GRAY + "No One Is Playing");
        mariokart.data("group", "mariokart");

        //TODO Add vanilla bedwarz

        // TEAM

        val ben = manager.createNewNPC(TEAM_MEMBER, "OutdatedVersion", new Location(lobby, 8.51, 63, -44.54, -29.1f, 19.8f));
        ben.data("quote", "ice is chewy");
        ben.lineSet(1, ChatColor.YELLOW + "" + net.md_5.bungee.api.ChatColor.BOLD + "NPC " + net.md_5.bungee.api.ChatColor.RESET + org.bukkit.ChatColor.YELLOW + " " + ben.getName());
        ben.skinSet("eyJ0aW1lc3RhbXAiOjE0OTk2Njk5NjY0OTIsInByb2ZpbGVJZCI6IjAzYzMzN2NkN2JlMDQ2OTRiOWIwZTJmZDAzZjU3MjU4IiwicHJvZmlsZU5hbWUiOiJPdXRkYXRlZFZlcnNpb24iLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmM0NmExYjU5ZTNhYmU0YWMyNDdjYWY5ZWE1ZGQzOWY4ZmZiOWQ1NTBkOGRkMzY0NjEwNWU5NGI5OTljIn0sIkNBUEUiOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81YzNjYTdlZTJhNDk4ZjFiNWQyNThkNWZhOTI3ZTYzZTQzMzE0M2FkZDU1MzhjZjYzYjZhOWI3OGFlNzM1In19fQ==", "nC99fqmbJe8ZuI1Pz4SN6u5bdBW1NEiAsJeZGLR4EjUA43tslrz7fDQmV2wIZRMVpTMM2WH+qd8pp6nmJNp7M7420NmhAtATGdIirM5srN8WBnbtRByJ13m2nbOCxN1p5kziFUvY2KlsC5Sk/RGrIz98S0ZH2jt+vVWPbxkNX1fRNIQmyPePd4Bv18UFANBowK/pbb17cb7DTDPp8MlunbLpqkRWmSLrlclf1jfSR5CpgkMD0jyPQvVxDdEy7RcLrBQK7XT5syjuYat84nFCXEGBMlAfXdTB1D+a+iW2Lt1YZCYR7EuXgO6JHnWzqi0xxC3xJ5CJGFbz2HdYGm9llQuRxgJsJ2KfUv+LoeNDbUXsvjguSdxgiAGbK0uaxGVDG0vnLN7rCVunwUR4HTUrOCGG1mBgjGAMzE5lz2+aSCmb3hpqDpHMSb5I02pefDoM1N60C+WcMTbTLoljvNRPeHQ6ZjFz1MW9gymKTXHlRgy4bAhkzqZywBs2dXnlqL1dNBGTYH4FoGctTZkxn5X8nGaIgcGAxwbjOMaHC5LjkcrdQD0RrebVC5M2wI6AXkOi5rNQqR2aQzA+TeP54sYNGhanN/PUORtYIAcU+LX1J5wVHrq07uxWcnj0s+nUQDCoHo5eT2UELOwzKq0LSo36BxW9O/gvNg9aBohTegvwRUQ=");

        val nokoa = manager.createNewNPC(TEAM_MEMBER, "Nokoa", new Location(lobby, 1.7, 63, -38.16, -106f, 5.7f));
        nokoa.data("quote", "I like trains! Do you like trains?");
        nokoa.lineSet(1, ChatColor.YELLOW + "" + net.md_5.bungee.api.ChatColor.BOLD + "NPC " + net.md_5.bungee.api.ChatColor.RESET + org.bukkit.ChatColor.YELLOW + " " + nokoa.getName());
        nokoa.skinSet("eyJ0aW1lc3RhbXAiOjE0OTk1ODMxNDIzNjUsInByb2ZpbGVJZCI6IjQ0MTJiM2I0ZTAwMDQ4OTViZTFlMThiNThkNDJjYzFkIiwicHJvZmlsZU5hbWUiOiJOb2tvYSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTMzNzYwYWY1ZWNkZDIwODJlMTk0ZTc0ODYyMTg4ZmE3MjI1Yjc3M2ViYjNkN2ZkNmRiNDQyZmZiYWJiYWUifX19", "ABh1gNaUFCnVtLtb9JObQtJ9ctRMhbT7b6AEcubU3q7y36N+eij3Q+XGuHfgv5e03lgd1lX3nNxOrxbt5NvBFh7ZwEDUqlaQMFqdMskDPqTRKR89CEd6vMzXgIvJQF+eZ/lNlHXCurSn8MuP1afhPlvm8bu4W/W1rk/XHCDmsKYkssChNMN9pvLxnxS3Mx/jcuMuJJfjgjxCWl2IrbtiOjrtXwMVqvhQy37N2eGIfC1FPKzdp5G8sIdrd4+/0RzrkC+jTA/8Uirdm8FZuu/xIIq9yB72Sw1EjKUl9JSJkn5OEOmtquVVCpoYavoNdhpRhWkUbMejdhMTmkzFQB2fsIgKuKXGXSCTIMuauZ3hRIVbCKWsvZIyelmtwzd5pH9ml/7GQvlOzQ02jMwagFNg7ydUBpTf4mIOT3sM5xEwV1Uyfg8aSIDauRTA1E76JuI9xKxOCTThg4GSylZyhiKiiAtezTypc/bIHam5nXlLFAg8H8zdWH5Irluc36eSOfhJlKvHzDQOyspXrFKt5k1oLSgoyH628qwR3dMclXVSpj09HGVMSWjhKaq2RFVvpUbjAMz05YGqwN18qBPXVBL1Gy3j+dmsnfFdu8OaUI4Qp6Gf7IlfCU86FAGWm4PnXLviNa4zK2dyRp8tFnc52LmhZ5zn/kLbkLyzFKsA1Q+Xj7w=");

        val neo = manager.createNewNPC(TEAM_MEMBER, "NeoMc_", new Location(lobby, -6.5, 64, -26.5, -30.3f, 0));
        neo.data("quote", "Command blocks make the world spin");
        neo.lineSet(1, ChatColor.YELLOW + "" + net.md_5.bungee.api.ChatColor.BOLD + "NPC " + net.md_5.bungee.api.ChatColor.RESET + org.bukkit.ChatColor.YELLOW + " " + neo.getName());
        neo.skinSet("eyJ0aW1lc3RhbXAiOjE0OTk1ODMwNTU4OTQsInByb2ZpbGVJZCI6IjY3MWRhNjhlNTAwZjRlZDRiYWQ1YmFlYmU2ZjAzMzM3IiwicHJvZmlsZU5hbWUiOiJOZW9NY18iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2JjYzhkZWM5OGRhNDRkOWFkZGE2MjIyMTM1ZDZiY2YzNjVlYWI3OGZhN2RjZTc5Yzg4M2Q1OGZlYWYifSwiQ0FQRSI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQzYTUxZDM0YjA3NmY5YWRhNTU1ZGNhNTYyMjA2YmQ5NDJlNDZhM2M0ZDVmODNjMmMyOWU1YjljM2Q3ZGJjYiJ9fX0=", "bZigeHg+9P0hogAqbuDXOJ8r3nc0ha/bC2/BvS+Hf4/UITmVJsxAvB4YJ9+WniC1rLaYW2SrNL8W8zkUFopF5H51dOVS6G1KNd/Lkj2PL8Wjfbz1aMiyTZf1srwNDnTDj9R7hHqjJDmSCNvxfQnS6isicyPB7XQFYgVzb44nnev1qyejzLkAWy9DVIbwpW7jYjMvoqvRIhNzjUeHsTOBIhoJ1GllBzboKyGkvqfUqzX0vACCUyKtJh645tPWtuuG9BwNkZ+AH38fweShxDd3fRGFE7eI+lvUE8oxCG9MVSWKqng96XFFHL8BQM+qDvD+1myCkalJspBL2FaXz0KBwNYIOeppo6jGfitv3oV02aCbwjbGGPEtxkCYm6Ar7jmHzZHmAEDVTeZEZt1LL7XEH9pgxlaGyPzhQ68yqEqr2wfblbaJIASUc2zR55Ada/2nvq4U8h+PPDTAg5WMMsSb9h+o+/FiwMBEtQXbN4FIasa5H2QEbo6+6E04qqqjTULr02ExU16eSJo1DGlaD/wXpHJCuSh8EmGxQ26Y0K0DONgofBNs1hcSTvIAD7C3fTarUDWrUb/gO5VZZ/RCqjoVQ3ycJSf/c5I7ODD7NEuWiPnHhTGiG+MyhSAIc9b6JGGUVpT1UhgOD+0n+4cSZc0+57Kf7VzBcIsEQRDA4oS37X8=");

        val dejay = manager.createNewNPC(TEAM_MEMBER, "DeJay6424", new Location(lobby, -4.5, 70, 45.5, 200f, 10f));
        dejay.data("quote", "I help make things happen!");
        dejay.lineSet(1, ChatColor.YELLOW + "" + net.md_5.bungee.api.ChatColor.BOLD + "NPC " + net.md_5.bungee.api.ChatColor.RESET + org.bukkit.ChatColor.YELLOW + " " + dejay.getName());
        dejay.skinSet("eyJ0aW1lc3RhbXAiOjE1MDI1ODQzOTE2NTMsInByb2ZpbGVJZCI6IjlmMTViZjM4OTZkZDRkMGNiYTNiY2M2ZTQ0NzQwMmVjIiwicHJvZmlsZU5hbWUiOiJEZUpheTY0MjQiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2RhMGU5M2I5ZTRmMzZmNjI0ZDRlNzJkZWYwMTEzNjZlYWM3NWIxYjNjMWRjODA1Y2YwYWU0M2Q5MDA2MSJ9fX0=", "JWCGgfVSD/NpliHn0cSs3eNOYbFT4+mJnblSxNSKkiUyYMELBmhXYVyXzBZXnO75FDhSODbcgCiyvRcqOLszAQtEkFI0UmqgBLPauNVjAe57g7MnGyc4kys+svYuVytwfeY+EDxj9sKnQdWUYN5t35ccZ7HCFSK85mhEJM86qm78Ae9GKSGOdddkWEVHwM+aqCVSOu6zUb+ffUIgkq2++81h8cAPmH2P9ESKD7Mcs1T+dimaOcEfT7mzsi4vFiLu1oQNMAmDnGAOoFxAYzRvuod2YITHRHsrp9GUuHV8WGYUWuUAf5apj5OYVgjAnJa8xMfNm+cRz82yh2AJMNyQ0RbVZr6oAQ3+joRxLDqxs+snuGaDL6haDNl6I+oSwz1DkzxT7Dw8ztXjVqngutt9HUu0F3uxqIaN/MpSL5aKPPeu7l5huYwS7EZ8cgLVBiKQoLeRPfqD1R+2PJR3M2IUwb3LI+g/VWMzPLuvYG6CsX6cC6nYdI3vyRZGoBsknIu30kdJTZXw94gCSEsBvSbft0y3VhSe4qYlEkWLAUY39rOi6Gh/+3WGxzH++1U63Wc7h7GqSceuJZrk45zc6l4WFvmbeRkbF5lpyHK4zQ7cYQkmP5lvSModuazzjqS8wLv6b23Lr51IQxeizw9aJgd6rNaLbWgKKNmBCpSYCUaHvyk=");

        val jp = manager.createNewNPC(TEAM_MEMBER, "Jp78", new Location(lobby,-18.666,65,37,-178.3f,5.4f));
        jp.data("quote", "eeks dee");
        jp.lineSet(1, ChatColor.GREEN + "" + ChatColor.BOLD + "NPC" + ChatColor.RESET + jp.getName());
        jp.skinSet("eyJ0aW1lc3RhbXAiOjE1MDI4NTYxMjEwMTQsInByb2ZpbGVJZCI6IjBkODg3YmZhZDdlNzQxMDY4ZWU3MDZkNjEzMzg0ZDUwIiwicHJvZmlsZU5hbWUiOiJKcDc4Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS80NTNiNWJhODUyZTg0ZWI5YTY0NDM1OGM2MmMzYzllOTZmZGYyNTUzOGI5ZTYyM2U3MWJjNzE3M2JlNzZiIn0sIkNBUEUiOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81YzNjYTdlZTJhNDk4ZjFiNWQyNThkNWZhOTI3ZTYzZTQzMzE0M2FkZDU1MzhjZjYzYjZhOWI3OGFlNzM1In19fQ==","Uj9FyrOtupKjFLrj/35VImuGrooXwsOBCm5TUFIG/1VuBVD3SM6QWZAlK6arFUnSWoVUwl1cTLAEnHk1p7oldmdWTr+wbF10UuzPSfGW9o0LSYxGE9HMDV6QHkmdlkz3ANVHosVWKQndhu9ALMs+Xy4n9aTmBEivn6cvyfmty12/v8LVsoq4/sIhjUgxGY6wHYZs9mgWhDiLMf7nGHqiBi8+nprqHj4tvLANWcOYaqv1OojtPwCwKzT+2lgcJGDg+0BOihZRZc8Vy0fuPnLe8zHG77VgRTnlAXZAqBdRv/Qqh78dye11vYIkYvSEi1O32nPppwqUp54JEjfDXY9QueyNPAMouhXyxYjuyUKHZ5UFi2I2yZXyNrFZ3oPjLlQlBgVpRhuWJLxIxcPzbeHv8rL6BBibnmratXhtjViWUL18PLRuch7AQ5jMqQ9mVjEItv4NDem2ZJC6tYZ2+RoukWpLblfOtLiAZxYKXdzQFAPuP+82kNFKE4BKvk6hvAet0k3APwxa6VH1qirO8SfpRX+1CV0QC5spsFRuqqHIg/nCBaM5KBIZJAHdSDhBz9F0XUfR83GnbfsSOmvo78QO/VyXZoCByj2FUJTJ0lBmZJeKtGQ4Dm0cO93BAL1z7THKOgcjSw7KbgTvvG5bhHmN+Uup1iimk0tAf6KksM46Fn4=");

        // EVENT
        /*val event = manager.createNewNPC(GO_TO_GAME, "Event", new Location(lobby, 10.5, 63, -3.5, 0.1f, -7.3f));
        event.skinSet("eyJ0aW1lc3RhbXAiOjE1MDAwNTUyNzM1NDQsInByb2ZpbGVJZCI6IjIzZjFhNTlmNDY5YjQzZGRiZGI1MzdiZmVjMTA0NzFmIiwicHJvZmlsZU5hbWUiOiIyODA3Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jYzU4YmQ1NzVmODMxZjhmM2FlNGI2YTZkNzNiYzk0YWYyNjI0NDRmZmViOWZhZDBhZjg0ZWVkOGM0NmRmIn19fQ==", "IMBUkPJFlW92I6E0Y/rauwjoEpC3jPoO2XilHT0xpaNGAItGxeMkDwZ9oMQCyfGqhRExLz79mYjdUS0QOQN3P9HYw4hrghQR/YO41tBvlkxlC8QksKjGAeKJPD7Ze9rvboXnPOvnYzkfdp818aylmRbjghEeyIPKtnjet3zckH5gio3ASSyUysa/eonU1t+9RwghIT99HdIkcX7SbGM1VafrKuK25wStmmMZ8GJzvfNxHO3DJaSx4e4nym+2VEEiCfptYydjSuYb8yUBJa7TtBVmZHvxmYmD9AuEObO6sRHvN3uzdPVy0pG71dPdsrsUlE8+2tcZ5H/Rv8bsaj0pB1qzQcpfUr7NA7IFY2RDrUhcm+IPCOW68NFEVP/sx31JJNv04F6TvDMbcs1lon4OGE7MNuf0oc+4sGSsrxvph0rRq4j1jpBhKyVm0yaVjuXkiJBI3CJu67uQIdS52Y4dpOs8mKzk2+zusFkufjVAgjuqKTDw/rVuJY8UBJrXMYqHpzA7xYOz2+aJbusQpvhy1eJMu0aJeT0Tpcj6e7eAw0Mq0/bvkbgOpbO9sFKau56ZeJHWeNJZvhd4pxIsQwvWqDElJ7CvcUBYRgq1blUsd1i4HSB7+/0AjYbZlL5jI3GJgLAQBhj4VsoLhxXwLJ+nJv+WTaLeHjASUL/xFnTZReM=");
        event.lineSet(1, GREEN + "Join " + bold(GOLD) + "WEEKEND EVENT");
        event.lineSet(2, AQUA + "Shulker Rush" + ChatColor.GRAY + " by The Minemakers");
        event.lineSet(3, GRAY + "8/4 to 8/6");
        event.data("group", "shulkerrush");*/
    }

    @Override
    public void disable()
    {
        database.release();
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

        event.getPlayer().sendTitle(BUILDER.subtitle(
                new ComponentBuilder("Welcome back, ")
                             .append(event.getPlayer().getName()).color(DARK_GREEN)
                             .append("!", NONE)
                             .create()
        ).build());
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


        inventory.setItem(10, chunkRunnerItemBuilder.build());
        inventory.setItem(1, glass(13));
        inventory.setItem(19, glass(13));

        ItemBuilder blastOffItemBuilder = new ItemBuilder(Material.FIREBALL);
        blastOffItemBuilder.name(Colors.bold(net.md_5.bungee.api.ChatColor.RED) + "Blast Off");
        blastOffItemBuilder.lore(ChatColor.DARK_GRAY + "Mini-game/PvP", "", ChatColor.GRAY + "Use your arsenal of exploding weapons", ChatColor.GRAY + "and tons of powerups to blast apart", ChatColor.GRAY + "the map! Be the last player standing", ChatColor.GRAY + "to win!", "", ChatColor.GRAY + "Developer: " + ChatColor.GOLD + "NeoMc", ChatColor.GRAY + "Credit: " + ChatColor.BLUE + "iWacky, Falcinspire, Dennisbuilds,", ChatColor.BLUE + "ItsZender, Jayjo, Corey977, JacobRuby,", ChatColor.BLUE + "Team Dracolyte & StainMine", ChatColor.GRAY + "Supports: " + ChatColor.YELLOW + "2 - 12 Players");

        inventory.setItem(12, blastOffItemBuilder.build());
        inventory.setItem(3, glass(1));
        inventory.setItem(21, glass(1));

        ItemBuilder bowplinkoItemBuilder = new ItemBuilder(Material.BOW);
        bowplinkoItemBuilder.name(Colors.bold(net.md_5.bungee.api.ChatColor.DARK_PURPLE) + "Bowplinko");
        bowplinkoItemBuilder.lore(ChatColor.DARK_GRAY + "Mini-game/Archery", "", ChatColor.GRAY + "A fast-paced archery war between", ChatColor.GRAY + "two teams, but with a twist.", ChatColor.GRAY + "If you get hit, you fall down", ChatColor.GRAY + "a plinko board!", "", ChatColor.GRAY + "Developer: " + ChatColor.GOLD + "NeoMc", ChatColor.GRAY + "Credit: " + ChatColor.BLUE + "iWacky", ChatColor.GRAY + "Supports: " + ChatColor.YELLOW + "2 - 24 Players");

        inventory.setItem(14, bowplinkoItemBuilder.build());
        inventory.setItem(5, glass(10));
        inventory.setItem(23, glass(10));

        ItemBuilder mariokart = new ItemBuilder(NETHER_STAR);
        mariokart.name(Colors.bold(ChatColor.DARK_AQUA) + "MarioKart");
        mariokart.lore(
                ChatColor.DARK_GRAY + "Race/Challenge",
                "",
                ChatColor.GRAY + "Hit the ground running in this epic",
                ChatColor.GRAY + "remake of MarioKart! Manuever around",
                ChatColor.GRAY + "bends, sidestep obstacles, and use",
                ChatColor.GRAY + "classic MarioKart powerups to help",
                ChatColor.GRAY + "you win! This map cycles between",
                ChatColor.GRAY + "\"Balloon Battle\" and \"Race\" modes!",
                "",
                ChatColor.GRAY + "Developer: " + ChatColor.DARK_AQUA + "Flamingosaurus",
                ChatColor.GRAY + "Credit: " + ChatColor.BLUE + "VioletRose",
                ChatColor.GRAY + "Supports: " + ChatColor.YELLOW + "2 - 24 Players"
        );

        inventory.setItem(16, mariokart.build());
        inventory.setItem(7, glass(14));
        inventory.setItem(25, glass(14));
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
                case 10:
                    sendTo(player, "chunkrunner");
                    break;

                case 12:
                    sendTo(player, "blastoff");
                    break;

                case 14:
                    sendTo(player, "bowplinko");
                    break;

                case 16:
                    sendTo(player, "mariokart");
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
