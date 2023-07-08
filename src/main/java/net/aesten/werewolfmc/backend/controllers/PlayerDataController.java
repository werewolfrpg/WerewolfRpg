package net.aesten.werewolfmc.backend.controllers;

import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import jakarta.persistence.TypedQuery;
import net.aesten.werewolfmc.WerewolfPlugin;
import net.aesten.werewolfmc.backend.models.PlayerData;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Collections;
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
            Session session = null;
            try {
                session = sessionFactory.openSession();
                Transaction tx = session.beginTransaction();
                session.persist(data);
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

    public void apiUpdatePlayer(Context ctx) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            PlayerData newData = ctx.bodyAsClass(PlayerData.class);
            session.merge(newData);
            tx.commit();
            ctx.status(200).json(newData);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
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
            Session session = null;
            try {
                session = sessionFactory.openSession();
                Transaction tx = session.beginTransaction();
                TypedQuery<PlayerData> query = session.createQuery("from PlayerData where dcId = :discord_id", PlayerData.class);
                query.setParameter("discord_id", discordId);
                List<PlayerData> results = query.getResultList();
                results.forEach(session::remove);
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

    public CompletableFuture<Void> setScoreOfPlayer(UUID minecraftId, int score) {
        return CompletableFuture.runAsync(()-> {
            Session session = null;
            try {
                session = sessionFactory.openSession();
                Transaction tx = session.beginTransaction();
                PlayerData data = session.get(PlayerData.class, minecraftId);
                if (data == null) {
                    throw new NotFoundResponse("Player not found");
                }
                data.setScore(score);
                session.merge(data);
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

    public CompletableFuture<PlayerData> addScoreToPlayer(UUID minecraftId, int score) {
        return CompletableFuture.supplyAsync(() -> {
            Session session = null;
            try {
                session = sessionFactory.openSession();
                Transaction tx = session.beginTransaction();
                PlayerData data = session.get(PlayerData.class, minecraftId);
                if (data == null) {
                    throw new NotFoundResponse("Player not found");
                }
                data.setScore(data.getScore() + score);
                session.merge(data);
                tx.commit();
                return data;
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

    public CompletableFuture<Integer> getScoreOfPlayer(UUID minecraftId) {
        return CompletableFuture.supplyAsync(() -> {
            PlayerData data = getPlayerDataByMinecraftId(minecraftId);
            return data != null ? data.getScore() : 0;
        });
    }

    public CompletableFuture<Long> getDiscordIdOfPlayer(UUID minecraftId) {
        return CompletableFuture.supplyAsync(() -> {
            PlayerData data = getPlayerDataByMinecraftId(minecraftId);
            return data != null ? data.getDcId() : 0;
        });
    }

    public CompletableFuture<PlayerData> getPlayerDataOfPlayer(UUID minecraftId) {
        return CompletableFuture.supplyAsync(() -> getPlayerDataByMinecraftId(minecraftId));
    }

    private PlayerData getPlayerDataByMinecraftId(UUID minecraftId) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            PlayerData data = session.get(PlayerData.class, minecraftId);
            if (data == null) {
                throw new NotFoundResponse("Player not found");
            }
            tx.commit();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public CompletableFuture<UUID> getMinecraftIdFromDiscordId(long discordId) {
        return CompletableFuture.supplyAsync(() -> {
            Session session = null;
            try {
                session = sessionFactory.openSession();
                Transaction tx = session.beginTransaction();
                TypedQuery<PlayerData> query = session.createQuery("from PlayerData where dcId = :discord_id", PlayerData.class);
                query.setParameter("discord_id", discordId);
                PlayerData result = query.getSingleResult();
                tx.commit();
                return result != null ? result.getMcId() : null;
            } catch (Exception e) {
                WerewolfPlugin.logConsole("Tried to fetch Minecraft ID from not registered Discord ID (expected behavior)");
                return null;
            } finally {
                if (session != null && session.isOpen()) {
                    session.close();
                }
            }
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

    private List<PlayerData> getAllPlayerData() {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            TypedQuery<PlayerData> query = session.createQuery("from PlayerData", PlayerData.class);
            List<PlayerData> results = query.getResultList();
            tx.commit();
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public CompletableFuture<List<UUID>> getAllMinecraftIds() {
        return CompletableFuture.supplyAsync(() -> {
            Session session = null;
            try {
                session = sessionFactory.openSession();
                Transaction tx = session.beginTransaction();
                TypedQuery<UUID> query = session.createQuery("select mcId from PlayerData", UUID.class);
                List<UUID> results = query.getResultList();
                tx.commit();
                return results;
            } catch (Exception e) {
                e.printStackTrace();
                return Collections.emptyList();
            } finally {
                if (session != null && session.isOpen()) {
                    session.close();
                }
            }
        });
    }

    public CompletableFuture<List<Long>> getAllDiscordIds() {
        return CompletableFuture.supplyAsync(() -> {
            Session session = null;
            try {
                session = sessionFactory.openSession();
                Transaction tx = session.beginTransaction();
                TypedQuery<Long> query = session.createQuery("select dcId from PlayerData", Long.class);
                List<Long> results = query.getResultList();
                tx.commit();
                return results;
            } catch (Exception e) {
                e.printStackTrace();
                return Collections.emptyList();
            } finally {
                if (session != null && session.isOpen()) {
                    session.close();
                }
            }
        });
    }

    public void apiGetPlayerFromDiscordId(Context ctx) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            long discordId = Long.parseLong(ctx.pathParam("discord_id"));
            TypedQuery<PlayerData> query = session.createQuery("from PlayerData where dcId = :discord_id", PlayerData.class);
            query.setParameter("discord_id", discordId);
            List<PlayerData> data = query.getResultList();
            tx.commit();
            ctx.json(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void apiGetPlayerFromMinecraftId(Context ctx) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            UUID minecraftId = UUID.fromString(ctx.pathParam("minecraft_id"));
            TypedQuery<PlayerData> query = session.createQuery("from PlayerData where mcId = :minecraft_id", PlayerData.class);
            query.setParameter("minecraft_id", minecraftId);
            List<PlayerData> data = query.getResultList();
            tx.commit();
            ctx.json(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public CompletableFuture<Integer> getPlayerRanking(UUID minecraftId) {
        return CompletableFuture.supplyAsync(() -> {
            Session session = null;
            try {
                session = sessionFactory.openSession();
                Transaction tx = session.beginTransaction();
                TypedQuery<UUID> query = session.createQuery("select mcId from PlayerData order by score desc", UUID.class);
                List<UUID> results = query.getResultList();
                tx.commit();
                return 1 + results.indexOf(minecraftId);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            } finally {
                if (session != null && session.isOpen()) {
                    session.close();
                }
            }
        });
    }
}
