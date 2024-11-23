package features;

import org.junit.jupiter.api.Test;
import org.noear.okldap.LdapClient;
import org.noear.okldap.LdapSession;
import org.noear.okldap.entity.LdapPerson;
import org.noear.okldap.exception.IllegalPaaswordException;
import org.noear.okldap.exception.IllegalPersonException;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonTest;


/**
 * @author noear
 */
@SolonTest
public class LoginTest {
    @Inject("${solon.ldap}")
    private LdapClient client;

    @Inject("${solon.ldap.test.userName}")
    private String userName;
    @Inject("${solon.ldap.test.userPassword}")
    private String userPassword;
    @Inject("${solon.ldap.test.groupOk1}")
    private String groupOk1;
    @Inject("${solon.ldap.test.groupOk2}")
    private String groupOk2;
    @Inject("${solon.ldap.test.groupNo}")
    private String groupNo;

    /**
     * 账号与密码直接验证
     */
    @Test
    public void login1() throws Exception {
        LdapPerson user = null;

        try (LdapSession session = client.open()) {
            user = session.findPersonOne(userName, userPassword);
        }

        System.out.println(user);

        assert user != null;

        assert user.getCn() != null;
        assert user.getAttr("cn") != null;
    }

    /**
     * 账号与密码分开验证（可提供更真实的失败描述）
     */
    @Test
    public void login1x() throws Exception {
        LdapPerson user = null;

        try (LdapSession session = client.open()) {
            user = session.findPersonOne(userName);
        }

        System.out.println(user);
        assert user != null;

        if (user == null) {
            throw new IllegalPersonException("用户不存在");
        }

        if (user.verifyPassword(userPassword) == false) {
            throw new IllegalPaaswordException("密码有误");
        }
    }

    /**
     * 增加组的验证
     */
    @Test
    public void login2() throws Exception {
        LdapPerson user = null;

        try (LdapSession session = client.open()) {
            user = session.findPersonOne(userName, userPassword);
            if (user != null) {
                if (user.inGroup(session.findGroupOne(groupOk1)) == false) {
                    user = null;
                }
            }
        }

        System.out.println(user);

        assert user != null;
    }

    /**
     * 增加组的分开验证（可提供更真实的失败描述）
     */
    @Test
    public void login2x() throws Exception {
        LdapPerson user = null;

        try (LdapSession session = client.open()) {
            user = session.findPersonOne(userName);


            System.out.println(user);
            assert user != null;

            if (user == null) {
                throw new IllegalPersonException("用户不存在");
            }

            if (user.verifyPassword(userPassword) == false) {
                throw new IllegalPaaswordException("密码有误");
            }

            if (user.inGroup(session.findGroupOne(groupOk1)) == false) {
                throw new IllegalPersonException("没有权限");
            }
        }
    }

    /**
     * 增加组的失败验证
     */
    @Test
    public void login3() throws Exception {
        LdapPerson user = null;

        try (LdapSession session = client.open()) {
            user = session.findPersonOne(userName, userPassword);
            if (user != null) {
                if (user.inGroup(session.findGroupOne(groupNo)) == false) {
                    user = null;
                }
            }
        }

        System.out.println(user);

        assert user == null;
    }
}
