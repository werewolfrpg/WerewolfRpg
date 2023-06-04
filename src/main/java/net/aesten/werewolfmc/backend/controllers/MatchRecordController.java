package net.aesten.werewolfmc.backend.controllers;

import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import net.aesten.werewolfmc.WerewolfPlugin;
import net.aesten.werewolfmc.backend.models.MatchRecord;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

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
            Session session = null;
            try {
                session = sessionFactory.openSession();
                Transaction tx = session.beginTransaction();
                session.persist(matchRecord);
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

    public void apiUpdateMatchRecord(Context ctx) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            MatchRecord newRecord = ctx.bodyAsClass(MatchRecord.class);
            session.merge(newRecord);
            tx.commit();
            session.close();
            ctx.status(200).json(newRecord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void apiDeleteMatch(Context ctx) {
        Session session = null;
        try {
            UUID matchId = UUID.fromString(ctx.pathParam("match_id"));
            session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            MatchRecord matchRecord = session.get(MatchRecord.class, matchId);
            if (matchRecord == null) {
                throw new NotFoundResponse("Match Record not found");
            }
            session.remove(matchRecord);
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

    public void apiGetMatchRecord(Context ctx) {
        Session session = null;
        try {
            UUID matchId = UUID.fromString(ctx.pathParam("match_id"));
            session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            MatchRecord matchRecord = session.get(MatchRecord.class, matchId);
            tx.commit();
            ctx.json(matchRecord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}
