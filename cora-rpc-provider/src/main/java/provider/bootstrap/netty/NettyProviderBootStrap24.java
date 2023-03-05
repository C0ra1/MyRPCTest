package provider.bootstrap.netty;

import lombok.extern.slf4j.Slf4j;
import monitor.RpcMonitor;
import monitor.RpcMonitorOperator;
import provider.netty.NettyServer24;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author C0ra1
 * @version 1.0
 * ʵ�ַ�����֮ǰ���ǱȽ�һ�µ� ÿ������Ҫ��һ���߳�ȥִ��
 * ԭ�������ǻ�����ͬ�������ǹر� �ų��� �������ܹرն�Ӧ�Ĺܵ�
 */
@Slf4j
public class NettyProviderBootStrap24 {
    static volatile AtomicInteger port = new AtomicInteger(6666); //��Ӧ�Ķ˿� Ҫ����ȥ ע�ᵽע������ȥ


    public static void main(String[] args) {
        //���ȶ�ԭ�����ݿ��е����ݽ������  ��Ϊ������������ �����ṩ�˿� ������Ҫ���¼���
        RpcMonitorOperator rpcMonitorOperator = new RpcMonitorOperator();
        rpcMonitorOperator.deleteAll();

        //ֱ�������ｫ��Ӧ�ķ���ʲô�Ľ��зֿ� Ȼ�󴫹�ȥ
        String methods = args[0];
        String nums = args[1];
        String[] methodArray = methods.split(",");
        String[] methodNumArray = nums.split(",");
        //���д���  ���ܻ������ ��ߵĶ˿�
        for (int i = 0; i < methodArray.length; ++i) {
            String methodName = methodArray[i];
            for (int methodNum = 0; methodNum < Integer.parseInt(methodNumArray[i]); methodNum++) {
                RpcMonitor rpcMonitor = new RpcMonitor();
                //TODO ����֮�󻹿��Լ������и��� ��Ϊ���Ļ� ������ڷ������� ��ô�Ϳ��Բ��÷�������ص�����
                rpcMonitor.setMethodName("127.0.0.1:" + port);
                rpcMonitor.setMethodDescription(methodName);
                rpcMonitorOperator.addServer(rpcMonitor);
                int nowPort = port.get();
                //��Ϊ�����������һ���߳� ����һ��
                new Thread(() -> NettyServer24.start(methodName, nowPort)).start();
                port.incrementAndGet();
            }
        }
    }
}
