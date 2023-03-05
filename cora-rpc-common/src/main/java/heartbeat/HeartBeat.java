package heartbeat;

import annotation.HeartBeatTool;

/**
 * @author C0ra1
 * 心跳机制 工具类   这个类别过时了 统一管理在统一配置类中
 */
@Deprecated
@HeartBeatTool(isOpenFunction = true,
        readerIdleTimeSeconds = 4,
        writerIdleTimeSeconds = 4,
        allIdleTimeSeconds = 2)
public interface HeartBeat {
}
