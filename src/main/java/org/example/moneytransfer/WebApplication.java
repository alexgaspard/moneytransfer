package org.example.moneytransfer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class WebApplication {

    public static void main(String[] args) {
        Server server = new Server(8080);

//        ServletContextHandler contextHandler = new ServletContextHandler();
//        contextHandler.setContextPath("/");
//        contextHandler.setResourceBase("src/main/webapp/WEB-INF/web.xml");

        String webapp = "src/main/webapp";
        WebAppContext context = new WebAppContext();
        context.setDescriptor(webapp + "/WEB-INF/web.xml");
        context.setResourceBase(webapp);
        context.setContextPath("/");
        context.setParentLoaderPriority(true);

        server.setHandler(context);
//        server.setHandler(contextHandler);

//        ServletHolder holder = contextHandler.addServlet(ServletContainer.class, "/api/*");
//        holder.setInitOrder(1);
//        holder.setInitParameter("jersey.config.server.provider.packages", "AppConfig");
////        holder.setInitParameter("jersey.config.server.provider.packages", "org.example.rest");
        try {
            server.start();
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.destroy();
        }
    }
}
