package consumer.api;

import entity.Person;
import entity.PersonPOJO;
import method.Customer;

/**
 * @author C0ra1
 * @version 1.0
 */
public class CustomerImpl implements Customer {
    @Override
    public String Hello(String msg) {
        return msg;
    }

    @Override
    public String Bye(String msg) {
        return msg;
    }

    @Override
    public String GetName(Person person) {
        return person.getName();
    }

    @Override
    public String GetName(PersonPOJO.Person personPOJO) {
        return personPOJO.getName();
    }

    @Override
    public Person GetPerson(Person person) {
        return person;
    }

    @Override
    public PersonPOJO.Person GetPerson(PersonPOJO.Person personPOJO) {
        return personPOJO;
    }
}
