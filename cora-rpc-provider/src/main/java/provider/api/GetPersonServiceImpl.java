package provider.api;

import entity.Person;
import entity.PersonPOJO;
import method.GetPersonService;

/**
 * @author C0ra1
 * @version 1.0
 */
public class GetPersonServiceImpl implements GetPersonService {
    @Override
    public Person sayGetPerson(Person person) {
        return person;
    }

    //protobuf
    @Override
    public PersonPOJO.Person sayGetPerson(PersonPOJO.Person person) {
        return person;
    }
}
