package org.example;
import jakarta.annotation.PostConstruct;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import jakarta.inject.Named;

@Named("myBean")
@ApplicationScoped
public class VaultAnnotation {
    public VaultAnnotation() {
    }
    @Inject
    private Vault vault;

    @PostConstruct
    public void init() {
        try {
            String vaultAddress = "http://127.0.0.1:8200";
            String vaultToken = "root";
            VaultConfig config = new VaultConfig()
                    .address(vaultAddress)
                    .token(vaultToken)
                    .build();
            this.vault = new Vault(config);
        } catch (VaultException e) {
            System.out.println("Vault initialization failed: " + e.getMessage());
        }
    }

    public String readSecret(String path) {
        String response = null;
        try {
            response = vault.logical().read(path).toString();
        } catch (VaultException e){
            System.out.println("not working");
        }
        return response;
    }

    public String hello(){
        return "zohn";
    }
}
