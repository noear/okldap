
[![Maven Central](https://img.shields.io/maven-central/v/org.noear/okldap.svg)](https://mvnrepository.com/search?q=g:org.noear%20AND%20okldap)

` QQ交流群：22200020 `

## OkLdap

* 如果想在你的业务系统快速使用 LDAP 进行登录？
* 如果你的内网系统想使用接口快速创建员工的 LDAP 账号？

你可以试试 OkLdap。

OkLdap 是专门为基于 LDAP 账号做内网整合的简便客户端。

### 主要功能:

1. 快速的登录接口及示例
2. 修改密码的接口
3. 基于员工的管理接口（添人、减人、修改）



### 演示：

**引入依赖:**

```xml
 <dependency>
    <groupId>org.noear</groupId>
    <artifactId>okldap</artifactId>
    <version>1.1.1</version>
</dependency>
```

**配置：**

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

**代码：（更多应用代码，可参考源码里的单元测试）**

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
    public Result create(String userName, String userPassword, String stageName, String realName) {
        try (LdapSession session = ldapClient.open()) {
            LdapPerson person = new LdapPerson();
            person.setCn(userName);
            person.setUserPassword(userPassword);
            person.setDisplayName(stageName);
            person.setGivenName(realName);

            session.createPerson("employee", person);
        }

        return Result.succeed();
    }

    /**
     *  修改用户::
     * */
    @Post
    @Mapping("update")
    public Result update(String userName, String stageName, String realName) {
        try (LdapSession session = ldapClient.open()) {
            LdapPerson person = session.findPersonOne(userName);
            person.setDisplayName(stageName);
            person.setGivenName(realName);

            session.updatePerson(person);
        }

        return Result.succeed();
    }
}
```
