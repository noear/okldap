package demo.dso.service;

import demo.model.User;
import org.noear.okldap.LdapSession;
import org.noear.okldap.LdapClient;
import org.noear.okldap.entity.LdapPerson;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

/**
 * @author noear
 */
@Component
public class LoginService {
    @Inject
    LdapClient ldapClient;

    /**
     * 登录示例1
     */
    public User tryLogin1(String userName, String userPassword) throws Exception {
        //1.查找用户
        LdapPerson person = null;
        try (LdapSession session = ldapClient.open()) {
            //查用户与证验密码分开，方便认别问题
            person = session.findPersonOne(userName, userPassword);
        }

        if (person == null) {
            return null;
        } else {
            //4.转为本地用户实体
            User user = new User();
            user.setUserName(person.getCn());
            user.setUserDisplayName(person.getDisplayName());

            return user;
        }
    }

    /**
     * 登录示例2(账号与密码分开验证　)
     */
    public User tryLogin2(String userName, String userPassword) throws Exception {
        //1.查找用户
        LdapPerson person = null;
        try (LdapSession session = ldapClient.open()) {
            //查用户与证验密码分开，方便认别问题
            person = session.findPersonOne(userName);
        }

        //2.检测用户是否存在
        if (person == null) {
            throw new RuntimeException("用户不存在");
        }

        //3.验证密码
        if (person.verifyPassword(userPassword) == false) {
            throw new RuntimeException("密码不正确");
        }

        //4.转为本地用户实体
        User user = new User();
        user.setUserName(person.getCn());
        user.setUserDisplayName(person.getDisplayName());

        return user;
    }

    /**
     * 登录示例3（带分组验证）
     */
    public User tryLogin3(String userName, String userPassword) throws Exception {
        //1.查找用户
        LdapPerson person = null;
        try (LdapSession session = ldapClient.open()) {
            //查用户与证验密码分开，方便认别问题
            person = session.findPersonOne(userName);

            //2.检测用户是否存在
            if (person == null) {
                throw new RuntimeException("用户不存在");
            }

            //3.验证密码
            if (person.verifyPassword(userPassword) == false) {
                throw new RuntimeException("密码不正确");
            }

            //4.验证角色
            if (person.inGroup(session.findGroupOne("manager")) == false) {
                throw new RuntimeException("没有权限");
            }
        }


        //4.转为本地用户实体
        User user = new User();
        user.setUserName(person.getCn());
        user.setUserDisplayName(person.getDisplayName());

        return user;
    }
}