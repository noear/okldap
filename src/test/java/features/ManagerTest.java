package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.okldap.LdapClient;
import org.noear.okldap.LdapSession;
import org.noear.okldap.entity.LdapGroup;
import org.noear.okldap.entity.LdapPerson;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;

import java.util.List;


/**
 * @author noear
 */
@RunWith(SolonJUnit4ClassRunner.class)
public class ManagerTest {
    @Inject("${solon.ldap}")
    private LdapClient client;

    @Test
    public void findPersonListByGroup() throws Exception {
        try (LdapSession session = client.open()) {
            List<LdapPerson> list = session.findPersonList("UI");
            System.out.println(list);
            assert list.size() == 3;
        }
    }

    @Test
    public void findGroupListAll() throws Exception {
        try (LdapSession session = client.open()) {
            List<LdapGroup> list = session.findGroupListAll();
            System.out.println(list);
            assert list.size() > 5;
        }
    }

    @Test
    public void deletePerson() throws Exception {
        try (LdapSession session = client.open()) {
            session.delete("uid=xxxxx,cn=test,dc=company,dc=com");
            session.deletePerson("xxxxx");
        }
    }


    @Test
    public void createPerson() throws Exception {
        try (LdapSession session = client.open()) {
            LdapPerson person = new LdapPerson();
            String userName = "user-" + System.currentTimeMillis();

            //必选
            person.setCn(userName);
            person.setUserPassword("123456");

            //可选
            person.setDisplayName(userName);
            person.setGivenName("noear-test");
            person.setMail(userName + "@xxx.com");

            session.createPerson("test", person);

            assert session.findPersonOne(userName, "123456") != null;
            session.deletePerson(userName);
            assert session.findPersonOne(userName, "123456") == null;
        }
    }
}
