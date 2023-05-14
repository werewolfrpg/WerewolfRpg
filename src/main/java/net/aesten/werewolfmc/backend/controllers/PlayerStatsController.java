package net.aesten.werewolfmc.backend.controllers;

import io.javalin.http.Context;
import jakarta.persistence.TypedQuery;
import net.aesten.werewolfmc.WerewolfPlugin;
import net.aesten.werewolfmc.backend.dtos.GlobalStatDTO;
import net.aesten.werewolfmc.backend.models.PlayerStats;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerStatsController {
    private final SessionFactory sessionFactory;

    public PlayerStatsController(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void apiSavePlayerStats(Context ctx) {
        try {
            PlayerStats stats = ctx.bodyAsClass(PlayerStats.class);
            savePlayerStats(stats).join();
            ctx.status(201).json(stats);
        } catch (Exception e) {
            WerewolfPlugin.logConsole("Error with api request");
            e.printStackTrace();
        }
    }

    public CompletableFuture<Void> savePlayerStats(PlayerStats stats) {
        return CompletableFuture.runAsync(() -> {
            Session session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            session.persist(stats);
            tx.commit();
            session.close();
        });
    }

    public void apiUpdateStats(Context ctx) {
        try {
            Session session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            PlayerStats newStats = ctx.bodyAsClass(PlayerStats.class);
            session.merge(newStats);
            tx.commit();
            session.close();
            ctx.status(200).json(newStats);
        } catch (Exception e) {
            WerewolfPlugin.logConsole("Error with api request");
            e.printStackTrace();
        }
    }

    public void apiDeleteStatsByMatchId(Context ctx) {
        try {
            UUID matchId = UUID.fromString(ctx.pathParam("match_id"));
            Session session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            TypedQuery<PlayerStats> query = session.createQuery("from PlayerStats where matchId = :match_id", PlayerStats.class);
            query.setParameter("match_id", matchId);
            List<PlayerStats> objectsToDelete = query.getResultList();
            for (PlayerStats stats : objectsToDelete) {
                session.remove(stats);
            }
            tx.commit();
            session.close();
            ctx.status(204);
        } catch (Exception e) {
            WerewolfPlugin.logConsole("Error with api request");
            e.printStackTrace();
        }
    }

    public void apiDeleteStatsByPlayerId(Context ctx) {
        try {
            UUID minecraftId = UUID.fromString(ctx.pathParam("minecraft_id"));
            Session session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            TypedQuery<PlayerStats> query = session.createQuery("from PlayerStats where playerId = :player_id", PlayerStats.class);
            query.setParameter("player_id", minecraftId);
            List<PlayerStats> objectsToDelete = query.getResultList();
            for (PlayerStats stats : objectsToDelete) {
                session.remove(stats);
            }
            tx.commit();
            session.close();
            ctx.status(204);
        } catch (Exception e) {
            WerewolfPlugin.logConsole("Error with api request");
            e.printStackTrace();
        }
    }

    public void apiDeleteStatsByPlayerIdAndMatchId(Context ctx) {
        try {
            UUID matchId = UUID.fromString(ctx.pathParam("match_id"));
            UUID minecraftId = UUID.fromString(ctx.pathParam("minecraft_id"));
            Session session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            TypedQuery<PlayerStats> query = session.createQuery("from PlayerStats where playerId = :player_id and matchId = :match_id", PlayerStats.class);
            query.setParameter("player_id", minecraftId);
            query.setParameter("match_id", matchId);
            List<PlayerStats> objectsToDelete = query.getResultList();
            objectsToDelete.forEach(session::remove);
            transaction.commit();
            session.close();
            ctx.status(204);
        } catch (Exception e) {
            WerewolfPlugin.logConsole("Error with api request");
            e.printStackTrace();
        }
    }

    public void apiGetAllStats(Context ctx) {
        try {
            Session session = sessionFactory.openSession();
            TypedQuery<PlayerStats> query = session.createQuery("from PlayerStats", PlayerStats.class);
            List<PlayerStats> results = query.getResultList();
            ctx.json(results);
            session.close();
        } catch (Exception e) {
            WerewolfPlugin.logConsole("Error with api request");
            e.printStackTrace();
        }
    }

    public void apiGetGlobalStatsOfPlayer(Context ctx) {
        try {
            Session session = sessionFactory.openSession();
            TypedQuery<PlayerStats> query = session.createQuery("FROM PlayerStats WHERE playerId = :uuid", PlayerStats.class);
            UUID mcId = UUID.fromString(ctx.pathParam("minecraft_id"));
            query.setParameter("uuid", mcId);
            List<PlayerStats> playerStatsList = query.getResultList();
            GlobalStatDTO globalStats = GlobalStatDTO.computeGlobalStats(playerStatsList);
            session.close();
            if (globalStats == null) {
                ctx.result("{}");
            } else {
                ctx.json(globalStats);
            }
        } catch (Exception e) {
            WerewolfPlugin.logConsole("Error with api request");
            e.printStackTrace();
        }
    }
}
