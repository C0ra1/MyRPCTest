package method;

import entity.Person;
import entity.PersonPOJO;

/**
 * @author C0ra1
 * @version 1.0
 */
public interface GetPersonService {
    Person sayGetPerson(Person person);

    //protobuf
    PersonPOJO.Person sayGetPerson(PersonPOJO.Person person);
}
