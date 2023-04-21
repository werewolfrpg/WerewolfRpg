package net.aesten.werewolfrpg.backend.controllers;

import io.javalin.http.Context;
import jakarta.persistence.TypedQuery;
import net.aesten.werewolfrpg.backend.models.PlayerStats;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.UUID;

public class PlayerStatsController {
    private final SessionFactory sessionFactory;

    public PlayerStatsController(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void apiSavePlayerStats(Context ctx) {
        PlayerStats stats = ctx.bodyAsClass(PlayerStats.class);
        savePlayerStats(stats);
        ctx.status(201).json(stats);
    }

    public void savePlayerStats(PlayerStats stats) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.persist(stats);
        tx.commit();
        session.close();
    }

    public void apiDeleteStatsByMatchId(Context ctx) {
        UUID matchId = UUID.fromString(ctx.pathParam("match_id"));
        deleteStatsByMatchId(matchId);
        ctx.status(204);
    }

    public void deleteStatsByMatchId(UUID matchId) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        TypedQuery<PlayerStats> query = session.createQuery("from PlayerStats where matchId = :match_id", PlayerStats.class);
        query.setParameter("match_id", matchId);
        List<PlayerStats> objectsToDelete = query.getResultList();
        for (PlayerStats stats : objectsToDelete) {
            session.remove(stats);
        }
        transaction.commit();
        session.close();
    }

    public void apiDeleteStatsByPlayerId(Context ctx) {
        UUID minecraftId = UUID.fromString(ctx.pathParam("minecraft_id"));
        deleteStatsByPlayerId(minecraftId);
        ctx.status(204);
    }

    public void deleteStatsByPlayerId(UUID minecraftId) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        TypedQuery<PlayerStats> query = session.createQuery("from PlayerStats where playerId = :player_id", PlayerStats.class);
        query.setParameter("player_id", minecraftId);
        List<PlayerStats> objectsToDelete = query.getResultList();
        for (PlayerStats stats : objectsToDelete) {
            session.remove(stats);
        }
        transaction.commit();
        session.close();
    }

    public void apiDeleteStatsByPlayerIdAndMatchId(Context ctx) {
        UUID matchId = UUID.fromString(ctx.pathParam("match_id"));
        UUID minecraftId = UUID.fromString(ctx.pathParam("minecraft_id"));
        deleteStatsByPlayerIdAndMatchId(minecraftId, matchId);
        ctx.status(204);
    }

    public void deleteStatsByPlayerIdAndMatchId(UUID minecraftId, UUID matchId) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        TypedQuery<PlayerStats> query = session.createQuery("from PlayerStats where playerId = :player_id and matchId = :match_id", PlayerStats.class);
        query.setParameter("player_id", minecraftId);
        query.setParameter("match_id", matchId);
        List<PlayerStats> objectsToDelete = query.getResultList();
        for (PlayerStats stats : objectsToDelete) {
            session.remove(stats);
        }
        transaction.commit();
        session.close();
    }
}
