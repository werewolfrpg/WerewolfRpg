package net.aesten.werewolfmc.backend.controllers;

import com.google.gson.annotations.SerializedName;
import io.javalin.http.Context;
import jakarta.persistence.TypedQuery;
import net.aesten.werewolfmc.WerewolfPlugin;
import net.aesten.werewolfmc.backend.dtos.LeaderboardDTO;
import net.aesten.werewolfmc.backend.models.PlayerData;
import net.aesten.werewolfmc.plugin.statistics.Result;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
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
            TypedQuery<Long> queryPlayerNumber = session.createQuery("SELECT COUNT(*) FROM PlayerData", Long.class);
            long playerNumber = queryPlayerNumber.getSingleResult();

            List<LeaderboardDTO> data = new ArrayList<>();
            results.forEach(pd -> {
                TypedQuery<Long> queryWonGames = session.createQuery("SELECT COUNT(*) FROM PlayerStats WHERE playerId = :minecraft_id AND result = :victory", Long.class);
                queryWonGames.setParameter("minecraft_id", pd.getMcId());
                queryWonGames.setParameter("victory", Result.VICTORY);
                long gamesWon = queryWonGames.getSingleResult();
                TypedQuery<Long> queryLostGames = session.createQuery("SELECT COUNT(*) FROM PlayerStats WHERE playerId = :minecraft_id AND result = :defeat", Long.class);
                queryLostGames.setParameter("minecraft_id", pd.getMcId());
                queryLostGames.setParameter("defeat", Result.DEFEAT);
                long gamesLost = queryLostGames.getSingleResult();
                data.add(new LeaderboardDTO(pd, gamesWon + gamesLost, gamesWon));
            });

            session.close();
            ctx.json(new PlayerDataResponse(data, pageNumber, entries, playerNumber));
        } catch (Exception e) {
            WerewolfPlugin.logConsole("Error with api request");
            e.printStackTrace();
        }
    }

    private static final class PlayerDataResponse {
        @SerializedName("meta")
        private Metadata metadata;
        @SerializedName("data")
        private List<LeaderboardDTO> data;

        public PlayerDataResponse(List<LeaderboardDTO> data, int pageNumber, int entriesPerPage, long playerNumber) {
            this.data = data;
            this.metadata = new Metadata(pageNumber, entriesPerPage, playerNumber, data.size());
        }

        public static final class Metadata {
            @SerializedName("pageNumber")
            private int pageNumber;
            @SerializedName("totalPageNumber")
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

        public Metadata getMetadata() {
            return metadata;
        }

        public void setMetadata(Metadata metadata) {
            this.metadata = metadata;
        }

        public List<LeaderboardDTO> getData() {
            return data;
        }

        public void setData(List<LeaderboardDTO> data) {
            this.data = data;
        }
    }
}
