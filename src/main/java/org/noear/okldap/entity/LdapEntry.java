package org.noear.okldap.entity;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import java.io.Serializable;

/**
 * Ldap 节点
 *
 * @author noear
 * @since 1.0
 */
public interface LdapEntry extends Serializable {
    /**
     * 获取DN（唯一标识）
     */
    String getDn();

    /**
     * 获取CN
     */
    String getCn();

    /**
     * 获取属性
     */
    Attributes getAttributes();

    default String getAttr(String name) throws NamingException {
        if (getAttributes() == null) {
            return null;
        }

        Attribute attr = getAttributes().get(name);
        if (attr == null) {
            return null;
        }

        return attr.get().toString();
    }

    void bind(SearchResult result) throws NamingException;
}
