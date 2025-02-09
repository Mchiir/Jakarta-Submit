package home.jakartasubmit.DAOs;

import home.jakartasubmit.models.User;
import home.jakartasubmit.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class UserDAO {

    private Session session;

    public UserDAO() {
        this.session = HibernateUtil.getSessionFactory().openSession();
    }

    public void saveUser(User user) {
        Transaction transaction = session.beginTransaction();
        session.save(user);
        transaction.commit();
    }

    public User getUserById(Long id) {
        return session.get(User.class, id);
    }

    public List<User> getAllUsers() {
        Query<User> query = session.createQuery("FROM User", User.class);
        return query.list();
    }

    public void updateUser(User user) {
        Transaction transaction = session.beginTransaction();
        session.update(user);
        transaction.commit();
    }

    public void deleteUser(Long id) {
        Transaction transaction = session.beginTransaction();
        User user = session.get(User.class, id);
        if (user != null) {
            session.delete(user);
        }
        transaction.commit();
    }

    public void close() {
        session.close();
    }
}
