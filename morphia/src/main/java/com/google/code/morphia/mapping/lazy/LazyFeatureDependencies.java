/**
 * 
 */
package com.google.code.morphia.mapping.lazy;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * 
 */
public class LazyFeatureDependencies {

    private static Boolean fullFilled;
    private static final Logger logger = Logger
            .getLogger(LazyFeatureDependencies.class.getName());

    public static boolean assertDependencyFullFilled() {
        final boolean fullfilled = testDependencyFullFilled();
        if (!fullfilled) {
            logger.warning("Lazy loading impossible due to missing dependencies.");
        }
        return fullfilled;
    }

    /**
     * @return
     */
    public static LazyProxyFactory createDefaultProxyFactory() {
        if (testDependencyFullFilled()) {
            final String factoryClassName = "com.google.code.morphia.mapping.lazy.CGLibLazyProxyFactory";
            try {
                return (LazyProxyFactory) Class.forName(factoryClassName)
                        .newInstance();
            }
            catch (final Exception e) {
                logger.log(Level.SEVERE, "While instanciating "
                        + factoryClassName, e);
            }
        }
        return null;
    }

    public static boolean testDependencyFullFilled() {
        if (fullFilled != null) {
            return fullFilled;
        }
        try {
            fullFilled = (Class.forName("net.sf.cglib.proxy.Enhancer") != null)
                    && (Class
                            .forName("com.thoughtworks.proxy.toys.hotswap.HotSwapping") != null);
        }
        catch (final ClassNotFoundException e) {
            fullFilled = false;
        }
        return fullFilled;
    }

    private LazyFeatureDependencies() {
    }
}
