package service;

import annotation.RpcClientBootStrap;
import annotation.RpcToolsSelector;
import entity.Person;
import entity.PersonPOJO;
import lombok.extern.slf4j.Slf4j;
import method.Customer;
import service.call.ChosenClientCall;

/**
 * @author C0ra1
 * @version 1.0
 * �ܿͻ���������  �û����� ʲô�汾��
 * ����ʲô���� ʹ��ʲôע������  ���л���ѡ�� �����������
 */

@Slf4j
//@RpcClientBootStrap(version = "2.4")
@RpcClientBootStrap(version = "2.0")
@RpcToolsSelector(rpcTool = "Netty")
//@RpcToolsSelector(rpcTool = "Nio")
public class ClientCall {
    public static void main(String[] args) {
        //ʵ�ֵ���
        Customer customer = ChosenClientCall.start();
        assert customer != null;

        // long start = System.currentTimeMillis();
        // log.info(customer.GetName(new Person("ףӢ̨")));
        //
        //���� 2.0�汾֮��
        Person person1 = customer.GetPerson(new Person("zz"));
        String msg1 = "��ȡ��Ӧ��" + person1.getClass() + "������Ϊ" + person1.getName();
        log.info(msg1);
        //
        new Thread(() -> {
            Customer customer1 = ChosenClientCall.start();
            Person person2 = customer1.GetPerson(new Person("zz"));
            String msg2 = "��ȡ��Ӧ��" + person2.getClass() + "������Ϊ" + person2.getName();
            log.info(msg2);
        }).start();

        log.info(customer.GetName(new Person("zzz")));
        log.info(customer.GetName(new Person("zzz")));
        log.info(customer.GetName(new Person("zzz")));
        log.info(customer.GetName(new Person("zzz")));
        //2.4�汾֮ǰ�Ĳ���
        //log.info(customer.GetPerson(PersonPOJO.Person.newBuilder().setName("ը����").build()));
        //����
         log.info(customer.GetName(PersonPOJO.Person.newBuilder().setName("ը����").build()));


        // nioʹ�ò���  nettyҲ����ʹ��
        log.info(customer.Hello("success"));
        log.info(customer.Bye("fail"));
        log.info(customer.Bye("fail"));
        log.info(customer.Bye("fail"));


        // long end = System.currentTimeMillis();
        // log.info(end-start);

    }
}
