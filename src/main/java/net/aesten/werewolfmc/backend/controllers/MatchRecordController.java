package net.aesten.werewolfmc.backend.controllers;

import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import jakarta.persistence.TypedQuery;
import net.aesten.werewolfmc.WerewolfPlugin;
import net.aesten.werewolfmc.backend.models.MatchRecord;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MatchRecordController {
    private final SessionFactory sessionFactory;

    public MatchRecordController(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void apiRecordMatch(Context ctx) {
        try {
            MatchRecord matchRecord = ctx.bodyAsClass(MatchRecord.class);
            recordMatch(matchRecord).join();
            ctx.status(201).json(matchRecord);
        } catch (Exception e) {
            WerewolfPlugin.logConsole("Error with api request");
            e.printStackTrace();
        }
    }

    public CompletableFuture<Void> recordMatch(MatchRecord matchRecord) {
        return CompletableFuture.runAsync(() -> {
            Session session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            session.persist(matchRecord);
            tx.commit();
            session.close();
        });
    }

    public void apiUpdateMatchRecord(Context ctx) {
        try {
            Session session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            MatchRecord newRecord = ctx.bodyAsClass(MatchRecord.class);
            session.merge(newRecord);
            tx.commit();
            session.close();
            ctx.status(200).json(newRecord);
        } catch (Exception e) {
            WerewolfPlugin.logConsole("Error with api request");
            e.printStackTrace();
        }
    }

    public void apiDeleteMatch(Context ctx) {
        try {
            UUID matchId = UUID.fromString(ctx.pathParam("match_id"));
            Session session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            MatchRecord matchRecord = session.get(MatchRecord.class, matchId);
            if (matchRecord == null) {
                throw new NotFoundResponse("Match Record not found");
            }
            session.remove(matchRecord);
            tx.commit();
            session.close();
            ctx.status(204);
        } catch (Exception e) {
            WerewolfPlugin.logConsole("Error with api request");
            e.printStackTrace();
        }
    }

    public void apiGetAllMatches(Context ctx) {
        try {
            Session session = sessionFactory.openSession();
            TypedQuery<MatchRecord> query = session.createQuery("from MatchRecord", MatchRecord.class);
            List<MatchRecord> results = query.getResultList();
            session.close();
            ctx.json(results);
        } catch (Exception e) {
            WerewolfPlugin.logConsole("Error with api request");
            e.printStackTrace();
        }
    }

    public void apiGetRecordsOfMatch(Context ctx) {
        try {
            Session session = sessionFactory.openSession();
            TypedQuery<MatchRecord> query = session.createQuery("FROM MatchRecord WHERE matchId = :match_id", MatchRecord.class);
            UUID matchId = UUID.fromString(ctx.pathParam("match_id"));
            query.setParameter("match_id", matchId);
            List<MatchRecord> results = query.getResultList();
            session.close();
            ctx.json(results);
        } catch (Exception e) {
            WerewolfPlugin.logConsole("Error with api request");
            e.printStackTrace();
        }
    }

    public void apiGetMatchHistoryOfPlayer(Context ctx) {
        try {
            Session session = sessionFactory.openSession();
            TypedQuery<MatchRecord> query = session.createQuery("FROM MatchRecord m WHERE m.matchId IN (SELECT ps.matchId FROM PlayerStats ps WHERE ps.playerId = :minecraft_id)", MatchRecord.class);
            UUID mcId = UUID.fromString(ctx.pathParam("minecraft_id"));
            query.setParameter("minecraft_id", mcId);
            List<MatchRecord> results = query.getResultList();
            session.close();
            ctx.json(results);
        } catch (Exception e) {
            WerewolfPlugin.logConsole("Error with api request");
            e.printStackTrace();
        }
    }
}
