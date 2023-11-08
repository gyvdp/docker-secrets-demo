package org.example;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.response.LogicalResponse;

public class Main {


    /*Default storage path for kubernetes service account*/
    final static String kube_path = "/var/run/secrets/kubernetes.io/serviceaccount";
    /*Default storage path for docker compose secrets*/
    final static String secrets_path = "/run/secrets/vault_token";
    static final String hostname_sql  = "jdbc:postgresql://"+System.getenv("POSTGRES_ADDR")+"/root";
    static final String hostname_vault  = "http://" + System.getenv("VAULT_ADDR") + ":8200";

    public static void main(String[] args){
        try {
            Thread.sleep(3000); // Wait for 3 seconds for docker environment to start
        } catch (InterruptedException ignored) {
            System.out.println("Can't wait to setup environment");
        }
        String username = "";
        String password = "";
        try {
            final VaultConfig config = new VaultConfig()
                    .address(hostname_vault).engineVersion(1)
                    .token(getToken()).build();
            final Vault vault = new Vault(config);
            LogicalResponse response = vault.logical().read("database/creds/readonly");
            System.out.println(response.getData());
            username = response.getData().get("username");
            password = response.getData().get("password");
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Can't build vault connector at " + hostname_vault);
        }

        /*Retrieve username and password from vault response*/
        System.out.println("Database username : " + username + " and password : " + password);
        /*Attempt to connect database*/
        try {
            System.out.println("Database connection attempt");
            Connection connection = DriverManager.getConnection(hostname_sql, username, password);
            Statement statement = connection.createStatement();
            String sql = "SELECT * FROM products";
            ResultSet resultSet = statement.executeQuery(sql);

            /*If connected print statement results*/
            while(resultSet.next()) {
                String productName = resultSet.getString("name");
                System.out.println("Product name: " + productName);
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the vault authentication token.
     * @return vault authentication token.
     */
    public static String getToken() {
        boolean kube_env = false;
        try (BufferedReader br = new BufferedReader(new FileReader(getPath(kube_env)))) {
            StringBuilder kube_token_bld = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                kube_token_bld.append(line);
            }
            return kube_token_bld.toString();

        } catch (FileNotFoundException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns the vault token path.
     * @param kube_env True if kubernetes service account.
     * @return Path to the token.
     */
    private static String getPath(boolean kube_env) {
        return kube_env? kube_path : secrets_path;
    }
}