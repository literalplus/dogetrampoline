package io.github.xxyy.dogetrampoline.api;

/**
 * Implemented by classes that proxy a NMS block.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 05.01.14
 */
public interface NMSBlockProxy {
    void hook();
    void unhook();
}
