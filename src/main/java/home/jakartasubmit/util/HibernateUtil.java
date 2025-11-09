package home.jakartasubmit.util;

import home.jakartasubmit.models.Submission;
import home.jakartasubmit.models.Task;
import home.jakartasubmit.models.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    public static SessionFactory getSessionFactory() {

        try {
            if (sessionFactory == null) {
                String dburl = null;
                String dbuser = null;
                String dbpassword = null;

                try {
                    Context initCtx = new InitialContext();
                    Context envCtx = (Context) initCtx.lookup("java:comp/env");

                    dburl = (String) envCtx.lookup("db/url");
                    dbuser = (String) envCtx.lookup("db/user");
                    dbpassword = (String) envCtx.lookup("db/password");
                } catch (NamingException ne) {
                    System.out.println("JNDI lookup failed or not configured — falling back to System environment variables.");
                }

                // Fallback to System environment if any are null
                if (dbuser == null) dbuser = System.getenv("DOCKER_PSQL_USER");
                if (dbpassword == null) dbpassword = System.getenv("DOCKER_PSQL_PASSWORD");
                if (dburl == null) dburl = System.getenv("DOCKER_PSQL_URL");

                // Safety check
                if (dburl == null || dbuser == null || dbpassword == null) {
                    throw new RuntimeException("Database configuration missing — neither JNDI nor System environment provided all variables.");
                }

//                System.out.println("DB URL: " + dburl);
//                System.out.println("DB User: " + dbuser);
//                System.out.println("DB Password: " + dbpassword);

                Configuration configuration = new Configuration();
                Properties settings = new Properties();
                //POSTGRESQL

//            settings.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
                settings.put(Environment.DRIVER, "org.postgresql.Driver");
                //Postgresql
//            settings.put(Environment.URL, "jdbc:mysql://localhost:3306/mymis_db");
                settings.put(Environment.URL,  dburl);
                settings.put(Environment.USER, dbuser);
                settings.put(Environment.PASS, dbpassword);

                //PostgreSQL
                settings.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
                settings.put(Environment.SHOW_SQL, true);
                settings.put(Environment.HBM2DDL_AUTO, "update");

                configuration.setProperties(settings);

                configuration.addAnnotatedClass(User.class);
                configuration.addAnnotatedClass(Task.class);
                configuration.addAnnotatedClass(Submission.class);

                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build();

                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize Hibernate SessionFactory", e);
        }
        return sessionFactory;
    }
}