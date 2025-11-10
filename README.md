# Jakarta-submit

A role-based submission web application built using **Jakarta EE (Servlets, JSP, JSTL, EL)**, styled with **Bootstrap**, and powered by **PostgreSQL** for persistent storage.  
This project implements **RBAC (Role-Based Access Control)** to manage different user roles: **Admin**, **Student**, and **Instructor**.

---

## Tech Stack

- **Java (Servlet, JSP, JSTL, EL)**
- **Bootstrap** – for responsive UI design
- **PostgreSQL** – for database management
- **Jakarta EE** – for web architecture (Servlets)

---

## Features

- User **Signup** and **Login**
- **Role-Based Access Control (RBAC)**:
  - **Admin** – Manage users and system roles
  - **Student** – Submit assignments and view feedback
  - **Instructor** – Grade submissions and manage tasks

---

## Configure bootstrap

- download (click first button) bootstrap css/js bundles from [bootstrap css/js](https://getbootstrap.com/docs/5.3/getting-started/download)
- extract the .zip
- create "resources/bootstrap" folders into src/main/webapp/
- move the css/js folders to the created bootstrap directory
- You're done.

---

## Run the Web App

### In development

#### General Running Maven Commands and Tomcat Configuration Panel

![Refreshing Project](fixtures/refreshing-project.png)

![Tomcat running panel](fixtures/tomcat%20conf%20pannel.png)

```sh
mvn clean
mvn dependency:purge-local-repository
mvn install
mvn package
py deploy_script.py
```

> Consider setting tomcat environment variables, follow [this guide](./tomcat_variables.md)
> Then access **localhost:5005/Jakarta-Submit-1.0-SNAPSHOT** and make sure Tomcat is running from the Tomcat configuration panel.
> Consider changing the Tomcat port number; currently, **5005** is being used.

---

#### Running the Web App from Tomcat Server IntelliJ Plugin

![Artifact Configuration](fixtures/artifact%20conf.png)  
![Tomcat Deployment Configuration](fixtures/tomcat%20depl%20conf.png)  
![Tomcat Server Configuration](fixtures/tomcat%20server%20conf.png)

1. **Configure Artifacts:**

   - Project Structure → Artifacts → Web Application: Exploded → From Modules → Select your project’s module/name
   - Apply → OK

2. **Make sure Tomcat is configured in Project Structure:**

   - Project's SDK (e.g., `C:\Program Files\Apache Software Foundation\Tomcat 11.0`)

3. **Add Tomcat Server:**

   - Press `ALT + SHIFT + F10` → Add Tomcat Server "Local" (not TomEE)

4. **Set the Application Context:**

   - Set to Tomcat base path (same as project's SDK)
   - URL: `http://localhost:5005/Jakarta_Submit_war_exploded`
   - JRE: `C:\Program Files\JAVA\BellSoft\LibericaJDK-21-Full`
   - HTTP PORT: **5005**
   - JMX PORT: **1717** (1099 already in use by IntelliJ JMX)

5. **Configure Deployment:**
   - Under Deployment panel: Delete all artifacts, add one set from artifact project structure (`Jakarta-Submit:war exploded`)
   - Set the application context: `/Jakarta_Submit_war_exploded`
   - Apply → OK/Run

You’ll be redirected to the web browser app at: `http://localhost:5005/Jakarta_Submit_war_exploded`

---

### In production (Docker)

```bash
docker-compose up -d --build
```

- then access "localhost:8080" (further docker deployment tricks are in [Dockerfile](./Dockerfile) and [docker-compose.yml](./docker-compose.yml))

---

### In production (Render.com Deployment, for me I used free tier)

#### Step 1: Provision PostgreSQL (mine will expire October 21, 2025)

- Go to Render Dashboard → New → PostgreSQL Database.
- Create a database, note the connection URL and credentials.

#### Step 2: Set Environment Variables on Render

- Go to Web Service → Environment → Environment Variables.
- Add the following (replace with your DB credentials):

```env
DB_HOST=<render-db-host>
DB_PORT=5432
DB_NAME=<database-name>
DB_USER=<username>
DB_PASSWORD=<password>
```

- Optional: use a single `DB_URL` instead:

```
DB_URL=jdbc:postgresql://<render-db-host>:5432/<database-name>?sslmode=require
DB_USER=<username>
DB_PASSWORD=<password>
```

#### Step 3: Configure Application to Read Env Vars

- Ensure `config.properties` uses placeholders:

```properties
db.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}?sslmode=require
db.user=${DB_USER}
db.password=${DB_PASSWORD}
```

- Add `ConfigLoader` or similar logic to expand `${VAR}` to environment variables.

#### Step 4: Deploy Web Service

- Option A (GitHub-based build): push code → Render auto-builds Docker image.
- Option B (Docker Hub image): push new image → manually deploy via Render dashboard.
- Render will start Tomcat and connect to the Render PostgreSQL instance.

#### Step 5: Access Your App

- URL will be provided by Render after deployment.
- Ensure all environment variables are correct and DB is reachable.

---

## Resources used

- [Docker deployment](https://medium.com/@poojithairosha/how-to-containerize-a-java-ee-web-application-with-mysql-database-using-docker-c09388ffdba6)
- [Follow tags of official tomcat's image](https://hub.docker.com/_/tomcat)
- [mvn central repository](https://mvnrepository.com)
- [mvn repository](https://mvnrepository.com/)
- [Bootstrap](https://getbootstrap.com/)
- [ChatGPT](https://chatgpt.com/)
- [Github copilot](https://github.com/copilot)

---

## Let's Connect and Follow Each Other

**Mchiir**  
[mugishachrispin590@gmail.com](mailto:mugishachrispin590@gmail.com)  
[GitHub – @Mchiir](https://github.com/Mchiir)

---

## Contributing

Pull requests are welcome!  
Feel free to fork the repository and submit a PR to enhance features or fix bugs.
