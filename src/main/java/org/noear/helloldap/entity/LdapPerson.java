package org.noear.helloldap.entity;

import lombok.Getter;
import lombok.ToString;
import org.noear.helloldap.utils.PasswordUtils;
import org.noear.helloldap.utils.TextUtils;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.nio.charset.StandardCharsets;

/**
 * Ldap 人员节点
 *
 * @author noear
 * @since 1.0
 * */
@Getter
@ToString
public class LdapPerson implements LdapNode {
    protected String dn;
    protected String cn;

    protected String uid;
    protected String uidNumber;

    protected String sn;
    protected String userPassword;
    protected String displayName;
    protected String givenName;
    protected String physicalDeliveryOfficeName;
    protected String mail;
    protected String description;

    protected String gidNumber;

    private Attributes attributes;

    public Attributes getAttributes() {
        if(attributes == null){
            attributes = new BasicAttributes();
        }

        return attributes;
    }

    /**
     * 准备
     * */
    public void prepare() {
        if (getAttributes().get("objectClass") == null) {
            Attribute objectClass = new BasicAttribute("objectClass");
            objectClass.add("inetOrgPerson");
            objectClass.add("posixAccount");
            objectClass.add("top");
            getAttributes().put(objectClass);
        }
    }

    public void setUserPassword(String userPasswordNew) {
        this.userPassword = userPasswordNew;

        String userPasswordNew2 = PasswordUtils.buildMd5Password(userPasswordNew);
        getAttributes().put("userPassword", userPasswordNew2.getBytes(StandardCharsets.UTF_8));
    }


    public void setCn(String val) {
        this.cn = val;
        getAttributes().put("cn", val);
        getAttributes().put("homeDirectory", "/home/users/" + val);

        if(getAttributes().get("sn") == null){
            setSn(val);
        }

        if(getAttributes().get("uid") == null){
            setUid(val);
        }

        if(getAttributes().get("uidNumber") == null){
            setUidNumber(String.valueOf(System.currentTimeMillis()));
        }
    }

    public void setUid(String val) {
        this.uid = val;
        getAttributes().put("uid", val);
    }

    public void setUidNumber(String val) {
        this.uidNumber = val;
        getAttributes().put("uidNumber", val);
    }

    public void setSn(String val) {
        this.sn = val;
        getAttributes().put("sn", val);
    }


    public void setDisplayName(String val) {
        this.displayName = val;
        getAttributes().put("displayName", val);
    }

    public void setGivenName(String val) {
        this.givenName = val;
        getAttributes().put("givenName", val);
    }

    public void setPhysicalDeliveryOfficeName(String val) {
        this.physicalDeliveryOfficeName = val;
        getAttributes().put("physicalDeliveryOfficeName", val);
    }

    public void setMail(String val) {
        this.mail = val;
        getAttributes().put("mail", val);
    }

    public void setDescription(String val) {
        this.description = val;
        getAttributes().put("description", val);
    }

    public void setGidNumber(String val) {
        this.gidNumber = val;
        getAttributes().put("gidNumber", val);
    }

    @Override
    public void bind(SearchResult result) throws NamingException {
        try {
            this.dn = result.getNameInNamespace();
        } catch (UnsupportedOperationException e) {
            this.dn = result.getName();
        }
        this.attributes = result.getAttributes();

        NamingEnumeration<String> keys = getAttributes().getIDs();

        while (keys.hasMore()) {
            String key = keys.next();
            Attribute val = getAttributes().get(key);

            if ("uid".equals(key)) {
                this.uid = (val.get().toString());
            } else if ("cn".equals(key)) {
                this.cn = (val.get().toString());
            } else if ("sn".equals(key)) {
                this.sn = (val.get().toString());
            } else if ("userPassword".equals(key)) {
                this.userPassword = (new String((byte[]) val.get()));
            } else if ("displayName".equals(key)) {
                this.displayName = (val.get().toString());
            } else if ("givenName".equals(key)) {
                this.givenName = (val.get().toString());
            } else if ("physicalDeliveryOfficeName".equals(key)) {
                this.physicalDeliveryOfficeName = (val.get().toString());
            } else if ("mail".equals(key)) {
                this.mail = (val.get().toString());
            } else if ("description".equals(key)) {
                this.description = (val.get().toString());
            } else if ("uidNumber".equals(key)) {
                this.uidNumber = (val.get().toString());
            } else if ("gidNumber".equals(key)) {
                this.gidNumber = (val.get().toString());
            }
        }
    }

    /**
     * 验证密码
     */
    public boolean verifyPassword(String userPassword) {
        if (TextUtils.isEmpty(userPassword)) {
            throw new IllegalArgumentException("userPassword");
        }

        String orgPassword = userPassword;

        if (this.userPassword != null) {
            if (this.userPassword.equals(orgPassword)) {
                return true;
            }

            if (this.userPassword.equals(PasswordUtils.buildMd5Password(orgPassword))) {
                return true;
            }
        }

        return false;
    }

    /**
     * 在一个组里
     */
    public boolean inGroup(LdapGroup group) {
        if (group == null) {
            return false;
        }

        //基于dn校验
        if (dn != null) {
            if (dn.contains("cn=" + group.getCn())) {
                return true;
            }
        }

        //基于组成员校验
        return group.hahMember(this.cn);
    }
}
