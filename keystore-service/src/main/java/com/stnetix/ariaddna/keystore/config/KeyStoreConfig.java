package com.stnetix.ariaddna.keystore.config;

import com.stnetix.ariaddna.keystore.IKeyStore;
import com.stnetix.ariaddna.keystore.impl.KeyStoreService;
import com.stnetix.ariaddna.keystore.utils.CertFactory;
import com.stnetix.ariaddna.keystore.utils.IPersistHelper;
import com.stnetix.ariaddna.keystore.utils.KeyFactory;
import com.stnetix.ariaddna.keystore.utils.PersistHelperImpl;
import com.stnetix.ariaddna.persistence.utils.AppConfiguration;
import org.springframework.context.annotation.*;

/**
 * Created by alexkotov on 01.06.17.
 */
@Configuration
@Import(AppConfiguration.class)
public class KeyStoreConfig {
    @Bean
    public IKeyStore keyStore(){
        return new KeyStoreService();
    }

    @Bean
    public IPersistHelper persistHelper(){
        return new PersistHelperImpl();
    }

    @Bean
    public CertFactory certFactory(IPersistHelper persistHelper){
        return new CertFactory(persistHelper);

    }

    @Bean
    public KeyFactory keyFactory(IPersistHelper persistHelper, CertFactory certFactory){
        return new KeyFactory(persistHelper, certFactory);
    }

}
