package io.curiositycore.landlord.util.api.coreprotect;

import io.curiositycore.landlord.util.maths.TimeUnit;
import net.coreprotect.CoreProtectAPI;

import java.util.List;

/**
 * Utility to conduct various <code>CoreProtectAPI</code> lookups.
 */
public class CoreprotectLookups {
    /**
     * The <code>CoreProtectAPI</code> instance being utilised within this check. This will have been
     * initialized <code>onEnable</code>.
     */
    private final CoreProtectAPI coreProtectAPI;

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
     *
     * @param playerName The name of the <code>Player</code> being looked up.
     * @param days The number of days in which the <code>Player</code>'s activity is to be looked up.
     * @param defaultTimeValue The default amount of seconds to add if a session involving a server crash is detected.
     * @return The number of minutes the <code>Player</code> has been active on the server, within the config-defined
     * amount of time.
     */
    public long playTimeLookup(String playerName, int days, int defaultTimeValue){
        int activityRangeInSeconds = (TimeUnit.DAY.timeConversion(days,TimeUnit.SECOND));
        List<String[]> sessionList = coreProtectAPI.sessionLookup(playerName, activityRangeInSeconds);

        return getTotalMilliseconds(sessionList,defaultTimeValue)/60000;
    }

    /**
     * Getter for the number of milliseconds played within the config-defined amount of time looked up.
     *
     * @param sessionList The <code>List</code> of lookups to be performed.
     * @param defaultTimeValue The default amount of seconds to add if a session involving a server crash is detected.
     * @return the number of milliseconds played.
     */
    private long getTotalMilliseconds(List<String[]> sessionList, int defaultTimeValue){
        CoreProtectAPI.ParseResult sessionAction;
        CoreProtectAPI.ParseResult previousAction = null;
        long totalMilliseconds = 0;

        for (String[] session : sessionList) {
            sessionAction = coreProtectAPI.parseResult(session);

            if (isLogin(sessionAction.getActionId())) { // Login Case
                totalMilliseconds += calculateSessionDuration(sessionAction, previousAction, defaultTimeValue);
            }

            previousAction = sessionAction;
        }


        return totalMilliseconds;
    }

    /**
     * Calculates the amount of milliseconds played in a potential game session.
     * @param sessionAction The most recently parsed session.
     * @param previousAction The most recently parsed session.
     * @param defaultTimeValue The default amount of seconds to add if a session involving a server crash is detected.
     * @return The amount of milliseconds played in a potential game session.
     */
    private long calculateSessionDuration(CoreProtectAPI.ParseResult sessionAction, CoreProtectAPI.ParseResult previousAction, int defaultTimeValue) {

        try {
            if (isRepeatSession(sessionAction.getActionId(), previousAction.getActionId())){
                return TimeUnit.MINUTE.timeConversion(defaultTimeValue, TimeUnit.SECOND);
            }

            return previousAction.getTimestamp()-sessionAction.getTimestamp();

            }
            catch(Exception exception){
                return 0;
        }
    }

    /**
     * Check to see if the 2 sessions being checked are identical to each other.
     * @param actionId The actionID (1 = Login, 0 = Logout) for the most recently parsed session.
     * @param previousActionId The actionID (1 = Login, 0 = Logout) for the most recently parsed session.
     * @return The <code>boolean</code> defining the result of the check.
     */
    private boolean isRepeatSession(int actionId, int previousActionId){
        return actionId == previousActionId;
    }

    /**
     * Check to see if a session's actionID is a login or logout instance.
     * @param actionId The actionID (1 = Login, 0 = Logout) for the most recently parsed session.
     * @return The <code>boolean</code> defining the result of the check.
     */
    private boolean isLogin(int actionId){
        return actionId == 1;
    }
}
