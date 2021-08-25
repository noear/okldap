package demo;

import org.noear.helloldap.LdapClient;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * @author noear
 */
@Configuration
public class Config {
    @Bean
    public LdapClient ldapClient(@Inject("${solon.ldap}") LdapClient ldapClient) {
        return ldapClient;
    }
}
