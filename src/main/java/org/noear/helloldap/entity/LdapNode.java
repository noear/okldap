package org.noear.helloldap.entity;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import java.io.Serializable;

/**
 * Ldap 节点
 *
 * @author noear
 * @since 1.0
 */
public interface LdapNode extends Serializable {
    /**
     * 获取CN
     * */
    String getCn();
    /**
     * 获取DN
     * */
    String getDn();

    /**
     * 获取属性
     * */
    Attributes getAttributes();

    void bind(SearchResult result) throws NamingException;
}
