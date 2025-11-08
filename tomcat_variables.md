# Run Tomcat with JNDI Environment Variables

This guide explains how to configure and access JNDI environment variables in a Java web application using **Tomcat** and **Hibernate**.

---

## 1. Prepare/Create the Context Configuration File

> create a file named "context.xml" somewhere (not in application directory, because this file will be pasted in built app from .war):

Add your JNDI environment variables inside it in this format:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Context>
    <!-- Database connection configuration -->
    <Environment name="db/url"
                 value="jdbc:postgresql://localhost:5432/proddb"
                 type="java.lang.String"
                 override="false" />

    <Environment name="db/user"
                 value="prod_user"
                 type="java.lang.String"
                 override="false" />

    <Environment name="db/password"
                 value="superSecret"
                 type="java.lang.String"
                 override="false" />
</Context>
```

---

## 2. Access JNDI Variables from HibernateUtil

In your `HibernateUtil.java`, use JNDI to read these environment variables:

```java
import javax.naming.NamingException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Look up JNDI environment
                Context initCtx = new InitialContext();
                Context envCtx = (Context) initCtx.lookup("java:comp/env");

                String dbUrl = (String) envCtx.lookup("db/url");
                String dbUser = (String) envCtx.lookup("db/user");
                String dbPassword = (String) envCtx.lookup("db/password");

                Configuration configuration = new Configuration();
                Properties settings = new Properties();

                settings.put(Environment.DRIVER, "org.postgresql.Driver");
                settings.put(Environment.URL, dbUrl);
                settings.put(Environment.USER, dbUser);
                settings.put(Environment.PASS, dbPassword);
                settings.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
                settings.put(Environment.HBM2DDL_AUTO, "update");
                settings.put(Environment.SHOW_SQL, true);

                configuration.setProperties(settings);

                // Add entity classes
                configuration.addAnnotatedClass(home.jakartasubmit.models.User.class);
                configuration.addAnnotatedClass(home.jakartasubmit.models.Task.class);
                configuration.addAnnotatedClass(home.jakartasubmit.models.Submission.class);

                ServiceRegistry serviceRegistry =
                        new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();

                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (NamingException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to read JNDI environment variables", e);
            }
        }
        return sessionFactory;
    }
}
```

---

## 3. Build the WAR File

From your project root, build your `.war` file using Maven:

```bash
mvn clean package
```

---

## 4. Deploy the WAR to Tomcat

1. Copy the generated `.war` file into Tomcat’s `webapps` directory:
   ```
   %CATALINA_HOME%\webapps\
   ```
2. Start Tomcat (you can run %CATALINA_HOME%\bin\startup.bat):
3. Tomcat automatically extracts and deploys the `.war` file.
4. copy the **context.xml** file into built app META-INF dir
   ```
   Jakarta-Submit-1.0-SNAPSHOT\META-INF\
   ```

> Then consider restarting tomcat

5. Access your application in the browser:
   ```
   http://localhost:<PORT>/Jakarta-Submit-1.0-SNAPSHOT
   ```

---

## 5. Verify the JNDI Environment Variables

> You can add debugging logs in HibernateUtil.java, to make sure this works

    ```
    System.out.println("Using DB URL: " + dbUrl);
    ```

> Then check recent tomcat logs (Recent file named like "tomcat11-stdout.2025-11-08.log")

---
