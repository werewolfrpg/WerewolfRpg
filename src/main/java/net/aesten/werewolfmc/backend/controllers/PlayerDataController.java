package net.aesten.werewolfmc.backend.controllers;

import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import jakarta.persistence.TypedQuery;
import net.aesten.werewolfmc.WerewolfPlugin;
import net.aesten.werewolfmc.backend.models.PlayerData;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerDataController {
    private final SessionFactory sessionFactory;

    public PlayerDataController(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void apiRegisterPlayer(Context ctx) {
        try {
            PlayerData data = ctx.bodyAsClass(PlayerData.class);
            registerPlayer(data).join();
            ctx.status(201).json(data);
        } catch (Exception e) {
            WerewolfPlugin.logConsole("Error with api request");
            e.printStackTrace();
        }

    }

    public CompletableFuture<Void> registerPlayer(PlayerData data) {
        return CompletableFuture.runAsync(() -> {
            Session session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            session.persist(data);
            tx.commit();
            session.close();
        });
    }

    public void apiUpdatePlayer(Context ctx) {
        try {
            Session session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            PlayerData newData = ctx.bodyAsClass(PlayerData.class);
            session.merge(newData);
            tx.commit();
            session.close();
            ctx.status(200).json(newData);
        } catch (Exception e) {
            WerewolfPlugin.logConsole("Error with api request");
            e.printStackTrace();
        }
    }

    public void apiDeletePlayerByDiscordId(Context ctx) {
        try {
            long discordId = Long.parseLong(ctx.pathParam("discord_id"));
            deletePlayerByDiscordId(discordId).join();
            ctx.status(204);
        } catch (Exception e) {
            WerewolfPlugin.logConsole("Error with api request");
            e.printStackTrace();
        }
    }

    public CompletableFuture<Void> deletePlayerByDiscordId(long discordId) {
        return CompletableFuture.runAsync(() -> {
            Session session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            TypedQuery<PlayerData> query = session.createQuery("from PlayerData where dcId = :discord_id", PlayerData.class);
            query.setParameter("discord_id", discordId);
            List<PlayerData> results = query.getResultList();
            results.forEach(session::remove);
            tx.commit();
            session.close();
        });
    }

    public CompletableFuture<Void> setScoreOfPlayer(UUID minecraftId, int score) {
        return CompletableFuture.runAsync(()-> {
            Session session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            PlayerData data = session.get(PlayerData.class, minecraftId);
            if (data == null) {
                throw new NotFoundResponse("Player not found");
            }
            data.setScore(score);
            session.merge(data);
            tx.commit();
            session.close();
        });
    }

    public CompletableFuture<PlayerData> addScoreToPlayer(UUID minecraftId, int score) {
        return CompletableFuture.supplyAsync(() -> {
            Session session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            PlayerData data = session.get(PlayerData.class, minecraftId);
            if (data == null) {
                throw new NotFoundResponse("Player not found");
            }
            data.setScore(data.getScore() + score);
            session.merge(data);
            tx.commit();
            session.close();
            return data;
        });
    }

    public CompletableFuture<Integer> getScoreOfPlayer(UUID minecraftId) {
        return CompletableFuture.supplyAsync(() -> {
            Session session = sessionFactory.openSession();
            PlayerData data = session.get(PlayerData.class, minecraftId);
            if (data == null) {
                throw new NotFoundResponse("Player not found");
            }
            session.close();
            return data.getScore();
        });
    }

    public CompletableFuture<Long> getDiscordIdOfPlayer(UUID minecraftId) {
        return CompletableFuture.supplyAsync(() -> {
            Session session = sessionFactory.openSession();
            PlayerData data = session.get(PlayerData.class, minecraftId);
            if (data == null) {
                throw new NotFoundResponse("Player not found");
            }
            session.close();
            return data.getDcId();
        });
    }

    public CompletableFuture<UUID> getMinecraftIdFromDiscordId(long discordId) {
        return CompletableFuture.supplyAsync(() -> {
            Session session = sessionFactory.openSession();
            TypedQuery<PlayerData> query = session.createQuery("from PlayerData where dcId = :discord_id", PlayerData.class);
            query.setParameter("discord_id", discordId);
            List<PlayerData> results = query.getResultList();
            session.close();
            if (results.size() != 0) return results.get(0).getMcId();
            return null;
        });
    }

    public void apiGetAllPlayerData(Context ctx) {
        try{
            ctx.json(getAllPlayerData());
        } catch (Exception e) {
            WerewolfPlugin.logConsole("Error with api request");
            e.printStackTrace();
        }
    }

    public List<PlayerData> getAllPlayerData() {
        Session session = sessionFactory.openSession();
        TypedQuery<PlayerData> query = session.createQuery("from PlayerData", PlayerData.class);
        List<PlayerData> results = query.getResultList();
        session.close();
        return results;
    }

    public CompletableFuture<List<UUID>> getAllMinecraftIds() {
        return CompletableFuture.supplyAsync(() -> {
            Session session = sessionFactory.openSession();
            TypedQuery<PlayerData> query = session.createQuery("from PlayerData", PlayerData.class);
            List<UUID> results = query.getResultList().stream().map(PlayerData::getMcId).toList();
            session.close();
            return results;
        });
    }

    public CompletableFuture<List<Long>> getAllDiscordIds() {
        return CompletableFuture.supplyAsync(() -> {
            Session session = sessionFactory.openSession();
            TypedQuery<PlayerData> query = session.createQuery("from PlayerData", PlayerData.class);
            List<Long> results = query.getResultList().stream().map(PlayerData::getDcId).toList();
            session.close();
            return results;
        });
    }

    public void apiGetPlayerFromDiscordId(Context ctx) {
        try {
            Session session = sessionFactory.openSession();
            long discordId = Long.parseLong(ctx.pathParam("discord_id"));
            TypedQuery<PlayerData> query = session.createQuery("from PlayerData where dcId = :discord_id", PlayerData.class);
            query.setParameter("discord_id", discordId);
            List<PlayerData> data = query.getResultList();
            session.close();
            ctx.json(data);
        } catch (Exception e) {
            WerewolfPlugin.logConsole("Error with api request");
            e.printStackTrace();
        }
    }

    public void apiGetPlayerFromMinecraftId(Context ctx) {
        try {
            Session session = sessionFactory.openSession();
            UUID minecraftId = UUID.fromString(ctx.pathParam("minecraft_id"));
            TypedQuery<PlayerData> query = session.createQuery("from PlayerData where mcId = :minecraft_id", PlayerData.class);
            query.setParameter("minecraft_id", minecraftId);
            List<PlayerData> data = query.getResultList();
            session.close();
            ctx.json(data);
        } catch (Exception e) {
            WerewolfPlugin.logConsole("Error with api request");
            e.printStackTrace();
        }
    }


}
