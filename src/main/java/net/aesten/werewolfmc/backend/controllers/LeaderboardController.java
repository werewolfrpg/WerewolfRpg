package net.aesten.werewolfmc.backend.controllers;

import io.javalin.http.Context;
import jakarta.persistence.TypedQuery;
import net.aesten.werewolfmc.WerewolfPlugin;
import net.aesten.werewolfmc.backend.models.PlayerData;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class LeaderboardController {
    private final SessionFactory sessionFactory;

    public LeaderboardController(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void apiGetPlayerIds(Context ctx) {
        try {
            Session session = sessionFactory.openSession();
            TypedQuery<PlayerData> query = session.createQuery("FROM PlayerData pd ORDER BY pd.score DESC", PlayerData.class);
            int pageNumber = Integer.parseInt(ctx.pathParam("page"));
            int entries = Integer.parseInt(ctx.pathParam("number"));
            int firstResult = (pageNumber - 1) * entries;
            query.setFirstResult(firstResult);
            query.setMaxResults(entries);
            List<PlayerData> results = query.getResultList();
            session.close();
            ctx.json(results);
        } catch (Exception e) {
            WerewolfPlugin.logConsole("Error with api request");
            e.printStackTrace();
        }
    }
}
