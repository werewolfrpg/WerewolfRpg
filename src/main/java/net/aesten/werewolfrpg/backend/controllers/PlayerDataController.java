package net.aesten.werewolfrpg.backend.controllers;

import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import jakarta.persistence.TypedQuery;
import net.aesten.werewolfrpg.backend.models.PlayerData;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.UUID;

public class PlayerDataController {
    private final SessionFactory sessionFactory;

    public PlayerDataController(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void apiRegisterPlayer(Context ctx) {
        PlayerData data = ctx.bodyAsClass(PlayerData.class);
        registerPlayer(data);
        ctx.status(201).json(data);
    }

    public void registerPlayer(PlayerData data) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.persist(data);
        tx.commit();
        session.close();
    }

    public void apiDeletePlayerByDiscordId(Context ctx) {
        long discordId = Long.parseLong(ctx.pathParam("discord_id"));
        deletePlayerByDiscordId(discordId);
        ctx.status(204);
    }

    public void deletePlayerByDiscordId(long discordId) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        PlayerData data = session.get(PlayerData.class, discordId);
        if (data == null) {
            throw new NotFoundResponse("Player data not found");
        }
        session.remove(data);
        tx.commit();
        session.close();
    }

    public void apiSetScoreOfPlayer(Context ctx) {
        UUID minecraftId = UUID.fromString(ctx.pathParam("minecraft_id"));
        int score = Integer.parseInt(ctx.pathParam("score"));
        PlayerData data = setScoreOfPlayer(minecraftId, score);
        ctx.json(data);
    }

    public PlayerData setScoreOfPlayer(UUID minecraftId, int score) {
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
        return data;
    }

    public void apiAddScoreToPlayer(Context ctx) {
        UUID minecraftId = UUID.fromString(ctx.pathParam("minecraft_id"));
        int score = Integer.parseInt(ctx.pathParam("score"));
        PlayerData data = addScoreToPlayer(minecraftId, score);
        ctx.json(data);
    }

    public PlayerData addScoreToPlayer(UUID minecraftId, int score) {
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
    }

    public void apiGetScoreOfPlayer(Context ctx) {
        UUID minecraftId = UUID.fromString(ctx.pathParam("minecraft_id"));
        try {
            int score = getScoreOfPlayer(minecraftId);
            ctx.json(score);
        } catch (NotFoundResponse r) {
            ctx.status(404);
        }
    }

    public int getScoreOfPlayer(UUID minecraftId) {
        Session session = sessionFactory.openSession();
        PlayerData data = session.get(PlayerData.class, minecraftId);
        if (data == null) {
            throw new NotFoundResponse("Player not found");
        }
        session.close();
        return data.getScore();
    }

    public void apiGetDiscordIdOfPlayer(Context ctx) {
        UUID minecraftId = UUID.fromString(ctx.pathParam("minecraft_id"));
        try {
            long dcId = getDiscordIdOfPlayer(minecraftId);
            ctx.json(dcId);
        } catch (NotFoundResponse r) {
            ctx.status(404);
        }
    }

    public long getDiscordIdOfPlayer(UUID minecraftId) {
        Session session = sessionFactory.openSession();
        PlayerData data = session.get(PlayerData.class, minecraftId);
        if (data == null) {
            throw new NotFoundResponse("Player not found");
        }
        session.close();
        return data.getDcId();
    }

    public void apiGetMinecraftIdFromDiscordId(Context ctx) {
        long dcId = Long.parseLong(ctx.pathParam("discord_id"));
        UUID mcId = getMinecraftIdFromDiscordId(dcId);
        if (mcId == null) ctx.status(404);
        else ctx.json(mcId);
    }

    public UUID getMinecraftIdFromDiscordId(long discordId) {
        Session session = sessionFactory.openSession();
        TypedQuery<PlayerData> query = session.createQuery("from PlayerData where dcId = :discord_id", PlayerData.class);
        query.setParameter("discord_id", discordId);
        List<PlayerData> results = query.getResultList();
        session.close();
        if (results.size() != 0) return results.get(0).getMcId();
        return null;
    }

    public void apiGetAllPlayerData(Context ctx) {
        ctx.json(getAllPlayerData());
    }

    public List<PlayerData> getAllPlayerData() {
        Session session = sessionFactory.openSession();
        TypedQuery<PlayerData> query = session.createQuery("from PlayerData", PlayerData.class);
        List<PlayerData> results = query.getResultList();
        session.close();
        return results;
    }

    public void apiGetAllMinecraftIds(Context ctx) {
        ctx.json(getAllMinecraftIds());
    }

    public List<UUID> getAllMinecraftIds() {
        Session session = sessionFactory.openSession();
        TypedQuery<PlayerData> query = session.createQuery("from PlayerData", PlayerData.class);
        List<UUID> results = query.getResultList().stream().map(PlayerData::getMcId).toList();
        session.close();
        return results;
    }

    public void apiGetAllDiscordIds(Context ctx) {
        ctx.json(getAllDiscordIds());
    }

    public List<Long> getAllDiscordIds() {
        Session session = sessionFactory.openSession();
        TypedQuery<PlayerData> query = session.createQuery("from PlayerData", PlayerData.class);
        List<Long> results = query.getResultList().stream().map(PlayerData::getDcId).toList();
        session.close();
        return results;
    }
}