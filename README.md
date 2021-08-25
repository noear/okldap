
[![Maven Central](https://img.shields.io/maven-central/v/org.noear/okldap.svg)](https://mvnrepository.com/search?q=g:org.noear%20AND%20okldap)

` QQ交流群：22200020 `


# okldap


配置示例：

```yaml
#必要选项
ldap:
  url: "ldap://127.0.0.1:389"
  baseDn: "DC=company,DC=com"
  bindDn: "cn=admin,dc=company,dc=com"
  paasword: "123456"
  userFilter: "cn=%s"
  groupFilter: "cn=%s"

#Demo选项
demo:
  limit:
    groupCn: "manager"
```

代码示例：（更多示例，可参考源码里的单元测试）

```java

//应用入口
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args);
    }
}

//Bean配置器
@Configuration
public class Config {
    @Bean
    public LdapClient ldapClient(@Inject("${ldap}") LdapClient ldapClient) {
        return ldapClient;
    }
}

//控制器示例
@Mapping("demo")
@Controller
public class DemoController {
    @Inject
    LdapClient ldapClient;

    @Inject("${demo.limit.groupCn}")
    String limitGroupCn;

    /**
     * 登录::
     * */
    @Post
    @Mapping("login")
    public Result login(String userName, String userPassword) {
        try (LdapSession session = ldapClient.open()) {
            LdapPerson person = session.findPersonOne(userName);

            if (person == null) {
                return Result.failure("用户不存在");
            }

            if (person.verifyPassword(userPassword) == false) {
                return Result.failure("用户密码不对");
            }

            if (Utils.isNotEmpty(limitGroupCn)) {
                if (person.inGroup(session.findGroupOne(limitGroupCn)) == false) {
                    return Result.failure("权限不够");
                }
            }

            //可同步到本地用户库...

            return Result.succeed();
        }
    }

    /**
     * 创建用户::
     * */
    @Post
    @Mapping("create")
    public Result create(String userName, String userPassword, String displayName) {
        try (LdapSession session = ldapClient.open()) {
            LdapPerson person = new LdapPerson();
            person.setCn(userName);
            person.setUserPassword(userPassword);
            person.setDisplayName(displayName);

            session.createPerson("employee", person);
        }

        return Result.succeed();
    }
}
```
