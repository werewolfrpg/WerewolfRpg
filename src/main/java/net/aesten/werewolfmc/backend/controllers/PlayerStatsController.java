package net.aesten.werewolfmc.backend.controllers;

import io.javalin.http.Context;
import jakarta.persistence.TypedQuery;
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
            e.printStackTrace();
        }
    }

    public CompletableFuture<Void> savePlayerStats(PlayerStats stats) {
        return CompletableFuture.runAsync(() -> {
            Session session = null;
            try {
                session = sessionFactory.openSession();
                Transaction tx = session.beginTransaction();
                session.persist(stats);
                tx.commit();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (session != null && session.isOpen()) {
                    session.close();
                }
            }
        });
    }

    public void apiUpdateStats(Context ctx) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            PlayerStats newStats = ctx.bodyAsClass(PlayerStats.class);
            session.merge(newStats);
            tx.commit();
            ctx.status(200).json(newStats);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void apiDeleteStatsByMatchId(Context ctx) {
        Session session = null;
        try {
            UUID matchId = UUID.fromString(ctx.pathParam("match_id"));
            session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            TypedQuery<PlayerStats> query = session.createQuery("from PlayerStats where matchId = :match_id", PlayerStats.class);
            query.setParameter("match_id", matchId);
            List<PlayerStats> objectsToDelete = query.getResultList();
            for (PlayerStats stats : objectsToDelete) {
                session.remove(stats);
            }
            tx.commit();
            ctx.status(204);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void apiDeleteStatsByPlayerId(Context ctx) {
        Session session = null;
        try {
            UUID minecraftId = UUID.fromString(ctx.pathParam("minecraft_id"));
            session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            TypedQuery<PlayerStats> query = session.createQuery("from PlayerStats where playerId = :player_id", PlayerStats.class);
            query.setParameter("player_id", minecraftId);
            List<PlayerStats> objectsToDelete = query.getResultList();
            for (PlayerStats stats : objectsToDelete) {
                session.remove(stats);
            }
            tx.commit();
            ctx.status(204);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void apiDeleteStatsByPlayerIdAndMatchId(Context ctx) {
        Session session = null;
        try {
            UUID matchId = UUID.fromString(ctx.pathParam("match_id"));
            UUID minecraftId = UUID.fromString(ctx.pathParam("minecraft_id"));
            session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            TypedQuery<PlayerStats> query = session.createQuery("from PlayerStats where playerId = :player_id and matchId = :match_id", PlayerStats.class);
            query.setParameter("player_id", minecraftId);
            query.setParameter("match_id", matchId);
            List<PlayerStats> objectsToDelete = query.getResultList();
            objectsToDelete.forEach(session::remove);
            tx.commit();
            ctx.status(204);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void apiGetGlobalStatsOfPlayer(Context ctx) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            TypedQuery<PlayerStats> query = session.createQuery("from PlayerStats where playerId = :uuid", PlayerStats.class);
            UUID mcId = UUID.fromString(ctx.pathParam("minecraft_id"));
            query.setParameter("uuid", mcId);
            List<PlayerStats> playerStatsList = query.getResultList();
            GlobalStatDTO globalStats = GlobalStatDTO.computeGlobalStats(playerStatsList);
            tx.commit();
            if (globalStats == null) {
                ctx.status(404);
            } else {
                ctx.json(globalStats);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void apiGetAllPlayerStatsOfMatch(Context ctx) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            TypedQuery<PlayerStats> query = session.createQuery("from PlayerStats where matchId = :match_id", PlayerStats.class);
            UUID matchId = UUID.fromString(ctx.pathParam("match_id"));
            query.setParameter("match_id", matchId);
            List<PlayerStats> results = query.getResultList();
            tx.commit();
            ctx.json(results);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public CompletableFuture<PlayerStats> getPlayerStatsOfMatch(UUID playerId, UUID matchId) {
        return CompletableFuture.supplyAsync(() -> {
            Session session = null;
            PlayerStats result;
            try {
                session = sessionFactory.openSession();
                Transaction tx = session.beginTransaction();
                TypedQuery<PlayerStats> query = session.createQuery("from PlayerStats where matchId = :match_id and playerId = :minecraft_id", PlayerStats.class);
                query.setParameter("match_id", matchId);
                query.setParameter("minecraft_id", playerId);
                result = query.getSingleResult();
                tx.commit();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (session != null && session.isOpen()) {
                    session.close();
                }
            }
        });
    }
}
