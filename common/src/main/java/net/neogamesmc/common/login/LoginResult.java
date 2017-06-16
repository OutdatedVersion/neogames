package net.neogamesmc.common.login;

import com.google.common.base.Objects;

/**
 * Representation of the outcome
 * for a {@link LoginRequest}.
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/03/2017 (5:54 PM)
 */
public class LoginResult
{

    /**
     * An approved {@link LoginResult}.
     */
    private static final LoginResult APPROVED_LOGIN = new LoginResult(Outcome.APPROVE, null);

    /**
     * Indication of what happened with a {@link LoginRequest}.
     */
    public enum Outcome
    {
        REJECT,
        APPROVE
    }

    /**
     * A representation of the outcome for the
     * request associated with this result.
     */
    public final Outcome decision;

    /**
     * In the case that the login was denied, what's
     * the reasoning behind doing so?
     * <p>
     * For example, being expelled from the network.
     */
    public final String reason;

    /**
     * Constructor
     *
     * @param decision See {@link #decision}
     * @param reason   See {@link #reason}
     */
    private LoginResult(Outcome decision, String reason)
    {
        this.decision = decision;
        this.reason = reason;
    }

    /**
     * Returns a result indicating that we've
     * denied a player from logging into the
     * network. This may be for a number of
     * reasons, but probably is going to
     * fall onto some punishment.
     *
     * @param reason The reason for rejection. This <strong>MUST</strong> be descriptive and decently thought out.
     *
     * @return The result
     */
    public static LoginResult reject(String reason)
    {
        return new LoginResult(Outcome.REJECT, reason);
    }

    /**
     * Indicates that the player's login
     * is approved from the handler associated
     * with this result.
     *
     * @return The result
     */
    public static LoginResult approve()
    {
        return APPROVED_LOGIN;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(decision, reason);
    }

}
