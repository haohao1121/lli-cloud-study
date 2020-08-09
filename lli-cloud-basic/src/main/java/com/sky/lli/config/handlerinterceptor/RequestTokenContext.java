package com.sky.lli.config.handlerinterceptor;

import org.springframework.core.NamedThreadLocal;

import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.nonNull;


/**
 * @author klaus
 * @date 2020-01-01
 */
public class RequestTokenContext extends ConcurrentHashMap<String, String> {
    private static final long serialVersionUID = -3893437651348498634L;

    private static final ThreadLocal<RequestTokenContext> THREAD_LOCAL = new NamedThreadLocal<RequestTokenContext>("TokenContext") {
        @Override
        protected RequestTokenContext initialValue() {
            return new RequestTokenContext();
        }
    };

    public static RequestTokenContext getCurrentContext() {
        return THREAD_LOCAL.get();
    }

    public static void unset() {
        THREAD_LOCAL.remove();
    }

    public void set(String key, String value) {
        if (nonNull(value)) {
            this.put(key, value);
        } else {
            this.remove(key);
        }
    }
}