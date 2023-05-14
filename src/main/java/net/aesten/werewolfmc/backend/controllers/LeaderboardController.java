package net.aesten.werewolfmc.backend.controllers;

import com.google.gson.annotations.SerializedName;
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
            TypedQuery<Long> query2 = session.createQuery("SELECT COUNT(*) FROM PlayerData", Long.class);
            long result = query2.getSingleResult();
            session.close();
            ctx.json(new PlayerDataResponse(results, pageNumber, entries, result));
        } catch (Exception e) {
            WerewolfPlugin.logConsole("Error with api request");
            e.printStackTrace();
        }
    }

    private static final class PlayerDataResponse {
        @SerializedName("meta")
        private Metadata metadata;
        @SerializedName("data")
        private List<PlayerData> data;

        public PlayerDataResponse(List<PlayerData> data, int pageNumber, int entriesPerPage, long playerNumber) {
            this.data = data;
            this.metadata = new Metadata(pageNumber, entriesPerPage, playerNumber, data.size());
        }

        public static final class Metadata {
            @SerializedName("page_number")
            private int pageNumber;
            @SerializedName("total_page_number")
            private int totalPageNumber;
            @SerializedName("entries")
            private int entries;

            public Metadata(int pageNumber, int entriesPerPage, long playerNumber, int entries) {
                int totalPages = (int) (playerNumber % entriesPerPage == 0 ? playerNumber / entriesPerPage : playerNumber / entriesPerPage + 1);
                this.pageNumber = pageNumber;
                this.entries = entries;
                this.totalPageNumber = totalPages;
            }

            public int getPageNumber() {
                return pageNumber;
            }

            public void setPageNumber(int pageNumber) {
                this.pageNumber = pageNumber;
            }

            public int getTotalPageNumber() {
                return totalPageNumber;
            }

            public void setTotalPageNumber(int totalPageNumber) {
                this.totalPageNumber = totalPageNumber;
            }

            public int getEntries() {
                return entries;
            }

            public void setEntries(int entries) {
                this.entries = entries;
            }
        }
    }
}
