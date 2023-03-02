package provider.bootstrap;

import provider.nio.NIOBlockingServer11;

import java.io.IOException;

/**
 * @author C0ra1
 * @version 1.0
 */
public class NIOProviderBootStrap11 {
    public static void main(String[] args) throws IOException {
        NIOBlockingServer11.start(6666);
    }
}
