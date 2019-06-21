package com.yanwu.spring.cloud.common.data.base;

import com.google.common.base.Objects;
import com.yanwu.spring.cloud.common.data.sharding.HiveRoutingKey;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.Assert;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Threadlocal class for HiveContext, the thread local context can be passed to child
 * thread. Please see <tt>InheritableThreadLocal</tt> for details
 */
public final class HiveContextHolder {

    private HiveContextHolder() {
    }

    private static final ThreadLocal<HiveContext> localContext = new InheritableThreadLocal<HiveContext>() {

        /**
         * Initial the HiveContext to eliminate tedious null-checking.
         */
        @Override
        protected HiveContext initialValue() {
            return new HiveContext();
        }

    };

    public static HiveContext getContext() {
        return localContext.get();
    }

    /**
     * Return HiveRoutingKey for routing usage, the returned object will always be valid HiveRoutingKey
     *
     * @return routing key
     */
    public static HiveRoutingKey getHiveContextRoutingKey() {
        HiveRoutingKey routingKey = getContext().getRoutingKey();
        if (routingKey == null) {
            throw new IllegalStateException("HiveRoutingKey has not benn set to HiveContext.");
        }
        routingKey.validate();
        return routingKey;
    }

    public static void setHiveContextRoutingKey(final HiveRoutingKey routingKey) {
        getContext().setRoutingKey(checkNotNull(routingKey));
    }

    public static void setContext(final HiveContext hiveContext) {
        Assert.notNull(hiveContext);
        if (HiveConfigDbTxContextHolder.get().inTransaction()) {
            HiveContext origHiveContext = getContext();
        }
        localContext.set(hiveContext);
    }

    public static void clear() {
        localContext.remove();
    }

    private static void assureEquals(final Object o1, final Object o2, final String name) {
        Assert.isTrue(Objects.equal(o1, o2), name + " MUST NOT be changed during transaction");
    }

    public static Set<Long> getRbacLocations() {
        return getContext().getRbacLocationSet();
    }

    public static boolean isRbacUser() {
        return !getContext().getRbacAllowAll();
    }

    public static boolean isRbacLocationEmpty() {
        return isRbacUser() && CollectionUtils.isEmpty(getRbacLocations());
    }
}
