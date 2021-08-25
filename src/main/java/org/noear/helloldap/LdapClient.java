package org.noear.helloldap;

import org.noear.helloldap.exception.IllegalConfigException;
import org.noear.helloldap.utils.TextUtils;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Hashtable;
import java.util.Properties;

/**
 * @author noear
 * @since 1.0
 */
public class LdapClient {
    private final Hashtable config;
    private String baseDn;
    private String userFilter = "%s";
    private String groupFilter = "%s";

    public void setBaseDn(String baseDn) {
        if (TextUtils.isNotEmpty(baseDn)) {
            this.baseDn = baseDn;
        }
    }

    public String getBaseDn() {
        return baseDn;
    }

    public void setUserFilter(String userFilter) {
        if (TextUtils.isNotEmpty(userFilter)) {
            this.userFilter = userFilter;
        }
    }

    public String getUserFilter() {
        return userFilter;
    }

    public void setGroupFilter(String groupFilter) {
        if (TextUtils.isNotEmpty(groupFilter)) {
            this.groupFilter = groupFilter;
        }
    }

    public String getGroupFilter() {
        return groupFilter;
    }

    public LdapClient(Properties props) {
        String url = props.getProperty("url");
        String bindDn = props.getProperty("bindDn");
        String paasword = props.getProperty("paasword");

        if (TextUtils.isEmpty(bindDn)) {
            bindDn = props.getProperty("username"); //兼容旧写法
        }

        setBaseDn(props.getProperty("baseDn"));
        setUserFilter(props.getProperty("userFilter"));
        setGroupFilter(props.getProperty("groupFilter"));

        config = buildConfig(url, bindDn, paasword);
    }

    public LdapClient(String url, String baseDn, String bindDn, String paasword) {
        setBaseDn(baseDn);

        config = buildConfig(url, bindDn, paasword);
    }


    /**
     * 构建配置
     */
    private Hashtable buildConfig(String url, String bindDn, String paasword) {
        if (TextUtils.isEmpty(baseDn)) {
            throw new IllegalConfigException("baseDn");
        }


        if (TextUtils.isEmpty(url)) {
            throw new IllegalConfigException("url");
        }

        if (TextUtils.isEmpty(bindDn)) {
            throw new IllegalConfigException("bindDn");
        }

        if (TextUtils.isEmpty(paasword)) {
            throw new IllegalConfigException("paasword");
        }

        String factory = "com.sun.jndi.ldap.LdapCtxFactory";
        String type = "simple"; // "none","simple","strong"

        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, factory);
        env.put(Context.PROVIDER_URL, url);
        env.put(Context.SECURITY_AUTHENTICATION, type);
        env.put(Context.SECURITY_PRINCIPAL, bindDn);
        env.put(Context.SECURITY_CREDENTIALS, paasword);

        return env;
    }

    /**
     * 打开一个会话
     */
    public LdapSession open() throws NamingException {
        LdapContext context = new InitialLdapContext(config, null);
        return new LdapSessionDefault(this, context);
    }
}
