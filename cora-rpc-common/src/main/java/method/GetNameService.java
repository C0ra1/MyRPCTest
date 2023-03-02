package method;

import entity.Person;
import entity.PersonPOJO;

/**
 * @author C0ra1
 * @version 1.0
 */

public interface GetNameService {
    String sayGetName(Person person);

    String sayGetName(PersonPOJO.Person person);
}
