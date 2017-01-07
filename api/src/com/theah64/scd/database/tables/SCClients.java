package com.theah64.scd.database.tables;

import com.sun.istack.internal.NotNull;
import com.theah64.scd.models.SCClient;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by theapache64 on 29/12/16.
 */
public class SCClients extends BaseTable<SCClient> {

    private static final String COLUMN_CLIENT_ID = "client_id";
    private static final String COLUMN_AS_TOTAL_HITS = "total_hits";
    private static SCClients instance;

    private SCClients() {
        super("sc_clients");
    }

    public static SCClients getInstance() {
        if (instance == null) {
            instance = new SCClients();
        }
        return instance;
    }

    @NotNull
    public List<SCClient> getAll() {
        List<SCClient> clients = null;
        final String query = "SELECT s.id,s.name,s.client_id,s.is_active, ((SELECT COUNT(t.id) FROM tracks t WHERE t.client_id = s.id) + (SELECT COUNT(dr.id) FROM download_requests dr WHERE dr.client_id = s.id)) AS total_hits FROM sc_clients s;";

        final Connection con = com.theah64.scd.database.Connection.getConnection();
        try {
            final Statement stmt = con.createStatement();
            final ResultSet rs = stmt.executeQuery(query);

            if (rs.first()) {

                clients = new ArrayList<>();

                do {

                    final String id = rs.getString(COLUMN_ID);
                    final String name = rs.getString(COLUMN_NAME);
                    final String clientId = rs.getString(COLUMN_CLIENT_ID);
                    final boolean isActive = rs.getBoolean(COLUMN_IS_ACTIVE);
                    final int totalHits = rs.getInt(COLUMN_AS_TOTAL_HITS);

                    clients.add(new SCClient(id, name, clientId, totalHits, isActive));

                } while (rs.next());
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (clients == null) {
            throw new IllegalArgumentException("Clients can't be null");
        }
        return clients;
    }

    public SCClient getLeastUsedClient() {

        System.out.println("Getting least-used-sc-client");

        SCClient client = null;
        final String query = "SELECT s.id,s.name,s.client_id, ((SELECT COUNT(t.id) FROM tracks t WHERE t.client_id = s.id) + (SELECT COUNT(dr.id) FROM download_requests dr WHERE dr.client_id = s.id)) AS total_hits FROM sc_clients s WHERE is_active =1 ORDER BY total_hits LIMIT 1;";

        final Connection con = com.theah64.scd.database.Connection.getConnection();
        try {
            final Statement stmt = con.createStatement();
            final ResultSet rs = stmt.executeQuery(query);

            if (rs.first()) {
                final String id = rs.getString(COLUMN_ID);
                final String name = rs.getString(COLUMN_NAME);
                final String clientId = rs.getString(COLUMN_CLIENT_ID);
                final int totalHits = rs.getInt(COLUMN_AS_TOTAL_HITS);

                client = new SCClient(id, name, clientId, totalHits, true);

                System.out.println("Found least-used-sc-client : " + client);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (client == null) {
            throw new IllegalArgumentException("No soundcloud client found");
        }

        return client;
    }
}
