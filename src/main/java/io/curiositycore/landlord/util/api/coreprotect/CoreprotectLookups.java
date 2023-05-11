package io.curiositycore.landlord.util.api.coreprotect;

import io.curiositycore.landlord.util.maths.TimeConverter;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.ListIterator;

/**
 * Utility to conduct various <code>CoreProtectAPI</code> lookups.
 */
public class CoreprotectLookups {
    /**
     * The <code>CoreProtectAPI</code> instance being utilised within this check. This will have been
     * initialized <code>onEnable</code>.
     */
    CoreProtectAPI coreProtectAPI;

    /**
     * Constructor that defines the <code>CoreProtectAPI</code> instance for the class.
     * @param coreProtectAPI The <code>CoreProtectAPI</code> instance being utilised for lookups in this class.
     */
    public CoreprotectLookups(CoreProtectAPI coreProtectAPI){
        this.coreProtectAPI = coreProtectAPI;
    }

    /**
     * Getter for the amount of minutes played within a config-defined amount of time. <i>Note that
     * the method with the <code>CoreProtectAPI</code> for looking up sessions is based in seconds.</i>
     * @param playerName The name of the <code>Player</code> being looked up.
     * @param days The number of days in which the <code>Player</code>'s activity is to be looked up.
     * @return The number of minutes the <code>Player</code> has been active on the server, within the config-defined
     * amount of time.
     */
    public long playTimeLookup(String playerName,int days){
        List<String[]> sessionList = coreProtectAPI.sessionLookup(playerName, TimeConverter.SECOND.timeConversion(TimeConverter.DAY.toTicks(days)));
        return getTotalMilliseconds(sessionList);
    }

    /**
     * Getter for the number of milliseconds played within the config-defined amount of time looked up.
     * @param sessionlist The <code>List</code> of lookups to be performed.
     * @return the number of Minecraft Ticks played.
     */
    private long getTotalMilliseconds(List<String[]> sessionlist){
        CoreProtectAPI.ParseResult sessionAction;
        CoreProtectAPI.ParseResult previousAction = null;
        long totalMilliseconds = 0;
        long timestamp;
        boolean isFirstAction = false;
        ListIterator<String[]> sessionListIterator = sessionlist.listIterator();
        String actionName;
        String previousActionName;

        while(sessionListIterator.hasNext()){

            if(!sessionListIterator.hasPrevious()){
                isFirstAction = true;
            }

            sessionAction = coreProtectAPI.parseResult(sessionListIterator.next());

            actionName = sessionAction.getActionString().toLowerCase();

            if(isFirstAction){
                previousActionName = "";
            }
            else {
                previousActionName = previousAction.getActionString().toLowerCase();

            }


            if(actionName == "login" && isFirstAction){

                timestamp = 0;
            }
            else if(actionName == "login"){
                timestamp = -sessionAction.getTimestamp();
            }
            else{
                timestamp = sessionAction.getTimestamp();
            }

            previousAction = sessionAction;
            totalMilliseconds += timestamp;

            if(previousActionName.equalsIgnoreCase(actionName) && !isFirstAction){
                totalMilliseconds -= timestamp;
                continue;
            }
            isFirstAction = false;


        }
        Bukkit.getLogger().info("Total of "+ (totalMilliseconds/1000/60) + "minutes");
        return totalMilliseconds;

    }
}
