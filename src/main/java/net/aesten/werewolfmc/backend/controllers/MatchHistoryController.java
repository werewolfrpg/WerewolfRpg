package net.aesten.werewolfmc.backend.controllers;

import com.google.gson.annotations.SerializedName;
import io.javalin.http.Context;
import jakarta.persistence.TypedQuery;
import net.aesten.werewolfmc.WerewolfPlugin;
import net.aesten.werewolfmc.backend.models.MatchRecord;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.UUID;

public class MatchHistoryController {
    private final SessionFactory sessionFactory;

    public MatchHistoryController(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void apiGetMatchHistory(Context ctx) {
        try {
            Session session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            TypedQuery<MatchRecord> query = session.createQuery("from MatchRecord order by endTime desc", MatchRecord.class);
            int pageNumber = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
            int entries = ctx.queryParamAsClass("number", Integer.class).getOrDefault(50);
            int firstResult = (pageNumber - 1) * entries;
            query.setFirstResult(firstResult);
            query.setMaxResults(entries);
            List<MatchRecord> data = query.getResultList();
            TypedQuery<Long> queryEntryNumber = session.createQuery("select count(*) from MatchRecord", Long.class);
            long totalEntries = queryEntryNumber.getSingleResult();
            tx.commit();
            session.close();
            ctx.json(new MatchHistory(data, pageNumber, entries, totalEntries));
        } catch (Exception e) {
            WerewolfPlugin.logConsole("Error with api request");
            e.printStackTrace();
        }
    }

    public void apiGetMatchHistoryOfPlayer(Context ctx) {
        try {
            Session session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            TypedQuery<MatchRecord> query = session.createQuery("from MatchRecord where matchId in (select ps.matchId from PlayerStats ps where ps.playerId = :minecraft_id) order by endTime desc ", MatchRecord.class);
            int pageNumber = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
            int entries = ctx.queryParamAsClass("number", Integer.class).getOrDefault(5);
            int firstResult = (pageNumber - 1) * entries;
            UUID mcId = UUID.fromString(ctx.pathParam("minecraft_id"));
            query.setParameter("minecraft_id", mcId);
            query.setFirstResult(firstResult);
            query.setMaxResults(entries);
            List<MatchRecord> data = query.getResultList();
            TypedQuery<Long> queryEntryNumber = session.createQuery("select count(*) from PlayerStats where playerId = :minecraft_id", Long.class);
            queryEntryNumber.setParameter("minecraft_id", mcId);
            long totalEntries = queryEntryNumber.getSingleResult();
            tx.commit();
            session.close();
            ctx.json(new MatchHistory(data, pageNumber, entries, totalEntries));
        } catch (Exception e) {
            WerewolfPlugin.logConsole("Error with api request");
            e.printStackTrace();
        }
    }



    private static final class MatchHistory {
        @SerializedName("meta")
        private Metadata metadata;
        @SerializedName("data")
        private List<MatchRecord> data;

        public MatchHistory(List<MatchRecord> data, int pageNumber, int entriesPerPage, long totalEntries) {
            this.data = data;
            this.metadata = new Metadata(pageNumber, entriesPerPage, totalEntries, data.size());
        }

        public static final class Metadata {
            @SerializedName("pageNumber")
            private int pageNumber;
            @SerializedName("totalPageNumber")
            private int totalPageNumber;
            @SerializedName("entries")
            private int entries;

            public Metadata(int pageNumber, int entriesPerPage, long totalEntries, int entries) {
                int totalPages = (int) (totalEntries % entriesPerPage == 0 ? totalEntries / entriesPerPage : totalEntries / entriesPerPage + 1);
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

        public List<MatchRecord> getData() {
            return data;
        }

        public void setData(List<MatchRecord> data) {
            this.data = data;
        }
    }
}
