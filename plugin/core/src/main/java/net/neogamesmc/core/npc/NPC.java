package net.neogamesmc.core.npc;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.val;
import net.minecraft.server.v1_11_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.CraftServer;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_11_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Handles, stores and manipulates a non-playable character
 */
public class NPC extends Reflections
{

    private int entityID;
    private Location location;
    @Getter private GameProfile gameProfile;
    private List<Player> viewers = new ArrayList<>();

    public Map<EnumItemSlot, org.bukkit.inventory.ItemStack> equipment = Maps.newHashMap();

    /**
     * The role this NPC serves.
     */
    @Getter private NPCType type;

    /**
     * Data for this NPC.
     */
    private Map<String, String> data;

    /**
     * Data for the lore lines of this NPC.
     * <p>
     * The map is laid out as: {@code Position -> Pair(Entity ID, Value, Entity)}.
     */
    private TIntObjectHashMap<LoreLineData> loreData = new TIntObjectHashMap<>();

    private List<Player> visibleFor = Lists.newArrayList();

    private String name;

    NPC(NPCType type, String name, Location location)
    {
        this.type = type;
        this.name = name;
        this.entityID = EntityIDs.get().assignID();
        this.location = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        val uuid = UUID.randomUUID();
        gameProfile = new GameProfile(uuid, uuid.toString().substring(0, 8));
    }

    /**
     * Set a data value
     *
     * @param key Key to store
     * @param val Value to set
     */
    public void data(String key, String val)
    {
        if (data == null)
            data = Maps.newHashMap();

        data.put(key, val);
    }

    /**
     * Grab data at the provided key.
     *
     * @param key The key
     * @return The data (value)
     */
    public String data(String key)
    {
        checkNotNull(data, "Make sure you set data before retrieving.");
        return data.get(key);
    }

    boolean canPlayerSee(Player player)
    {
        return visibleFor.contains(player);
    }

    /**
     * Specify that the player parameter is within line of sight of this NPC
     *
     * @param player
     */
    void addPlayerToVisibleList(Player player)
    {
        if (!visibleFor.contains(player))
            visibleFor.add(player);
    }

    /**
     * Specify that the player parameter can no longer see this NPC.
     * This will ensure that the NPC is respawned.
     *
     * @param player
     *
     * @return
     */
    boolean removePlayerFromVisibleList(Player player)
    {
        return visibleFor.remove(player);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Sets the none playable character's skin using base 64 textures.
     * Applies the textures to the game profile of the NPC
     *
     * @param val the skin in base 64
     * @param sig the signature of the skin in base 64
     */
    public void skinSet(String val, String sig)
    {
        gameProfile.getProperties().put("textures", new Property("textures", val, sig));
    }

    /**
     * Spawns a none playable character which can be viewed by the player parameter
     * Also spawns entity's equipment, and name tags.
     *
     * @param player
     */
    void spawn(Player player)
    {
        // Add player to viewers list so it can be respawned for the viewers when they go out of sight.
        if (!viewers.contains(player))
            viewers.add(player);

        // Creating the NPC using the EntityPlayer because I couldn't figure out what datawatcher values I need to use.
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();

        EntityPlayer npc;
        npc = new EntityPlayer(nmsServer, nmsWorld, gameProfile, new PlayerInteractManager(nmsWorld));
        npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn(npc);
        setValue(packet, "a", entityID);
        setValue(packet, "b", gameProfile.getId());
        setValue(packet, "c", location.getX());
        setValue(packet, "d", location.getY());
        setValue(packet, "e", location.getZ());
        setValue(packet, "f", getFixRotation(location.getYaw()));
        setValue(packet, "g", getFixRotation(location.getPitch()));

        npc.getDataWatcher().set(new DataWatcherObject<>(13, DataWatcherRegistry.a), (byte) 0xFF);


        // Spawn the NPC and its data
        sendNpcInfo(player);
        sendPacket(packet, player);

        // Make the player look at the right direction
        headRotation(player, location.getYaw(), location.getPitch());

        // Apply the items the player is wearing/ holding
        for (Map.Entry<EnumItemSlot, org.bukkit.inventory.ItemStack> entry : equipment.entrySet())
        {
            val stack = CraftItemStack.asNMSCopy(entry.getValue());

            equip(entry.getKey(), stack);
        }

         // Adds the nametags above NPC
        loreData.forEachEntry((position, data) ->
        {
            spawnArmorStand(data.id, player, data.text, true, position);
            return true;
        });
    }

    /**
     * Returns a list of players who can view the NPC.
     * This list contains all players are are and can be viewing the NPC regardless of distance
     *
     * @return
     */
    List<Player> getViewers()
    {
        return viewers;
    }

    /**
     * Equips this NPC with items/ armor
     *
     * @param slot      The item slot of the NPC which specifies where you are trying to apply the item to.
     * @param itemstack The item to be applied for the NPC.
     */
    void equip(EnumItemSlot slot, ItemStack itemstack)
    {
        PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment();
        setValue(packet, "a", entityID);
        setValue(packet, "b", slot);
        setValue(packet, "c", itemstack);
        sendPacket(packet);
    }

    /**
     * Grab the value of the stuff at the position.
     *
     * @param position The position
     * @return The text
     */
    public String lineText(int position)
    {
        val fetch = loreData.get(position);

        return fetch == null ? null : fetch.text;
    }

    /**
     * Sets the text of the NPC nametag at the provided position from 1 to 5
     * Does NOT automatically reapply the nametag to the NPC if it is currently spawned.
     *
     * @param position
     * @param text
     */
    public void lineSet(int position, String text)
    {
        if (position <= 0 || position > 5)
            throw new IllegalArgumentException("Index must be greater than 0 and less than 6");

        if (text.length() > 40)
            throw new IllegalArgumentException("Text cannot be longer than 40 characters");


        int id;
        try
        {
            id = loreData.get(entityID).id;
        }
        catch (Exception e)
        {
            id = EntityIDs.get().assignID();
        }

        loreData.put(position, new LoreLineData(id, null, text));
    }

    /**
     * Removes lore line at position X containing text from NPC if exists
     *
     * @param player   The player whom will no longer see the lore line above an NPC
     * @param position The position of the lore line 1 - 5
     */
    public void lineDestroy(Player player, int position)
    {
        if (loreData.containsKey(position))
        {
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(loreData.get(position).id);
            sendPacket(packet, player);
            loreData.remove(position);
        }
    }

    /**
     * @param position
     */
    public void lineUpdate(int position, String to)
    {
        val data = loreData.get(position);

        if (data != null)
        {
            // Update entity
            val obj = DataWatcher.a(Entity.class, DataWatcherRegistry.d);

            data.stand.getDataWatcher().register(obj, "");
            data.stand.getDataWatcher().set(obj, to);

            // Send out packet
            val packet = new PacketPlayOutEntityMetadata(entityID, data.stand.getDataWatcher(), true);

            for (Player player : viewers)
                sendPacket(packet, player);

            // Update locally in map
            loreData.get(position).text(to);
        }
    }

    /**
     * Spawns an armor stand to the client as a packet, containing only the name.
     *
     * @param id       Entity ID for the armorstand to be spawned
     * @param player   The player who will be able to view the armorstand nametag
     * @param name     The name of the armorstand to be displayed
     * @param small    Whether the armorstand should be small or not
     * @param position The position of the name tag, so they will stack on eachother.
     */
    private void spawnArmorStand(int id, Player player, String name, boolean small, int position)
    {
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        EntityArmorStand arm = new EntityArmorStand(world);

        arm.setNoGravity(true);
        arm.setCustomNameVisible(true);
        arm.setSmall(small);
        arm.setInvisible(true);
        arm.setCustomName(name);
        arm.setArms(false);
        arm.setBasePlate(true);
        arm.collides = false;

        arm.setLocation(location.getX(), location.getY() + 0.80 + (((position - 1) * 0.3)), location.getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(arm);
        setValue(packet, "a", id);

        sendPacket(packet, player);

        loreData.get(position).stand(arm);
    }

    /**
     * Destroys the NPC entity, and its nametags armorstands
     *
     * @param player The player which will no longer be able to view this NPC
     */
    public void destroy(Player player)
    {
        val destroyPlayer = new PacketPlayOutEntityDestroy(entityID);
        sendPacket(destroyPlayer, player);

        loreData.forEachEntry((position, data) ->
        {
            val lorePacket = new PacketPlayOutEntityDestroy(data.id);
            sendPacket(lorePacket, player);
            return true;
        });

        removeNPCInfo(player);
    }

    /**
     * Adds the NPC to the tab list in order to provide NPC with skin textures
     * Only added to the player parameter
     *
     * @param player The player who will receive the packet
     */
    public void sendNpcInfo(Player player)
    {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();

        try
        {
            Class c = Class.forName("net.minecraft.server.v1_11_R1.PacketPlayOutPlayerInfo$PlayerInfoData");
            @SuppressWarnings ( "unchecked" ) List<Object> players = (List<Object>) getValue(packet, "b");
            players.add(c.getConstructor(packet.getClass(), com.mojang.authlib.GameProfile.class, int.class, EnumGamemode.class, IChatBaseComponent.class).newInstance(packet, gameProfile, 1, EnumGamemode.NOT_SET, CraftChatMessage.fromString(ChatColor.DARK_GRAY + this.gameProfile.getId().toString().substring(0, 8))[0]));
            setValue(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
            setValue(packet, "b", players);

            sendPacket(packet, player);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Removes the NPC from the tab list
     * only removed from the player parameter
     *
     * @param player The player who will receive the remove packet
     */
    public void removeNPCInfo(Player player)
    {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        try
        {
            Class c = Class.forName("net.minecraft.server.v1_11_R1.PacketPlayOutPlayerInfo$PlayerInfoData");
            @SuppressWarnings ( "unchecked" ) List<Object> players = (List<Object>) getValue(packet, "b");
            players.add(c.getConstructor(packet.getClass(), com.mojang.authlib.GameProfile.class, int.class, EnumGamemode.class, IChatBaseComponent.class).newInstance(packet, gameProfile, 1, EnumGamemode.NOT_SET, CraftChatMessage.fromString(gameProfile.getName())[0]));
            setValue(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
            setValue(packet, "b", players);

            sendPacket(packet, player);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public Location getLocation()
    {
        return this.location;
    }

    public void teleport(Location location)
    {
        PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport();
        setValue(packet, "a", entityID);
        setValue(packet, "b", getFixLocation(location.getX()));
        setValue(packet, "c", getFixLocation(location.getY()));
        setValue(packet, "d", getFixLocation(location.getZ()));
        setValue(packet, "e", getFixRotation(location.getYaw()));
        setValue(packet, "f", getFixRotation(location.getPitch()));

        sendPacket(packet);
        headRotation(location.getYaw(), location.getPitch());
        this.location = location.clone();
    }

    public void headRotation(float yaw, float pitch)
    {
        PacketPlayOutEntity.PacketPlayOutEntityLook packet = new PacketPlayOutEntity.PacketPlayOutEntityLook(entityID, getFixRotation(yaw), getFixRotation(pitch), true);
        PacketPlayOutEntityHeadRotation packetHead = new PacketPlayOutEntityHeadRotation();
        setValue(packetHead, "a", entityID);
        setValue(packetHead, "b", getFixRotation(yaw));

        sendPacket(packet);
        sendPacket(packetHead);
    }

    public void headRotation(Player p, float yaw, float pitch)
    {
        PacketPlayOutEntity.PacketPlayOutEntityLook packet = new PacketPlayOutEntity.PacketPlayOutEntityLook(entityID, getFixRotation(yaw), getFixRotation(pitch), true);
        PacketPlayOutEntityHeadRotation packetHead = new PacketPlayOutEntityHeadRotation();
        setValue(packetHead, "a", entityID);
        setValue(packetHead, "b", getFixRotation(yaw));

        sendPacket(packet, p);
        sendPacket(packetHead, p);
    }

    public int getFixLocation(double pos)
    {
        return (int) MathHelper.floor(pos * 32.0D);
    }

    public byte getFixRotation(float yawpitch)
    {
        return (byte) ((int) (yawpitch * 256.0F / 360.0F));
    }

    public int getEntityID()
    {
        return entityID;
    }

    @Data
    @AllArgsConstructor
    private static class LoreLineData
    {
        int id;
        EntityArmorStand stand;
        String text;
    }

}