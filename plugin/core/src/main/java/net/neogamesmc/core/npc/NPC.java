package net.neogamesmc.core.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_11_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.CraftServer;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_11_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Field;
import java.util.*;


/**
 * Handles, stores and manipulates a non-playable character
 */
public class NPC extends Reflections {


    private int entityID;
    private Location location;
    private GameProfile gameprofile;
    private List<Player> viewers = new ArrayList<>();

    private String quote;


    public HashMap<EnumItemSlot, org.bukkit.inventory.ItemStack> equipment = new HashMap<>();


    private HashMap<Integer, Integer> loreLinesIDs = new HashMap<>();
    private HashMap<Integer, String> loreLinesText = new HashMap<>();

    private ArrayList<Player> visibleFor = new ArrayList<>();


    private String name;

    public NPC(String name, Location location) {

        this.name = name;
        entityID = EntityIDs.get().assignID();
        UUID uuid = UUID.randomUUID();
        gameprofile = new GameProfile(uuid, uuid.toString().substring(0, 8));


        this.location = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }



    public boolean canPlayerSee(Player player) {
        return visibleFor.contains(player);
    }


    /**
     * Specify that the player parameter is within line of sight of this NPC
     *
     * @param player
     */
    public void addPlayerToVisibleList(Player player) {
        if (!visibleFor.contains(player))
            visibleFor.add(player);
    }

    /**
     * Specify that the player parameter can no longer see this NPC.
     * This will ensure that the NPC is respawned.
     *
     * @param player
     * @return
     */
    public boolean removePlayerFromVisibleList(Player player) {
        return visibleFor.remove(player);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the none playable character's skin using base 64 textures.
     * Applies the textures to the game profile of the NPC
     *
     * @param val the skin in base 64
     * @param sig the signature of the skin in base 64
     */
    public void setSkin(String val, String sig) {
        String value = val;
        String signature = sig;
        gameprofile.getProperties().put("textures", new Property("textures", value, signature));
    }

    /**
     * Spawns a none playable character which can be viewed by the player parameter
     * Also spawns entity's equipment, and name tags.
     *
     * @param player
     */
    public void spawn(Player player) {
        System.out.println("spawn method");

        /*
        Add player to viewers list so it can be respawned for the viewers when they go out of sight.
         */
        if (!viewers.contains(player))
            viewers.add(player);

        /*
        Creating the NPC using the EntityPlayer because I couldn't figure out what datawatcher values I need to use.
         */
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
        EntityPlayer npc;
        npc = new EntityPlayer(nmsServer, nmsWorld, gameprofile, new PlayerInteractManager(nmsWorld));
        npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());


        PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn(npc);
        setValue(packet, "a", entityID);
        setValue(packet, "b", gameprofile.getId());
        setValue(packet, "c", location.getX());
        setValue(packet, "d", location.getY());
        setValue(packet, "e", location.getZ());
        setValue(packet, "f", getFixRotation(location.getYaw()));
        setValue(packet, "g", getFixRotation(location.getPitch()));

        try
        {
            DataWatcherObject<Byte> humanSettings = DataWatcher.a(EntityHuman.class, DataWatcherRegistry.a);

            DataWatcher watcher = npc.getDataWatcher();
            watcher.register(humanSettings, (byte) 1);

            // Display a player's skin with every layer enabled
            // 127 is bit mask with every layer enabled -- Fetched from packet sniffing
            // why doesn't this work
            // please help
            watcher.set(humanSettings, (byte) 127);
            setValue(packet, "h", watcher);

            System.out.println("Set val for 'h' :: " + ((DataWatcher) getValue(packet, "h")).get(humanSettings).byteValue());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        /*
        Spawn the NPC and its data
         */
        sendNpcInfo(player);
        sendPacket(packet, player);

        /*
        Make the player look at the right direction
         */
        headRotation(player, location.getYaw(), location.getPitch());


        /*
        Hides NPCs (EntityPlayer) defualt name tag, by adding player to a scoreboard team and making it invisible.
         */
        org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        String teamName = "npcs";
        Team team = scoreboard.getTeam(teamName);

        if (team == null)
            team = scoreboard.registerNewTeam(teamName);
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);


        team.addEntry(gameprofile.getName());


        /*
        Apply the items the player is wearing/ holding
         */
        Iterator it = equipment.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            EnumItemSlot slot = (EnumItemSlot) pair.getKey();
            ItemStack itemStack = CraftItemStack.asNMSCopy((org.bukkit.inventory.ItemStack) pair.getValue());

            equip(slot, itemStack);
        }

        /*
        Adds the nametags above NPC
         */

        Iterator it2 = loreLinesText.entrySet().iterator();
        while (it2.hasNext()) {
            Map.Entry pair = (Map.Entry) it2.next();
            int position = (int) pair.getKey();
            String text = (String) pair.getValue();
            int id = loreLinesIDs.get(position);

            spawnArmorStand(id, player, text, true, position);

        }


    }

    /**
     * Returns a list of players who can view the NPC.
     * This list contains all players are are and can be viewing the NPC regardless of distance
     *
     * @return
     */
    public List<Player> getViewers() {
        return viewers;
    }

    /**
     * Equips this NPC with items/ armor
     *
     * @param slot      The item slot of the NPC which specifies where you are trying to apply the item to.
     * @param itemstack The item to be applied for the NPC.
     */
    public void equip(EnumItemSlot slot, ItemStack itemstack) {
        PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment();
        setValue(packet, "a", entityID);
        setValue(packet, "b", slot);
        setValue(packet, "c", itemstack);
        sendPacket(packet);
    }

    public String getLine(int position) {
        if (loreLinesText.containsKey(position)) {
            return loreLinesText.get(position);
        }
        return null;

    }

    /**
     * Sets the text of the NPC nametag at the provided position from 1 to 5
     * Does NOT automaticaly reapply the nametag to the NPC if it is currently spawned.
     *
     * @param position
     * @param text
     */
    public void setLine(int position, String text) {


        if (position <= 0 || position > 5)
            throw new IllegalArgumentException("Index must be greater than 0 and less than 6");

        if (text.length() > 40)
            throw new IllegalArgumentException("Text cannot be longer than 40 characters");


        int id;
        try {
            id = loreLinesIDs.get(position);
        } catch (Exception e) {
            id = EntityIDs.get().assignID();
        }

        loreLinesText.put(position, text);
        loreLinesIDs.put(position, id);


    }

    /**
     * Removes lore line at position X containing text from NPC if exists
     *
     * @param player   The player whom will no longer see the lore line above an NPC
     * @param position The position of the lore line 1 - 5
     */
    private void destroyLoreline(Player player, int position) {
        if (loreLinesIDs.containsKey(position)) {
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(new int[]{loreLinesIDs.get(position)});
            sendPacket(packet, player);
            loreLinesIDs.remove(position);
            loreLinesText.remove(position);
        }
    }

    /**
     * Spawns an armor stand to the client as a packet, containing only the name.
     *
     * @param id       Entity ID for the armorstand to be spawned
     * @param p        The player who will be able to view the armorstand nametag
     * @param name     The name of the armorstand to be displayed
     * @param small    Whether the armorstand should be small or not
     * @param position The position of the name tag, so they will stack on eachother.
     */
    private void spawnArmorStand(int id, Player p, String name, boolean small, int position) {
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


        arm.setLocation(location.getX(), location.getY() + 0.70 + (((position - 1) * 0.3)), location.getZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(arm);
        setValue(packet, "a", id);


        sendPacket(packet, p);


    }

    /**
     * Destroys the NPC entity, and its nametags armorstands
     *
     * @param p The player which will no longer be able to view this NPC
     */
    public void destroy(Player p) {

        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(new int[]{entityID});
        sendPacket(packet, p);

        Iterator it = loreLinesIDs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            int id = (int) pair.getValue();
            PacketPlayOutEntityDestroy lorePacket = new PacketPlayOutEntityDestroy(new int[]{id});
            sendPacket(lorePacket, p);

        }

        removeNpcInfo(p);

    }

    /**
     * Adds the NPC to the tab list in order to provide NPC with skin textures
     * Only added to the player parameter
     *
     * @param player The player who will receive the packet
     */
    public void sendNpcInfo(Player player) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        try {
            String className = "net.minecraft.server.v1_11_R1.PacketPlayOutPlayerInfo$PlayerInfoData";
            Class c = Class.forName(className);
            @SuppressWarnings("unchecked")
            List<Object> players = (List<Object>) getValue(packet, "b");
            players.add(c.getConstructor(packet.getClass(), com.mojang.authlib.GameProfile.class, int.class, net.minecraft.server.v1_11_R1.EnumGamemode.class, net.minecraft.server.v1_11_R1.IChatBaseComponent.class).newInstance(packet, gameprofile, 1, EnumGamemode.NOT_SET, CraftChatMessage.fromString(ChatColor.DARK_GRAY + "[NPC] " + this.gameprofile.getId().toString().substring(0, 8))[0]));
            setValue(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
            setValue(packet, "b", players);

            sendPacket(packet, player);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * Removes the NPC from the tab list
     * only removed from the player parameter
     *
     * @param player The player who will receive the remove packet
     */
    public void removeNpcInfo(Player player) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        try {
            String className = "net.minecraft.server.v1_11_R1.PacketPlayOutPlayerInfo$PlayerInfoData";
            Class c = Class.forName(className);
            @SuppressWarnings("unchecked")
            List<Object> players = (List<Object>) getValue(packet, "b");
            players.add(c.getConstructor(packet.getClass(), com.mojang.authlib.GameProfile.class, int.class, net.minecraft.server.v1_11_R1.EnumGamemode.class, net.minecraft.server.v1_11_R1.IChatBaseComponent.class).newInstance(packet, gameprofile, 1, EnumGamemode.NOT_SET, CraftChatMessage.fromString(gameprofile.getName())[0]));
            setValue(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
            setValue(packet, "b", players);

            sendPacket(packet, player);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Location getLocation() {

        return this.location;
    }

    public void teleport(Location location) {
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

    public void headRotation(float yaw, float pitch) {
        PacketPlayOutEntity.PacketPlayOutEntityLook packet = new PacketPlayOutEntity.PacketPlayOutEntityLook(entityID, getFixRotation(yaw), getFixRotation(pitch), true);
        PacketPlayOutEntityHeadRotation packetHead = new PacketPlayOutEntityHeadRotation();
        setValue(packetHead, "a", entityID);
        setValue(packetHead, "b", getFixRotation(yaw));


        sendPacket(packet);
        sendPacket(packetHead);
    }

    public void headRotation(Player p, float yaw, float pitch) {
        PacketPlayOutEntity.PacketPlayOutEntityLook packet = new PacketPlayOutEntity.PacketPlayOutEntityLook(entityID, getFixRotation(yaw), getFixRotation(pitch), true);
        PacketPlayOutEntityHeadRotation packetHead = new PacketPlayOutEntityHeadRotation();
        setValue(packetHead, "a", entityID);
        setValue(packetHead, "b", getFixRotation(yaw));


        sendPacket(packet, p);
        sendPacket(packetHead, p);
    }

    public int getFixLocation(double pos) {
        return (int) MathHelper.floor(pos * 32.0D);
    }

    public byte getFixRotation(float yawpitch) {
        return (byte) ((int) (yawpitch * 256.0F / 360.0F));
    }

    public int getEntityID() {
        return entityID;
    }

    private List<Field> getInheritedPrivateFields(Class<?> type) {
        List<Field> result = new ArrayList<Field>();

        Class<?> i = type;
        while (i != null && i != Object.class) {
            Collections.addAll(result, i.getDeclaredFields());
            i = i.getSuperclass();
        }

        return result;
    }


}