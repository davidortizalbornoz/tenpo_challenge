-- Crear el esquema tempo_schema
CREATE SCHEMA IF NOT EXISTS tempo_schema;

-- Cambiar al esquema tempo_schema
SET search_path TO tempo_schema;


CREATE TABLE invoke_history
(
    trace_id character varying NOT NULL,
    span_id character varying NOT NULL,
    date_time_invoke timestamp with time zone NOT NULL,
    http_verb character varying,
    endpoint character varying NOT NULL,
    io_request_response character varying,
    http_response_code character varying,
    type_invoke character varying NOT NULL,
    id_invoke numeric,
    CONSTRAINT "PK_INVOKE" PRIMARY KEY (id_invoke)
);

ALTER TABLE IF EXISTS invoke_history
    OWNER to postgres;

-- Otorgar permisos al usuario 'myuser' en el esquema tempo_schema
GRANT ALL PRIVILEGES ON SCHEMA tempo_schema TO postgres;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA tempo_schema TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA tempo_schema TO postgres;

-- Establecer tempo_schema como el esquema por defecto para myuser
ALTER USER postgres SET search_path TO tempo_schema;