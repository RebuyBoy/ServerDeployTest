package com.rakecounter.dtos;

import com.rakecounter.models.Session;

import java.util.ArrayList;
import java.util.List;

public class Sessions {
    Session session;
    private List<Session> sessions;

    public Sessions() {
        this.sessions = new ArrayList<>();
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }
}
