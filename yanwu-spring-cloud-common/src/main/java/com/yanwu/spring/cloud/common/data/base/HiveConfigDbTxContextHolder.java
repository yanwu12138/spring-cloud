package com.yanwu.spring.cloud.common.data.base;

import lombok.ToString;

public class HiveConfigDbTxContextHolder {

    @ToString
    final static public class Context {
        private int depth;

        /**
         * Carefully call this method
         */
        public void beginTx() {
            ++depth;
        }

        /**
         * Carefully call this method
         */
        public void endTx() {
            --depth;
        }

        public boolean inTransaction() {
            return depth > 0;
        }
    }

    final static private ThreadLocal<Context> context = new ThreadLocal<Context>() {
        @Override
        protected Context initialValue() {
            return new Context();
        }
    };

    static public void clear() {
        context.remove();
    }

    static public Context get() {
        return context.get();
    }
}
