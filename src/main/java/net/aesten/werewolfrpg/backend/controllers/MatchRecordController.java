package net.aesten.werewolfrpg.backend.controllers;

import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import net.aesten.werewolfrpg.backend.models.MatchRecord;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.UUID;

public class MatchRecordController {
    private final SessionFactory sessionFactory;

    public MatchRecordController(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void apiRecordMatch(Context ctx) {
        MatchRecord matchRecord = ctx.bodyAsClass(MatchRecord.class);
        recordMatch(matchRecord);
        ctx.status(201).json(matchRecord);
    }

    public void recordMatch(MatchRecord matchRecord) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.persist(matchRecord);
        tx.commit();
        session.close();
    }

    public void apiDeleteMatch(Context ctx) {
        UUID matchId = UUID.fromString(ctx.pathParam("match_id"));
        deleteMatch(matchId);
        ctx.status(204);
    }

    public void deleteMatch(UUID matchId) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        MatchRecord matchRecord = session.get(MatchRecord.class, matchId);
        if (matchRecord == null) {
            throw new NotFoundResponse("Match Record not found");
        }
        session.remove(matchRecord);
        tx.commit();
        session.close();
    }
}
