package com.google.code.morphia.mapping.lazy;

import java.lang.reflect.Method;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.kit.ObjectReference;
import com.thoughtworks.proxy.toys.delegate.DelegationMode;
import com.thoughtworks.proxy.toys.hotswap.HotSwappingInvoker;

class NonFinalizingHotSwappingInvoker extends HotSwappingInvoker {

    private static final long serialVersionUID = 1L;

    public NonFinalizingHotSwappingInvoker(final Class[] types,
            final ProxyFactory proxyFactory,
            final ObjectReference delegateReference,
            final DelegationMode delegationMode) {
        super(types, proxyFactory, delegateReference, delegationMode);
    }

    @Override
    public Object invoke(final Object proxy, final Method method,
            final Object[] args) throws Throwable {
        if ("finalize".equals(method.getName()) && (args != null)
                && (args.length == 0)) {
            return null;
        }

        return super.invoke(proxy, method, args);
    }

}
