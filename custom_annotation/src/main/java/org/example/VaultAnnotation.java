package org.example;
import jakarta.annotation.PostConstruct;

import jakarta.enterprise.context.ApplicationScoped;


import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;

@ApplicationScoped
public class VaultAnnotation {
    private Vault vault;

    @PostConstruct
    public void init() {
        try {
            String vaultAddress = "http://127.0.0.1:8200";
            String vaultToken = "root";
            VaultConfig config = new VaultConfig()
                    .address(vaultAddress)
                    .token(vaultToken)
                    .build().engineVersion(1);
            this.vault = new Vault(config);
        } catch (VaultException e) {
            System.out.println("Vault initialization failed: " + e.getMessage());
        }
    }

    public String readSecret(String path) {
        String response = null;
        try {
            response = vault.logical().read(path).getData().toString();
        } catch (VaultException e){
            System.out.println("not working");
        }
        return response;
    }
}
