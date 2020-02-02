package io.xpring.xrpl;

import org.immutables.value.Value;

import java.util.Optional;

/**
 * Represents classic address components on the XRP Ledger
 */
@Value.Immutable
public interface ClassicAddress {
    /**
     * @return the address component of the classic address
     */
    public String address();

    /**
     * @return the tag component of the classic address
     */
    public Optional<Long> tag();

    /**
     * @return a boolean indicating whether this address is for use on a test network
     */
    public booelan isTest();

}
