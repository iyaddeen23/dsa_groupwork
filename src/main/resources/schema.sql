-- Ghana Smart Service Operations Optimizer
-- University Campus Service Hub — SQLite schema (6 tables)
-- Loaded by gh.edu.campushub.db.DatabaseInitializer

PRAGMA foreign_keys = ON;

DROP TABLE IF EXISTS audit_events;
DROP TABLE IF EXISTS algorithm_runs;
DROP TABLE IF EXISTS service_requests;
DROP TABLE IF EXISTS resources;
DROP TABLE IF EXISTS roads;
DROP TABLE IF EXISTS locations;

CREATE TABLE locations (
    location_id     TEXT PRIMARY KEY,
    name            TEXT NOT NULL,
    area            TEXT NOT NULL,
    location_type   TEXT NOT NULL,
    x_coord         REAL NOT NULL,
    y_coord         REAL NOT NULL
);

CREATE TABLE roads (
    road_id             TEXT PRIMARY KEY,
    from_location_id    TEXT NOT NULL REFERENCES locations(location_id),
    to_location_id      TEXT NOT NULL REFERENCES locations(location_id),
    distance_km         REAL NOT NULL,
    travel_time_min     REAL NOT NULL,
    condition_weight    REAL NOT NULL
);

CREATE TABLE resources (
    resource_id         TEXT PRIMARY KEY,
    resource_type       TEXT NOT NULL,
    home_location_id    TEXT NOT NULL REFERENCES locations(location_id),
    capacity            INTEGER NOT NULL,
    availability_status TEXT NOT NULL CHECK (availability_status IN ('AVAILABLE', 'BUSY', 'OFFLINE'))
);

CREATE TABLE service_requests (
    request_id             TEXT PRIMARY KEY,
    source_location_id     TEXT NOT NULL REFERENCES locations(location_id),
    destination_location_id TEXT NOT NULL REFERENCES locations(location_id),
    category               TEXT NOT NULL,
    urgency                INTEGER NOT NULL CHECK (urgency BETWEEN 1 AND 5),
    time_submitted         TEXT NOT NULL,
    deadline               TEXT NOT NULL,
    status                 TEXT NOT NULL CHECK (status IN ('NEW', 'ASSIGNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'))
);

CREATE TABLE algorithm_runs (
    run_id              TEXT PRIMARY KEY,
    algorithm_name      TEXT NOT NULL,
    input_size          INTEGER NOT NULL,
    execution_time_ms   REAL NOT NULL,
    memory_kb           REAL,
    run_at              TEXT NOT NULL
);

-- Stack-based undo/audit log: every mutating console action is pushed here
-- so the app can pop-and-undo the most recent change (M5 stack evidence).
CREATE TABLE audit_events (
    event_id        INTEGER PRIMARY KEY AUTOINCREMENT,
    event_type      TEXT NOT NULL,
    entity_table    TEXT NOT NULL,
    entity_id       TEXT NOT NULL,
    before_state    TEXT,
    after_state     TEXT,
    created_at      TEXT NOT NULL
);

CREATE INDEX idx_roads_from ON roads(from_location_id);
CREATE INDEX idx_roads_to ON roads(to_location_id);
CREATE INDEX idx_requests_status ON service_requests(status);
CREATE INDEX idx_requests_urgency ON service_requests(urgency);
