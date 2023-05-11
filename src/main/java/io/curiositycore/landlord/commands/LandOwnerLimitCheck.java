package io.curiositycore.landlord.commands;

import io.curiositycore.landlord.Landlord;
import io.curiositycore.landlord.util.api.lands.LandActivityChecker;
import io.curiositycore.landlord.util.messages.PlayerMessages;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.flags.type.Flags;
import me.angeschossen.lands.api.land.Land;
import me.angeschossen.lands.api.player.LandPlayer;
import me.angeschossen.lands.api.role.Role;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.*;

/**
 * Command that executes a manual check of every land within the server to enforce the user-set Owner Limit.
 */
public class LandOwnerLimitCheck extends SubCommand {
    /**
     * The limit on the amount of <code>Land</code> claims a <code>Player</code> can own.
     */
    private int ownedLandsLimit;
    /**
     * An instance of the <code>Landlord</code> class.
     */
    private final Landlord landlordPlugin;
    /**
     * The <code>LandsIntegration</code> instance, essentially the Lands API, being utilised within the
     * lookups of this class. This will have been initialized <code>onEnable</code>.
     */
    private LandsIntegration landsAPI;

    /**
     * The <code>CoreProtectAPI</code> instance being utilised within lookups of this class. This will have been
     * initialized <code>onEnable</code>.
     */
    private CoreProtectAPI coreProtectAPI;
    /**
     * A <code>HashMap</code> defining the <code>UUID</code> of <code>Player</code>s as the <code>Key</code>
     * and an <code>ArrayList</code> of each <code>Land</code> they are a member in.
     */
    private HashMap<UUID,ArrayList<Land>> ownerHashMap;

    /**
     * An instance of the <code>PlayerMessages</code> class, used to define any messages sent to a <code>Player</code>
     * executing this <code>SubCommand</code>
     */
    private PlayerMessages playerMessages;


    /**
     * Constructor for the runnable. Ensures the various APIs and initial owner <code>HashMap</code> are set.
     * @param landlordPlugin The <code>Plugin</code> instance for the landlord Plugin.
     * @param landsAPI The <code>LandsIntegration</code> instance, essentially the Lands API.
     * @param coreProtectAPI The <code>CoreProtectAPI</code> instance being checked.
     */
    public LandOwnerLimitCheck(Landlord landlordPlugin, LandsIntegration landsAPI, CoreProtectAPI coreProtectAPI) {
        this.landlordPlugin = landlordPlugin;
        this.landsAPI = landsAPI;
        this.coreProtectAPI = coreProtectAPI;
        this.ownedLandsLimit = 3;
    }
    @Override
    public String getName() {
        return "landownerlimitcheck";
    }

    @Override
    public String getDescription() {
        return "Does a server wide check of each land owner, to ensure they do not go over the config-defined land owner limit.";
    }

    @Override
    public String getSyntax() {
        return "/landlord ownercheck <number of days checked>";
    }

    @Override
    public void perform(CommandSender player, String[] arguments) {
        this.playerMessages = new PlayerMessages(Bukkit.getPlayer(player.getName()));
        this.ownerHashMap = getOwnerHashMap();
        ownerLimitsEnforcer();
    }

    /**
     * Getter for the <code>HashMap</code> containing player UUIDs and how many <code>Land</code>s they own.
     * @return The <code>HashMap</code> of player UUIDs and how many <code>Land</code>s they own.
     */
    private HashMap<UUID,ArrayList<Land>> getOwnerHashMap(){

        ArrayList<Land> landList = new ArrayList<>(landsAPI.getLands());
        HashMap<UUID,ArrayList<Land>> ownerHashMap= new HashMap<>();
        landList.forEach(land -> {
            ArrayList<Land> ownerLandsList = new ArrayList<>();
            UUID ownerUID = land.getOwnerUID();

            if (ownerHashMap.containsKey(ownerUID)) {
                ownerLandsList.addAll(ownerHashMap.get(ownerUID));
            }

            ownerLandsList.add(land);
            ownerHashMap.put(ownerUID,ownerLandsList);

        });
        return ownerHashMap;
    }

    /**
     * Enforces the config-defined <code>Land</code> ownership limit upon each player within the <code>Land</code>
     * owner <code>HashMap</code>.
     * @return The <code>int</code> of the number of owners who have had lands removed.
     */
    private int ownerLimitsEnforcer(){
        int numberOfActionsTaken = 0;
        for(UUID ownerUID : ownerHashMap.keySet()){
            Land youngestLand;
            UUID newOwnerUID;

            if(ownerHashMap.get(ownerUID).size() <= ownedLandsLimit){
               continue;
            }

            numberOfActionsTaken +=1;
            youngestLand = getYoungestLand(ownerUID);
            newOwnerUID = determineNewOwnerOfLand(ownerUID,youngestLand);
            if(newOwnerUID==null){
                youngestLand.delete((LandPlayer) null);
                Bukkit.getLogger().info("[Landlord] "+ youngestLand.getName() + "Was deleted as the owner was" +
                        " over the ownership limit and there was no suitable replacement owner.");
            }
            transferLandOwnership(ownerUID,newOwnerUID,youngestLand);


        }
        return numberOfActionsTaken;
    }

    /**
     * Transfers the ownership of a <code>Land</code> from one <code>Player</code> to another.
     * @param oldOwnerUID The <code>UUID</code> of the current owner.
     * @param newOwnerUID The <code>UUID</code> of the <code>Player</code> to be given <code>Land</code> ownership.
     * @param trasferalLand The <code>Land</code> where the ownership transferal is being executed.
     */
    private void transferLandOwnership(UUID oldOwnerUID, UUID newOwnerUID, Land trasferalLand){
        if(oldOwnerUID == newOwnerUID){
            trasferalLand.delete((LandPlayer) null);
            Bukkit.getServer().getLogger().info("[Landlord] " + trasferalLand.getName() + "has been deleted as" +
                    " there were no alternative owners to transfer the claim to.");
            return;
        }

        trasferalLand.setOwner(newOwnerUID);
        String oldOwnerName = Bukkit.getServer().getOfflinePlayer(oldOwnerUID).getName();
        String newOwnerName = Bukkit.getServer().getOfflinePlayer(newOwnerUID).getName();
        Bukkit.getServer().getLogger().info("[Landlord] "+trasferalLand.getName()+ "has changed owner from " + oldOwnerName+" to "+ newOwnerName+"!");
    }
    /**
     * Getter for the <code>UUID</code> of the ideal candidate to take over a <code>Land</code> claim whose Owner is
     * above the config-defined <code>Land</code> ownership limit.
     * @param ownerUID The <code>UUID</code> of the ideal candidate for <code>Land</code> ownership transfer.
     * @param youngestLand The most recently created <code>Land</code> of a <code>Land</code> owner who is currently
     * above the config-defined <code>Land</code> ownership limit.
     * @return The <code>UUID</code> of the ideal candidate to take over the <code>Land</code> claim
     */
    private UUID determineNewOwnerOfLand(UUID ownerUID, Land youngestLand){
        UUID newOwnerUID;
        ArrayList<UUID> trustedMemberCandidatesList = getTrustedMemberCandidates(youngestLand);

        if(trustedMemberCandidatesList.contains(ownerUID)){
            trustedMemberCandidatesList.remove(ownerUID);
        }



        newOwnerUID = getIdealNewOwner(trustedMemberCandidatesList,youngestLand,ownerUID);

        if(newOwnerUID == null){
            newOwnerUID = getMostActiveLandMember(youngestLand,ownerUID);
        }

        return newOwnerUID;

    }

    /**
     * Determine the most recently created land of a <code>Land</code> owner who is currently above the config-defined
     * <code>Land</code> ownership limit.
     * @param ownerUID The <code>UUID</code> of the <code>Land</code> owner who is currently above the config-defined
     * <code>Land</code> ownership limit.
     * @return The most recently created <code>Land</code> of a <code>Land</code> owner who is currently above the
     * config-defined <code>Land</code> ownership limit.
     */
    private Land getYoungestLand(UUID ownerUID){
        HashMap<Land,Long> landCreationTimeHashMap = new HashMap<>();
        Map.Entry<Land,Long> minEntry = null;

        ownerHashMap.get(ownerUID).forEach(land->{
            landCreationTimeHashMap.put(land,land.getCreationTime());
        });

        for(Map.Entry<Land, Long> entry : landCreationTimeHashMap.entrySet()){
            if(minEntry==null||entry.getValue().compareTo(minEntry.getValue()) < 0){
                minEntry = entry;
            }
        }

        if(minEntry == null){
            return null;}

        return minEntry.getKey();

    }

    /**
     * Getter for the <code>UUID</code> of the most ideal candidate for <code>Land</code> ownership transferal.
     *
     * @param trustedMemberCandidatesList An <code>ArrayList</code> of ideal candidates for the <code>Land</code>
     * transfer.
     * @param youngestLand The most recently created <code>Land</code> of a <code>Land</code> owner who is currently
     * above the config-defined <code>Land</code> ownership limit.
     * @param ownerUID The <code>UUID</code> of the <code>Land</code> owner who is currently above the config-defined
     * <code>Land</code> ownership limit.
     * @return The <code>UUID</code> of the most ideal candidate.
     */
    private UUID getIdealNewOwner(ArrayList<UUID> trustedMemberCandidatesList, Land youngestLand, UUID ownerUID){
        if(trustedMemberCandidatesList.contains(ownerUID)){
            trustedMemberCandidatesList.remove(ownerUID);
        }
        LandActivityChecker landActivityChecker = new LandActivityChecker(landlordPlugin,youngestLand,coreProtectAPI,trustedMemberCandidatesList);

        return landActivityChecker.getMostActiveMember(true, ownerUID);

    }
    /**
     * Getter for the <code>ArrayList</code> of all the ideal candidates for <code>Land</code> ownership transferal
     * for a <code>Land</code> whose owner has gone above the config-defined <code>Land</code> ownership limit.
     * @param youngestLand The most recently created <code>Land</code> of a <code>Land</code> owner who is currently
     * above the config-defined <code>Land</code> ownership limit.
     * @return The <code>ArrayList</code> of all the ideal candidates for <code>Land</code> ownership transferal.
     */
    private ArrayList<UUID> getTrustedMemberCandidates(Land youngestLand){
        ArrayList<UUID> trustedPlayerUIDs = new ArrayList<> (youngestLand.getTrustedPlayers());
        ArrayList<UUID> candidatePlayerUIDs = new ArrayList<>();

        trustedPlayerUIDs.forEach(trustedMemberUID-> {
            Role trustedMemberRole = youngestLand.getDefaultArea().getRole(trustedMemberUID);
            boolean canEditLand = trustedMemberRole.hasFlag(Flags.SETTING_EDIT_LAND);
            boolean canEditRoles = trustedMemberRole.hasFlag(Flags.SETTING_EDIT_ROLE);

            if(canEditLand && canEditRoles){
                candidatePlayerUIDs.add(trustedMemberUID);
            }

        });

        return candidatePlayerUIDs;

    }

    /**
     * Getter for the <code>UUID</code> of the most active <code>Land</code> member within a <code>Land</code> whose
     * owner has gone above the config-Defined <code>Land</code> ownership limit.
     *
     * @param youngestLand The most recently created <code>Land</code> of a <code>Land</code> owner who is currently
     * above the config-defined <code>Land</code> ownership limit.
     * @param ownerUID The <code>UUID</code> of the <code>Land</code> owner who is currently above the config-defined
     * <code>Land</code> ownership limit.
     * @return The <code>UUID</code> of the most active <code>Land</code> member.
     */
    private UUID getMostActiveLandMember(Land youngestLand, UUID ownerUID){
        LandActivityChecker landActivityChecker = new LandActivityChecker(landlordPlugin,youngestLand,coreProtectAPI);

        return landActivityChecker.getMostActiveMember(false,ownerUID);

    }



}
