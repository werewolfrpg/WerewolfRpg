package net.aesten.werewolfrpg.backend.controllers;

import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import jakarta.persistence.TypedQuery;
import net.aesten.werewolfrpg.WerewolfRpg;
import net.aesten.werewolfrpg.backend.models.MatchRecord;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.UUID;

public class MatchRecordController {
    private final SessionFactory sessionFactory;

    public MatchRecordController(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void apiRecordMatch(Context ctx) {
        try {
            MatchRecord matchRecord = ctx.bodyAsClass(MatchRecord.class);
            recordMatch(matchRecord);
            ctx.status(201).json(matchRecord);
        } catch (Exception e) {
            WerewolfRpg.logConsole("Error with api request");
            e.printStackTrace();
        }
    }

    public void recordMatch(MatchRecord matchRecord) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.persist(matchRecord);
        tx.commit();
        session.close();
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
            WerewolfRpg.logConsole("Error with api request");
            e.printStackTrace();
        }
    }

    public void apiDeleteMatch(Context ctx) {
        try {
            UUID matchId = UUID.fromString(ctx.pathParam("match_id"));
            deleteMatch(matchId);
            ctx.status(204);
        } catch (Exception e) {
            WerewolfRpg.logConsole("Error with api request");
            e.printStackTrace();
        }
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

    public void apiGetAllMatches(Context ctx) {
        try {
            ctx.json(getAllMatches());
        } catch (Exception e) {
            WerewolfRpg.logConsole("Error with api request");
            e.printStackTrace();
        }
    }

    public List<MatchRecord> getAllMatches() {
        Session session = sessionFactory.openSession();
        TypedQuery<MatchRecord> query = session.createQuery("from MatchRecord", MatchRecord.class);
        List<MatchRecord> results = query.getResultList();
        session.close();
        return results;
    }
}
