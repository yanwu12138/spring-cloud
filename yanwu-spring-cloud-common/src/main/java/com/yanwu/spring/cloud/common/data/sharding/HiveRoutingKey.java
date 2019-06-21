package com.yanwu.spring.cloud.common.data.sharding;

import com.yanwu.spring.cloud.common.data.datasource.HiveResourceType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Routing key for Hive-Manager Shard routing algorithm.
 */
@Data
@NoArgsConstructor
public class HiveRoutingKey implements Serializable {

    private static final long serialVersionUID = 3512870298789882059L;

    private HiveResourceType resourceType;

    private long ownerId;

    /**
     * The flag to indicate whether is random routing feature enabled.
     * <p>
     * Default is disabled, ONLY enable it in special cases.
     */
    private boolean randomRoutingEnabled = false;

    public HiveRoutingKey(final HiveResourceType resourceType, final long ownerId) {
        setResourceType(resourceType);
        setOwnerId(ownerId);
    }

    /**
     * The ONLY way to get random routing feature enabled {@link HiveRoutingKey}.
     * <p>
     * WARNING:
     * <ul>
     * <li>DON'T CASUALLY ENABLE THIS FEATURE.
     * <li>THIS IS A BACK DOOR FOR VERY ADVANCED USE CASES.
     * <li>ONE SHALL NEVER USE THIS AND START WRITING TO A RANDOM SHARD.
     *
     * @param resourceType resource type
     * @return random routing feature enabled {@link HiveRoutingKey}
     */
    public static HiveRoutingKey randomRoutingKeyOf(final HiveResourceType resourceType) {
        HiveRoutingKey routingKey = new HiveRoutingKey();
        routingKey.enableRandomRouting().setResourceType(resourceType);
        return routingKey;
    }

    /**
     * Enable random routing feature.
     * <p>
     * Make this method private to ensure no change allowed outside.
     *
     * @return random routing feature enabled HiveRoutingKey instance
     */
    private HiveRoutingKey enableRandomRouting() {
        randomRoutingEnabled = true;
        return this;
    }

    /**
     * Check whether random routing feature enabled.
     *
     * @return whether random routing feature enabled
     */
    public boolean isRandomRoutingEnabled() {
        return randomRoutingEnabled;
    }

    /**
     * Only allow set valid owner id.
     *
     * @param ownerId owner id
     * @throws IllegalArgumentException if {@code ownerId} is not valid
     */
    public void setOwnerId(final long ownerId) {
        checkArgument(ownerId > 0, "Invalid ownerId: MUST be positive");
        this.ownerId = ownerId;
    }

    /**
     * Do validation, ensure routing key is correctly set.
     */
    public void validate() {
        if (!isRandomRoutingEnabled()) {
            checkArgument(ownerId > 0, "Invalid HiveRoutingKey: ownerId MUST be positive");
        }
    }

}
