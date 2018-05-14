package org.example;

import org.example.persistence.SQLAdapter;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

public class AppConfig extends ResourceConfig {
    public AppConfig() {
        AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {
                SQLAdapter sqlAdapter = new SQLAdapter();
                bind(sqlAdapter).to(SQLAdapter.class);
            }
        };
        register(binder);
        packages("org.example.rest"); //packages
    }
}
