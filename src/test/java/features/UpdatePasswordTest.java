package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.okldap.LdapClient;
import org.noear.okldap.LdapSession;
import org.noear.okldap.entity.LdapPerson;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;

/**
 * @author noear 2021/8/25 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
public class UpdatePasswordTest {
    @Inject("${solon.ldap}")
    private LdapClient client;


    @Test
    public void updatePersonPassword() throws Exception {
        try (LdapSession session = client.open()) {
            //创建一个人,用于此测试
            String userName = "user-ssssss";
            String userPassword = "L85cYVxZj3eI81ay";
            String userPasswordNew = "pbH412oBDyq6elPd";

            if(session.findPersonOne(userName) == null) {
                LdapPerson person = new LdapPerson();
                person.setCn(userName);
                person.setUserPassword(userPassword);

                session.createPerson("test", person);
            }

            //开始测试
            LdapPerson person = session.findPersonOne(userName, userPassword);
            System.out.println(person);
            assert person != null;

            session.updatePersonPassword(userName, userPassword, userPasswordNew);

            person = session.findPersonOne(userName, userPasswordNew);
            System.out.println(person);
            assert person != null;

            session.updatePersonPassword(userName, userPasswordNew, userPassword);
        }
    }
}
