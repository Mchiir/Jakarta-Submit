package home.jakartasubmit.util;

import home.jakartasubmit.models.Submission;
import home.jakartasubmit.models.Task;
import home.jakartasubmit.models.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    private static Properties props = ConfigLoader.loadProperties();

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration();
            Properties settings = new Properties();
            //POSTGRESQL

//            settings.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
            settings.put(Environment.DRIVER, "org.postgresql.Driver");
            //Postgresql
//            settings.put(Environment.URL, "jdbc:mysql://localhost:3306/mymis_db");
            settings.put(Environment.URL, props.getProperty("db.url"));
            settings.put(Environment.USER, props.getProperty("db.user"));
            settings.put(Environment.PASS, props.getProperty("db.password"));

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
        return sessionFactory;
    }
}