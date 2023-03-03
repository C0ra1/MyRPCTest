package provider.bootstrap.nio;

import provider.nio.NIOBlockingServer11;
import provider.nio.NIONonBlockingServer10;

import java.io.IOException;

/**
 * @author C0ra1
 * @version 1.0
 */

public class NIOProviderBootStrap10 {
    public static void main(String[] args) throws IOException {
        NIONonBlockingServer10.start(6666);
    }
}
