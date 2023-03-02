package provider.api;

import method.ByeService;

/**
 * @author C0ra1
 * @version 1.0
 */
public class ByeServiceImpl implements ByeService {
    @Override
    public String sayBye(String saying) {
        return "Bye," + saying;
    }
}
