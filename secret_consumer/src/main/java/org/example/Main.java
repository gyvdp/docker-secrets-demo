package org.example;

import org.springframework.vault.authentication.KubernetesAuthentication;
import org.springframework.vault.authentication.KubernetesAuthenticationOptions;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;


import java.io.*;
import java.util.Map;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class Main {

    public static boolean kube_env = true;
    public static final int port_vault = 8200;
    public static final String scheme = "http";

    public static VaultEndpoint ve = new VaultEndpoint();

    public static void main(String[] args)  {
        try {
            Thread.sleep(3000); // Wait for 5 seconds
        } catch (InterruptedException ignored) {}
        final String hostname_vault = System.getenv("VAULT_ADDR");
        final String host_sql = System.getenv("POSTGRES_ADDR");
        final String hostname_sql  = "jdbc:postgresql://"+host_sql+"/root";
        ve.setHost(hostname_vault);
        ve.setPort(port_vault);
        ve.setScheme(scheme);
        /*Setup variables*/
        String auth_token = find_token("/var/run/secrets/kubernetes.io/serviceaccount");
        if (auth_token == null) {
            System.out.println("Kube token not found");
            auth_token = find_token("/run/secrets/vault_token");
            System.out.println("Docker-compose token found");

            kube_env = false;
        }

        /*Connect to vault and read credentials for database*/
        VaultTemplate vt = vault_connection(auth_token);
        Map<String, Object> credentials = vt.read("database/creds/readonly").getData();

        /*Retrieve username and password from vault response*/
        final String username = (String) credentials.get("username");
        final String password = (String) credentials.get("password");
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
    /*Returns JWT stored at path*/
    public static String find_token(String path){
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            StringBuilder kube_token_bld = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null){
                kube_token_bld.append(line);
            }
            return kube_token_bld.toString();

        } catch (FileNotFoundException e) {
            /*No token secret found*/
            return null;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static VaultTemplate vault_connection(String auth_token){
        if (kube_env){
            KubernetesAuthenticationOptions options = KubernetesAuthenticationOptions.builder()
                    .path("kubernetes") // The Kubernetes authentication path in Vault (default is "kubernetes")
                    .role("my-app-role") // The Kubernetes authentication role in Vault
                    .jwtSupplier(() -> "YOUR_KUBE_JWT_TOKEN") // A supplier that provides the Kubernetes JWT token
                    .build();
            return new VaultTemplate(ve, new KubernetesAuthentication(options, null));
        } else {
            return new VaultTemplate(ve, new TokenAuthentication(auth_token));
        }
    }


    /*TODO*/
    /*Create docker image*/
    /*Create 3 docker services easy to run mysql, vault and consumer*/

}