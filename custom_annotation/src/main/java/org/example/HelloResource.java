package org.example;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.io.PrintWriter;

@Path("/hello")
public class HelloResource {
        @Inject
        private VaultAnnotation vaultAnnotation;
        static String PAGE_HEADER = "<html><head><title>helloworld</title></head><body>";
        static String PAGE_FOOTER = "</body></html>";

        @GET
        @Produces(MediaType.TEXT_PLAIN)
        @Path("/{name}")
        public String doVault(@PathParam("name") String value){
            return vaultAnnotation.readSecret(value);
        }

}
