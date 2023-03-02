package provider.api;

import entity.Person;
import entity.PersonPOJO;
import method.GetNameService;

/**
 * @author C0ra1
 * @version 1.0
 */
public class GetNameServiceImpl implements GetNameService {
    @Override
    public String sayGetName(Person person) {
        return person.getName();
    }

    //protobuf
    @Override
    public String sayGetName(PersonPOJO.Person person) {
        return person.getName();
    }
}
