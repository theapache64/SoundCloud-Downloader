package com.theah64.scd.database.tables;

import com.theah64.scd.models.SCClient;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

    public SCClient getLeastUsedClient() {
        SCClient client = null;
        final String query = "SELECT scc.id, scc.name, scc.client_id, (COUNT(t.id) + COUNT(dr.id)) AS total_hits FROM sc_clients scc LEFT JOIN tracks t ON t.client_id = scc.id LEFT JOIN download_requests dr ON dr.client_id = scc.id GROUP BY scc.id ORDER BY total_hits LIMIT 1;";
        final Connection con = com.theah64.scd.database.Connection.getConnection();
        try {
            final Statement stmt = con.createStatement();
            final ResultSet rs = stmt.executeQuery(query);

            if (rs.first()) {
                final String id = rs.getString(COLUMN_ID);
                final String name = rs.getString(COLUMN_NAME);
                final String clientId = rs.getString(COLUMN_CLIENT_ID);
                final int totalHits = rs.getInt(COLUMN_AS_TOTAL_HITS);

                client = new SCClient(id, name, clientId, totalHits);
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
