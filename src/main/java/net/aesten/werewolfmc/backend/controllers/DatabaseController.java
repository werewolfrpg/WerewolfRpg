package net.aesten.werewolfmc.backend.controllers;

import net.aesten.werewolfmc.backend.models.PlayerData;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.concurrent.CompletableFuture;


public class DatabaseController {
    private final SessionFactory sessionFactory;

    public DatabaseController(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public CompletableFuture<Void> wipeDatabase() {
        return CompletableFuture.runAsync(() -> {
            Session session = null;
            try {
                session = sessionFactory.openSession();
                Transaction tx = session.beginTransaction();
                String deleteQuery = "DELETE FROM MatchRecord";
                session.createQuery(deleteQuery).executeUpdate();
                deleteQuery = "DELETE FROM PlayerStats";
                session.createQuery(deleteQuery).executeUpdate();
                List<PlayerData> entities = session.createQuery("FROM PlayerData ", PlayerData.class).list();
                for (PlayerData data : entities) {
                    data.setScore(0);
                    session.merge(data);
                }
                tx.commit();
                session.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (session != null && session.isOpen()) {
                    session.close();
                }
            }
        });
    }
}
