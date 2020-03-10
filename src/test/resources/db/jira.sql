--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.15
-- Dumped by pg_dump version 9.6.8

-- Started on 2020-03-09 03:15:41 UTC

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 5745 (class 1262 OID 16404)
-- Name: jira; Type: DATABASE; Schema: -; Owner: -
--

-- CREATE DATABASE jira WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'C' LC_CTYPE = 'C';


-- \connect jira

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 1 (class 3079 OID 13308)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 5747 (class 0 OID 0)
-- Dependencies: 1
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 492 (class 1259 OID 19979)
-- Name: AO_0A5972_NOTIFICATION_SETTING; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_0A5972_NOTIFICATION_SETTING" (
    "ID" bigint NOT NULL,
    "KEY" character varying(255) NOT NULL,
    "TYPE" character varying(255) NOT NULL,
    "USER_KEY" character varying(255) NOT NULL,
    "VALUE" character varying(255) NOT NULL
);


--
-- TOC entry 491 (class 1259 OID 19977)
-- Name: AO_0A5972_NOTIFICATION_SETTING_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_0A5972_NOTIFICATION_SETTING_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5748 (class 0 OID 0)
-- Dependencies: 491
-- Name: AO_0A5972_NOTIFICATION_SETTING_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_0A5972_NOTIFICATION_SETTING_ID_seq" OWNED BY public."AO_0A5972_NOTIFICATION_SETTING"."ID";


--
-- TOC entry 493 (class 1259 OID 19988)
-- Name: AO_0A5972_PUSH_REGISTRATION; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_0A5972_PUSH_REGISTRATION" (
    "BUILD" character varying(255) NOT NULL,
    "ENDPOINT" character varying(450),
    "ID" character varying(255) NOT NULL,
    "OS" character varying(255) NOT NULL,
    "TOKEN" character varying(450),
    "USER_KEY" character varying(255) NOT NULL
);


--
-- TOC entry 494 (class 1259 OID 19998)
-- Name: AO_0AC321_RECOMMENDATION_AO; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_0AC321_RECOMMENDATION_AO" (
    "CATEGORY" character varying(255),
    "CUSTOM_FIELD_ID" bigint,
    "ID" character varying(255) NOT NULL,
    "NAME" character varying(255),
    "PERFORMANCE_IMPACT" double precision,
    "PROJECT_IDS" text,
    "RESOLVED" boolean,
    "TYPE" character varying(255)
);


--
-- TOC entry 505 (class 1259 OID 20077)
-- Name: AO_21D670_WHITELIST_RULES; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_21D670_WHITELIST_RULES" (
    "ALLOWINBOUND" boolean,
    "EXPRESSION" text NOT NULL,
    "ID" integer NOT NULL,
    "TYPE" character varying(255) NOT NULL
);


--
-- TOC entry 504 (class 1259 OID 20075)
-- Name: AO_21D670_WHITELIST_RULES_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_21D670_WHITELIST_RULES_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5749 (class 0 OID 0)
-- Dependencies: 504
-- Name: AO_21D670_WHITELIST_RULES_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_21D670_WHITELIST_RULES_ID_seq" OWNED BY public."AO_21D670_WHITELIST_RULES"."ID";


--
-- TOC entry 484 (class 1259 OID 19912)
-- Name: AO_21F425_MESSAGE_AO; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_21F425_MESSAGE_AO" (
    "CONTENT" text NOT NULL,
    "ID" character varying(255) NOT NULL
);


--
-- TOC entry 483 (class 1259 OID 19903)
-- Name: AO_21F425_MESSAGE_MAPPING_AO; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_21F425_MESSAGE_MAPPING_AO" (
    "ID" integer NOT NULL,
    "MESSAGE_ID" character varying(255) NOT NULL,
    "USER_HASH" character varying(255) NOT NULL
);


--
-- TOC entry 482 (class 1259 OID 19901)
-- Name: AO_21F425_MESSAGE_MAPPING_AO_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_21F425_MESSAGE_MAPPING_AO_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5750 (class 0 OID 0)
-- Dependencies: 482
-- Name: AO_21F425_MESSAGE_MAPPING_AO_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_21F425_MESSAGE_MAPPING_AO_ID_seq" OWNED BY public."AO_21F425_MESSAGE_MAPPING_AO"."ID";


--
-- TOC entry 481 (class 1259 OID 19892)
-- Name: AO_21F425_USER_PROPERTY_AO; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_21F425_USER_PROPERTY_AO" (
    "ID" integer NOT NULL,
    "KEY" character varying(255) NOT NULL,
    "USER" character varying(255) NOT NULL,
    "VALUE" character varying(255) NOT NULL
);


--
-- TOC entry 480 (class 1259 OID 19890)
-- Name: AO_21F425_USER_PROPERTY_AO_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_21F425_USER_PROPERTY_AO_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5751 (class 0 OID 0)
-- Dependencies: 480
-- Name: AO_21F425_USER_PROPERTY_AO_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_21F425_USER_PROPERTY_AO_ID_seq" OWNED BY public."AO_21F425_USER_PROPERTY_AO"."ID";


--
-- TOC entry 365 (class 1259 OID 19057)
-- Name: AO_38321B_CUSTOM_CONTENT_LINK; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_38321B_CUSTOM_CONTENT_LINK" (
    "CONTENT_KEY" character varying(255),
    "ID" integer NOT NULL,
    "LINK_LABEL" character varying(255),
    "LINK_URL" character varying(255),
    "SEQUENCE" integer DEFAULT 0
);


--
-- TOC entry 364 (class 1259 OID 19055)
-- Name: AO_38321B_CUSTOM_CONTENT_LINK_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_38321B_CUSTOM_CONTENT_LINK_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5752 (class 0 OID 0)
-- Dependencies: 364
-- Name: AO_38321B_CUSTOM_CONTENT_LINK_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_38321B_CUSTOM_CONTENT_LINK_ID_seq" OWNED BY public."AO_38321B_CUSTOM_CONTENT_LINK"."ID";


--
-- TOC entry 419 (class 1259 OID 19435)
-- Name: AO_3B1893_LOOP_DETECTION; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_3B1893_LOOP_DETECTION" (
    "COUNTER" integer DEFAULT 0 NOT NULL,
    "EXPIRES_AT" bigint DEFAULT 0 NOT NULL,
    "ID" integer NOT NULL,
    "SENDER_EMAIL" text NOT NULL
);


--
-- TOC entry 418 (class 1259 OID 19433)
-- Name: AO_3B1893_LOOP_DETECTION_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_3B1893_LOOP_DETECTION_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5753 (class 0 OID 0)
-- Dependencies: 418
-- Name: AO_3B1893_LOOP_DETECTION_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_3B1893_LOOP_DETECTION_ID_seq" OWNED BY public."AO_3B1893_LOOP_DETECTION"."ID";


--
-- TOC entry 519 (class 1259 OID 20188)
-- Name: AO_4789DD_HEALTH_CHECK_STATUS; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_4789DD_HEALTH_CHECK_STATUS" (
    "APPLICATION_NAME" character varying(255),
    "COMPLETE_KEY" character varying(255),
    "DESCRIPTION" text,
    "FAILED_DATE" timestamp without time zone,
    "FAILURE_REASON" text,
    "ID" integer NOT NULL,
    "IS_HEALTHY" boolean,
    "IS_RESOLVED" boolean,
    "RESOLVED_DATE" timestamp without time zone,
    "SEVERITY" character varying(255),
    "STATUS_NAME" character varying(255) NOT NULL
);


--
-- TOC entry 518 (class 1259 OID 20186)
-- Name: AO_4789DD_HEALTH_CHECK_STATUS_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_4789DD_HEALTH_CHECK_STATUS_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5754 (class 0 OID 0)
-- Dependencies: 518
-- Name: AO_4789DD_HEALTH_CHECK_STATUS_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_4789DD_HEALTH_CHECK_STATUS_ID_seq" OWNED BY public."AO_4789DD_HEALTH_CHECK_STATUS"."ID";


--
-- TOC entry 521 (class 1259 OID 20199)
-- Name: AO_4789DD_PROPERTIES; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_4789DD_PROPERTIES" (
    "ID" integer NOT NULL,
    "PROPERTY_NAME" character varying(255) NOT NULL,
    "PROPERTY_VALUE" character varying(255) NOT NULL
);


--
-- TOC entry 520 (class 1259 OID 20197)
-- Name: AO_4789DD_PROPERTIES_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_4789DD_PROPERTIES_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5755 (class 0 OID 0)
-- Dependencies: 520
-- Name: AO_4789DD_PROPERTIES_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_4789DD_PROPERTIES_ID_seq" OWNED BY public."AO_4789DD_PROPERTIES"."ID";


--
-- TOC entry 525 (class 1259 OID 20224)
-- Name: AO_4789DD_READ_NOTIFICATIONS; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_4789DD_READ_NOTIFICATIONS" (
    "ID" integer NOT NULL,
    "IS_SNOOZED" boolean,
    "NOTIFICATION_ID" integer NOT NULL,
    "SNOOZE_COUNT" integer,
    "SNOOZE_DATE" timestamp without time zone,
    "USER_KEY" character varying(255) NOT NULL
);


--
-- TOC entry 524 (class 1259 OID 20222)
-- Name: AO_4789DD_READ_NOTIFICATIONS_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_4789DD_READ_NOTIFICATIONS_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5756 (class 0 OID 0)
-- Dependencies: 524
-- Name: AO_4789DD_READ_NOTIFICATIONS_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_4789DD_READ_NOTIFICATIONS_ID_seq" OWNED BY public."AO_4789DD_READ_NOTIFICATIONS"."ID";


--
-- TOC entry 523 (class 1259 OID 20210)
-- Name: AO_4789DD_TASK_MONITOR; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_4789DD_TASK_MONITOR" (
    "CLUSTERED_TASK_ID" character varying(255),
    "CREATED_TIMESTAMP" bigint DEFAULT 0,
    "ID" integer NOT NULL,
    "NODE_ID" character varying(255),
    "PROGRESS_MESSAGE" text,
    "PROGRESS_PERCENTAGE" integer,
    "SERIALIZED_ERRORS" text,
    "SERIALIZED_WARNINGS" text,
    "TASK_ID" character varying(255) NOT NULL,
    "TASK_MONITOR_KIND" character varying(255),
    "TASK_STATUS" text
);


--
-- TOC entry 522 (class 1259 OID 20208)
-- Name: AO_4789DD_TASK_MONITOR_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_4789DD_TASK_MONITOR_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5757 (class 0 OID 0)
-- Dependencies: 522
-- Name: AO_4789DD_TASK_MONITOR_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_4789DD_TASK_MONITOR_ID_seq" OWNED BY public."AO_4789DD_TASK_MONITOR"."ID";


--
-- TOC entry 496 (class 1259 OID 20008)
-- Name: AO_4AEACD_WEBHOOK_DAO; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_4AEACD_WEBHOOK_DAO" (
    "ENABLED" boolean,
    "ENCODED_EVENTS" text,
    "FILTER" text,
    "ID" integer NOT NULL,
    "JQL" character varying(255),
    "LAST_UPDATED" timestamp without time zone NOT NULL,
    "LAST_UPDATED_USER" character varying(255) NOT NULL,
    "NAME" text NOT NULL,
    "REGISTRATION_METHOD" character varying(255) NOT NULL,
    "URL" text NOT NULL,
    "PARAMETERS" text,
    "EXCLUDE_ISSUE_DETAILS" boolean
);


--
-- TOC entry 495 (class 1259 OID 20006)
-- Name: AO_4AEACD_WEBHOOK_DAO_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_4AEACD_WEBHOOK_DAO_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5758 (class 0 OID 0)
-- Dependencies: 495
-- Name: AO_4AEACD_WEBHOOK_DAO_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_4AEACD_WEBHOOK_DAO_ID_seq" OWNED BY public."AO_4AEACD_WEBHOOK_DAO"."ID";


--
-- TOC entry 363 (class 1259 OID 19045)
-- Name: AO_550953_SHORTCUT; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_550953_SHORTCUT" (
    "ID" integer NOT NULL,
    "NAME" character varying(255),
    "PROJECT_ID" bigint,
    "SHORTCUT_URL" text,
    "URL" character varying(255),
    "ICON" character varying(255)
);


--
-- TOC entry 362 (class 1259 OID 19043)
-- Name: AO_550953_SHORTCUT_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_550953_SHORTCUT_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5759 (class 0 OID 0)
-- Dependencies: 362
-- Name: AO_550953_SHORTCUT_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_550953_SHORTCUT_ID_seq" OWNED BY public."AO_550953_SHORTCUT"."ID";


--
-- TOC entry 517 (class 1259 OID 20152)
-- Name: AO_563AEE_ACTIVITY_ENTITY; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_563AEE_ACTIVITY_ENTITY" (
    "ACTIVITY_ID" bigint NOT NULL,
    "ACTOR_ID" integer,
    "CONTENT" text,
    "GENERATOR_DISPLAY_NAME" character varying(255),
    "GENERATOR_ID" character varying(450),
    "ICON_ID" integer,
    "ID" character varying(450),
    "ISSUE_KEY" character varying(255),
    "OBJECT_ID" integer,
    "POSTER" character varying(255),
    "PROJECT_KEY" character varying(255),
    "PUBLISHED" timestamp without time zone,
    "TARGET_ID" integer,
    "TITLE" character varying(255),
    "URL" character varying(450),
    "USERNAME" character varying(255),
    "VERB" character varying(450)
);


--
-- TOC entry 516 (class 1259 OID 20150)
-- Name: AO_563AEE_ACTIVITY_ENTITY_ACTIVITY_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_563AEE_ACTIVITY_ENTITY_ACTIVITY_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5760 (class 0 OID 0)
-- Dependencies: 516
-- Name: AO_563AEE_ACTIVITY_ENTITY_ACTIVITY_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_563AEE_ACTIVITY_ENTITY_ACTIVITY_ID_seq" OWNED BY public."AO_563AEE_ACTIVITY_ENTITY"."ACTIVITY_ID";


--
-- TOC entry 509 (class 1259 OID 20100)
-- Name: AO_563AEE_ACTOR_ENTITY; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_563AEE_ACTOR_ENTITY" (
    "FULL_NAME" character varying(255),
    "ID" integer NOT NULL,
    "PROFILE_PAGE_URI" character varying(450),
    "PROFILE_PICTURE_URI" character varying(450),
    "USERNAME" character varying(255)
);


--
-- TOC entry 508 (class 1259 OID 20098)
-- Name: AO_563AEE_ACTOR_ENTITY_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_563AEE_ACTOR_ENTITY_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5761 (class 0 OID 0)
-- Dependencies: 508
-- Name: AO_563AEE_ACTOR_ENTITY_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_563AEE_ACTOR_ENTITY_ID_seq" OWNED BY public."AO_563AEE_ACTOR_ENTITY"."ID";


--
-- TOC entry 511 (class 1259 OID 20111)
-- Name: AO_563AEE_MEDIA_LINK_ENTITY; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_563AEE_MEDIA_LINK_ENTITY" (
    "DURATION" integer,
    "HEIGHT" integer,
    "ID" integer NOT NULL,
    "URL" character varying(450),
    "WIDTH" integer
);


--
-- TOC entry 510 (class 1259 OID 20109)
-- Name: AO_563AEE_MEDIA_LINK_ENTITY_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_563AEE_MEDIA_LINK_ENTITY_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5762 (class 0 OID 0)
-- Dependencies: 510
-- Name: AO_563AEE_MEDIA_LINK_ENTITY_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_563AEE_MEDIA_LINK_ENTITY_ID_seq" OWNED BY public."AO_563AEE_MEDIA_LINK_ENTITY"."ID";


--
-- TOC entry 513 (class 1259 OID 20119)
-- Name: AO_563AEE_OBJECT_ENTITY; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_563AEE_OBJECT_ENTITY" (
    "CONTENT" character varying(255),
    "DISPLAY_NAME" character varying(255),
    "ID" integer NOT NULL,
    "IMAGE_ID" integer,
    "OBJECT_ID" character varying(450),
    "OBJECT_TYPE" character varying(450),
    "SUMMARY" character varying(255),
    "URL" character varying(450)
);


--
-- TOC entry 512 (class 1259 OID 20117)
-- Name: AO_563AEE_OBJECT_ENTITY_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_563AEE_OBJECT_ENTITY_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5763 (class 0 OID 0)
-- Dependencies: 512
-- Name: AO_563AEE_OBJECT_ENTITY_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_563AEE_OBJECT_ENTITY_ID_seq" OWNED BY public."AO_563AEE_OBJECT_ENTITY"."ID";


--
-- TOC entry 515 (class 1259 OID 20135)
-- Name: AO_563AEE_TARGET_ENTITY; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_563AEE_TARGET_ENTITY" (
    "CONTENT" character varying(255),
    "DISPLAY_NAME" character varying(255),
    "ID" integer NOT NULL,
    "IMAGE_ID" integer,
    "OBJECT_ID" character varying(450),
    "OBJECT_TYPE" character varying(450),
    "SUMMARY" character varying(255),
    "URL" character varying(450)
);


--
-- TOC entry 514 (class 1259 OID 20133)
-- Name: AO_563AEE_TARGET_ENTITY_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_563AEE_TARGET_ENTITY_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5764 (class 0 OID 0)
-- Dependencies: 514
-- Name: AO_563AEE_TARGET_ENTITY_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_563AEE_TARGET_ENTITY_ID_seq" OWNED BY public."AO_563AEE_TARGET_ENTITY"."ID";


--
-- TOC entry 477 (class 1259 OID 19861)
-- Name: AO_575BF5_DEV_SUMMARY; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_575BF5_DEV_SUMMARY" (
    "CREATED" timestamp without time zone NOT NULL,
    "ID" integer NOT NULL,
    "ISSUE_ID" bigint DEFAULT 0 NOT NULL,
    "JSON" text,
    "PROVIDER_SOURCE_ID" character varying(255) NOT NULL,
    "UPDATED" timestamp without time zone NOT NULL
);


--
-- TOC entry 476 (class 1259 OID 19859)
-- Name: AO_575BF5_DEV_SUMMARY_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_575BF5_DEV_SUMMARY_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5765 (class 0 OID 0)
-- Dependencies: 476
-- Name: AO_575BF5_DEV_SUMMARY_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_575BF5_DEV_SUMMARY_ID_seq" OWNED BY public."AO_575BF5_DEV_SUMMARY"."ID";


--
-- TOC entry 475 (class 1259 OID 19850)
-- Name: AO_575BF5_PROCESSED_COMMITS; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_575BF5_PROCESSED_COMMITS" (
    "COMMIT_HASH" character varying(255) NOT NULL,
    "ID" integer NOT NULL,
    "METADATA_HASH" character varying(255)
);


--
-- TOC entry 474 (class 1259 OID 19848)
-- Name: AO_575BF5_PROCESSED_COMMITS_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_575BF5_PROCESSED_COMMITS_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5766 (class 0 OID 0)
-- Dependencies: 474
-- Name: AO_575BF5_PROCESSED_COMMITS_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_575BF5_PROCESSED_COMMITS_ID_seq" OWNED BY public."AO_575BF5_PROCESSED_COMMITS"."ID";


--
-- TOC entry 473 (class 1259 OID 19841)
-- Name: AO_575BF5_PROVIDER_ISSUE; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_575BF5_PROVIDER_ISSUE" (
    "ID" integer NOT NULL,
    "ISSUE_ID" bigint DEFAULT 0 NOT NULL,
    "PROVIDER_SOURCE_ID" character varying(255) NOT NULL,
    "STALE_AFTER" bigint
);


--
-- TOC entry 472 (class 1259 OID 19839)
-- Name: AO_575BF5_PROVIDER_ISSUE_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_575BF5_PROVIDER_ISSUE_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5767 (class 0 OID 0)
-- Dependencies: 472
-- Name: AO_575BF5_PROVIDER_ISSUE_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_575BF5_PROVIDER_ISSUE_ID_seq" OWNED BY public."AO_575BF5_PROVIDER_ISSUE"."ID";


--
-- TOC entry 479 (class 1259 OID 19873)
-- Name: AO_575BF5_PROVIDER_SEQ_NO; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_575BF5_PROVIDER_SEQ_NO" (
    "ID" integer NOT NULL,
    "PROVIDER_SOURCE_ID" character varying(255) NOT NULL,
    "SEQUENCE_NO" character varying(255)
);


--
-- TOC entry 478 (class 1259 OID 19871)
-- Name: AO_575BF5_PROVIDER_SEQ_NO_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_575BF5_PROVIDER_SEQ_NO_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5768 (class 0 OID 0)
-- Dependencies: 478
-- Name: AO_575BF5_PROVIDER_SEQ_NO_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_575BF5_PROVIDER_SEQ_NO_ID_seq" OWNED BY public."AO_575BF5_PROVIDER_SEQ_NO"."ID";


--
-- TOC entry 501 (class 1259 OID 20047)
-- Name: AO_587B34_GLANCE_CONFIG; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_587B34_GLANCE_CONFIG" (
    "ROOM_ID" bigint DEFAULT 0 NOT NULL,
    "STATE" character varying(255),
    "SYNC_NEEDED" boolean,
    "APPLICATION_USER_KEY" character varying(255),
    "JQL" character varying(255),
    "NAME" character varying(255)
);


--
-- TOC entry 503 (class 1259 OID 20055)
-- Name: AO_587B34_PROJECT_CONFIG; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_587B34_PROJECT_CONFIG" (
    "CONFIGURATION_GROUP_ID" character varying(255) NOT NULL,
    "ID" integer NOT NULL,
    "NAME" character varying(255),
    "NAME_UNIQUE_CONSTRAINT" character varying(255) NOT NULL,
    "PROJECT_ID" bigint DEFAULT 0 NOT NULL,
    "ROOM_ID" bigint DEFAULT 0 NOT NULL,
    "VALUE" character varying(255)
);


--
-- TOC entry 502 (class 1259 OID 20053)
-- Name: AO_587B34_PROJECT_CONFIG_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_587B34_PROJECT_CONFIG_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5769 (class 0 OID 0)
-- Dependencies: 502
-- Name: AO_587B34_PROJECT_CONFIG_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_587B34_PROJECT_CONFIG_ID_seq" OWNED BY public."AO_587B34_PROJECT_CONFIG"."ID";


--
-- TOC entry 498 (class 1259 OID 20019)
-- Name: AO_5FB9D7_AOHIP_CHAT_LINK; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_5FB9D7_AOHIP_CHAT_LINK" (
    "ADDON_TOKEN_EXPIRY" timestamp without time zone,
    "API_URL" character varying(255),
    "CONNECT_DESCRIPTOR" text,
    "GROUP_ID" integer DEFAULT 0,
    "GROUP_NAME" character varying(255),
    "ID" integer NOT NULL,
    "OAUTH_ID" character varying(255),
    "SECRET_KEY" character varying(255),
    "SYSTEM_PASSWORD" character varying(255),
    "SYSTEM_TOKEN_EXPIRY" timestamp without time zone,
    "SYSTEM_USER" character varying(255),
    "SYSTEM_USER_TOKEN" character varying(255),
    "TOKEN" character varying(255)
);


--
-- TOC entry 497 (class 1259 OID 20017)
-- Name: AO_5FB9D7_AOHIP_CHAT_LINK_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_5FB9D7_AOHIP_CHAT_LINK_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5770 (class 0 OID 0)
-- Dependencies: 497
-- Name: AO_5FB9D7_AOHIP_CHAT_LINK_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_5FB9D7_AOHIP_CHAT_LINK_ID_seq" OWNED BY public."AO_5FB9D7_AOHIP_CHAT_LINK"."ID";


--
-- TOC entry 500 (class 1259 OID 20031)
-- Name: AO_5FB9D7_AOHIP_CHAT_USER; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_5FB9D7_AOHIP_CHAT_USER" (
    "HIP_CHAT_LINK_ID" integer,
    "HIP_CHAT_USER_ID" character varying(255),
    "HIP_CHAT_USER_NAME" character varying(255),
    "ID" integer NOT NULL,
    "REFRESH_CODE" character varying(255),
    "USER_KEY" character varying(255),
    "USER_SCOPES" character varying(255),
    "USER_TOKEN" character varying(255),
    "USER_TOKEN_EXPIRY" timestamp without time zone
);


--
-- TOC entry 499 (class 1259 OID 20029)
-- Name: AO_5FB9D7_AOHIP_CHAT_USER_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_5FB9D7_AOHIP_CHAT_USER_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5771 (class 0 OID 0)
-- Dependencies: 499
-- Name: AO_5FB9D7_AOHIP_CHAT_USER_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_5FB9D7_AOHIP_CHAT_USER_ID_seq" OWNED BY public."AO_5FB9D7_AOHIP_CHAT_USER"."ID";


--
-- TOC entry 385 (class 1259 OID 19163)
-- Name: AO_60DB71_AUDITENTRY; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_60DB71_AUDITENTRY" (
    "CATEGORY" character varying(255) NOT NULL,
    "DATA" text NOT NULL,
    "ENTITY_CLASS" character varying(255) NOT NULL,
    "ENTITY_ID" bigint NOT NULL,
    "ID" bigint NOT NULL,
    "TIME" bigint,
    "USER" character varying(255)
);


--
-- TOC entry 384 (class 1259 OID 19161)
-- Name: AO_60DB71_AUDITENTRY_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_60DB71_AUDITENTRY_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5772 (class 0 OID 0)
-- Dependencies: 384
-- Name: AO_60DB71_AUDITENTRY_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_60DB71_AUDITENTRY_ID_seq" OWNED BY public."AO_60DB71_AUDITENTRY"."ID";


--
-- TOC entry 397 (class 1259 OID 19255)
-- Name: AO_60DB71_BOARDADMINS; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_60DB71_BOARDADMINS" (
    "ID" bigint NOT NULL,
    "KEY" character varying(255) NOT NULL,
    "RAPID_VIEW_ID" bigint NOT NULL,
    "TYPE" character varying(255) NOT NULL
);


--
-- TOC entry 396 (class 1259 OID 19253)
-- Name: AO_60DB71_BOARDADMINS_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_60DB71_BOARDADMINS_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5773 (class 0 OID 0)
-- Dependencies: 396
-- Name: AO_60DB71_BOARDADMINS_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_60DB71_BOARDADMINS_ID_seq" OWNED BY public."AO_60DB71_BOARDADMINS"."ID";


--
-- TOC entry 389 (class 1259 OID 19193)
-- Name: AO_60DB71_CARDCOLOR; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_60DB71_CARDCOLOR" (
    "COLOR" character varying(255),
    "ID" bigint NOT NULL,
    "POS" integer DEFAULT 0 NOT NULL,
    "RAPID_VIEW_ID" bigint NOT NULL,
    "STRATEGY" character varying(255),
    "VAL" character varying(255)
);


--
-- TOC entry 388 (class 1259 OID 19191)
-- Name: AO_60DB71_CARDCOLOR_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_60DB71_CARDCOLOR_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5774 (class 0 OID 0)
-- Dependencies: 388
-- Name: AO_60DB71_CARDCOLOR_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_60DB71_CARDCOLOR_ID_seq" OWNED BY public."AO_60DB71_CARDCOLOR"."ID";


--
-- TOC entry 387 (class 1259 OID 19176)
-- Name: AO_60DB71_CARDLAYOUT; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_60DB71_CARDLAYOUT" (
    "FIELD_ID" character varying(255) NOT NULL,
    "ID" bigint NOT NULL,
    "MODE_NAME" character varying(255) NOT NULL,
    "POS" integer DEFAULT 0 NOT NULL,
    "RAPID_VIEW_ID" bigint NOT NULL
);


--
-- TOC entry 386 (class 1259 OID 19174)
-- Name: AO_60DB71_CARDLAYOUT_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_60DB71_CARDLAYOUT_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5775 (class 0 OID 0)
-- Dependencies: 386
-- Name: AO_60DB71_CARDLAYOUT_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_60DB71_CARDLAYOUT_ID_seq" OWNED BY public."AO_60DB71_CARDLAYOUT"."ID";


--
-- TOC entry 395 (class 1259 OID 19241)
-- Name: AO_60DB71_COLUMN; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_60DB71_COLUMN" (
    "ID" bigint NOT NULL,
    "MAXIM" double precision,
    "MINIM" double precision,
    "NAME" character varying(255),
    "POS" integer DEFAULT 0 NOT NULL,
    "RAPID_VIEW_ID" bigint NOT NULL
);


--
-- TOC entry 411 (class 1259 OID 19374)
-- Name: AO_60DB71_COLUMNSTATUS; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_60DB71_COLUMNSTATUS" (
    "COLUMN_ID" bigint NOT NULL,
    "ID" bigint NOT NULL,
    "POS" integer DEFAULT 0 NOT NULL,
    "STATUS_ID" character varying(255)
);


--
-- TOC entry 410 (class 1259 OID 19372)
-- Name: AO_60DB71_COLUMNSTATUS_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_60DB71_COLUMNSTATUS_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5776 (class 0 OID 0)
-- Dependencies: 410
-- Name: AO_60DB71_COLUMNSTATUS_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_60DB71_COLUMNSTATUS_ID_seq" OWNED BY public."AO_60DB71_COLUMNSTATUS"."ID";


--
-- TOC entry 394 (class 1259 OID 19239)
-- Name: AO_60DB71_COLUMN_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_60DB71_COLUMN_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5777 (class 0 OID 0)
-- Dependencies: 394
-- Name: AO_60DB71_COLUMN_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_60DB71_COLUMN_ID_seq" OWNED BY public."AO_60DB71_COLUMN"."ID";


--
-- TOC entry 391 (class 1259 OID 19210)
-- Name: AO_60DB71_DETAILVIEWFIELD; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_60DB71_DETAILVIEWFIELD" (
    "FIELD_ID" character varying(255) NOT NULL,
    "ID" bigint NOT NULL,
    "POS" integer DEFAULT 0 NOT NULL,
    "RAPID_VIEW_ID" bigint NOT NULL
);


--
-- TOC entry 390 (class 1259 OID 19208)
-- Name: AO_60DB71_DETAILVIEWFIELD_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_60DB71_DETAILVIEWFIELD_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5778 (class 0 OID 0)
-- Dependencies: 390
-- Name: AO_60DB71_DETAILVIEWFIELD_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_60DB71_DETAILVIEWFIELD_ID_seq" OWNED BY public."AO_60DB71_DETAILVIEWFIELD"."ID";


--
-- TOC entry 399 (class 1259 OID 19271)
-- Name: AO_60DB71_ESTIMATESTATISTIC; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_60DB71_ESTIMATESTATISTIC" (
    "FIELD_ID" character varying(255),
    "ID" bigint NOT NULL,
    "RAPID_VIEW_ID" bigint NOT NULL,
    "TYPE_ID" character varying(255) NOT NULL
);


--
-- TOC entry 398 (class 1259 OID 19269)
-- Name: AO_60DB71_ESTIMATESTATISTIC_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_60DB71_ESTIMATESTATISTIC_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5779 (class 0 OID 0)
-- Dependencies: 398
-- Name: AO_60DB71_ESTIMATESTATISTIC_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_60DB71_ESTIMATESTATISTIC_ID_seq" OWNED BY public."AO_60DB71_ESTIMATESTATISTIC"."ID";


--
-- TOC entry 369 (class 1259 OID 19081)
-- Name: AO_60DB71_ISSUERANKING; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_60DB71_ISSUERANKING" (
    "CUSTOM_FIELD_ID" bigint DEFAULT 0 NOT NULL,
    "ID" bigint NOT NULL,
    "ISSUE_ID" bigint DEFAULT 0 NOT NULL,
    "NEXT_ID" bigint
);


--
-- TOC entry 375 (class 1259 OID 19110)
-- Name: AO_60DB71_ISSUERANKINGLOG; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_60DB71_ISSUERANKINGLOG" (
    "CUSTOM_FIELD_ID" bigint,
    "ID" bigint NOT NULL,
    "ISSUE_ID" bigint,
    "LOG_TYPE" character varying(255),
    "NEW_NEXT_ID" bigint,
    "NEW_PREVIOUS_ID" bigint,
    "OLD_NEXT_ID" bigint,
    "OLD_PREVIOUS_ID" bigint,
    "SANITY_CHECKED" character varying(255)
);


--
-- TOC entry 374 (class 1259 OID 19108)
-- Name: AO_60DB71_ISSUERANKINGLOG_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_60DB71_ISSUERANKINGLOG_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5780 (class 0 OID 0)
-- Dependencies: 374
-- Name: AO_60DB71_ISSUERANKINGLOG_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_60DB71_ISSUERANKINGLOG_ID_seq" OWNED BY public."AO_60DB71_ISSUERANKINGLOG"."ID";


--
-- TOC entry 368 (class 1259 OID 19079)
-- Name: AO_60DB71_ISSUERANKING_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_60DB71_ISSUERANKING_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5781 (class 0 OID 0)
-- Dependencies: 368
-- Name: AO_60DB71_ISSUERANKING_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_60DB71_ISSUERANKING_ID_seq" OWNED BY public."AO_60DB71_ISSUERANKING"."ID";


--
-- TOC entry 377 (class 1259 OID 19121)
-- Name: AO_60DB71_LEXORANK; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_60DB71_LEXORANK" (
    "BUCKET" integer DEFAULT 0,
    "FIELD_ID" bigint DEFAULT 0 NOT NULL,
    "ID" bigint NOT NULL,
    "ISSUE_ID" bigint DEFAULT 0 NOT NULL,
    "LOCK_HASH" character varying(255),
    "LOCK_TIME" bigint,
    "RANK" character varying(255) NOT NULL,
    "TYPE" integer DEFAULT 0 NOT NULL
);


--
-- TOC entry 371 (class 1259 OID 19091)
-- Name: AO_60DB71_LEXORANKBALANCER; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_60DB71_LEXORANKBALANCER" (
    "DISABLE_RANK_OPERATIONS" boolean NOT NULL,
    "FIELD_ID" bigint NOT NULL,
    "ID" bigint NOT NULL,
    "REBALANCE_TIME" bigint NOT NULL
);


--
-- TOC entry 370 (class 1259 OID 19089)
-- Name: AO_60DB71_LEXORANKBALANCER_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_60DB71_LEXORANKBALANCER_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5782 (class 0 OID 0)
-- Dependencies: 370
-- Name: AO_60DB71_LEXORANKBALANCER_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_60DB71_LEXORANKBALANCER_ID_seq" OWNED BY public."AO_60DB71_LEXORANKBALANCER"."ID";


--
-- TOC entry 376 (class 1259 OID 19119)
-- Name: AO_60DB71_LEXORANK_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_60DB71_LEXORANK_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5783 (class 0 OID 0)
-- Dependencies: 376
-- Name: AO_60DB71_LEXORANK_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_60DB71_LEXORANK_ID_seq" OWNED BY public."AO_60DB71_LEXORANK"."ID";


--
-- TOC entry 413 (class 1259 OID 19393)
-- Name: AO_60DB71_NONWORKINGDAY; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_60DB71_NONWORKINGDAY" (
    "ID" bigint NOT NULL,
    "ISO8601_DATE" character varying(255) NOT NULL,
    "WORKING_DAYS_ID" bigint NOT NULL
);


--
-- TOC entry 412 (class 1259 OID 19391)
-- Name: AO_60DB71_NONWORKINGDAY_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_60DB71_NONWORKINGDAY_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5784 (class 0 OID 0)
-- Dependencies: 412
-- Name: AO_60DB71_NONWORKINGDAY_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_60DB71_NONWORKINGDAY_ID_seq" OWNED BY public."AO_60DB71_NONWORKINGDAY"."ID";


--
-- TOC entry 393 (class 1259 OID 19224)
-- Name: AO_60DB71_QUICKFILTER; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_60DB71_QUICKFILTER" (
    "DESCRIPTION" character varying(255),
    "ID" bigint NOT NULL,
    "LONG_QUERY" text,
    "NAME" character varying(255) NOT NULL,
    "POS" integer DEFAULT 0 NOT NULL,
    "QUERY" character varying(255),
    "RAPID_VIEW_ID" bigint NOT NULL
);


--
-- TOC entry 392 (class 1259 OID 19222)
-- Name: AO_60DB71_QUICKFILTER_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_60DB71_QUICKFILTER_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5785 (class 0 OID 0)
-- Dependencies: 392
-- Name: AO_60DB71_QUICKFILTER_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_60DB71_QUICKFILTER_ID_seq" OWNED BY public."AO_60DB71_QUICKFILTER"."ID";


--
-- TOC entry 381 (class 1259 OID 19147)
-- Name: AO_60DB71_RANKABLEOBJECT; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_60DB71_RANKABLEOBJECT" (
    "ID" bigint NOT NULL,
    "TYPE" character varying(255) NOT NULL
);


--
-- TOC entry 380 (class 1259 OID 19145)
-- Name: AO_60DB71_RANKABLEOBJECT_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_60DB71_RANKABLEOBJECT_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5786 (class 0 OID 0)
-- Dependencies: 380
-- Name: AO_60DB71_RANKABLEOBJECT_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_60DB71_RANKABLEOBJECT_ID_seq" OWNED BY public."AO_60DB71_RANKABLEOBJECT"."ID";


--
-- TOC entry 373 (class 1259 OID 19099)
-- Name: AO_60DB71_RAPIDVIEW; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_60DB71_RAPIDVIEW" (
    "CARD_COLOR_STRATEGY" character varying(255),
    "ID" bigint NOT NULL,
    "KAN_PLAN_ENABLED" boolean,
    "NAME" character varying(255) NOT NULL,
    "OLD_DONE_ISSUES_CUTOFF" character varying(255),
    "OWNER_USER_NAME" character varying(255) NOT NULL,
    "SAVED_FILTER_ID" bigint NOT NULL,
    "SHOW_DAYS_IN_COLUMN" boolean,
    "SHOW_EPIC_AS_PANEL" boolean,
    "SPRINTS_ENABLED" boolean,
    "SWIMLANE_STRATEGY" character varying(255)
);


--
-- TOC entry 372 (class 1259 OID 19097)
-- Name: AO_60DB71_RAPIDVIEW_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_60DB71_RAPIDVIEW_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5787 (class 0 OID 0)
-- Dependencies: 372
-- Name: AO_60DB71_RAPIDVIEW_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_60DB71_RAPIDVIEW_ID_seq" OWNED BY public."AO_60DB71_RAPIDVIEW"."ID";


--
-- TOC entry 379 (class 1259 OID 19136)
-- Name: AO_60DB71_SPRINT; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_60DB71_SPRINT" (
    "CLOSED" boolean NOT NULL,
    "COMPLETE_DATE" bigint,
    "END_DATE" bigint,
    "GOAL" text,
    "ID" bigint NOT NULL,
    "NAME" character varying(255) NOT NULL,
    "RAPID_VIEW_ID" bigint,
    "SEQUENCE" bigint,
    "STARTED" boolean,
    "START_DATE" bigint
);


--
-- TOC entry 378 (class 1259 OID 19134)
-- Name: AO_60DB71_SPRINT_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_60DB71_SPRINT_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5788 (class 0 OID 0)
-- Dependencies: 378
-- Name: AO_60DB71_SPRINT_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_60DB71_SPRINT_ID_seq" OWNED BY public."AO_60DB71_SPRINT"."ID";


--
-- TOC entry 407 (class 1259 OID 19332)
-- Name: AO_60DB71_STATSFIELD; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_60DB71_STATSFIELD" (
    "ID" bigint NOT NULL,
    "RAPID_VIEW_ID" bigint NOT NULL,
    "TYPE_ID" character varying(255) NOT NULL
);


--
-- TOC entry 406 (class 1259 OID 19330)
-- Name: AO_60DB71_STATSFIELD_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_60DB71_STATSFIELD_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5789 (class 0 OID 0)
-- Dependencies: 406
-- Name: AO_60DB71_STATSFIELD_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_60DB71_STATSFIELD_ID_seq" OWNED BY public."AO_60DB71_STATSFIELD"."ID";


--
-- TOC entry 403 (class 1259 OID 19303)
-- Name: AO_60DB71_SUBQUERY; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_60DB71_SUBQUERY" (
    "ID" bigint NOT NULL,
    "LONG_QUERY" text,
    "QUERY" character varying(255),
    "RAPID_VIEW_ID" bigint,
    "SECTION" character varying(255) NOT NULL
);


--
-- TOC entry 402 (class 1259 OID 19301)
-- Name: AO_60DB71_SUBQUERY_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_60DB71_SUBQUERY_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5790 (class 0 OID 0)
-- Dependencies: 402
-- Name: AO_60DB71_SUBQUERY_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_60DB71_SUBQUERY_ID_seq" OWNED BY public."AO_60DB71_SUBQUERY"."ID";


--
-- TOC entry 409 (class 1259 OID 19345)
-- Name: AO_60DB71_SWIMLANE; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_60DB71_SWIMLANE" (
    "DEFAULT_LANE" boolean NOT NULL,
    "DESCRIPTION" character varying(255),
    "ID" bigint NOT NULL,
    "LONG_QUERY" text,
    "NAME" character varying(255) NOT NULL,
    "POS" integer DEFAULT 0 NOT NULL,
    "QUERY" character varying(255),
    "RAPID_VIEW_ID" bigint NOT NULL
);


--
-- TOC entry 408 (class 1259 OID 19343)
-- Name: AO_60DB71_SWIMLANE_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_60DB71_SWIMLANE_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5791 (class 0 OID 0)
-- Dependencies: 408
-- Name: AO_60DB71_SWIMLANE_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_60DB71_SWIMLANE_ID_seq" OWNED BY public."AO_60DB71_SWIMLANE"."ID";


--
-- TOC entry 401 (class 1259 OID 19287)
-- Name: AO_60DB71_TRACKINGSTATISTIC; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_60DB71_TRACKINGSTATISTIC" (
    "FIELD_ID" character varying(255),
    "ID" bigint NOT NULL,
    "RAPID_VIEW_ID" bigint NOT NULL,
    "TYPE_ID" character varying(255) NOT NULL
);


--
-- TOC entry 400 (class 1259 OID 19285)
-- Name: AO_60DB71_TRACKINGSTATISTIC_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_60DB71_TRACKINGSTATISTIC_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5792 (class 0 OID 0)
-- Dependencies: 400
-- Name: AO_60DB71_TRACKINGSTATISTIC_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_60DB71_TRACKINGSTATISTIC_ID_seq" OWNED BY public."AO_60DB71_TRACKINGSTATISTIC"."ID";


--
-- TOC entry 383 (class 1259 OID 19155)
-- Name: AO_60DB71_VERSION; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_60DB71_VERSION" (
    "ID" bigint NOT NULL,
    "START_DATE" bigint,
    "VERSION_ID" bigint NOT NULL
);


--
-- TOC entry 382 (class 1259 OID 19153)
-- Name: AO_60DB71_VERSION_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_60DB71_VERSION_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5793 (class 0 OID 0)
-- Dependencies: 382
-- Name: AO_60DB71_VERSION_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_60DB71_VERSION_ID_seq" OWNED BY public."AO_60DB71_VERSION"."ID";


--
-- TOC entry 405 (class 1259 OID 19319)
-- Name: AO_60DB71_WORKINGDAYS; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_60DB71_WORKINGDAYS" (
    "FRIDAY" boolean NOT NULL,
    "ID" bigint NOT NULL,
    "MONDAY" boolean NOT NULL,
    "RAPID_VIEW_ID" bigint NOT NULL,
    "SATURDAY" boolean NOT NULL,
    "SUNDAY" boolean NOT NULL,
    "THURSDAY" boolean NOT NULL,
    "TIMEZONE" character varying(255) NOT NULL,
    "TUESDAY" boolean NOT NULL,
    "WEDNESDAY" boolean NOT NULL
);


--
-- TOC entry 404 (class 1259 OID 19317)
-- Name: AO_60DB71_WORKINGDAYS_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_60DB71_WORKINGDAYS_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5794 (class 0 OID 0)
-- Dependencies: 404
-- Name: AO_60DB71_WORKINGDAYS_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_60DB71_WORKINGDAYS_ID_seq" OWNED BY public."AO_60DB71_WORKINGDAYS"."ID";


--
-- TOC entry 486 (class 1259 OID 19925)
-- Name: AO_733371_EVENT; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_733371_EVENT" (
    "ACTION" character varying(255),
    "ACTION_ID" character varying(255),
    "CREATED" timestamp without time zone NOT NULL,
    "EVENT_BUNDLE_ID" character varying(255),
    "EVENT_TYPE" bigint NOT NULL,
    "ID" bigint NOT NULL,
    "USER_KEY" character varying(255)
);


--
-- TOC entry 485 (class 1259 OID 19923)
-- Name: AO_733371_EVENT_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_733371_EVENT_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5795 (class 0 OID 0)
-- Dependencies: 485
-- Name: AO_733371_EVENT_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_733371_EVENT_ID_seq" OWNED BY public."AO_733371_EVENT"."ID";


--
-- TOC entry 490 (class 1259 OID 19955)
-- Name: AO_733371_EVENT_PARAMETER; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_733371_EVENT_PARAMETER" (
    "EVENT_ID" bigint NOT NULL,
    "ID" bigint NOT NULL,
    "NAME" character varying(255) NOT NULL,
    "VALUE" text
);


--
-- TOC entry 489 (class 1259 OID 19953)
-- Name: AO_733371_EVENT_PARAMETER_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_733371_EVENT_PARAMETER_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5796 (class 0 OID 0)
-- Dependencies: 489
-- Name: AO_733371_EVENT_PARAMETER_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_733371_EVENT_PARAMETER_ID_seq" OWNED BY public."AO_733371_EVENT_PARAMETER"."ID";


--
-- TOC entry 488 (class 1259 OID 19939)
-- Name: AO_733371_EVENT_RECIPIENT; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_733371_EVENT_RECIPIENT" (
    "CONSUMER_NAME" character varying(255) NOT NULL,
    "CREATED" timestamp without time zone NOT NULL,
    "EVENT_ID" bigint NOT NULL,
    "ID" bigint NOT NULL,
    "SEND_DATE" timestamp without time zone,
    "STATUS" character varying(255) NOT NULL,
    "UPDATED" timestamp without time zone,
    "USER_KEY" character varying(255) NOT NULL
);


--
-- TOC entry 487 (class 1259 OID 19937)
-- Name: AO_733371_EVENT_RECIPIENT_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_733371_EVENT_RECIPIENT_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5797 (class 0 OID 0)
-- Dependencies: 487
-- Name: AO_733371_EVENT_RECIPIENT_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_733371_EVENT_RECIPIENT_ID_seq" OWNED BY public."AO_733371_EVENT_RECIPIENT"."ID";


--
-- TOC entry 417 (class 1259 OID 19424)
-- Name: AO_97EDAB_USERINVITATION; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_97EDAB_USERINVITATION" (
    "APPLICATION_KEYS" character varying(255),
    "EMAIL_ADDRESS" character varying(255),
    "EXPIRY" timestamp without time zone,
    "ID" integer NOT NULL,
    "REDEEMED" boolean,
    "SENDER_USERNAME" character varying(255),
    "TOKEN" character varying(255)
);


--
-- TOC entry 416 (class 1259 OID 19422)
-- Name: AO_97EDAB_USERINVITATION_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_97EDAB_USERINVITATION_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5798 (class 0 OID 0)
-- Dependencies: 416
-- Name: AO_97EDAB_USERINVITATION_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_97EDAB_USERINVITATION_ID_seq" OWNED BY public."AO_97EDAB_USERINVITATION"."ID";


--
-- TOC entry 367 (class 1259 OID 19070)
-- Name: AO_A0B856_WEB_HOOK_LISTENER_AO; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_A0B856_WEB_HOOK_LISTENER_AO" (
    "DESCRIPTION" text,
    "ENABLED" boolean,
    "EVENTS" text,
    "EXCLUDE_BODY" boolean,
    "FILTERS" text,
    "ID" integer NOT NULL,
    "LAST_UPDATED" timestamp without time zone NOT NULL,
    "LAST_UPDATED_USER" character varying(255),
    "NAME" text NOT NULL,
    "PARAMETERS" text,
    "REGISTRATION_METHOD" character varying(255) NOT NULL,
    "URL" text NOT NULL
);


--
-- TOC entry 366 (class 1259 OID 19068)
-- Name: AO_A0B856_WEB_HOOK_LISTENER_AO_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_A0B856_WEB_HOOK_LISTENER_AO_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5799 (class 0 OID 0)
-- Dependencies: 366
-- Name: AO_A0B856_WEB_HOOK_LISTENER_AO_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_A0B856_WEB_HOOK_LISTENER_AO_ID_seq" OWNED BY public."AO_A0B856_WEB_HOOK_LISTENER_AO"."ID";


--
-- TOC entry 361 (class 1259 OID 19033)
-- Name: AO_B9A0F0_APPLIED_TEMPLATE; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_B9A0F0_APPLIED_TEMPLATE" (
    "ID" integer NOT NULL,
    "PROJECT_ID" bigint DEFAULT 0,
    "PROJECT_TEMPLATE_MODULE_KEY" character varying(255),
    "PROJECT_TEMPLATE_WEB_ITEM_KEY" character varying(255)
);


--
-- TOC entry 360 (class 1259 OID 19031)
-- Name: AO_B9A0F0_APPLIED_TEMPLATE_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_B9A0F0_APPLIED_TEMPLATE_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5800 (class 0 OID 0)
-- Dependencies: 360
-- Name: AO_B9A0F0_APPLIED_TEMPLATE_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_B9A0F0_APPLIED_TEMPLATE_ID_seq" OWNED BY public."AO_B9A0F0_APPLIED_TEMPLATE"."ID";


--
-- TOC entry 415 (class 1259 OID 19411)
-- Name: AO_C16815_ALERT_AO; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_C16815_ALERT_AO" (
    "CREATED_DATE" bigint DEFAULT 0,
    "DETAILS_JSON" text,
    "ID" bigint NOT NULL,
    "ISSUE_COMPONENT_ID" character varying(255),
    "ISSUE_ID" character varying(255),
    "ISSUE_SEVERITY" integer DEFAULT 0,
    "NODE_NAME" character varying(255),
    "TRIGGER_MODULE" character varying(255),
    "TRIGGER_PLUGIN_KEY" character varying(255),
    "TRIGGER_PLUGIN_KEY_VERSION" character varying(255),
    "TRIGGER_PLUGIN_VERSION" character varying(255)
);


--
-- TOC entry 414 (class 1259 OID 19409)
-- Name: AO_C16815_ALERT_AO_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_C16815_ALERT_AO_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5801 (class 0 OID 0)
-- Dependencies: 414
-- Name: AO_C16815_ALERT_AO_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_C16815_ALERT_AO_ID_seq" OWNED BY public."AO_C16815_ALERT_AO"."ID";


--
-- TOC entry 421 (class 1259 OID 19448)
-- Name: AO_CFF990_AOTRANSITION_FAILURE; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_CFF990_AOTRANSITION_FAILURE" (
    "ERROR_MESSAGES" text,
    "FAILURE_TIME" timestamp without time zone,
    "ID" integer NOT NULL,
    "ISSUE_ID" bigint DEFAULT 0,
    "LOG_REFERRAL_HASH" character varying(255),
    "TRANSITION_ID" bigint DEFAULT 0,
    "TRIGGER_ID" bigint DEFAULT 0,
    "USER_KEY" character varying(255),
    "WORKFLOW_ID" character varying(255)
);


--
-- TOC entry 420 (class 1259 OID 19446)
-- Name: AO_CFF990_AOTRANSITION_FAILURE_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_CFF990_AOTRANSITION_FAILURE_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5802 (class 0 OID 0)
-- Dependencies: 420
-- Name: AO_CFF990_AOTRANSITION_FAILURE_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_CFF990_AOTRANSITION_FAILURE_ID_seq" OWNED BY public."AO_CFF990_AOTRANSITION_FAILURE"."ID";


--
-- TOC entry 445 (class 1259 OID 19620)
-- Name: AO_E8B6CC_BRANCH; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_E8B6CC_BRANCH" (
    "ID" integer NOT NULL,
    "NAME" character varying(255),
    "REPOSITORY_ID" integer
);


--
-- TOC entry 441 (class 1259 OID 19589)
-- Name: AO_E8B6CC_BRANCH_HEAD_MAPPING; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_E8B6CC_BRANCH_HEAD_MAPPING" (
    "BRANCH_NAME" character varying(255),
    "HEAD" character varying(255),
    "ID" integer NOT NULL,
    "REPOSITORY_ID" integer
);


--
-- TOC entry 440 (class 1259 OID 19587)
-- Name: AO_E8B6CC_BRANCH_HEAD_MAPPING_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_E8B6CC_BRANCH_HEAD_MAPPING_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5803 (class 0 OID 0)
-- Dependencies: 440
-- Name: AO_E8B6CC_BRANCH_HEAD_MAPPING_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_E8B6CC_BRANCH_HEAD_MAPPING_ID_seq" OWNED BY public."AO_E8B6CC_BRANCH_HEAD_MAPPING"."ID";


--
-- TOC entry 444 (class 1259 OID 19618)
-- Name: AO_E8B6CC_BRANCH_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_E8B6CC_BRANCH_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5804 (class 0 OID 0)
-- Dependencies: 444
-- Name: AO_E8B6CC_BRANCH_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_E8B6CC_BRANCH_ID_seq" OWNED BY public."AO_E8B6CC_BRANCH"."ID";


--
-- TOC entry 431 (class 1259 OID 19507)
-- Name: AO_E8B6CC_CHANGESET_MAPPING; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_E8B6CC_CHANGESET_MAPPING" (
    "AUTHOR" character varying(255),
    "AUTHOR_EMAIL" character varying(255),
    "BRANCH" character varying(255),
    "DATE" timestamp without time zone,
    "FILES_DATA" text,
    "FILE_COUNT" integer DEFAULT 0,
    "FILE_DETAILS_JSON" text,
    "ID" integer NOT NULL,
    "ISSUE_KEY" character varying(255),
    "MESSAGE" text,
    "NODE" character varying(255),
    "PARENTS_DATA" character varying(255),
    "PROJECT_KEY" character varying(255),
    "RAW_AUTHOR" character varying(255),
    "RAW_NODE" character varying(255),
    "REPOSITORY_ID" integer DEFAULT 0,
    "SMARTCOMMIT_AVAILABLE" boolean,
    "VERSION" integer
);


--
-- TOC entry 430 (class 1259 OID 19505)
-- Name: AO_E8B6CC_CHANGESET_MAPPING_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_E8B6CC_CHANGESET_MAPPING_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5805 (class 0 OID 0)
-- Dependencies: 430
-- Name: AO_E8B6CC_CHANGESET_MAPPING_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_E8B6CC_CHANGESET_MAPPING_ID_seq" OWNED BY public."AO_E8B6CC_CHANGESET_MAPPING"."ID";


--
-- TOC entry 459 (class 1259 OID 19709)
-- Name: AO_E8B6CC_COMMIT; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_E8B6CC_COMMIT" (
    "AUTHOR" character varying(255),
    "AUTHOR_AVATAR_URL" character varying(255),
    "DATE" timestamp without time zone NOT NULL,
    "DOMAIN_ID" integer DEFAULT 0 NOT NULL,
    "ID" integer NOT NULL,
    "MERGE" boolean,
    "MESSAGE" text,
    "NODE" character varying(255),
    "RAW_AUTHOR" character varying(255)
);


--
-- TOC entry 458 (class 1259 OID 19707)
-- Name: AO_E8B6CC_COMMIT_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_E8B6CC_COMMIT_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5806 (class 0 OID 0)
-- Dependencies: 458
-- Name: AO_E8B6CC_COMMIT_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_E8B6CC_COMMIT_ID_seq" OWNED BY public."AO_E8B6CC_COMMIT"."ID";


--
-- TOC entry 451 (class 1259 OID 19655)
-- Name: AO_E8B6CC_GIT_HUB_EVENT; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_E8B6CC_GIT_HUB_EVENT" (
    "CREATED_AT" timestamp without time zone NOT NULL,
    "GIT_HUB_ID" character varying(255) DEFAULT '0'::character varying NOT NULL,
    "ID" integer NOT NULL,
    "REPOSITORY_ID" integer NOT NULL,
    "SAVE_POINT" boolean
);


--
-- TOC entry 450 (class 1259 OID 19653)
-- Name: AO_E8B6CC_GIT_HUB_EVENT_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_E8B6CC_GIT_HUB_EVENT_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5807 (class 0 OID 0)
-- Dependencies: 450
-- Name: AO_E8B6CC_GIT_HUB_EVENT_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_E8B6CC_GIT_HUB_EVENT_ID_seq" OWNED BY public."AO_E8B6CC_GIT_HUB_EVENT"."ID";


--
-- TOC entry 423 (class 1259 OID 19462)
-- Name: AO_E8B6CC_ISSUE_MAPPING; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_E8B6CC_ISSUE_MAPPING" (
    "ID" integer NOT NULL,
    "ISSUE_ID" character varying(255),
    "NODE" character varying(255),
    "PROJECT_KEY" character varying(255),
    "REPOSITORY_URI" character varying(255)
);


--
-- TOC entry 422 (class 1259 OID 19460)
-- Name: AO_E8B6CC_ISSUE_MAPPING_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_E8B6CC_ISSUE_MAPPING_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5808 (class 0 OID 0)
-- Dependencies: 422
-- Name: AO_E8B6CC_ISSUE_MAPPING_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_E8B6CC_ISSUE_MAPPING_ID_seq" OWNED BY public."AO_E8B6CC_ISSUE_MAPPING"."ID";


--
-- TOC entry 427 (class 1259 OID 19484)
-- Name: AO_E8B6CC_ISSUE_MAPPING_V2; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_E8B6CC_ISSUE_MAPPING_V2" (
    "AUTHOR" character varying(255),
    "BRANCH" character varying(255),
    "DATE" timestamp without time zone,
    "FILES_DATA" text,
    "ID" integer NOT NULL,
    "ISSUE_ID" character varying(255),
    "MESSAGE" text,
    "NODE" character varying(255),
    "PARENTS_DATA" character varying(255),
    "RAW_AUTHOR" character varying(255),
    "RAW_NODE" character varying(255),
    "REPOSITORY_ID" integer DEFAULT 0,
    "VERSION" integer
);


--
-- TOC entry 426 (class 1259 OID 19482)
-- Name: AO_E8B6CC_ISSUE_MAPPING_V2_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_E8B6CC_ISSUE_MAPPING_V2_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5809 (class 0 OID 0)
-- Dependencies: 426
-- Name: AO_E8B6CC_ISSUE_MAPPING_V2_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_E8B6CC_ISSUE_MAPPING_V2_ID_seq" OWNED BY public."AO_E8B6CC_ISSUE_MAPPING_V2"."ID";


--
-- TOC entry 465 (class 1259 OID 19755)
-- Name: AO_E8B6CC_ISSUE_TO_BRANCH; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_E8B6CC_ISSUE_TO_BRANCH" (
    "BRANCH_ID" integer,
    "ID" integer NOT NULL,
    "ISSUE_KEY" character varying(255)
);


--
-- TOC entry 464 (class 1259 OID 19753)
-- Name: AO_E8B6CC_ISSUE_TO_BRANCH_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_E8B6CC_ISSUE_TO_BRANCH_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5810 (class 0 OID 0)
-- Dependencies: 464
-- Name: AO_E8B6CC_ISSUE_TO_BRANCH_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_E8B6CC_ISSUE_TO_BRANCH_ID_seq" OWNED BY public."AO_E8B6CC_ISSUE_TO_BRANCH"."ID";


--
-- TOC entry 439 (class 1259 OID 19568)
-- Name: AO_E8B6CC_ISSUE_TO_CHANGESET; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_E8B6CC_ISSUE_TO_CHANGESET" (
    "CHANGESET_ID" integer,
    "ID" integer NOT NULL,
    "ISSUE_KEY" character varying(255),
    "PROJECT_KEY" character varying(255)
);


--
-- TOC entry 438 (class 1259 OID 19566)
-- Name: AO_E8B6CC_ISSUE_TO_CHANGESET_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_E8B6CC_ISSUE_TO_CHANGESET_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5811 (class 0 OID 0)
-- Dependencies: 438
-- Name: AO_E8B6CC_ISSUE_TO_CHANGESET_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_E8B6CC_ISSUE_TO_CHANGESET_ID_seq" OWNED BY public."AO_E8B6CC_ISSUE_TO_CHANGESET"."ID";


--
-- TOC entry 457 (class 1259 OID 19697)
-- Name: AO_E8B6CC_MESSAGE; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_E8B6CC_MESSAGE" (
    "ADDRESS" character varying(255) NOT NULL,
    "ID" integer NOT NULL,
    "PAYLOAD" text NOT NULL,
    "PAYLOAD_TYPE" character varying(255) NOT NULL,
    "PRIORITY" integer DEFAULT 0 NOT NULL
);


--
-- TOC entry 456 (class 1259 OID 19695)
-- Name: AO_E8B6CC_MESSAGE_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_E8B6CC_MESSAGE_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5812 (class 0 OID 0)
-- Dependencies: 456
-- Name: AO_E8B6CC_MESSAGE_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_E8B6CC_MESSAGE_ID_seq" OWNED BY public."AO_E8B6CC_MESSAGE"."ID";


--
-- TOC entry 469 (class 1259 OID 19790)
-- Name: AO_E8B6CC_MESSAGE_QUEUE_ITEM; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_E8B6CC_MESSAGE_QUEUE_ITEM" (
    "ID" integer NOT NULL,
    "LAST_FAILED" timestamp without time zone,
    "MESSAGE_ID" integer NOT NULL,
    "QUEUE" character varying(255) NOT NULL,
    "RETRIES_COUNT" integer DEFAULT 0 NOT NULL,
    "STATE" character varying(255) NOT NULL,
    "STATE_INFO" character varying(255)
);


--
-- TOC entry 468 (class 1259 OID 19788)
-- Name: AO_E8B6CC_MESSAGE_QUEUE_ITEM_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_E8B6CC_MESSAGE_QUEUE_ITEM_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5813 (class 0 OID 0)
-- Dependencies: 468
-- Name: AO_E8B6CC_MESSAGE_QUEUE_ITEM_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_E8B6CC_MESSAGE_QUEUE_ITEM_ID_seq" OWNED BY public."AO_E8B6CC_MESSAGE_QUEUE_ITEM"."ID";


--
-- TOC entry 467 (class 1259 OID 19777)
-- Name: AO_E8B6CC_MESSAGE_TAG; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_E8B6CC_MESSAGE_TAG" (
    "ID" integer NOT NULL,
    "MESSAGE_ID" integer,
    "TAG" character varying(255)
);


--
-- TOC entry 466 (class 1259 OID 19775)
-- Name: AO_E8B6CC_MESSAGE_TAG_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_E8B6CC_MESSAGE_TAG_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5814 (class 0 OID 0)
-- Dependencies: 466
-- Name: AO_E8B6CC_MESSAGE_TAG_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_E8B6CC_MESSAGE_TAG_ID_seq" OWNED BY public."AO_E8B6CC_MESSAGE_TAG"."ID";


--
-- TOC entry 433 (class 1259 OID 19520)
-- Name: AO_E8B6CC_ORGANIZATION_MAPPING; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_E8B6CC_ORGANIZATION_MAPPING" (
    "ACCESS_TOKEN" character varying(255),
    "ADMIN_PASSWORD" character varying(255),
    "ADMIN_USERNAME" character varying(255),
    "APPROVAL_STATE" character varying(255),
    "AUTOLINK_NEW_REPOS" boolean,
    "DEFAULT_GROUPS_SLUGS" character varying(255),
    "DVCS_TYPE" character varying(255),
    "HOST_URL" character varying(255),
    "ID" integer NOT NULL,
    "LAST_POLLED" bigint,
    "NAME" character varying(255),
    "OAUTH_KEY" character varying(255),
    "OAUTH_SECRET" character varying(255),
    "PRINCIPAL_ID" character varying(255),
    "SMARTCOMMITS_FOR_NEW_REPOS" boolean
);


--
-- TOC entry 432 (class 1259 OID 19518)
-- Name: AO_E8B6CC_ORGANIZATION_MAPPING_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_E8B6CC_ORGANIZATION_MAPPING_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5815 (class 0 OID 0)
-- Dependencies: 432
-- Name: AO_E8B6CC_ORGANIZATION_MAPPING_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_E8B6CC_ORGANIZATION_MAPPING_ID_seq" OWNED BY public."AO_E8B6CC_ORGANIZATION_MAPPING"."ID";


--
-- TOC entry 453 (class 1259 OID 19669)
-- Name: AO_E8B6CC_ORG_TO_PROJECT; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_E8B6CC_ORG_TO_PROJECT" (
    "ID" integer NOT NULL,
    "ORGANIZATION_ID" integer,
    "PROJECT_KEY" character varying(255)
);


--
-- TOC entry 452 (class 1259 OID 19667)
-- Name: AO_E8B6CC_ORG_TO_PROJECT_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_E8B6CC_ORG_TO_PROJECT_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5816 (class 0 OID 0)
-- Dependencies: 452
-- Name: AO_E8B6CC_ORG_TO_PROJECT_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_E8B6CC_ORG_TO_PROJECT_ID_seq" OWNED BY public."AO_E8B6CC_ORG_TO_PROJECT"."ID";


--
-- TOC entry 425 (class 1259 OID 19473)
-- Name: AO_E8B6CC_PROJECT_MAPPING; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_E8B6CC_PROJECT_MAPPING" (
    "ID" integer NOT NULL,
    "PASSWORD" character varying(255),
    "PROJECT_KEY" character varying(255),
    "REPOSITORY_URI" character varying(255),
    "USERNAME" character varying(255)
);


--
-- TOC entry 424 (class 1259 OID 19471)
-- Name: AO_E8B6CC_PROJECT_MAPPING_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_E8B6CC_PROJECT_MAPPING_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5817 (class 0 OID 0)
-- Dependencies: 424
-- Name: AO_E8B6CC_PROJECT_MAPPING_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_E8B6CC_PROJECT_MAPPING_ID_seq" OWNED BY public."AO_E8B6CC_PROJECT_MAPPING"."ID";


--
-- TOC entry 429 (class 1259 OID 19496)
-- Name: AO_E8B6CC_PROJECT_MAPPING_V2; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_E8B6CC_PROJECT_MAPPING_V2" (
    "ACCESS_TOKEN" character varying(255),
    "ADMIN_PASSWORD" character varying(255),
    "ADMIN_USERNAME" character varying(255),
    "ID" integer NOT NULL,
    "LAST_COMMIT_DATE" timestamp without time zone,
    "PROJECT_KEY" character varying(255),
    "REPOSITORY_NAME" character varying(255),
    "REPOSITORY_TYPE" character varying(255),
    "REPOSITORY_URL" character varying(255)
);


--
-- TOC entry 428 (class 1259 OID 19494)
-- Name: AO_E8B6CC_PROJECT_MAPPING_V2_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_E8B6CC_PROJECT_MAPPING_V2_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5818 (class 0 OID 0)
-- Dependencies: 428
-- Name: AO_E8B6CC_PROJECT_MAPPING_V2_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_E8B6CC_PROJECT_MAPPING_V2_ID_seq" OWNED BY public."AO_E8B6CC_PROJECT_MAPPING_V2"."ID";


--
-- TOC entry 447 (class 1259 OID 19633)
-- Name: AO_E8B6CC_PR_ISSUE_KEY; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_E8B6CC_PR_ISSUE_KEY" (
    "DOMAIN_ID" integer DEFAULT 0 NOT NULL,
    "ID" integer NOT NULL,
    "ISSUE_KEY" character varying(255),
    "PULL_REQUEST_ID" integer DEFAULT 0
);


--
-- TOC entry 446 (class 1259 OID 19631)
-- Name: AO_E8B6CC_PR_ISSUE_KEY_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_E8B6CC_PR_ISSUE_KEY_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5819 (class 0 OID 0)
-- Dependencies: 446
-- Name: AO_E8B6CC_PR_ISSUE_KEY_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_E8B6CC_PR_ISSUE_KEY_ID_seq" OWNED BY public."AO_E8B6CC_PR_ISSUE_KEY"."ID";


--
-- TOC entry 463 (class 1259 OID 19736)
-- Name: AO_E8B6CC_PR_PARTICIPANT; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_E8B6CC_PR_PARTICIPANT" (
    "APPROVED" boolean,
    "DOMAIN_ID" integer DEFAULT 0 NOT NULL,
    "ID" integer NOT NULL,
    "PULL_REQUEST_ID" integer,
    "ROLE" character varying(255),
    "USERNAME" character varying(255)
);


--
-- TOC entry 462 (class 1259 OID 19734)
-- Name: AO_E8B6CC_PR_PARTICIPANT_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_E8B6CC_PR_PARTICIPANT_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5820 (class 0 OID 0)
-- Dependencies: 462
-- Name: AO_E8B6CC_PR_PARTICIPANT_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_E8B6CC_PR_PARTICIPANT_ID_seq" OWNED BY public."AO_E8B6CC_PR_PARTICIPANT"."ID";


--
-- TOC entry 471 (class 1259 OID 19809)
-- Name: AO_E8B6CC_PR_TO_COMMIT; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_E8B6CC_PR_TO_COMMIT" (
    "COMMIT_ID" integer NOT NULL,
    "DOMAIN_ID" integer DEFAULT 0 NOT NULL,
    "ID" integer NOT NULL,
    "REQUEST_ID" integer NOT NULL
);


--
-- TOC entry 470 (class 1259 OID 19807)
-- Name: AO_E8B6CC_PR_TO_COMMIT_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_E8B6CC_PR_TO_COMMIT_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5821 (class 0 OID 0)
-- Dependencies: 470
-- Name: AO_E8B6CC_PR_TO_COMMIT_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_E8B6CC_PR_TO_COMMIT_ID_seq" OWNED BY public."AO_E8B6CC_PR_TO_COMMIT"."ID";


--
-- TOC entry 443 (class 1259 OID 19606)
-- Name: AO_E8B6CC_PULL_REQUEST; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_E8B6CC_PULL_REQUEST" (
    "AUTHOR" character varying(255),
    "COMMENT_COUNT" integer DEFAULT 0,
    "CREATED_ON" timestamp without time zone,
    "DESTINATION_BRANCH" character varying(255),
    "DOMAIN_ID" integer DEFAULT 0 NOT NULL,
    "EXECUTED_BY" character varying(255),
    "ID" integer NOT NULL,
    "LAST_STATUS" character varying(255),
    "NAME" character varying(255),
    "REMOTE_ID" bigint,
    "SOURCE_BRANCH" character varying(255),
    "SOURCE_REPO" character varying(255),
    "TO_REPOSITORY_ID" integer DEFAULT 0,
    "UPDATED_ON" timestamp without time zone,
    "URL" character varying(255)
);


--
-- TOC entry 442 (class 1259 OID 19604)
-- Name: AO_E8B6CC_PULL_REQUEST_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_E8B6CC_PULL_REQUEST_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5822 (class 0 OID 0)
-- Dependencies: 442
-- Name: AO_E8B6CC_PULL_REQUEST_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_E8B6CC_PULL_REQUEST_ID_seq" OWNED BY public."AO_E8B6CC_PULL_REQUEST"."ID";


--
-- TOC entry 435 (class 1259 OID 19531)
-- Name: AO_E8B6CC_REPOSITORY_MAPPING; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_E8B6CC_REPOSITORY_MAPPING" (
    "ACTIVITY_LAST_SYNC" timestamp without time zone,
    "DELETED" boolean,
    "FORK" boolean,
    "FORK_OF_NAME" character varying(255),
    "FORK_OF_OWNER" character varying(255),
    "FORK_OF_SLUG" character varying(255),
    "ID" integer NOT NULL,
    "LAST_CHANGESET_NODE" character varying(255),
    "LAST_COMMIT_DATE" timestamp without time zone,
    "LINKED" boolean,
    "LOGO" text,
    "NAME" character varying(255),
    "ORGANIZATION_ID" integer DEFAULT 0,
    "SLUG" character varying(255),
    "SMARTCOMMITS_ENABLED" boolean,
    "UPDATE_LINK_AUTHORISED" boolean
);


--
-- TOC entry 434 (class 1259 OID 19529)
-- Name: AO_E8B6CC_REPOSITORY_MAPPING_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_E8B6CC_REPOSITORY_MAPPING_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5823 (class 0 OID 0)
-- Dependencies: 434
-- Name: AO_E8B6CC_REPOSITORY_MAPPING_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_E8B6CC_REPOSITORY_MAPPING_ID_seq" OWNED BY public."AO_E8B6CC_REPOSITORY_MAPPING"."ID";


--
-- TOC entry 437 (class 1259 OID 19550)
-- Name: AO_E8B6CC_REPO_TO_CHANGESET; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_E8B6CC_REPO_TO_CHANGESET" (
    "CHANGESET_ID" integer,
    "ID" integer NOT NULL,
    "REPOSITORY_ID" integer
);


--
-- TOC entry 436 (class 1259 OID 19548)
-- Name: AO_E8B6CC_REPO_TO_CHANGESET_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_E8B6CC_REPO_TO_CHANGESET_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5824 (class 0 OID 0)
-- Dependencies: 436
-- Name: AO_E8B6CC_REPO_TO_CHANGESET_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_E8B6CC_REPO_TO_CHANGESET_ID_seq" OWNED BY public."AO_E8B6CC_REPO_TO_CHANGESET"."ID";


--
-- TOC entry 461 (class 1259 OID 19721)
-- Name: AO_E8B6CC_REPO_TO_PROJECT; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_E8B6CC_REPO_TO_PROJECT" (
    "ID" integer NOT NULL,
    "PROJECT_KEY" character varying(255),
    "REPOSITORY_ID" integer
);


--
-- TOC entry 460 (class 1259 OID 19719)
-- Name: AO_E8B6CC_REPO_TO_PROJECT_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_E8B6CC_REPO_TO_PROJECT_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5825 (class 0 OID 0)
-- Dependencies: 460
-- Name: AO_E8B6CC_REPO_TO_PROJECT_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_E8B6CC_REPO_TO_PROJECT_ID_seq" OWNED BY public."AO_E8B6CC_REPO_TO_PROJECT"."ID";


--
-- TOC entry 455 (class 1259 OID 19682)
-- Name: AO_E8B6CC_SYNC_AUDIT_LOG; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_E8B6CC_SYNC_AUDIT_LOG" (
    "END_DATE" timestamp without time zone,
    "EXC_TRACE" text,
    "FIRST_REQUEST_DATE" timestamp without time zone,
    "FLIGHT_TIME_MS" integer DEFAULT 0,
    "ID" integer NOT NULL,
    "NUM_REQUESTS" integer DEFAULT 0,
    "REPO_ID" integer DEFAULT 0,
    "START_DATE" timestamp without time zone,
    "SYNC_STATUS" character varying(255),
    "SYNC_TYPE" character varying(255),
    "TOTAL_ERRORS" integer DEFAULT 0
);


--
-- TOC entry 454 (class 1259 OID 19680)
-- Name: AO_E8B6CC_SYNC_AUDIT_LOG_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_E8B6CC_SYNC_AUDIT_LOG_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5826 (class 0 OID 0)
-- Dependencies: 454
-- Name: AO_E8B6CC_SYNC_AUDIT_LOG_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_E8B6CC_SYNC_AUDIT_LOG_ID_seq" OWNED BY public."AO_E8B6CC_SYNC_AUDIT_LOG"."ID";


--
-- TOC entry 449 (class 1259 OID 19643)
-- Name: AO_E8B6CC_SYNC_EVENT; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_E8B6CC_SYNC_EVENT" (
    "EVENT_CLASS" text NOT NULL,
    "EVENT_DATE" timestamp without time zone NOT NULL,
    "EVENT_JSON" text NOT NULL,
    "ID" integer NOT NULL,
    "REPO_ID" integer DEFAULT 0 NOT NULL,
    "SCHEDULED_SYNC" boolean
);


--
-- TOC entry 448 (class 1259 OID 19641)
-- Name: AO_E8B6CC_SYNC_EVENT_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_E8B6CC_SYNC_EVENT_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5827 (class 0 OID 0)
-- Dependencies: 448
-- Name: AO_E8B6CC_SYNC_EVENT_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_E8B6CC_SYNC_EVENT_ID_seq" OWNED BY public."AO_E8B6CC_SYNC_EVENT"."ID";


--
-- TOC entry 507 (class 1259 OID 20088)
-- Name: AO_ED669C_SEEN_ASSERTIONS; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."AO_ED669C_SEEN_ASSERTIONS" (
    "ASSERTION_ID" character varying(255) NOT NULL,
    "EXPIRY_TIMESTAMP" bigint DEFAULT 0 NOT NULL,
    "ID" integer NOT NULL
);


--
-- TOC entry 506 (class 1259 OID 20086)
-- Name: AO_ED669C_SEEN_ASSERTIONS_ID_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public."AO_ED669C_SEEN_ASSERTIONS_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5828 (class 0 OID 0)
-- Dependencies: 506
-- Name: AO_ED669C_SEEN_ASSERTIONS_ID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public."AO_ED669C_SEEN_ASSERTIONS_ID_seq" OWNED BY public."AO_ED669C_SEEN_ASSERTIONS"."ID";


--
-- TOC entry 187 (class 1259 OID 16421)
-- Name: app_user; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.app_user (
    id numeric(18,0) NOT NULL,
    user_key character varying(255),
    lower_user_name character varying(255)
);


--
-- TOC entry 188 (class 1259 OID 16429)
-- Name: audit_changed_value; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.audit_changed_value (
    id numeric(18,0) NOT NULL,
    log_id numeric(18,0),
    name character varying(255),
    delta_from text,
    delta_to text
);


--
-- TOC entry 189 (class 1259 OID 16437)
-- Name: audit_item; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.audit_item (
    id numeric(18,0) NOT NULL,
    log_id numeric(18,0),
    object_type character varying(60),
    object_id character varying(255),
    object_name character varying(255),
    object_parent_id character varying(255),
    object_parent_name character varying(255)
);


--
-- TOC entry 190 (class 1259 OID 16445)
-- Name: audit_log; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.audit_log (
    id numeric(18,0) NOT NULL,
    remote_address character varying(60),
    created timestamp with time zone,
    author_key character varying(255),
    summary character varying(255),
    category character varying(255),
    object_type character varying(60),
    object_id character varying(255),
    object_name character varying(255),
    object_parent_id character varying(255),
    object_parent_name character varying(255),
    author_type numeric(9,0),
    event_source_name character varying(255),
    description character varying(255),
    long_description text,
    search_field text
);


--
-- TOC entry 191 (class 1259 OID 16453)
-- Name: avatar; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.avatar (
    id numeric(18,0) NOT NULL,
    filename character varying(255),
    contenttype character varying(255),
    avatartype character varying(60),
    owner character varying(255),
    systemavatar numeric(9,0)
);


--
-- TOC entry 192 (class 1259 OID 16461)
-- Name: board; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.board (
    id numeric(18,0) NOT NULL,
    jql text
);


--
-- TOC entry 193 (class 1259 OID 16469)
-- Name: boardproject; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.boardproject (
    board_id numeric(18,0) NOT NULL,
    project_id numeric(18,0) NOT NULL
);


--
-- TOC entry 194 (class 1259 OID 16474)
-- Name: changegroup; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.changegroup (
    id numeric(18,0) NOT NULL,
    issueid numeric(18,0),
    author character varying(255),
    created timestamp with time zone
);


--
-- TOC entry 195 (class 1259 OID 16479)
-- Name: changeitem; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.changeitem (
    id numeric(18,0) NOT NULL,
    groupid numeric(18,0),
    fieldtype character varying(255),
    field character varying(255),
    oldvalue text,
    oldstring text,
    newvalue text,
    newstring text
);


--
-- TOC entry 201 (class 1259 OID 16512)
-- Name: clusteredjob; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.clusteredjob (
    id numeric(18,0) NOT NULL,
    job_id character varying(255),
    job_runner_key character varying(255),
    sched_type character(1),
    interval_millis numeric(18,0),
    first_run numeric(18,0),
    cron_expression character varying(255),
    time_zone character varying(60),
    next_run numeric(18,0),
    version numeric(18,0),
    parameters bytea
);


--
-- TOC entry 196 (class 1259 OID 16487)
-- Name: clusterlockstatus; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.clusterlockstatus (
    id numeric(18,0) NOT NULL,
    lock_name character varying(255),
    locked_by_node character varying(60),
    update_time numeric(18,0)
);


--
-- TOC entry 197 (class 1259 OID 16492)
-- Name: clustermessage; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.clustermessage (
    id numeric(18,0) NOT NULL,
    source_node character varying(60),
    destination_node character varying(60),
    claimed_by_node character varying(60),
    message character varying(255),
    message_time timestamp with time zone
);


--
-- TOC entry 198 (class 1259 OID 16497)
-- Name: clusternode; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.clusternode (
    node_id character varying(60) NOT NULL,
    node_state character varying(60),
    "timestamp" numeric(18,0),
    ip character varying(60),
    cache_listener_port numeric(18,0),
    node_build_number numeric(18,0),
    node_version character varying(60)
);


--
-- TOC entry 199 (class 1259 OID 16502)
-- Name: clusternodeheartbeat; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.clusternodeheartbeat (
    node_id character varying(60) NOT NULL,
    heartbeat_time numeric(18,0),
    database_time numeric(18,0)
);


--
-- TOC entry 200 (class 1259 OID 16507)
-- Name: clusterupgradestate; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.clusterupgradestate (
    id numeric(18,0) NOT NULL,
    database_time numeric(18,0),
    cluster_build_number numeric(18,0),
    cluster_version character varying(60),
    state character varying(60),
    order_number numeric(18,0)
);


--
-- TOC entry 202 (class 1259 OID 16520)
-- Name: columnlayout; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.columnlayout (
    id numeric(18,0) NOT NULL,
    username character varying(255),
    searchrequest numeric(18,0)
);


--
-- TOC entry 203 (class 1259 OID 16525)
-- Name: columnlayoutitem; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.columnlayoutitem (
    id numeric(18,0) NOT NULL,
    columnlayout numeric(18,0),
    fieldidentifier character varying(255),
    horizontalposition numeric(18,0)
);


--
-- TOC entry 204 (class 1259 OID 16530)
-- Name: component; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.component (
    id numeric(18,0) NOT NULL,
    project numeric(18,0),
    cname character varying(255),
    description text,
    url character varying(255),
    lead character varying(255),
    assigneetype numeric(18,0),
    archived character varying(10)
);


--
-- TOC entry 205 (class 1259 OID 16538)
-- Name: configurationcontext; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.configurationcontext (
    id numeric(18,0) NOT NULL,
    projectcategory numeric(18,0),
    project numeric(18,0),
    customfield character varying(255),
    fieldconfigscheme numeric(18,0)
);


--
-- TOC entry 206 (class 1259 OID 16543)
-- Name: customfield; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.customfield (
    id numeric(18,0) NOT NULL,
    cfkey character varying(255),
    customfieldtypekey character varying(255),
    customfieldsearcherkey character varying(255),
    cfname character varying(255),
    description text,
    defaultvalue character varying(255),
    fieldtype numeric(18,0),
    project numeric(18,0),
    issuetype character varying(255)
);


--
-- TOC entry 207 (class 1259 OID 16551)
-- Name: customfieldoption; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.customfieldoption (
    id numeric(18,0) NOT NULL,
    customfield numeric(18,0),
    customfieldconfig numeric(18,0),
    parentoptionid numeric(18,0),
    sequence numeric(18,0),
    customvalue character varying(255),
    optiontype character varying(60),
    disabled character varying(60)
);


--
-- TOC entry 208 (class 1259 OID 16556)
-- Name: customfieldvalue; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.customfieldvalue (
    id numeric(18,0) NOT NULL,
    issue numeric(18,0),
    customfield numeric(18,0),
    updated numeric(18,0),
    parentkey character varying(255),
    stringvalue character varying(255),
    numbervalue double precision,
    textvalue text,
    datevalue timestamp with time zone,
    valuetype character varying(255)
);


--
-- TOC entry 186 (class 1259 OID 16413)
-- Name: cwd_application; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cwd_application (
    id numeric(18,0) NOT NULL,
    application_name character varying(255),
    lower_application_name character varying(255),
    created_date timestamp with time zone,
    updated_date timestamp with time zone,
    active numeric(9,0),
    description character varying(255),
    application_type character varying(255),
    credential character varying(255)
);


--
-- TOC entry 325 (class 1259 OID 17381)
-- Name: cwd_application_address; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cwd_application_address (
    application_id numeric(18,0) NOT NULL,
    remote_address character varying(255) NOT NULL,
    encoded_address_binary character varying(255),
    remote_address_mask numeric(9,0)
);


--
-- TOC entry 210 (class 1259 OID 16572)
-- Name: cwd_directory; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cwd_directory (
    id numeric(18,0) NOT NULL,
    directory_name character varying(255),
    lower_directory_name character varying(255),
    created_date timestamp with time zone,
    updated_date timestamp with time zone,
    active numeric(9,0),
    description character varying(255),
    impl_class character varying(255),
    lower_impl_class character varying(255),
    directory_type character varying(60),
    directory_position numeric(18,0)
);


--
-- TOC entry 211 (class 1259 OID 16580)
-- Name: cwd_directory_attribute; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cwd_directory_attribute (
    directory_id numeric(18,0) NOT NULL,
    attribute_name character varying(255) NOT NULL,
    attribute_value character varying(4000)
);


--
-- TOC entry 212 (class 1259 OID 16588)
-- Name: cwd_directory_operation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cwd_directory_operation (
    directory_id numeric(18,0) NOT NULL,
    operation_type character varying(60) NOT NULL
);


--
-- TOC entry 242 (class 1259 OID 16804)
-- Name: cwd_group; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cwd_group (
    id numeric(18,0) NOT NULL,
    group_name character varying(255),
    lower_group_name character varying(255),
    active numeric(9,0),
    local numeric(9,0),
    created_date timestamp with time zone,
    updated_date timestamp with time zone,
    description character varying(255),
    lower_description character varying(255),
    group_type character varying(60),
    directory_id numeric(18,0)
);


--
-- TOC entry 243 (class 1259 OID 16812)
-- Name: cwd_group_attributes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cwd_group_attributes (
    id numeric(18,0) NOT NULL,
    group_id numeric(18,0),
    directory_id numeric(18,0),
    attribute_name character varying(255),
    attribute_value character varying(255),
    lower_attribute_value character varying(255)
);


--
-- TOC entry 272 (class 1259 OID 17023)
-- Name: cwd_membership; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cwd_membership (
    id numeric(18,0) NOT NULL,
    parent_id numeric(18,0),
    child_id numeric(18,0),
    membership_type character varying(60),
    group_type character varying(60),
    parent_name character varying(255),
    lower_parent_name character varying(255),
    child_name character varying(255),
    lower_child_name character varying(255),
    directory_id numeric(18,0)
);


--
-- TOC entry 345 (class 1259 OID 17529)
-- Name: cwd_user; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cwd_user (
    id numeric(18,0) NOT NULL,
    directory_id numeric(18,0),
    user_name character varying(255),
    lower_user_name character varying(255),
    active numeric(9,0),
    created_date timestamp with time zone,
    updated_date timestamp with time zone,
    first_name character varying(255),
    lower_first_name character varying(255),
    last_name character varying(255),
    lower_last_name character varying(255),
    display_name character varying(255),
    lower_display_name character varying(255),
    email_address character varying(255),
    lower_email_address character varying(255),
    credential character varying(255),
    deleted_externally numeric(9,0),
    external_id character varying(255)
);


--
-- TOC entry 347 (class 1259 OID 17542)
-- Name: cwd_user_attributes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cwd_user_attributes (
    id numeric(18,0) NOT NULL,
    user_id numeric(18,0),
    directory_id numeric(18,0),
    attribute_name character varying(255),
    attribute_value character varying(255),
    lower_attribute_value character varying(255)
);


--
-- TOC entry 209 (class 1259 OID 16564)
-- Name: deadletter; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.deadletter (
    id numeric(18,0) NOT NULL,
    message_id character varying(255),
    last_seen numeric(18,0),
    mail_server_id numeric(18,0),
    folder_name character varying(255)
);


--
-- TOC entry 214 (class 1259 OID 16601)
-- Name: draftworkflowscheme; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.draftworkflowscheme (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    description text,
    workflow_scheme_id numeric(18,0),
    last_modified_date timestamp with time zone,
    last_modified_user character varying(255)
);


--
-- TOC entry 215 (class 1259 OID 16609)
-- Name: draftworkflowschemeentity; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.draftworkflowschemeentity (
    id numeric(18,0) NOT NULL,
    scheme numeric(18,0),
    workflow character varying(255),
    issuetype character varying(255)
);


--
-- TOC entry 216 (class 1259 OID 16617)
-- Name: entity_property; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.entity_property (
    id numeric(18,0) NOT NULL,
    entity_name character varying(255),
    entity_id numeric(18,0),
    property_key character varying(255),
    created timestamp with time zone,
    updated timestamp with time zone,
    json_value text
);


--
-- TOC entry 217 (class 1259 OID 16625)
-- Name: entity_property_index_document; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.entity_property_index_document (
    id numeric(18,0) NOT NULL,
    plugin_key character varying(255),
    module_key character varying(255),
    entity_key character varying(255),
    updated timestamp with time zone,
    document text
);


--
-- TOC entry 218 (class 1259 OID 16633)
-- Name: entity_translation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.entity_translation (
    id numeric(18,0) NOT NULL,
    entity_name character varying(255),
    entity_id numeric(18,0),
    locale character varying(60),
    trans_name character varying(255),
    trans_desc character varying(255)
);


--
-- TOC entry 220 (class 1259 OID 16649)
-- Name: external_entities; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.external_entities (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    entitytype character varying(255)
);


--
-- TOC entry 221 (class 1259 OID 16657)
-- Name: externalgadget; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.externalgadget (
    id numeric(18,0) NOT NULL,
    gadget_xml text
);


--
-- TOC entry 222 (class 1259 OID 16665)
-- Name: favouriteassociations; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.favouriteassociations (
    id numeric(18,0) NOT NULL,
    username character varying(255),
    entitytype character varying(60),
    entityid numeric(18,0),
    sequence numeric(18,0)
);


--
-- TOC entry 223 (class 1259 OID 16670)
-- Name: feature; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.feature (
    id numeric(18,0) NOT NULL,
    feature_name character varying(255),
    feature_type character varying(10),
    user_key character varying(255)
);


--
-- TOC entry 224 (class 1259 OID 16678)
-- Name: fieldconfigscheme; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.fieldconfigscheme (
    id numeric(18,0) NOT NULL,
    configname character varying(255),
    description text,
    fieldid character varying(60),
    customfield numeric(18,0)
);


--
-- TOC entry 225 (class 1259 OID 16686)
-- Name: fieldconfigschemeissuetype; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.fieldconfigschemeissuetype (
    id numeric(18,0) NOT NULL,
    issuetype character varying(255),
    fieldconfigscheme numeric(18,0),
    fieldconfiguration numeric(18,0)
);


--
-- TOC entry 226 (class 1259 OID 16691)
-- Name: fieldconfiguration; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.fieldconfiguration (
    id numeric(18,0) NOT NULL,
    configname character varying(255),
    description text,
    fieldid character varying(60),
    customfield numeric(18,0)
);


--
-- TOC entry 227 (class 1259 OID 16699)
-- Name: fieldlayout; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.fieldlayout (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    description character varying(255),
    layout_type character varying(255),
    layoutscheme numeric(18,0)
);


--
-- TOC entry 228 (class 1259 OID 16707)
-- Name: fieldlayoutitem; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.fieldlayoutitem (
    id numeric(18,0) NOT NULL,
    fieldlayout numeric(18,0),
    fieldidentifier character varying(255),
    description text,
    verticalposition numeric(18,0),
    ishidden character varying(60),
    isrequired character varying(60),
    renderertype character varying(255)
);


--
-- TOC entry 229 (class 1259 OID 16715)
-- Name: fieldlayoutscheme; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.fieldlayoutscheme (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    description text
);


--
-- TOC entry 230 (class 1259 OID 16723)
-- Name: fieldlayoutschemeassociation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.fieldlayoutschemeassociation (
    id numeric(18,0) NOT NULL,
    issuetype character varying(255),
    project numeric(18,0),
    fieldlayoutscheme numeric(18,0)
);


--
-- TOC entry 231 (class 1259 OID 16728)
-- Name: fieldlayoutschemeentity; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.fieldlayoutschemeentity (
    id numeric(18,0) NOT NULL,
    scheme numeric(18,0),
    issuetype character varying(255),
    fieldlayout numeric(18,0)
);


--
-- TOC entry 232 (class 1259 OID 16733)
-- Name: fieldscreen; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.fieldscreen (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    description character varying(255)
);


--
-- TOC entry 233 (class 1259 OID 16741)
-- Name: fieldscreenlayoutitem; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.fieldscreenlayoutitem (
    id numeric(18,0) NOT NULL,
    fieldidentifier character varying(255),
    sequence numeric(18,0),
    fieldscreentab numeric(18,0)
);


--
-- TOC entry 234 (class 1259 OID 16746)
-- Name: fieldscreenscheme; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.fieldscreenscheme (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    description character varying(255)
);


--
-- TOC entry 235 (class 1259 OID 16754)
-- Name: fieldscreenschemeitem; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.fieldscreenschemeitem (
    id numeric(18,0) NOT NULL,
    operation numeric(18,0),
    fieldscreen numeric(18,0),
    fieldscreenscheme numeric(18,0)
);


--
-- TOC entry 236 (class 1259 OID 16759)
-- Name: fieldscreentab; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.fieldscreentab (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    description character varying(255),
    sequence numeric(18,0),
    fieldscreen numeric(18,0)
);


--
-- TOC entry 237 (class 1259 OID 16767)
-- Name: fileattachment; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.fileattachment (
    id numeric(18,0) NOT NULL,
    issueid numeric(18,0),
    mimetype character varying(255),
    filename character varying(255),
    created timestamp with time zone,
    filesize numeric(18,0),
    author character varying(255),
    zip numeric(9,0),
    thumbnailable numeric(9,0)
);


--
-- TOC entry 238 (class 1259 OID 16775)
-- Name: filtersubscription; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.filtersubscription (
    id numeric(18,0) NOT NULL,
    filter_i_d numeric(18,0),
    username character varying(60),
    groupname character varying(60),
    last_run timestamp with time zone,
    email_on_empty character varying(10)
);


--
-- TOC entry 239 (class 1259 OID 16780)
-- Name: gadgetuserpreference; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.gadgetuserpreference (
    id numeric(18,0) NOT NULL,
    portletconfiguration numeric(18,0),
    userprefkey character varying(255),
    userprefvalue text
);


--
-- TOC entry 240 (class 1259 OID 16788)
-- Name: genericconfiguration; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.genericconfiguration (
    id numeric(18,0) NOT NULL,
    datatype character varying(60),
    datakey character varying(60),
    xmlvalue text
);


--
-- TOC entry 241 (class 1259 OID 16796)
-- Name: globalpermissionentry; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.globalpermissionentry (
    id numeric(18,0) NOT NULL,
    permission character varying(255),
    group_id character varying(255)
);


--
-- TOC entry 285 (class 1259 OID 17112)
-- Name: groupbase; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.groupbase (
    id numeric(18,0) NOT NULL,
    groupname character varying(255)
);


--
-- TOC entry 245 (class 1259 OID 16828)
-- Name: issue_field_option; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.issue_field_option (
    id numeric(18,0) NOT NULL,
    option_id numeric(18,0),
    field_key character varying(255),
    option_value character varying(255),
    properties text
);


--
-- TOC entry 246 (class 1259 OID 16836)
-- Name: issue_field_option_scope; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.issue_field_option_scope (
    id numeric(18,0) NOT NULL,
    option_id numeric(18,0),
    entity_id character varying(255),
    scope_type character varying(255)
);


--
-- TOC entry 247 (class 1259 OID 16844)
-- Name: issuelink; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.issuelink (
    id numeric(18,0) NOT NULL,
    linktype numeric(18,0),
    source numeric(18,0),
    destination numeric(18,0),
    sequence numeric(18,0)
);


--
-- TOC entry 248 (class 1259 OID 16849)
-- Name: issuelinktype; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.issuelinktype (
    id numeric(18,0) NOT NULL,
    linkname character varying(255),
    inward character varying(255),
    outward character varying(255),
    pstyle character varying(60)
);


--
-- TOC entry 249 (class 1259 OID 16857)
-- Name: issuesecurityscheme; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.issuesecurityscheme (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    description text,
    defaultlevel numeric(18,0)
);


--
-- TOC entry 337 (class 1259 OID 17471)
-- Name: issuestatus; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.issuestatus (
    id character varying(60) NOT NULL,
    sequence numeric(18,0),
    pname character varying(60),
    description text,
    iconurl character varying(255),
    statuscategory numeric(18,0)
);


--
-- TOC entry 250 (class 1259 OID 16865)
-- Name: issuetype; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.issuetype (
    id character varying(60) NOT NULL,
    sequence numeric(18,0),
    pname character varying(60),
    pstyle character varying(60),
    description text,
    iconurl character varying(255),
    avatar numeric(18,0)
);


--
-- TOC entry 251 (class 1259 OID 16873)
-- Name: issuetypescreenscheme; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.issuetypescreenscheme (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    description character varying(255)
);


--
-- TOC entry 252 (class 1259 OID 16881)
-- Name: issuetypescreenschemeentity; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.issuetypescreenschemeentity (
    id numeric(18,0) NOT NULL,
    issuetype character varying(255),
    scheme numeric(18,0),
    fieldscreenscheme numeric(18,0)
);


--
-- TOC entry 185 (class 1259 OID 16405)
-- Name: jiraaction; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.jiraaction (
    id numeric(18,0) NOT NULL,
    issueid numeric(18,0),
    author character varying(255),
    actiontype character varying(255),
    actionlevel character varying(255),
    rolelevel numeric(18,0),
    actionbody text,
    created timestamp with time zone,
    updateauthor character varying(255),
    updated timestamp with time zone,
    actionnum numeric(18,0)
);


--
-- TOC entry 213 (class 1259 OID 16593)
-- Name: jiradraftworkflows; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.jiradraftworkflows (
    id numeric(18,0) NOT NULL,
    parentname character varying(255),
    descriptor text
);


--
-- TOC entry 219 (class 1259 OID 16641)
-- Name: jiraeventtype; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.jiraeventtype (
    id numeric(18,0) NOT NULL,
    template_id numeric(18,0),
    name character varying(255),
    description text,
    event_type character varying(60)
);


--
-- TOC entry 244 (class 1259 OID 16820)
-- Name: jiraissue; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.jiraissue (
    id numeric(18,0) NOT NULL,
    pkey character varying(255),
    issuenum numeric(18,0),
    project numeric(18,0),
    reporter character varying(255),
    assignee character varying(255),
    creator character varying(255),
    issuetype character varying(255),
    summary character varying(255),
    description text,
    environment text,
    priority character varying(255),
    resolution character varying(255),
    issuestatus character varying(255),
    created timestamp with time zone,
    updated timestamp with time zone,
    duedate timestamp with time zone,
    resolutiondate timestamp with time zone,
    votes numeric(18,0),
    watches numeric(18,0),
    timeoriginalestimate numeric(18,0),
    timeestimate numeric(18,0),
    timespent numeric(18,0),
    workflow_id numeric(18,0),
    security numeric(18,0),
    fixfor numeric(18,0),
    component numeric(18,0),
    archived character(1),
    archivedby character varying(255),
    archiveddate timestamp with time zone
);


--
-- TOC entry 299 (class 1259 OID 17200)
-- Name: jiraperms; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.jiraperms (
    id numeric(18,0) NOT NULL,
    permtype numeric(18,0),
    projectid numeric(18,0),
    groupname character varying(255)
);


--
-- TOC entry 355 (class 1259 OID 17594)
-- Name: jiraworkflows; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.jiraworkflows (
    id numeric(18,0) NOT NULL,
    workflowname character varying(255),
    creatorname character varying(255),
    descriptor text,
    islocked character varying(60)
);


--
-- TOC entry 358 (class 1259 OID 17618)
-- Name: jiraworkflowstatuses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.jiraworkflowstatuses (
    id numeric(18,0) NOT NULL,
    status character varying(255),
    parentname character varying(255)
);


--
-- TOC entry 253 (class 1259 OID 16886)
-- Name: jquartz_blob_triggers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.jquartz_blob_triggers (
    sched_name character varying(120),
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    blob_data bytea
);


--
-- TOC entry 254 (class 1259 OID 16894)
-- Name: jquartz_calendars; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.jquartz_calendars (
    sched_name character varying(120),
    calendar_name character varying(200) NOT NULL,
    calendar bytea
);


--
-- TOC entry 255 (class 1259 OID 16902)
-- Name: jquartz_cron_triggers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.jquartz_cron_triggers (
    sched_name character varying(120),
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    cron_expression character varying(120),
    time_zone_id character varying(80)
);


--
-- TOC entry 256 (class 1259 OID 16910)
-- Name: jquartz_fired_triggers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.jquartz_fired_triggers (
    sched_name character varying(120),
    entry_id character varying(95) NOT NULL,
    trigger_name character varying(200),
    trigger_group character varying(200),
    is_volatile boolean,
    instance_name character varying(200),
    fired_time numeric(18,0),
    sched_time numeric(18,0),
    priority numeric(9,0),
    state character varying(16),
    job_name character varying(200),
    job_group character varying(200),
    is_stateful boolean,
    is_nonconcurrent boolean,
    is_update_data boolean,
    requests_recovery boolean
);


--
-- TOC entry 257 (class 1259 OID 16918)
-- Name: jquartz_job_details; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.jquartz_job_details (
    sched_name character varying(120),
    job_name character varying(200) NOT NULL,
    job_group character varying(200) NOT NULL,
    description character varying(250),
    job_class_name character varying(250),
    is_durable boolean,
    is_volatile boolean,
    is_stateful boolean,
    is_nonconcurrent boolean,
    is_update_data boolean,
    requests_recovery boolean,
    job_data bytea
);


--
-- TOC entry 258 (class 1259 OID 16926)
-- Name: jquartz_job_listeners; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.jquartz_job_listeners (
    job_name character varying(200) NOT NULL,
    job_group character varying(200) NOT NULL,
    job_listener character varying(200) NOT NULL
);


--
-- TOC entry 259 (class 1259 OID 16934)
-- Name: jquartz_locks; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.jquartz_locks (
    sched_name character varying(120),
    lock_name character varying(40) NOT NULL
);


--
-- TOC entry 260 (class 1259 OID 16939)
-- Name: jquartz_paused_trigger_grps; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.jquartz_paused_trigger_grps (
    sched_name character varying(120),
    trigger_group character varying(200) NOT NULL
);


--
-- TOC entry 261 (class 1259 OID 16944)
-- Name: jquartz_scheduler_state; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.jquartz_scheduler_state (
    sched_name character varying(120),
    instance_name character varying(200) NOT NULL,
    last_checkin_time numeric(18,0),
    checkin_interval numeric(18,0)
);


--
-- TOC entry 263 (class 1259 OID 16957)
-- Name: jquartz_simple_triggers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.jquartz_simple_triggers (
    sched_name character varying(120),
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    repeat_count numeric(18,0),
    repeat_interval numeric(18,0),
    times_triggered numeric(18,0)
);


--
-- TOC entry 262 (class 1259 OID 16949)
-- Name: jquartz_simprop_triggers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.jquartz_simprop_triggers (
    sched_name character varying(120),
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    str_prop_1 character varying(512),
    str_prop_2 character varying(512),
    str_prop_3 character varying(512),
    int_prop_1 numeric(9,0),
    int_prop_2 numeric(9,0),
    long_prop_1 numeric(18,0),
    long_prop_2 numeric(18,0),
    dec_prop_1 numeric(13,4),
    dec_prop_2 numeric(13,4),
    bool_prop_1 boolean,
    bool_prop_2 boolean
);


--
-- TOC entry 264 (class 1259 OID 16965)
-- Name: jquartz_trigger_listeners; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.jquartz_trigger_listeners (
    trigger_name character varying(200),
    trigger_group character varying(200) NOT NULL,
    trigger_listener character varying(200) NOT NULL
);


--
-- TOC entry 265 (class 1259 OID 16973)
-- Name: jquartz_triggers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.jquartz_triggers (
    sched_name character varying(120),
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    job_name character varying(200),
    job_group character varying(200),
    is_volatile boolean,
    description character varying(250),
    next_fire_time numeric(18,0),
    prev_fire_time numeric(18,0),
    priority numeric(9,0),
    trigger_state character varying(16),
    trigger_type character varying(8),
    start_time numeric(18,0),
    end_time numeric(18,0),
    calendar_name character varying(200),
    misfire_instr numeric(4,0),
    job_data bytea
);


--
-- TOC entry 266 (class 1259 OID 16981)
-- Name: label; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.label (
    id numeric(18,0) NOT NULL,
    fieldid numeric(18,0),
    issue numeric(18,0),
    label character varying(255)
);


--
-- TOC entry 267 (class 1259 OID 16986)
-- Name: licenserolesdefault; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.licenserolesdefault (
    id numeric(18,0) NOT NULL,
    license_role_name character varying(255)
);


--
-- TOC entry 268 (class 1259 OID 16991)
-- Name: licenserolesgroup; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.licenserolesgroup (
    id numeric(18,0) NOT NULL,
    license_role_name character varying(255),
    group_id character varying(255),
    primary_group character(1)
);


--
-- TOC entry 269 (class 1259 OID 16999)
-- Name: listenerconfig; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.listenerconfig (
    id numeric(18,0) NOT NULL,
    clazz character varying(255),
    listenername character varying(255)
);


--
-- TOC entry 270 (class 1259 OID 17007)
-- Name: mailserver; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.mailserver (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    description text,
    mailfrom character varying(255),
    prefix character varying(60),
    smtp_port character varying(60),
    protocol character varying(60),
    server_type character varying(60),
    servername character varying(255),
    jndilocation character varying(255),
    mailusername character varying(255),
    mailpassword character varying(255),
    istlsrequired character varying(60),
    timeout numeric(18,0),
    socks_port character varying(60),
    socks_host character varying(60)
);


--
-- TOC entry 271 (class 1259 OID 17015)
-- Name: managedconfigurationitem; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.managedconfigurationitem (
    id numeric(18,0) NOT NULL,
    item_id character varying(255),
    item_type character varying(255),
    managed character varying(10),
    access_level character varying(255),
    source character varying(255),
    description_key character varying(255)
);


--
-- TOC entry 288 (class 1259 OID 17130)
-- Name: membershipbase; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.membershipbase (
    id numeric(18,0) NOT NULL,
    user_name character varying(255),
    group_name character varying(255)
);


--
-- TOC entry 273 (class 1259 OID 17031)
-- Name: moved_issue_key; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.moved_issue_key (
    id numeric(18,0) NOT NULL,
    old_issue_key character varying(255),
    issue_id numeric(18,0)
);


--
-- TOC entry 274 (class 1259 OID 17036)
-- Name: nodeassociation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.nodeassociation (
    source_node_id numeric(18,0) NOT NULL,
    source_node_entity character varying(60) NOT NULL,
    sink_node_id numeric(18,0) NOT NULL,
    sink_node_entity character varying(60) NOT NULL,
    association_type character varying(60) NOT NULL,
    sequence numeric(9,0)
);


--
-- TOC entry 275 (class 1259 OID 17041)
-- Name: nodeindexcounter; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.nodeindexcounter (
    id numeric(18,0) NOT NULL,
    node_id character varying(60),
    sending_node_id character varying(60),
    index_operation_id numeric(18,0)
);


--
-- TOC entry 276 (class 1259 OID 17046)
-- Name: notification; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.notification (
    id numeric(18,0) NOT NULL,
    scheme numeric(18,0),
    event character varying(60),
    event_type_id numeric(18,0),
    template_id numeric(18,0),
    notif_type character varying(60),
    notif_parameter character varying(60)
);


--
-- TOC entry 277 (class 1259 OID 17051)
-- Name: notificationinstance; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.notificationinstance (
    id numeric(18,0) NOT NULL,
    notificationtype character varying(60),
    source numeric(18,0),
    emailaddress character varying(255),
    messageid character varying(255)
);


--
-- TOC entry 278 (class 1259 OID 17059)
-- Name: notificationscheme; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.notificationscheme (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    description text
);


--
-- TOC entry 279 (class 1259 OID 17067)
-- Name: oauthconsumer; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.oauthconsumer (
    id numeric(18,0) NOT NULL,
    created timestamp with time zone,
    consumername character varying(255),
    consumer_key character varying(255),
    consumerservice character varying(255),
    public_key text,
    private_key text,
    description text,
    callback text,
    signature_method character varying(60),
    shared_secret text
);


--
-- TOC entry 280 (class 1259 OID 17075)
-- Name: oauthconsumertoken; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.oauthconsumertoken (
    id numeric(18,0) NOT NULL,
    created timestamp with time zone,
    token_key character varying(255),
    token character varying(255),
    token_secret character varying(255),
    token_type character varying(60),
    consumer_key character varying(255)
);


--
-- TOC entry 281 (class 1259 OID 17083)
-- Name: oauthspconsumer; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.oauthspconsumer (
    id numeric(18,0) NOT NULL,
    created timestamp with time zone,
    consumer_key character varying(255),
    consumername character varying(255),
    public_key text,
    description text,
    callback text,
    two_l_o_allowed character varying(60),
    executing_two_l_o_user character varying(255),
    two_l_o_impersonation_allowed character varying(60),
    three_l_o_allowed character varying(60)
);


--
-- TOC entry 282 (class 1259 OID 17091)
-- Name: oauthsptoken; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.oauthsptoken (
    id numeric(18,0) NOT NULL,
    created timestamp with time zone,
    token character varying(255),
    token_secret character varying(255),
    token_type character varying(60),
    consumer_key character varying(255),
    username character varying(255),
    ttl numeric(18,0),
    spauth character varying(60),
    callback text,
    spverifier character varying(255),
    spversion character varying(60),
    session_handle character varying(255),
    session_creation_time timestamp with time zone,
    session_last_renewal_time timestamp with time zone,
    session_time_to_live timestamp with time zone
);


--
-- TOC entry 298 (class 1259 OID 17195)
-- Name: optionconfiguration; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.optionconfiguration (
    id numeric(18,0) NOT NULL,
    fieldid character varying(60),
    optionid character varying(60),
    fieldconfig numeric(18,0),
    sequence numeric(18,0)
);


--
-- TOC entry 283 (class 1259 OID 17099)
-- Name: os_currentstep; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.os_currentstep (
    id numeric(18,0) NOT NULL,
    entry_id numeric(18,0),
    step_id numeric(9,0),
    action_id numeric(9,0),
    owner character varying(255),
    start_date timestamp with time zone,
    due_date timestamp with time zone,
    finish_date timestamp with time zone,
    status character varying(60),
    caller character varying(255)
);


--
-- TOC entry 284 (class 1259 OID 17107)
-- Name: os_currentstep_prev; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.os_currentstep_prev (
    id numeric(18,0) NOT NULL,
    previous_id numeric(18,0) NOT NULL
);


--
-- TOC entry 286 (class 1259 OID 17117)
-- Name: os_historystep; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.os_historystep (
    id numeric(18,0) NOT NULL,
    entry_id numeric(18,0),
    step_id numeric(9,0),
    action_id numeric(9,0),
    owner character varying(255),
    start_date timestamp with time zone,
    due_date timestamp with time zone,
    finish_date timestamp with time zone,
    status character varying(60),
    caller character varying(255)
);


--
-- TOC entry 287 (class 1259 OID 17125)
-- Name: os_historystep_prev; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.os_historystep_prev (
    id numeric(18,0) NOT NULL,
    previous_id numeric(18,0) NOT NULL
);


--
-- TOC entry 297 (class 1259 OID 17190)
-- Name: os_wfentry; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.os_wfentry (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    initialized numeric(9,0),
    state numeric(9,0)
);


--
-- TOC entry 300 (class 1259 OID 17205)
-- Name: permissionscheme; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.permissionscheme (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    description text
);


--
-- TOC entry 301 (class 1259 OID 17213)
-- Name: permissionschemeattribute; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.permissionschemeattribute (
    id numeric(18,0) NOT NULL,
    scheme numeric(18,0),
    attribute_key character varying(255),
    attribute_value text
);


--
-- TOC entry 302 (class 1259 OID 17221)
-- Name: pluginstate; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.pluginstate (
    pluginkey character varying(255) NOT NULL,
    pluginenabled character varying(60)
);


--
-- TOC entry 303 (class 1259 OID 17226)
-- Name: pluginversion; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.pluginversion (
    id numeric(18,0) NOT NULL,
    pluginname character varying(255),
    pluginkey character varying(255),
    pluginversion character varying(255),
    created timestamp with time zone
);


--
-- TOC entry 304 (class 1259 OID 17234)
-- Name: portalpage; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.portalpage (
    id numeric(18,0) NOT NULL,
    username character varying(255),
    pagename character varying(255),
    description character varying(255),
    sequence numeric(18,0),
    fav_count numeric(18,0),
    layout character varying(255),
    ppversion numeric(18,0)
);


--
-- TOC entry 305 (class 1259 OID 17242)
-- Name: portletconfiguration; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.portletconfiguration (
    id numeric(18,0) NOT NULL,
    portalpage numeric(18,0),
    portlet_id character varying(255),
    column_number numeric(9,0),
    positionseq numeric(9,0),
    gadget_xml text,
    color character varying(255),
    dashboard_module_complete_key text
);


--
-- TOC entry 306 (class 1259 OID 17250)
-- Name: priority; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.priority (
    id character varying(60) NOT NULL,
    sequence numeric(18,0),
    pname character varying(60),
    description text,
    iconurl character varying(255),
    status_color character varying(60)
);


--
-- TOC entry 307 (class 1259 OID 17258)
-- Name: productlicense; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.productlicense (
    id numeric(18,0) NOT NULL,
    license text
);


--
-- TOC entry 308 (class 1259 OID 17266)
-- Name: project; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.project (
    id numeric(18,0) NOT NULL,
    pname character varying(255),
    url character varying(255),
    lead character varying(255),
    description text,
    pkey character varying(255),
    pcounter numeric(18,0),
    assigneetype numeric(18,0),
    avatar numeric(18,0),
    originalkey character varying(255),
    projecttype character varying(255)
);


--
-- TOC entry 311 (class 1259 OID 17287)
-- Name: project_key; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.project_key (
    id numeric(18,0) NOT NULL,
    project_id numeric(18,0),
    project_key character varying(255)
);


--
-- TOC entry 309 (class 1259 OID 17274)
-- Name: projectcategory; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.projectcategory (
    id numeric(18,0) NOT NULL,
    cname character varying(255),
    description text
);


--
-- TOC entry 310 (class 1259 OID 17282)
-- Name: projectchangedtime; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.projectchangedtime (
    project_id numeric(18,0) NOT NULL,
    issue_changed_time timestamp with time zone
);


--
-- TOC entry 312 (class 1259 OID 17292)
-- Name: projectrole; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.projectrole (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    description text
);


--
-- TOC entry 313 (class 1259 OID 17300)
-- Name: projectroleactor; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.projectroleactor (
    id numeric(18,0) NOT NULL,
    pid numeric(18,0),
    projectroleid numeric(18,0),
    roletype character varying(255),
    roletypeparameter character varying(255)
);


--
-- TOC entry 352 (class 1259 OID 17573)
-- Name: projectversion; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.projectversion (
    id numeric(18,0) NOT NULL,
    project numeric(18,0),
    vname character varying(255),
    description text,
    sequence numeric(18,0),
    released character varying(10),
    archived character varying(10),
    url character varying(255),
    startdate timestamp with time zone,
    releasedate timestamp with time zone
);


--
-- TOC entry 289 (class 1259 OID 17138)
-- Name: propertydata; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.propertydata (
    id numeric(18,0) NOT NULL,
    propertyvalue oid
);


--
-- TOC entry 290 (class 1259 OID 17143)
-- Name: propertydate; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.propertydate (
    id numeric(18,0) NOT NULL,
    propertyvalue timestamp with time zone
);


--
-- TOC entry 291 (class 1259 OID 17148)
-- Name: propertydecimal; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.propertydecimal (
    id numeric(18,0) NOT NULL,
    propertyvalue double precision
);


--
-- TOC entry 292 (class 1259 OID 17153)
-- Name: propertyentry; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.propertyentry (
    id numeric(18,0) NOT NULL,
    entity_name character varying(255),
    entity_id numeric(18,0),
    property_key character varying(255),
    propertytype numeric(9,0)
);


--
-- TOC entry 293 (class 1259 OID 17161)
-- Name: propertynumber; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.propertynumber (
    id numeric(18,0) NOT NULL,
    propertyvalue numeric(18,0)
);


--
-- TOC entry 294 (class 1259 OID 17166)
-- Name: propertystring; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.propertystring (
    id numeric(18,0) NOT NULL,
    propertyvalue text
);


--
-- TOC entry 295 (class 1259 OID 17174)
-- Name: propertytext; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.propertytext (
    id numeric(18,0) NOT NULL,
    propertyvalue text
);


--
-- TOC entry 314 (class 1259 OID 17308)
-- Name: qrtz_calendars; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.qrtz_calendars (
    id numeric(18,0),
    calendar_name character varying(255) NOT NULL,
    calendar text
);


--
-- TOC entry 315 (class 1259 OID 17316)
-- Name: qrtz_cron_triggers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.qrtz_cron_triggers (
    id numeric(18,0) NOT NULL,
    trigger_id numeric(18,0),
    cronexperssion character varying(255)
);


--
-- TOC entry 316 (class 1259 OID 17321)
-- Name: qrtz_fired_triggers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.qrtz_fired_triggers (
    id numeric(18,0),
    entry_id character varying(255) NOT NULL,
    trigger_id numeric(18,0),
    trigger_listener character varying(255),
    fired_time timestamp with time zone,
    trigger_state character varying(255)
);


--
-- TOC entry 317 (class 1259 OID 17329)
-- Name: qrtz_job_details; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.qrtz_job_details (
    id numeric(18,0) NOT NULL,
    job_name character varying(255),
    job_group character varying(255),
    class_name character varying(255),
    is_durable character varying(60),
    is_stateful character varying(60),
    requests_recovery character varying(60),
    job_data character varying(255)
);


--
-- TOC entry 318 (class 1259 OID 17337)
-- Name: qrtz_job_listeners; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.qrtz_job_listeners (
    id numeric(18,0) NOT NULL,
    job numeric(18,0),
    job_listener character varying(255)
);


--
-- TOC entry 319 (class 1259 OID 17342)
-- Name: qrtz_simple_triggers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.qrtz_simple_triggers (
    id numeric(18,0) NOT NULL,
    trigger_id numeric(18,0),
    repeat_count numeric(9,0),
    repeat_interval numeric(18,0),
    times_triggered numeric(9,0)
);


--
-- TOC entry 320 (class 1259 OID 17347)
-- Name: qrtz_trigger_listeners; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.qrtz_trigger_listeners (
    id numeric(18,0) NOT NULL,
    trigger_id numeric(18,0),
    trigger_listener character varying(255)
);


--
-- TOC entry 321 (class 1259 OID 17352)
-- Name: qrtz_triggers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.qrtz_triggers (
    id numeric(18,0) NOT NULL,
    trigger_name character varying(255),
    trigger_group character varying(255),
    job numeric(18,0),
    next_fire timestamp with time zone,
    trigger_state character varying(255),
    trigger_type character varying(60),
    start_time timestamp with time zone,
    end_time timestamp with time zone,
    calendar_name character varying(255),
    misfire_instr numeric(9,0)
);


--
-- TOC entry 322 (class 1259 OID 17360)
-- Name: reindex_component; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.reindex_component (
    id numeric(18,0) NOT NULL,
    request_id numeric(18,0),
    affected_index character varying(60),
    entity_type character varying(60)
);


--
-- TOC entry 323 (class 1259 OID 17365)
-- Name: reindex_request; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.reindex_request (
    id numeric(18,0) NOT NULL,
    type character varying(60),
    request_time timestamp with time zone,
    start_time timestamp with time zone,
    completion_time timestamp with time zone,
    status character varying(60),
    execution_node_id character varying(60),
    query text
);


--
-- TOC entry 324 (class 1259 OID 17373)
-- Name: remembermetoken; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.remembermetoken (
    id numeric(18,0) NOT NULL,
    created timestamp with time zone,
    token character varying(255),
    username character varying(255)
);


--
-- TOC entry 326 (class 1259 OID 17389)
-- Name: remotelink; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.remotelink (
    id numeric(18,0) NOT NULL,
    issueid numeric(18,0),
    globalid character varying(255),
    title character varying(255),
    summary text,
    url text,
    iconurl text,
    icontitle text,
    relationship character varying(255),
    resolved character(1),
    statusname character varying(255),
    statusdescription text,
    statusiconurl text,
    statusicontitle text,
    statusiconlink text,
    statuscategorykey character varying(255),
    statuscategorycolorname character varying(255),
    applicationtype character varying(255),
    applicationname character varying(255)
);


--
-- TOC entry 327 (class 1259 OID 17397)
-- Name: replicatedindexoperation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.replicatedindexoperation (
    id numeric(18,0) NOT NULL,
    index_time timestamp with time zone,
    node_id character varying(60),
    affected_index character varying(60),
    entity_type character varying(60),
    affected_ids text,
    operation character varying(60),
    filename character varying(255)
);


--
-- TOC entry 328 (class 1259 OID 17405)
-- Name: resolution; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.resolution (
    id character varying(60) NOT NULL,
    sequence numeric(18,0),
    pname character varying(60),
    description text,
    iconurl character varying(255)
);


--
-- TOC entry 329 (class 1259 OID 17413)
-- Name: rundetails; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.rundetails (
    id numeric(18,0) NOT NULL,
    job_id character varying(255),
    start_time timestamp with time zone,
    run_duration numeric(18,0),
    run_outcome character(1),
    info_message character varying(255)
);


--
-- TOC entry 330 (class 1259 OID 17421)
-- Name: schemeissuesecurities; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.schemeissuesecurities (
    id numeric(18,0) NOT NULL,
    scheme numeric(18,0),
    security numeric(18,0),
    sec_type character varying(255),
    sec_parameter character varying(255)
);


--
-- TOC entry 331 (class 1259 OID 17429)
-- Name: schemeissuesecuritylevels; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.schemeissuesecuritylevels (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    description text,
    scheme numeric(18,0)
);


--
-- TOC entry 332 (class 1259 OID 17437)
-- Name: schemepermissions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.schemepermissions (
    id numeric(18,0) NOT NULL,
    scheme numeric(18,0),
    permission numeric(18,0),
    perm_type character varying(255),
    perm_parameter character varying(255),
    permission_key character varying(255)
);


--
-- TOC entry 333 (class 1259 OID 17445)
-- Name: searchrequest; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.searchrequest (
    id numeric(18,0) NOT NULL,
    filtername character varying(255),
    authorname character varying(255),
    description text,
    username character varying(255),
    groupname character varying(255),
    projectid numeric(18,0),
    reqcontent text,
    fav_count numeric(18,0),
    filtername_lower character varying(255)
);


--
-- TOC entry 334 (class 1259 OID 17453)
-- Name: sequence_value_item; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sequence_value_item (
    seq_name character varying(60) NOT NULL,
    seq_id numeric(18,0)
);


--
-- TOC entry 335 (class 1259 OID 17458)
-- Name: serviceconfig; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.serviceconfig (
    id numeric(18,0) NOT NULL,
    delaytime numeric(18,0),
    clazz character varying(255),
    servicename character varying(255),
    cron_expression character varying(255)
);


--
-- TOC entry 336 (class 1259 OID 17466)
-- Name: sharepermissions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sharepermissions (
    id numeric(18,0) NOT NULL,
    entityid numeric(18,0),
    entitytype character varying(60),
    sharetype character varying(10),
    param1 character varying(255),
    param2 character varying(60),
    rights numeric(9,0)
);


--
-- TOC entry 338 (class 1259 OID 17479)
-- Name: tempattachmentsmonitor; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tempattachmentsmonitor (
    temporary_attachment_id character varying(255) NOT NULL,
    form_token character varying(255),
    file_name character varying(255),
    content_type character varying(255),
    file_size numeric(18,0),
    created_time numeric(18,0)
);


--
-- TOC entry 339 (class 1259 OID 17487)
-- Name: trackback_ping; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.trackback_ping (
    id numeric(18,0) NOT NULL,
    issue numeric(18,0),
    url character varying(255),
    title character varying(255),
    blogname character varying(255),
    excerpt character varying(255),
    created timestamp with time zone
);


--
-- TOC entry 340 (class 1259 OID 17495)
-- Name: trustedapp; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.trustedapp (
    id numeric(18,0) NOT NULL,
    application_id character varying(255),
    name character varying(255),
    public_key text,
    ip_match text,
    url_match text,
    timeout numeric(18,0),
    created timestamp with time zone,
    created_by character varying(255),
    updated timestamp with time zone,
    updated_by character varying(255)
);


--
-- TOC entry 341 (class 1259 OID 17503)
-- Name: upgradehistory; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.upgradehistory (
    id numeric(18,0),
    upgradeclass character varying(255) NOT NULL,
    targetbuild character varying(255),
    status character varying(255),
    downgradetaskrequired character(1)
);


--
-- TOC entry 342 (class 1259 OID 17511)
-- Name: upgradetaskhistory; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.upgradetaskhistory (
    id numeric(18,0) NOT NULL,
    upgrade_task_factory_key character varying(255),
    build_number numeric(9,0),
    status character varying(60),
    upgrade_type character varying(10)
);


--
-- TOC entry 343 (class 1259 OID 17516)
-- Name: upgradetaskhistoryauditlog; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.upgradetaskhistoryauditlog (
    id numeric(18,0) NOT NULL,
    upgrade_task_factory_key character varying(255),
    build_number numeric(9,0),
    status character varying(60),
    upgrade_type character varying(10),
    timeperformed timestamp with time zone,
    action character varying(10)
);


--
-- TOC entry 344 (class 1259 OID 17521)
-- Name: upgradeversionhistory; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.upgradeversionhistory (
    id numeric(18,0),
    timeperformed timestamp with time zone,
    targetbuild character varying(255) NOT NULL,
    targetversion character varying(255)
);


--
-- TOC entry 346 (class 1259 OID 17537)
-- Name: userassociation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.userassociation (
    source_name character varying(60) NOT NULL,
    sink_node_id numeric(18,0) NOT NULL,
    sink_node_entity character varying(60) NOT NULL,
    association_type character varying(60) NOT NULL,
    sequence numeric(9,0),
    created timestamp with time zone
);


--
-- TOC entry 296 (class 1259 OID 17182)
-- Name: userbase; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.userbase (
    id numeric(18,0) NOT NULL,
    username character varying(255),
    password_hash character varying(255)
);


--
-- TOC entry 348 (class 1259 OID 17550)
-- Name: userhistoryitem; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.userhistoryitem (
    id numeric(18,0) NOT NULL,
    entitytype character varying(10),
    entityid character varying(60),
    username character varying(255),
    lastviewed numeric(18,0),
    data text
);


--
-- TOC entry 349 (class 1259 OID 17558)
-- Name: userpickerfilter; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.userpickerfilter (
    id numeric(18,0) NOT NULL,
    customfield numeric(18,0),
    customfieldconfig numeric(18,0),
    enabled character varying(60)
);


--
-- TOC entry 350 (class 1259 OID 17563)
-- Name: userpickerfiltergroup; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.userpickerfiltergroup (
    id numeric(18,0) NOT NULL,
    userpickerfilter numeric(18,0),
    groupname character varying(255)
);


--
-- TOC entry 351 (class 1259 OID 17568)
-- Name: userpickerfilterrole; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.userpickerfilterrole (
    id numeric(18,0) NOT NULL,
    userpickerfilter numeric(18,0),
    projectroleid numeric(18,0)
);


--
-- TOC entry 353 (class 1259 OID 17581)
-- Name: versioncontrol; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.versioncontrol (
    id numeric(18,0) NOT NULL,
    vcsname character varying(255),
    vcsdescription character varying(255),
    vcstype character varying(255)
);


--
-- TOC entry 354 (class 1259 OID 17589)
-- Name: votehistory; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.votehistory (
    id numeric(18,0) NOT NULL,
    issueid numeric(18,0),
    votes numeric(18,0),
    "timestamp" timestamp with time zone
);


--
-- TOC entry 356 (class 1259 OID 17602)
-- Name: workflowscheme; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.workflowscheme (
    id numeric(18,0) NOT NULL,
    name character varying(255),
    description text
);


--
-- TOC entry 357 (class 1259 OID 17610)
-- Name: workflowschemeentity; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.workflowschemeentity (
    id numeric(18,0) NOT NULL,
    scheme numeric(18,0),
    workflow character varying(255),
    issuetype character varying(255)
);


--
-- TOC entry 359 (class 1259 OID 17626)
-- Name: worklog; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.worklog (
    id numeric(18,0) NOT NULL,
    issueid numeric(18,0),
    author character varying(255),
    grouplevel character varying(255),
    rolelevel numeric(18,0),
    worklogbody text,
    created timestamp with time zone,
    updateauthor character varying(255),
    updated timestamp with time zone,
    startdate timestamp with time zone,
    timeworked numeric(18,0)
);


--
-- TOC entry 4405 (class 2604 OID 19982)
-- Name: AO_0A5972_NOTIFICATION_SETTING ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_0A5972_NOTIFICATION_SETTING" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_0A5972_NOTIFICATION_SETTING_ID_seq"'::regclass);


--
-- TOC entry 4414 (class 2604 OID 20080)
-- Name: AO_21D670_WHITELIST_RULES ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_21D670_WHITELIST_RULES" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_21D670_WHITELIST_RULES_ID_seq"'::regclass);


--
-- TOC entry 4401 (class 2604 OID 19906)
-- Name: AO_21F425_MESSAGE_MAPPING_AO ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_21F425_MESSAGE_MAPPING_AO" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_21F425_MESSAGE_MAPPING_AO_ID_seq"'::regclass);


--
-- TOC entry 4400 (class 2604 OID 19895)
-- Name: AO_21F425_USER_PROPERTY_AO ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_21F425_USER_PROPERTY_AO" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_21F425_USER_PROPERTY_AO_ID_seq"'::regclass);


--
-- TOC entry 4299 (class 2604 OID 19060)
-- Name: AO_38321B_CUSTOM_CONTENT_LINK ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_38321B_CUSTOM_CONTENT_LINK" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_38321B_CUSTOM_CONTENT_LINK_ID_seq"'::regclass);


--
-- TOC entry 4344 (class 2604 OID 19440)
-- Name: AO_3B1893_LOOP_DETECTION ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_3B1893_LOOP_DETECTION" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_3B1893_LOOP_DETECTION_ID_seq"'::regclass);


--
-- TOC entry 4422 (class 2604 OID 20191)
-- Name: AO_4789DD_HEALTH_CHECK_STATUS ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_4789DD_HEALTH_CHECK_STATUS" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_4789DD_HEALTH_CHECK_STATUS_ID_seq"'::regclass);


--
-- TOC entry 4423 (class 2604 OID 20202)
-- Name: AO_4789DD_PROPERTIES ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_4789DD_PROPERTIES" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_4789DD_PROPERTIES_ID_seq"'::regclass);


--
-- TOC entry 4426 (class 2604 OID 20227)
-- Name: AO_4789DD_READ_NOTIFICATIONS ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_4789DD_READ_NOTIFICATIONS" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_4789DD_READ_NOTIFICATIONS_ID_seq"'::regclass);


--
-- TOC entry 4425 (class 2604 OID 20214)
-- Name: AO_4789DD_TASK_MONITOR ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_4789DD_TASK_MONITOR" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_4789DD_TASK_MONITOR_ID_seq"'::regclass);


--
-- TOC entry 4406 (class 2604 OID 20011)
-- Name: AO_4AEACD_WEBHOOK_DAO ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_4AEACD_WEBHOOK_DAO" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_4AEACD_WEBHOOK_DAO_ID_seq"'::regclass);


--
-- TOC entry 4298 (class 2604 OID 19048)
-- Name: AO_550953_SHORTCUT ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_550953_SHORTCUT" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_550953_SHORTCUT_ID_seq"'::regclass);


--
-- TOC entry 4421 (class 2604 OID 20155)
-- Name: AO_563AEE_ACTIVITY_ENTITY ACTIVITY_ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_563AEE_ACTIVITY_ENTITY" ALTER COLUMN "ACTIVITY_ID" SET DEFAULT nextval('public."AO_563AEE_ACTIVITY_ENTITY_ACTIVITY_ID_seq"'::regclass);


--
-- TOC entry 4417 (class 2604 OID 20103)
-- Name: AO_563AEE_ACTOR_ENTITY ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_563AEE_ACTOR_ENTITY" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_563AEE_ACTOR_ENTITY_ID_seq"'::regclass);


--
-- TOC entry 4418 (class 2604 OID 20114)
-- Name: AO_563AEE_MEDIA_LINK_ENTITY ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_563AEE_MEDIA_LINK_ENTITY" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_563AEE_MEDIA_LINK_ENTITY_ID_seq"'::regclass);


--
-- TOC entry 4419 (class 2604 OID 20122)
-- Name: AO_563AEE_OBJECT_ENTITY ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_563AEE_OBJECT_ENTITY" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_563AEE_OBJECT_ENTITY_ID_seq"'::regclass);


--
-- TOC entry 4420 (class 2604 OID 20138)
-- Name: AO_563AEE_TARGET_ENTITY ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_563AEE_TARGET_ENTITY" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_563AEE_TARGET_ENTITY_ID_seq"'::regclass);


--
-- TOC entry 4397 (class 2604 OID 19864)
-- Name: AO_575BF5_DEV_SUMMARY ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_575BF5_DEV_SUMMARY" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_575BF5_DEV_SUMMARY_ID_seq"'::regclass);


--
-- TOC entry 4396 (class 2604 OID 19853)
-- Name: AO_575BF5_PROCESSED_COMMITS ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_575BF5_PROCESSED_COMMITS" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_575BF5_PROCESSED_COMMITS_ID_seq"'::regclass);


--
-- TOC entry 4394 (class 2604 OID 19844)
-- Name: AO_575BF5_PROVIDER_ISSUE ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_575BF5_PROVIDER_ISSUE" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_575BF5_PROVIDER_ISSUE_ID_seq"'::regclass);


--
-- TOC entry 4399 (class 2604 OID 19876)
-- Name: AO_575BF5_PROVIDER_SEQ_NO ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_575BF5_PROVIDER_SEQ_NO" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_575BF5_PROVIDER_SEQ_NO_ID_seq"'::regclass);


--
-- TOC entry 4411 (class 2604 OID 20058)
-- Name: AO_587B34_PROJECT_CONFIG ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_587B34_PROJECT_CONFIG" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_587B34_PROJECT_CONFIG_ID_seq"'::regclass);


--
-- TOC entry 4408 (class 2604 OID 20023)
-- Name: AO_5FB9D7_AOHIP_CHAT_LINK ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_5FB9D7_AOHIP_CHAT_LINK" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_5FB9D7_AOHIP_CHAT_LINK_ID_seq"'::regclass);


--
-- TOC entry 4409 (class 2604 OID 20034)
-- Name: AO_5FB9D7_AOHIP_CHAT_USER ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_5FB9D7_AOHIP_CHAT_USER" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_5FB9D7_AOHIP_CHAT_USER_ID_seq"'::regclass);


--
-- TOC entry 4316 (class 2604 OID 19166)
-- Name: AO_60DB71_AUDITENTRY ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_AUDITENTRY" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_60DB71_AUDITENTRY_ID_seq"'::regclass);


--
-- TOC entry 4327 (class 2604 OID 19258)
-- Name: AO_60DB71_BOARDADMINS ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_BOARDADMINS" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_60DB71_BOARDADMINS_ID_seq"'::regclass);


--
-- TOC entry 4319 (class 2604 OID 19196)
-- Name: AO_60DB71_CARDCOLOR ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_CARDCOLOR" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_60DB71_CARDCOLOR_ID_seq"'::regclass);


--
-- TOC entry 4317 (class 2604 OID 19179)
-- Name: AO_60DB71_CARDLAYOUT ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_CARDLAYOUT" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_60DB71_CARDLAYOUT_ID_seq"'::regclass);


--
-- TOC entry 4325 (class 2604 OID 19244)
-- Name: AO_60DB71_COLUMN ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_COLUMN" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_60DB71_COLUMN_ID_seq"'::regclass);


--
-- TOC entry 4335 (class 2604 OID 19377)
-- Name: AO_60DB71_COLUMNSTATUS ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_COLUMNSTATUS" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_60DB71_COLUMNSTATUS_ID_seq"'::regclass);


--
-- TOC entry 4321 (class 2604 OID 19213)
-- Name: AO_60DB71_DETAILVIEWFIELD ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_DETAILVIEWFIELD" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_60DB71_DETAILVIEWFIELD_ID_seq"'::regclass);


--
-- TOC entry 4328 (class 2604 OID 19274)
-- Name: AO_60DB71_ESTIMATESTATISTIC ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_ESTIMATESTATISTIC" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_60DB71_ESTIMATESTATISTIC_ID_seq"'::regclass);


--
-- TOC entry 4303 (class 2604 OID 19085)
-- Name: AO_60DB71_ISSUERANKING ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_ISSUERANKING" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_60DB71_ISSUERANKING_ID_seq"'::regclass);


--
-- TOC entry 4307 (class 2604 OID 19113)
-- Name: AO_60DB71_ISSUERANKINGLOG ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_ISSUERANKINGLOG" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_60DB71_ISSUERANKINGLOG_ID_seq"'::regclass);


--
-- TOC entry 4310 (class 2604 OID 19126)
-- Name: AO_60DB71_LEXORANK ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_LEXORANK" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_60DB71_LEXORANK_ID_seq"'::regclass);


--
-- TOC entry 4305 (class 2604 OID 19094)
-- Name: AO_60DB71_LEXORANKBALANCER ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_LEXORANKBALANCER" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_60DB71_LEXORANKBALANCER_ID_seq"'::regclass);


--
-- TOC entry 4337 (class 2604 OID 19396)
-- Name: AO_60DB71_NONWORKINGDAY ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_NONWORKINGDAY" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_60DB71_NONWORKINGDAY_ID_seq"'::regclass);


--
-- TOC entry 4323 (class 2604 OID 19227)
-- Name: AO_60DB71_QUICKFILTER ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_QUICKFILTER" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_60DB71_QUICKFILTER_ID_seq"'::regclass);


--
-- TOC entry 4314 (class 2604 OID 19150)
-- Name: AO_60DB71_RANKABLEOBJECT ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_RANKABLEOBJECT" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_60DB71_RANKABLEOBJECT_ID_seq"'::regclass);


--
-- TOC entry 4306 (class 2604 OID 19102)
-- Name: AO_60DB71_RAPIDVIEW ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_RAPIDVIEW" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_60DB71_RAPIDVIEW_ID_seq"'::regclass);


--
-- TOC entry 4313 (class 2604 OID 19139)
-- Name: AO_60DB71_SPRINT ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_SPRINT" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_60DB71_SPRINT_ID_seq"'::regclass);


--
-- TOC entry 4332 (class 2604 OID 19335)
-- Name: AO_60DB71_STATSFIELD ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_STATSFIELD" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_60DB71_STATSFIELD_ID_seq"'::regclass);


--
-- TOC entry 4330 (class 2604 OID 19306)
-- Name: AO_60DB71_SUBQUERY ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_SUBQUERY" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_60DB71_SUBQUERY_ID_seq"'::regclass);


--
-- TOC entry 4333 (class 2604 OID 19348)
-- Name: AO_60DB71_SWIMLANE ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_SWIMLANE" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_60DB71_SWIMLANE_ID_seq"'::regclass);


--
-- TOC entry 4329 (class 2604 OID 19290)
-- Name: AO_60DB71_TRACKINGSTATISTIC ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_TRACKINGSTATISTIC" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_60DB71_TRACKINGSTATISTIC_ID_seq"'::regclass);


--
-- TOC entry 4315 (class 2604 OID 19158)
-- Name: AO_60DB71_VERSION ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_VERSION" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_60DB71_VERSION_ID_seq"'::regclass);


--
-- TOC entry 4331 (class 2604 OID 19322)
-- Name: AO_60DB71_WORKINGDAYS ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_WORKINGDAYS" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_60DB71_WORKINGDAYS_ID_seq"'::regclass);


--
-- TOC entry 4402 (class 2604 OID 19928)
-- Name: AO_733371_EVENT ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_733371_EVENT" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_733371_EVENT_ID_seq"'::regclass);


--
-- TOC entry 4404 (class 2604 OID 19958)
-- Name: AO_733371_EVENT_PARAMETER ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_733371_EVENT_PARAMETER" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_733371_EVENT_PARAMETER_ID_seq"'::regclass);


--
-- TOC entry 4403 (class 2604 OID 19942)
-- Name: AO_733371_EVENT_RECIPIENT ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_733371_EVENT_RECIPIENT" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_733371_EVENT_RECIPIENT_ID_seq"'::regclass);


--
-- TOC entry 4341 (class 2604 OID 19427)
-- Name: AO_97EDAB_USERINVITATION ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_97EDAB_USERINVITATION" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_97EDAB_USERINVITATION_ID_seq"'::regclass);


--
-- TOC entry 4301 (class 2604 OID 19073)
-- Name: AO_A0B856_WEB_HOOK_LISTENER_AO ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_A0B856_WEB_HOOK_LISTENER_AO" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_A0B856_WEB_HOOK_LISTENER_AO_ID_seq"'::regclass);


--
-- TOC entry 4296 (class 2604 OID 19036)
-- Name: AO_B9A0F0_APPLIED_TEMPLATE ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_B9A0F0_APPLIED_TEMPLATE" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_B9A0F0_APPLIED_TEMPLATE_ID_seq"'::regclass);


--
-- TOC entry 4339 (class 2604 OID 19415)
-- Name: AO_C16815_ALERT_AO ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_C16815_ALERT_AO" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_C16815_ALERT_AO_ID_seq"'::regclass);


--
-- TOC entry 4345 (class 2604 OID 19451)
-- Name: AO_CFF990_AOTRANSITION_FAILURE ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_CFF990_AOTRANSITION_FAILURE" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_CFF990_AOTRANSITION_FAILURE_ID_seq"'::regclass);


--
-- TOC entry 4367 (class 2604 OID 19623)
-- Name: AO_E8B6CC_BRANCH ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_BRANCH" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_E8B6CC_BRANCH_ID_seq"'::regclass);


--
-- TOC entry 4362 (class 2604 OID 19592)
-- Name: AO_E8B6CC_BRANCH_HEAD_MAPPING ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_BRANCH_HEAD_MAPPING" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_E8B6CC_BRANCH_HEAD_MAPPING_ID_seq"'::regclass);


--
-- TOC entry 4355 (class 2604 OID 19511)
-- Name: AO_E8B6CC_CHANGESET_MAPPING ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_CHANGESET_MAPPING" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_E8B6CC_CHANGESET_MAPPING_ID_seq"'::regclass);


--
-- TOC entry 4384 (class 2604 OID 19713)
-- Name: AO_E8B6CC_COMMIT ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_COMMIT" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_E8B6CC_COMMIT_ID_seq"'::regclass);


--
-- TOC entry 4374 (class 2604 OID 19659)
-- Name: AO_E8B6CC_GIT_HUB_EVENT ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_GIT_HUB_EVENT" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_E8B6CC_GIT_HUB_EVENT_ID_seq"'::regclass);


--
-- TOC entry 4349 (class 2604 OID 19465)
-- Name: AO_E8B6CC_ISSUE_MAPPING ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_ISSUE_MAPPING" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_E8B6CC_ISSUE_MAPPING_ID_seq"'::regclass);


--
-- TOC entry 4351 (class 2604 OID 19487)
-- Name: AO_E8B6CC_ISSUE_MAPPING_V2 ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_ISSUE_MAPPING_V2" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_E8B6CC_ISSUE_MAPPING_V2_ID_seq"'::regclass);


--
-- TOC entry 4388 (class 2604 OID 19758)
-- Name: AO_E8B6CC_ISSUE_TO_BRANCH ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_ISSUE_TO_BRANCH" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_E8B6CC_ISSUE_TO_BRANCH_ID_seq"'::regclass);


--
-- TOC entry 4361 (class 2604 OID 19571)
-- Name: AO_E8B6CC_ISSUE_TO_CHANGESET ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_ISSUE_TO_CHANGESET" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_E8B6CC_ISSUE_TO_CHANGESET_ID_seq"'::regclass);


--
-- TOC entry 4381 (class 2604 OID 19700)
-- Name: AO_E8B6CC_MESSAGE ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_MESSAGE" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_E8B6CC_MESSAGE_ID_seq"'::regclass);


--
-- TOC entry 4390 (class 2604 OID 19793)
-- Name: AO_E8B6CC_MESSAGE_QUEUE_ITEM ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_MESSAGE_QUEUE_ITEM" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_E8B6CC_MESSAGE_QUEUE_ITEM_ID_seq"'::regclass);


--
-- TOC entry 4389 (class 2604 OID 19780)
-- Name: AO_E8B6CC_MESSAGE_TAG ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_MESSAGE_TAG" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_E8B6CC_MESSAGE_TAG_ID_seq"'::regclass);


--
-- TOC entry 4357 (class 2604 OID 19523)
-- Name: AO_E8B6CC_ORGANIZATION_MAPPING ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_ORGANIZATION_MAPPING" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_E8B6CC_ORGANIZATION_MAPPING_ID_seq"'::regclass);


--
-- TOC entry 4375 (class 2604 OID 19672)
-- Name: AO_E8B6CC_ORG_TO_PROJECT ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_ORG_TO_PROJECT" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_E8B6CC_ORG_TO_PROJECT_ID_seq"'::regclass);


--
-- TOC entry 4350 (class 2604 OID 19476)
-- Name: AO_E8B6CC_PROJECT_MAPPING ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_PROJECT_MAPPING" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_E8B6CC_PROJECT_MAPPING_ID_seq"'::regclass);


--
-- TOC entry 4353 (class 2604 OID 19499)
-- Name: AO_E8B6CC_PROJECT_MAPPING_V2 ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_PROJECT_MAPPING_V2" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_E8B6CC_PROJECT_MAPPING_V2_ID_seq"'::regclass);


--
-- TOC entry 4369 (class 2604 OID 19637)
-- Name: AO_E8B6CC_PR_ISSUE_KEY ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_PR_ISSUE_KEY" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_E8B6CC_PR_ISSUE_KEY_ID_seq"'::regclass);


--
-- TOC entry 4387 (class 2604 OID 19740)
-- Name: AO_E8B6CC_PR_PARTICIPANT ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_PR_PARTICIPANT" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_E8B6CC_PR_PARTICIPANT_ID_seq"'::regclass);


--
-- TOC entry 4393 (class 2604 OID 19813)
-- Name: AO_E8B6CC_PR_TO_COMMIT ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_PR_TO_COMMIT" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_E8B6CC_PR_TO_COMMIT_ID_seq"'::regclass);


--
-- TOC entry 4365 (class 2604 OID 19611)
-- Name: AO_E8B6CC_PULL_REQUEST ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_PULL_REQUEST" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_E8B6CC_PULL_REQUEST_ID_seq"'::regclass);


--
-- TOC entry 4358 (class 2604 OID 19534)
-- Name: AO_E8B6CC_REPOSITORY_MAPPING ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_REPOSITORY_MAPPING" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_E8B6CC_REPOSITORY_MAPPING_ID_seq"'::regclass);


--
-- TOC entry 4360 (class 2604 OID 19553)
-- Name: AO_E8B6CC_REPO_TO_CHANGESET ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_REPO_TO_CHANGESET" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_E8B6CC_REPO_TO_CHANGESET_ID_seq"'::regclass);


--
-- TOC entry 4385 (class 2604 OID 19724)
-- Name: AO_E8B6CC_REPO_TO_PROJECT ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_REPO_TO_PROJECT" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_E8B6CC_REPO_TO_PROJECT_ID_seq"'::regclass);


--
-- TOC entry 4378 (class 2604 OID 19686)
-- Name: AO_E8B6CC_SYNC_AUDIT_LOG ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_SYNC_AUDIT_LOG" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_E8B6CC_SYNC_AUDIT_LOG_ID_seq"'::regclass);


--
-- TOC entry 4371 (class 2604 OID 19646)
-- Name: AO_E8B6CC_SYNC_EVENT ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_SYNC_EVENT" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_E8B6CC_SYNC_EVENT_ID_seq"'::regclass);


--
-- TOC entry 4416 (class 2604 OID 20092)
-- Name: AO_ED669C_SEEN_ASSERTIONS ID; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_ED669C_SEEN_ASSERTIONS" ALTER COLUMN "ID" SET DEFAULT nextval('public."AO_ED669C_SEEN_ASSERTIONS_ID_seq"'::regclass);


--
-- TOC entry 5706 (class 0 OID 19979)
-- Dependencies: 492
-- Data for Name: AO_0A5972_NOTIFICATION_SETTING; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_0A5972_NOTIFICATION_SETTING" ("ID", "KEY", "TYPE", "USER_KEY", "VALUE") FROM stdin;
\.


--
-- TOC entry 5829 (class 0 OID 0)
-- Dependencies: 491
-- Name: AO_0A5972_NOTIFICATION_SETTING_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_0A5972_NOTIFICATION_SETTING_ID_seq"', 1, false);


--
-- TOC entry 5707 (class 0 OID 19988)
-- Dependencies: 493
-- Data for Name: AO_0A5972_PUSH_REGISTRATION; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_0A5972_PUSH_REGISTRATION" ("BUILD", "ENDPOINT", "ID", "OS", "TOKEN", "USER_KEY") FROM stdin;
\.


--
-- TOC entry 5708 (class 0 OID 19998)
-- Dependencies: 494
-- Data for Name: AO_0AC321_RECOMMENDATION_AO; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_0AC321_RECOMMENDATION_AO" ("CATEGORY", "CUSTOM_FIELD_ID", "ID", "NAME", "PERFORMANCE_IMPACT", "PROJECT_IDS", "RESOLVED", "TYPE") FROM stdin;
\.


--
-- TOC entry 5719 (class 0 OID 20077)
-- Dependencies: 505
-- Data for Name: AO_21D670_WHITELIST_RULES; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_21D670_WHITELIST_RULES" ("ALLOWINBOUND", "EXPRESSION", "ID", "TYPE") FROM stdin;
f	http://www.atlassian.com/*	1	WILDCARD_EXPRESSION
f	http://www.atlassian.com/*	2	WILDCARD_EXPRESSION
\.


--
-- TOC entry 5830 (class 0 OID 0)
-- Dependencies: 504
-- Name: AO_21D670_WHITELIST_RULES_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_21D670_WHITELIST_RULES_ID_seq"', 2, true);


--
-- TOC entry 5698 (class 0 OID 19912)
-- Dependencies: 484
-- Data for Name: AO_21F425_MESSAGE_AO; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_21F425_MESSAGE_AO" ("CONTENT", "ID") FROM stdin;
\.


--
-- TOC entry 5697 (class 0 OID 19903)
-- Dependencies: 483
-- Data for Name: AO_21F425_MESSAGE_MAPPING_AO; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_21F425_MESSAGE_MAPPING_AO" ("ID", "MESSAGE_ID", "USER_HASH") FROM stdin;
\.


--
-- TOC entry 5831 (class 0 OID 0)
-- Dependencies: 482
-- Name: AO_21F425_MESSAGE_MAPPING_AO_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_21F425_MESSAGE_MAPPING_AO_ID_seq"', 1, false);


--
-- TOC entry 5695 (class 0 OID 19892)
-- Dependencies: 481
-- Data for Name: AO_21F425_USER_PROPERTY_AO; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_21F425_USER_PROPERTY_AO" ("ID", "KEY", "USER", "VALUE") FROM stdin;
\.


--
-- TOC entry 5832 (class 0 OID 0)
-- Dependencies: 480
-- Name: AO_21F425_USER_PROPERTY_AO_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_21F425_USER_PROPERTY_AO_ID_seq"', 1, false);


--
-- TOC entry 5579 (class 0 OID 19057)
-- Dependencies: 365
-- Data for Name: AO_38321B_CUSTOM_CONTENT_LINK; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_38321B_CUSTOM_CONTENT_LINK" ("CONTENT_KEY", "ID", "LINK_LABEL", "LINK_URL", "SEQUENCE") FROM stdin;
\.


--
-- TOC entry 5833 (class 0 OID 0)
-- Dependencies: 364
-- Name: AO_38321B_CUSTOM_CONTENT_LINK_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_38321B_CUSTOM_CONTENT_LINK_ID_seq"', 1, false);


--
-- TOC entry 5633 (class 0 OID 19435)
-- Dependencies: 419
-- Data for Name: AO_3B1893_LOOP_DETECTION; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_3B1893_LOOP_DETECTION" ("COUNTER", "EXPIRES_AT", "ID", "SENDER_EMAIL") FROM stdin;
\.


--
-- TOC entry 5834 (class 0 OID 0)
-- Dependencies: 418
-- Name: AO_3B1893_LOOP_DETECTION_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_3B1893_LOOP_DETECTION_ID_seq"', 1, false);


--
-- TOC entry 5733 (class 0 OID 20188)
-- Dependencies: 519
-- Data for Name: AO_4789DD_HEALTH_CHECK_STATUS; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_4789DD_HEALTH_CHECK_STATUS" ("APPLICATION_NAME", "COMPLETE_KEY", "DESCRIPTION", "FAILED_DATE", "FAILURE_REASON", "ID", "IS_HEALTHY", "IS_RESOLVED", "RESOLVED_DATE", "SEVERITY", "STATUS_NAME") FROM stdin;
\.


--
-- TOC entry 5835 (class 0 OID 0)
-- Dependencies: 518
-- Name: AO_4789DD_HEALTH_CHECK_STATUS_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_4789DD_HEALTH_CHECK_STATUS_ID_seq"', 1, false);


--
-- TOC entry 5735 (class 0 OID 20199)
-- Dependencies: 521
-- Data for Name: AO_4789DD_PROPERTIES; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_4789DD_PROPERTIES" ("ID", "PROPERTY_NAME", "PROPERTY_VALUE") FROM stdin;
1	last-run	1583722759730
\.


--
-- TOC entry 5836 (class 0 OID 0)
-- Dependencies: 520
-- Name: AO_4789DD_PROPERTIES_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_4789DD_PROPERTIES_ID_seq"', 1, true);


--
-- TOC entry 5739 (class 0 OID 20224)
-- Dependencies: 525
-- Data for Name: AO_4789DD_READ_NOTIFICATIONS; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_4789DD_READ_NOTIFICATIONS" ("ID", "IS_SNOOZED", "NOTIFICATION_ID", "SNOOZE_COUNT", "SNOOZE_DATE", "USER_KEY") FROM stdin;
\.


--
-- TOC entry 5837 (class 0 OID 0)
-- Dependencies: 524
-- Name: AO_4789DD_READ_NOTIFICATIONS_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_4789DD_READ_NOTIFICATIONS_ID_seq"', 1, false);


--
-- TOC entry 5737 (class 0 OID 20210)
-- Dependencies: 523
-- Data for Name: AO_4789DD_TASK_MONITOR; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_4789DD_TASK_MONITOR" ("CLUSTERED_TASK_ID", "CREATED_TIMESTAMP", "ID", "NODE_ID", "PROGRESS_MESSAGE", "PROGRESS_PERCENTAGE", "SERIALIZED_ERRORS", "SERIALIZED_WARNINGS", "TASK_ID", "TASK_MONITOR_KIND", "TASK_STATUS") FROM stdin;
\.


--
-- TOC entry 5838 (class 0 OID 0)
-- Dependencies: 522
-- Name: AO_4789DD_TASK_MONITOR_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_4789DD_TASK_MONITOR_ID_seq"', 1, false);


--
-- TOC entry 5710 (class 0 OID 20008)
-- Dependencies: 496
-- Data for Name: AO_4AEACD_WEBHOOK_DAO; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_4AEACD_WEBHOOK_DAO" ("ENABLED", "ENCODED_EVENTS", "FILTER", "ID", "JQL", "LAST_UPDATED", "LAST_UPDATED_USER", "NAME", "REGISTRATION_METHOD", "URL", "PARAMETERS", "EXCLUDE_ISSUE_DETAILS") FROM stdin;
\.


--
-- TOC entry 5839 (class 0 OID 0)
-- Dependencies: 495
-- Name: AO_4AEACD_WEBHOOK_DAO_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_4AEACD_WEBHOOK_DAO_ID_seq"', 1, false);


--
-- TOC entry 5577 (class 0 OID 19045)
-- Dependencies: 363
-- Data for Name: AO_550953_SHORTCUT; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_550953_SHORTCUT" ("ID", "NAME", "PROJECT_ID", "SHORTCUT_URL", "URL", "ICON") FROM stdin;
\.


--
-- TOC entry 5840 (class 0 OID 0)
-- Dependencies: 362
-- Name: AO_550953_SHORTCUT_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_550953_SHORTCUT_ID_seq"', 1, false);


--
-- TOC entry 5731 (class 0 OID 20152)
-- Dependencies: 517
-- Data for Name: AO_563AEE_ACTIVITY_ENTITY; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_563AEE_ACTIVITY_ENTITY" ("ACTIVITY_ID", "ACTOR_ID", "CONTENT", "GENERATOR_DISPLAY_NAME", "GENERATOR_ID", "ICON_ID", "ID", "ISSUE_KEY", "OBJECT_ID", "POSTER", "PROJECT_KEY", "PUBLISHED", "TARGET_ID", "TITLE", "URL", "USERNAME", "VERB") FROM stdin;
\.


--
-- TOC entry 5841 (class 0 OID 0)
-- Dependencies: 516
-- Name: AO_563AEE_ACTIVITY_ENTITY_ACTIVITY_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_563AEE_ACTIVITY_ENTITY_ACTIVITY_ID_seq"', 1, false);


--
-- TOC entry 5723 (class 0 OID 20100)
-- Dependencies: 509
-- Data for Name: AO_563AEE_ACTOR_ENTITY; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_563AEE_ACTOR_ENTITY" ("FULL_NAME", "ID", "PROFILE_PAGE_URI", "PROFILE_PICTURE_URI", "USERNAME") FROM stdin;
\.


--
-- TOC entry 5842 (class 0 OID 0)
-- Dependencies: 508
-- Name: AO_563AEE_ACTOR_ENTITY_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_563AEE_ACTOR_ENTITY_ID_seq"', 1, false);


--
-- TOC entry 5725 (class 0 OID 20111)
-- Dependencies: 511
-- Data for Name: AO_563AEE_MEDIA_LINK_ENTITY; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_563AEE_MEDIA_LINK_ENTITY" ("DURATION", "HEIGHT", "ID", "URL", "WIDTH") FROM stdin;
\.


--
-- TOC entry 5843 (class 0 OID 0)
-- Dependencies: 510
-- Name: AO_563AEE_MEDIA_LINK_ENTITY_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_563AEE_MEDIA_LINK_ENTITY_ID_seq"', 1, false);


--
-- TOC entry 5727 (class 0 OID 20119)
-- Dependencies: 513
-- Data for Name: AO_563AEE_OBJECT_ENTITY; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_563AEE_OBJECT_ENTITY" ("CONTENT", "DISPLAY_NAME", "ID", "IMAGE_ID", "OBJECT_ID", "OBJECT_TYPE", "SUMMARY", "URL") FROM stdin;
\.


--
-- TOC entry 5844 (class 0 OID 0)
-- Dependencies: 512
-- Name: AO_563AEE_OBJECT_ENTITY_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_563AEE_OBJECT_ENTITY_ID_seq"', 1, false);


--
-- TOC entry 5729 (class 0 OID 20135)
-- Dependencies: 515
-- Data for Name: AO_563AEE_TARGET_ENTITY; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_563AEE_TARGET_ENTITY" ("CONTENT", "DISPLAY_NAME", "ID", "IMAGE_ID", "OBJECT_ID", "OBJECT_TYPE", "SUMMARY", "URL") FROM stdin;
\.


--
-- TOC entry 5845 (class 0 OID 0)
-- Dependencies: 514
-- Name: AO_563AEE_TARGET_ENTITY_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_563AEE_TARGET_ENTITY_ID_seq"', 1, false);


--
-- TOC entry 5691 (class 0 OID 19861)
-- Dependencies: 477
-- Data for Name: AO_575BF5_DEV_SUMMARY; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_575BF5_DEV_SUMMARY" ("CREATED", "ID", "ISSUE_ID", "JSON", "PROVIDER_SOURCE_ID", "UPDATED") FROM stdin;
\.


--
-- TOC entry 5846 (class 0 OID 0)
-- Dependencies: 476
-- Name: AO_575BF5_DEV_SUMMARY_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_575BF5_DEV_SUMMARY_ID_seq"', 1, false);


--
-- TOC entry 5689 (class 0 OID 19850)
-- Dependencies: 475
-- Data for Name: AO_575BF5_PROCESSED_COMMITS; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_575BF5_PROCESSED_COMMITS" ("COMMIT_HASH", "ID", "METADATA_HASH") FROM stdin;
\.


--
-- TOC entry 5847 (class 0 OID 0)
-- Dependencies: 474
-- Name: AO_575BF5_PROCESSED_COMMITS_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_575BF5_PROCESSED_COMMITS_ID_seq"', 1, false);


--
-- TOC entry 5687 (class 0 OID 19841)
-- Dependencies: 473
-- Data for Name: AO_575BF5_PROVIDER_ISSUE; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_575BF5_PROVIDER_ISSUE" ("ID", "ISSUE_ID", "PROVIDER_SOURCE_ID", "STALE_AFTER") FROM stdin;
\.


--
-- TOC entry 5848 (class 0 OID 0)
-- Dependencies: 472
-- Name: AO_575BF5_PROVIDER_ISSUE_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_575BF5_PROVIDER_ISSUE_ID_seq"', 1, false);


--
-- TOC entry 5693 (class 0 OID 19873)
-- Dependencies: 479
-- Data for Name: AO_575BF5_PROVIDER_SEQ_NO; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_575BF5_PROVIDER_SEQ_NO" ("ID", "PROVIDER_SOURCE_ID", "SEQUENCE_NO") FROM stdin;
\.


--
-- TOC entry 5849 (class 0 OID 0)
-- Dependencies: 478
-- Name: AO_575BF5_PROVIDER_SEQ_NO_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_575BF5_PROVIDER_SEQ_NO_ID_seq"', 1, false);


--
-- TOC entry 5715 (class 0 OID 20047)
-- Dependencies: 501
-- Data for Name: AO_587B34_GLANCE_CONFIG; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_587B34_GLANCE_CONFIG" ("ROOM_ID", "STATE", "SYNC_NEEDED", "APPLICATION_USER_KEY", "JQL", "NAME") FROM stdin;
\.


--
-- TOC entry 5717 (class 0 OID 20055)
-- Dependencies: 503
-- Data for Name: AO_587B34_PROJECT_CONFIG; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_587B34_PROJECT_CONFIG" ("CONFIGURATION_GROUP_ID", "ID", "NAME", "NAME_UNIQUE_CONSTRAINT", "PROJECT_ID", "ROOM_ID", "VALUE") FROM stdin;
\.


--
-- TOC entry 5850 (class 0 OID 0)
-- Dependencies: 502
-- Name: AO_587B34_PROJECT_CONFIG_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_587B34_PROJECT_CONFIG_ID_seq"', 1, false);


--
-- TOC entry 5712 (class 0 OID 20019)
-- Dependencies: 498
-- Data for Name: AO_5FB9D7_AOHIP_CHAT_LINK; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_5FB9D7_AOHIP_CHAT_LINK" ("ADDON_TOKEN_EXPIRY", "API_URL", "CONNECT_DESCRIPTOR", "GROUP_ID", "GROUP_NAME", "ID", "OAUTH_ID", "SECRET_KEY", "SYSTEM_PASSWORD", "SYSTEM_TOKEN_EXPIRY", "SYSTEM_USER", "SYSTEM_USER_TOKEN", "TOKEN") FROM stdin;
\.


--
-- TOC entry 5851 (class 0 OID 0)
-- Dependencies: 497
-- Name: AO_5FB9D7_AOHIP_CHAT_LINK_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_5FB9D7_AOHIP_CHAT_LINK_ID_seq"', 1, false);


--
-- TOC entry 5714 (class 0 OID 20031)
-- Dependencies: 500
-- Data for Name: AO_5FB9D7_AOHIP_CHAT_USER; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_5FB9D7_AOHIP_CHAT_USER" ("HIP_CHAT_LINK_ID", "HIP_CHAT_USER_ID", "HIP_CHAT_USER_NAME", "ID", "REFRESH_CODE", "USER_KEY", "USER_SCOPES", "USER_TOKEN", "USER_TOKEN_EXPIRY") FROM stdin;
\.


--
-- TOC entry 5852 (class 0 OID 0)
-- Dependencies: 499
-- Name: AO_5FB9D7_AOHIP_CHAT_USER_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_5FB9D7_AOHIP_CHAT_USER_ID_seq"', 1, false);


--
-- TOC entry 5599 (class 0 OID 19163)
-- Dependencies: 385
-- Data for Name: AO_60DB71_AUDITENTRY; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_60DB71_AUDITENTRY" ("CATEGORY", "DATA", "ENTITY_CLASS", "ENTITY_ID", "ID", "TIME", "USER") FROM stdin;
\.


--
-- TOC entry 5853 (class 0 OID 0)
-- Dependencies: 384
-- Name: AO_60DB71_AUDITENTRY_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_60DB71_AUDITENTRY_ID_seq"', 1, false);


--
-- TOC entry 5611 (class 0 OID 19255)
-- Dependencies: 397
-- Data for Name: AO_60DB71_BOARDADMINS; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_60DB71_BOARDADMINS" ("ID", "KEY", "RAPID_VIEW_ID", "TYPE") FROM stdin;
1	JIRAUSER10000	1	USER
\.


--
-- TOC entry 5854 (class 0 OID 0)
-- Dependencies: 396
-- Name: AO_60DB71_BOARDADMINS_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_60DB71_BOARDADMINS_ID_seq"', 1, true);


--
-- TOC entry 5603 (class 0 OID 19193)
-- Dependencies: 389
-- Data for Name: AO_60DB71_CARDCOLOR; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_60DB71_CARDCOLOR" ("COLOR", "ID", "POS", "RAPID_VIEW_ID", "STRATEGY", "VAL") FROM stdin;
\.


--
-- TOC entry 5855 (class 0 OID 0)
-- Dependencies: 388
-- Name: AO_60DB71_CARDCOLOR_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_60DB71_CARDCOLOR_ID_seq"', 1, false);


--
-- TOC entry 5601 (class 0 OID 19176)
-- Dependencies: 387
-- Data for Name: AO_60DB71_CARDLAYOUT; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_60DB71_CARDLAYOUT" ("FIELD_ID", "ID", "MODE_NAME", "POS", "RAPID_VIEW_ID") FROM stdin;
\.


--
-- TOC entry 5856 (class 0 OID 0)
-- Dependencies: 386
-- Name: AO_60DB71_CARDLAYOUT_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_60DB71_CARDLAYOUT_ID_seq"', 1, false);


--
-- TOC entry 5609 (class 0 OID 19241)
-- Dependencies: 395
-- Data for Name: AO_60DB71_COLUMN; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_60DB71_COLUMN" ("ID", "MAXIM", "MINIM", "NAME", "POS", "RAPID_VIEW_ID") FROM stdin;
4	\N	\N	gh.workflow.preset.todo	0	1
5	\N	\N	gh.workflow.preset.inprogress	1	1
6	\N	\N	gh.workflow.preset.done	2	1
\.


--
-- TOC entry 5625 (class 0 OID 19374)
-- Dependencies: 411
-- Data for Name: AO_60DB71_COLUMNSTATUS; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_60DB71_COLUMNSTATUS" ("COLUMN_ID", "ID", "POS", "STATUS_ID") FROM stdin;
4	4	0	10000
5	5	0	3
6	6	0	10001
\.


--
-- TOC entry 5857 (class 0 OID 0)
-- Dependencies: 410
-- Name: AO_60DB71_COLUMNSTATUS_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_60DB71_COLUMNSTATUS_ID_seq"', 6, true);


--
-- TOC entry 5858 (class 0 OID 0)
-- Dependencies: 394
-- Name: AO_60DB71_COLUMN_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_60DB71_COLUMN_ID_seq"', 6, true);


--
-- TOC entry 5605 (class 0 OID 19210)
-- Dependencies: 391
-- Data for Name: AO_60DB71_DETAILVIEWFIELD; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_60DB71_DETAILVIEWFIELD" ("FIELD_ID", "ID", "POS", "RAPID_VIEW_ID") FROM stdin;
status	1	0	1
priority	2	1	1
components	3	2	1
labels	4	3	1
versions	5	4	1
fixVersions	6	5	1
issuelinks	7	6	1
reporter	8	7	1
assignee	9	8	1
created	10	9	1
updated	11	10	1
customfield_10102	12	11	1
\.


--
-- TOC entry 5859 (class 0 OID 0)
-- Dependencies: 390
-- Name: AO_60DB71_DETAILVIEWFIELD_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_60DB71_DETAILVIEWFIELD_ID_seq"', 12, true);


--
-- TOC entry 5613 (class 0 OID 19271)
-- Dependencies: 399
-- Data for Name: AO_60DB71_ESTIMATESTATISTIC; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_60DB71_ESTIMATESTATISTIC" ("FIELD_ID", "ID", "RAPID_VIEW_ID", "TYPE_ID") FROM stdin;
customfield_10106	1	1	field
\.


--
-- TOC entry 5860 (class 0 OID 0)
-- Dependencies: 398
-- Name: AO_60DB71_ESTIMATESTATISTIC_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_60DB71_ESTIMATESTATISTIC_ID_seq"', 1, true);


--
-- TOC entry 5583 (class 0 OID 19081)
-- Dependencies: 369
-- Data for Name: AO_60DB71_ISSUERANKING; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_60DB71_ISSUERANKING" ("CUSTOM_FIELD_ID", "ID", "ISSUE_ID", "NEXT_ID") FROM stdin;
\.


--
-- TOC entry 5589 (class 0 OID 19110)
-- Dependencies: 375
-- Data for Name: AO_60DB71_ISSUERANKINGLOG; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_60DB71_ISSUERANKINGLOG" ("CUSTOM_FIELD_ID", "ID", "ISSUE_ID", "LOG_TYPE", "NEW_NEXT_ID", "NEW_PREVIOUS_ID", "OLD_NEXT_ID", "OLD_PREVIOUS_ID", "SANITY_CHECKED") FROM stdin;
\.


--
-- TOC entry 5861 (class 0 OID 0)
-- Dependencies: 374
-- Name: AO_60DB71_ISSUERANKINGLOG_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_60DB71_ISSUERANKINGLOG_ID_seq"', 1, false);


--
-- TOC entry 5862 (class 0 OID 0)
-- Dependencies: 368
-- Name: AO_60DB71_ISSUERANKING_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_60DB71_ISSUERANKING_ID_seq"', 1, false);


--
-- TOC entry 5591 (class 0 OID 19121)
-- Dependencies: 377
-- Data for Name: AO_60DB71_LEXORANK; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_60DB71_LEXORANK" ("BUCKET", "FIELD_ID", "ID", "ISSUE_ID", "LOCK_HASH", "LOCK_TIME", "RANK", "TYPE") FROM stdin;
0	10100	7	-9223372036854775808	\N	\N	0|000000:	0
0	10100	9	10000	\N	\N	0|hzzzzz:	1
0	10100	10	10001	\N	\N	0|i00007:	1
0	10100	11	10002	\N	\N	0|i0000f:	1
0	10100	12	10003	\N	\N	0|i0000n:	1
0	10100	13	10004	\N	\N	0|i0000v:	1
0	10100	14	10005	\N	\N	0|i00013:	1
0	10100	15	10006	\N	\N	0|i0001b:	1
0	10100	16	10007	\N	\N	0|i0001j:	1
0	10100	17	10008	\N	\N	0|i0001r:	1
0	10100	18	10009	\N	\N	0|i0001z:	1
0	10100	19	10010	\N	\N	0|i00027:	1
0	10100	20	10011	\N	\N	0|i0002f:	1
0	10100	21	10012	\N	\N	0|i0002n:	1
0	10100	22	10013	\N	\N	0|i0002v:	1
0	10100	23	10014	\N	\N	0|i00033:	1
0	10100	24	10015	\N	\N	0|i0003b:	1
0	10100	25	10016	\N	\N	0|i0003j:	1
0	10100	26	10017	\N	\N	0|i0003r:	1
0	10100	27	10018	\N	\N	0|i0003z:	1
0	10100	28	10019	\N	\N	0|i00047:	1
0	10100	29	10020	\N	\N	0|i0004f:	1
0	10100	31	10022	\N	\N	0|i0004v:	1
0	10100	8	9223372036854775807	\N	\N	0|zzzzzz:	2
0	10100	30	10021	\N	\N	0|i0004n:	1
\.


--
-- TOC entry 5585 (class 0 OID 19091)
-- Dependencies: 371
-- Data for Name: AO_60DB71_LEXORANKBALANCER; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_60DB71_LEXORANKBALANCER" ("DISABLE_RANK_OPERATIONS", "FIELD_ID", "ID", "REBALANCE_TIME") FROM stdin;
\.


--
-- TOC entry 5863 (class 0 OID 0)
-- Dependencies: 370
-- Name: AO_60DB71_LEXORANKBALANCER_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_60DB71_LEXORANKBALANCER_ID_seq"', 1, false);


--
-- TOC entry 5864 (class 0 OID 0)
-- Dependencies: 376
-- Name: AO_60DB71_LEXORANK_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_60DB71_LEXORANK_ID_seq"', 31, true);


--
-- TOC entry 5627 (class 0 OID 19393)
-- Dependencies: 413
-- Data for Name: AO_60DB71_NONWORKINGDAY; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_60DB71_NONWORKINGDAY" ("ID", "ISO8601_DATE", "WORKING_DAYS_ID") FROM stdin;
\.


--
-- TOC entry 5865 (class 0 OID 0)
-- Dependencies: 412
-- Name: AO_60DB71_NONWORKINGDAY_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_60DB71_NONWORKINGDAY_ID_seq"', 1, false);


--
-- TOC entry 5607 (class 0 OID 19224)
-- Dependencies: 393
-- Data for Name: AO_60DB71_QUICKFILTER; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_60DB71_QUICKFILTER" ("DESCRIPTION", "ID", "LONG_QUERY", "NAME", "POS", "QUERY", "RAPID_VIEW_ID") FROM stdin;
gh.rapid.quickfilters.only.my.issues.desc	1	assignee = currentUser()	gh.rapid.quickfilters.only.my.issues	0	\N	1
gh.rapid.quickfilters.recently.updated.desc	2	updatedDate >= -1d	gh.rapid.quickfilters.recently.updated	1	\N	1
\.


--
-- TOC entry 5866 (class 0 OID 0)
-- Dependencies: 392
-- Name: AO_60DB71_QUICKFILTER_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_60DB71_QUICKFILTER_ID_seq"', 2, true);


--
-- TOC entry 5595 (class 0 OID 19147)
-- Dependencies: 381
-- Data for Name: AO_60DB71_RANKABLEOBJECT; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_60DB71_RANKABLEOBJECT" ("ID", "TYPE") FROM stdin;
\.


--
-- TOC entry 5867 (class 0 OID 0)
-- Dependencies: 380
-- Name: AO_60DB71_RANKABLEOBJECT_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_60DB71_RANKABLEOBJECT_ID_seq"', 1, false);


--
-- TOC entry 5587 (class 0 OID 19099)
-- Dependencies: 373
-- Data for Name: AO_60DB71_RAPIDVIEW; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_60DB71_RAPIDVIEW" ("CARD_COLOR_STRATEGY", "ID", "KAN_PLAN_ENABLED", "NAME", "OLD_DONE_ISSUES_CUTOFF", "OWNER_USER_NAME", "SAVED_FILTER_ID", "SHOW_DAYS_IN_COLUMN", "SHOW_EPIC_AS_PANEL", "SPRINTS_ENABLED", "SWIMLANE_STRATEGY") FROM stdin;
none	1	\N	TESR board	NONE	JIRAUSER10000	10000	f	t	t	parentChild
\.


--
-- TOC entry 5868 (class 0 OID 0)
-- Dependencies: 372
-- Name: AO_60DB71_RAPIDVIEW_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_60DB71_RAPIDVIEW_ID_seq"', 1, true);


--
-- TOC entry 5593 (class 0 OID 19136)
-- Dependencies: 379
-- Data for Name: AO_60DB71_SPRINT; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_60DB71_SPRINT" ("CLOSED", "COMPLETE_DATE", "END_DATE", "GOAL", "ID", "NAME", "RAPID_VIEW_ID", "SEQUENCE", "STARTED", "START_DATE") FROM stdin;
f	\N	1584022136551	\N	1	Sample Sprint 2	1	\N	t	1582811336551
t	1582802337189	1582807137189	\N	2	Sample Sprint 1	1	\N	t	1581597537189
\.


--
-- TOC entry 5869 (class 0 OID 0)
-- Dependencies: 378
-- Name: AO_60DB71_SPRINT_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_60DB71_SPRINT_ID_seq"', 2, true);


--
-- TOC entry 5621 (class 0 OID 19332)
-- Dependencies: 407
-- Data for Name: AO_60DB71_STATSFIELD; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_60DB71_STATSFIELD" ("ID", "RAPID_VIEW_ID", "TYPE_ID") FROM stdin;
1	1	none
\.


--
-- TOC entry 5870 (class 0 OID 0)
-- Dependencies: 406
-- Name: AO_60DB71_STATSFIELD_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_60DB71_STATSFIELD_ID_seq"', 1, true);


--
-- TOC entry 5617 (class 0 OID 19303)
-- Dependencies: 403
-- Data for Name: AO_60DB71_SUBQUERY; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_60DB71_SUBQUERY" ("ID", "LONG_QUERY", "QUERY", "RAPID_VIEW_ID", "SECTION") FROM stdin;
\.


--
-- TOC entry 5871 (class 0 OID 0)
-- Dependencies: 402
-- Name: AO_60DB71_SUBQUERY_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_60DB71_SUBQUERY_ID_seq"', 1, false);


--
-- TOC entry 5623 (class 0 OID 19345)
-- Dependencies: 409
-- Data for Name: AO_60DB71_SWIMLANE; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_60DB71_SWIMLANE" ("DEFAULT_LANE", "DESCRIPTION", "ID", "LONG_QUERY", "NAME", "POS", "QUERY", "RAPID_VIEW_ID") FROM stdin;
t		1		gh.rapid.swimlane.default.name	0	\N	1
\.


--
-- TOC entry 5872 (class 0 OID 0)
-- Dependencies: 408
-- Name: AO_60DB71_SWIMLANE_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_60DB71_SWIMLANE_ID_seq"', 1, true);


--
-- TOC entry 5615 (class 0 OID 19287)
-- Dependencies: 401
-- Data for Name: AO_60DB71_TRACKINGSTATISTIC; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_60DB71_TRACKINGSTATISTIC" ("FIELD_ID", "ID", "RAPID_VIEW_ID", "TYPE_ID") FROM stdin;
\N	1	1	none
\.


--
-- TOC entry 5873 (class 0 OID 0)
-- Dependencies: 400
-- Name: AO_60DB71_TRACKINGSTATISTIC_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_60DB71_TRACKINGSTATISTIC_ID_seq"', 1, true);


--
-- TOC entry 5597 (class 0 OID 19155)
-- Dependencies: 383
-- Data for Name: AO_60DB71_VERSION; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_60DB71_VERSION" ("ID", "START_DATE", "VERSION_ID") FROM stdin;
\.


--
-- TOC entry 5874 (class 0 OID 0)
-- Dependencies: 382
-- Name: AO_60DB71_VERSION_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_60DB71_VERSION_ID_seq"', 1, false);


--
-- TOC entry 5619 (class 0 OID 19319)
-- Dependencies: 405
-- Data for Name: AO_60DB71_WORKINGDAYS; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_60DB71_WORKINGDAYS" ("FRIDAY", "ID", "MONDAY", "RAPID_VIEW_ID", "SATURDAY", "SUNDAY", "THURSDAY", "TIMEZONE", "TUESDAY", "WEDNESDAY") FROM stdin;
t	1	t	1	f	f	t	defaultTimeZoneId	t	t
\.


--
-- TOC entry 5875 (class 0 OID 0)
-- Dependencies: 404
-- Name: AO_60DB71_WORKINGDAYS_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_60DB71_WORKINGDAYS_ID_seq"', 1, true);


--
-- TOC entry 5700 (class 0 OID 19925)
-- Dependencies: 486
-- Data for Name: AO_733371_EVENT; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_733371_EVENT" ("ACTION", "ACTION_ID", "CREATED", "EVENT_BUNDLE_ID", "EVENT_TYPE", "ID", "USER_KEY") FROM stdin;
\.


--
-- TOC entry 5876 (class 0 OID 0)
-- Dependencies: 485
-- Name: AO_733371_EVENT_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_733371_EVENT_ID_seq"', 1, false);


--
-- TOC entry 5704 (class 0 OID 19955)
-- Dependencies: 490
-- Data for Name: AO_733371_EVENT_PARAMETER; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_733371_EVENT_PARAMETER" ("EVENT_ID", "ID", "NAME", "VALUE") FROM stdin;
\.


--
-- TOC entry 5877 (class 0 OID 0)
-- Dependencies: 489
-- Name: AO_733371_EVENT_PARAMETER_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_733371_EVENT_PARAMETER_ID_seq"', 1, false);


--
-- TOC entry 5702 (class 0 OID 19939)
-- Dependencies: 488
-- Data for Name: AO_733371_EVENT_RECIPIENT; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_733371_EVENT_RECIPIENT" ("CONSUMER_NAME", "CREATED", "EVENT_ID", "ID", "SEND_DATE", "STATUS", "UPDATED", "USER_KEY") FROM stdin;
\.


--
-- TOC entry 5878 (class 0 OID 0)
-- Dependencies: 487
-- Name: AO_733371_EVENT_RECIPIENT_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_733371_EVENT_RECIPIENT_ID_seq"', 1, false);


--
-- TOC entry 5631 (class 0 OID 19424)
-- Dependencies: 417
-- Data for Name: AO_97EDAB_USERINVITATION; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_97EDAB_USERINVITATION" ("APPLICATION_KEYS", "EMAIL_ADDRESS", "EXPIRY", "ID", "REDEEMED", "SENDER_USERNAME", "TOKEN") FROM stdin;
\.


--
-- TOC entry 5879 (class 0 OID 0)
-- Dependencies: 416
-- Name: AO_97EDAB_USERINVITATION_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_97EDAB_USERINVITATION_ID_seq"', 1, false);


--
-- TOC entry 5581 (class 0 OID 19070)
-- Dependencies: 367
-- Data for Name: AO_A0B856_WEB_HOOK_LISTENER_AO; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_A0B856_WEB_HOOK_LISTENER_AO" ("DESCRIPTION", "ENABLED", "EVENTS", "EXCLUDE_BODY", "FILTERS", "ID", "LAST_UPDATED", "LAST_UPDATED_USER", "NAME", "PARAMETERS", "REGISTRATION_METHOD", "URL") FROM stdin;
\.


--
-- TOC entry 5880 (class 0 OID 0)
-- Dependencies: 366
-- Name: AO_A0B856_WEB_HOOK_LISTENER_AO_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_A0B856_WEB_HOOK_LISTENER_AO_ID_seq"', 1, false);


--
-- TOC entry 5575 (class 0 OID 19033)
-- Dependencies: 361
-- Data for Name: AO_B9A0F0_APPLIED_TEMPLATE; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_B9A0F0_APPLIED_TEMPLATE" ("ID", "PROJECT_ID", "PROJECT_TEMPLATE_MODULE_KEY", "PROJECT_TEMPLATE_WEB_ITEM_KEY") FROM stdin;
1	10000	com.pyxis.greenhopper.jira:gh-scrum-template	com.pyxis.greenhopper.jira:gh-scrum-template
\.


--
-- TOC entry 5881 (class 0 OID 0)
-- Dependencies: 360
-- Name: AO_B9A0F0_APPLIED_TEMPLATE_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_B9A0F0_APPLIED_TEMPLATE_ID_seq"', 1, true);


--
-- TOC entry 5629 (class 0 OID 19411)
-- Dependencies: 415
-- Data for Name: AO_C16815_ALERT_AO; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_C16815_ALERT_AO" ("CREATED_DATE", "DETAILS_JSON", "ID", "ISSUE_COMPONENT_ID", "ISSUE_ID", "ISSUE_SEVERITY", "NODE_NAME", "TRIGGER_MODULE", "TRIGGER_PLUGIN_KEY", "TRIGGER_PLUGIN_KEY_VERSION", "TRIGGER_PLUGIN_VERSION") FROM stdin;
\.


--
-- TOC entry 5882 (class 0 OID 0)
-- Dependencies: 414
-- Name: AO_C16815_ALERT_AO_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_C16815_ALERT_AO_ID_seq"', 1, false);


--
-- TOC entry 5635 (class 0 OID 19448)
-- Dependencies: 421
-- Data for Name: AO_CFF990_AOTRANSITION_FAILURE; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_CFF990_AOTRANSITION_FAILURE" ("ERROR_MESSAGES", "FAILURE_TIME", "ID", "ISSUE_ID", "LOG_REFERRAL_HASH", "TRANSITION_ID", "TRIGGER_ID", "USER_KEY", "WORKFLOW_ID") FROM stdin;
\.


--
-- TOC entry 5883 (class 0 OID 0)
-- Dependencies: 420
-- Name: AO_CFF990_AOTRANSITION_FAILURE_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_CFF990_AOTRANSITION_FAILURE_ID_seq"', 1, false);


--
-- TOC entry 5659 (class 0 OID 19620)
-- Dependencies: 445
-- Data for Name: AO_E8B6CC_BRANCH; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_E8B6CC_BRANCH" ("ID", "NAME", "REPOSITORY_ID") FROM stdin;
\.


--
-- TOC entry 5655 (class 0 OID 19589)
-- Dependencies: 441
-- Data for Name: AO_E8B6CC_BRANCH_HEAD_MAPPING; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_E8B6CC_BRANCH_HEAD_MAPPING" ("BRANCH_NAME", "HEAD", "ID", "REPOSITORY_ID") FROM stdin;
\.


--
-- TOC entry 5884 (class 0 OID 0)
-- Dependencies: 440
-- Name: AO_E8B6CC_BRANCH_HEAD_MAPPING_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_E8B6CC_BRANCH_HEAD_MAPPING_ID_seq"', 1, false);


--
-- TOC entry 5885 (class 0 OID 0)
-- Dependencies: 444
-- Name: AO_E8B6CC_BRANCH_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_E8B6CC_BRANCH_ID_seq"', 1, false);


--
-- TOC entry 5645 (class 0 OID 19507)
-- Dependencies: 431
-- Data for Name: AO_E8B6CC_CHANGESET_MAPPING; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_E8B6CC_CHANGESET_MAPPING" ("AUTHOR", "AUTHOR_EMAIL", "BRANCH", "DATE", "FILES_DATA", "FILE_COUNT", "FILE_DETAILS_JSON", "ID", "ISSUE_KEY", "MESSAGE", "NODE", "PARENTS_DATA", "PROJECT_KEY", "RAW_AUTHOR", "RAW_NODE", "REPOSITORY_ID", "SMARTCOMMIT_AVAILABLE", "VERSION") FROM stdin;
\.


--
-- TOC entry 5886 (class 0 OID 0)
-- Dependencies: 430
-- Name: AO_E8B6CC_CHANGESET_MAPPING_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_E8B6CC_CHANGESET_MAPPING_ID_seq"', 1, false);


--
-- TOC entry 5673 (class 0 OID 19709)
-- Dependencies: 459
-- Data for Name: AO_E8B6CC_COMMIT; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_E8B6CC_COMMIT" ("AUTHOR", "AUTHOR_AVATAR_URL", "DATE", "DOMAIN_ID", "ID", "MERGE", "MESSAGE", "NODE", "RAW_AUTHOR") FROM stdin;
\.


--
-- TOC entry 5887 (class 0 OID 0)
-- Dependencies: 458
-- Name: AO_E8B6CC_COMMIT_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_E8B6CC_COMMIT_ID_seq"', 1, false);


--
-- TOC entry 5665 (class 0 OID 19655)
-- Dependencies: 451
-- Data for Name: AO_E8B6CC_GIT_HUB_EVENT; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_E8B6CC_GIT_HUB_EVENT" ("CREATED_AT", "GIT_HUB_ID", "ID", "REPOSITORY_ID", "SAVE_POINT") FROM stdin;
\.


--
-- TOC entry 5888 (class 0 OID 0)
-- Dependencies: 450
-- Name: AO_E8B6CC_GIT_HUB_EVENT_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_E8B6CC_GIT_HUB_EVENT_ID_seq"', 1, false);


--
-- TOC entry 5637 (class 0 OID 19462)
-- Dependencies: 423
-- Data for Name: AO_E8B6CC_ISSUE_MAPPING; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_E8B6CC_ISSUE_MAPPING" ("ID", "ISSUE_ID", "NODE", "PROJECT_KEY", "REPOSITORY_URI") FROM stdin;
\.


--
-- TOC entry 5889 (class 0 OID 0)
-- Dependencies: 422
-- Name: AO_E8B6CC_ISSUE_MAPPING_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_E8B6CC_ISSUE_MAPPING_ID_seq"', 1, false);


--
-- TOC entry 5641 (class 0 OID 19484)
-- Dependencies: 427
-- Data for Name: AO_E8B6CC_ISSUE_MAPPING_V2; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_E8B6CC_ISSUE_MAPPING_V2" ("AUTHOR", "BRANCH", "DATE", "FILES_DATA", "ID", "ISSUE_ID", "MESSAGE", "NODE", "PARENTS_DATA", "RAW_AUTHOR", "RAW_NODE", "REPOSITORY_ID", "VERSION") FROM stdin;
\.


--
-- TOC entry 5890 (class 0 OID 0)
-- Dependencies: 426
-- Name: AO_E8B6CC_ISSUE_MAPPING_V2_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_E8B6CC_ISSUE_MAPPING_V2_ID_seq"', 1, false);


--
-- TOC entry 5679 (class 0 OID 19755)
-- Dependencies: 465
-- Data for Name: AO_E8B6CC_ISSUE_TO_BRANCH; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_E8B6CC_ISSUE_TO_BRANCH" ("BRANCH_ID", "ID", "ISSUE_KEY") FROM stdin;
\.


--
-- TOC entry 5891 (class 0 OID 0)
-- Dependencies: 464
-- Name: AO_E8B6CC_ISSUE_TO_BRANCH_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_E8B6CC_ISSUE_TO_BRANCH_ID_seq"', 1, false);


--
-- TOC entry 5653 (class 0 OID 19568)
-- Dependencies: 439
-- Data for Name: AO_E8B6CC_ISSUE_TO_CHANGESET; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_E8B6CC_ISSUE_TO_CHANGESET" ("CHANGESET_ID", "ID", "ISSUE_KEY", "PROJECT_KEY") FROM stdin;
\.


--
-- TOC entry 5892 (class 0 OID 0)
-- Dependencies: 438
-- Name: AO_E8B6CC_ISSUE_TO_CHANGESET_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_E8B6CC_ISSUE_TO_CHANGESET_ID_seq"', 1, false);


--
-- TOC entry 5671 (class 0 OID 19697)
-- Dependencies: 457
-- Data for Name: AO_E8B6CC_MESSAGE; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_E8B6CC_MESSAGE" ("ADDRESS", "ID", "PAYLOAD", "PAYLOAD_TYPE", "PRIORITY") FROM stdin;
\.


--
-- TOC entry 5893 (class 0 OID 0)
-- Dependencies: 456
-- Name: AO_E8B6CC_MESSAGE_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_E8B6CC_MESSAGE_ID_seq"', 1, false);


--
-- TOC entry 5683 (class 0 OID 19790)
-- Dependencies: 469
-- Data for Name: AO_E8B6CC_MESSAGE_QUEUE_ITEM; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_E8B6CC_MESSAGE_QUEUE_ITEM" ("ID", "LAST_FAILED", "MESSAGE_ID", "QUEUE", "RETRIES_COUNT", "STATE", "STATE_INFO") FROM stdin;
\.


--
-- TOC entry 5894 (class 0 OID 0)
-- Dependencies: 468
-- Name: AO_E8B6CC_MESSAGE_QUEUE_ITEM_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_E8B6CC_MESSAGE_QUEUE_ITEM_ID_seq"', 1, false);


--
-- TOC entry 5681 (class 0 OID 19777)
-- Dependencies: 467
-- Data for Name: AO_E8B6CC_MESSAGE_TAG; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_E8B6CC_MESSAGE_TAG" ("ID", "MESSAGE_ID", "TAG") FROM stdin;
\.


--
-- TOC entry 5895 (class 0 OID 0)
-- Dependencies: 466
-- Name: AO_E8B6CC_MESSAGE_TAG_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_E8B6CC_MESSAGE_TAG_ID_seq"', 1, false);


--
-- TOC entry 5647 (class 0 OID 19520)
-- Dependencies: 433
-- Data for Name: AO_E8B6CC_ORGANIZATION_MAPPING; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_E8B6CC_ORGANIZATION_MAPPING" ("ACCESS_TOKEN", "ADMIN_PASSWORD", "ADMIN_USERNAME", "APPROVAL_STATE", "AUTOLINK_NEW_REPOS", "DEFAULT_GROUPS_SLUGS", "DVCS_TYPE", "HOST_URL", "ID", "LAST_POLLED", "NAME", "OAUTH_KEY", "OAUTH_SECRET", "PRINCIPAL_ID", "SMARTCOMMITS_FOR_NEW_REPOS") FROM stdin;
\.


--
-- TOC entry 5896 (class 0 OID 0)
-- Dependencies: 432
-- Name: AO_E8B6CC_ORGANIZATION_MAPPING_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_E8B6CC_ORGANIZATION_MAPPING_ID_seq"', 1, false);


--
-- TOC entry 5667 (class 0 OID 19669)
-- Dependencies: 453
-- Data for Name: AO_E8B6CC_ORG_TO_PROJECT; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_E8B6CC_ORG_TO_PROJECT" ("ID", "ORGANIZATION_ID", "PROJECT_KEY") FROM stdin;
\.


--
-- TOC entry 5897 (class 0 OID 0)
-- Dependencies: 452
-- Name: AO_E8B6CC_ORG_TO_PROJECT_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_E8B6CC_ORG_TO_PROJECT_ID_seq"', 1, false);


--
-- TOC entry 5639 (class 0 OID 19473)
-- Dependencies: 425
-- Data for Name: AO_E8B6CC_PROJECT_MAPPING; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_E8B6CC_PROJECT_MAPPING" ("ID", "PASSWORD", "PROJECT_KEY", "REPOSITORY_URI", "USERNAME") FROM stdin;
\.


--
-- TOC entry 5898 (class 0 OID 0)
-- Dependencies: 424
-- Name: AO_E8B6CC_PROJECT_MAPPING_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_E8B6CC_PROJECT_MAPPING_ID_seq"', 1, false);


--
-- TOC entry 5643 (class 0 OID 19496)
-- Dependencies: 429
-- Data for Name: AO_E8B6CC_PROJECT_MAPPING_V2; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_E8B6CC_PROJECT_MAPPING_V2" ("ACCESS_TOKEN", "ADMIN_PASSWORD", "ADMIN_USERNAME", "ID", "LAST_COMMIT_DATE", "PROJECT_KEY", "REPOSITORY_NAME", "REPOSITORY_TYPE", "REPOSITORY_URL") FROM stdin;
\.


--
-- TOC entry 5899 (class 0 OID 0)
-- Dependencies: 428
-- Name: AO_E8B6CC_PROJECT_MAPPING_V2_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_E8B6CC_PROJECT_MAPPING_V2_ID_seq"', 1, false);


--
-- TOC entry 5661 (class 0 OID 19633)
-- Dependencies: 447
-- Data for Name: AO_E8B6CC_PR_ISSUE_KEY; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_E8B6CC_PR_ISSUE_KEY" ("DOMAIN_ID", "ID", "ISSUE_KEY", "PULL_REQUEST_ID") FROM stdin;
\.


--
-- TOC entry 5900 (class 0 OID 0)
-- Dependencies: 446
-- Name: AO_E8B6CC_PR_ISSUE_KEY_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_E8B6CC_PR_ISSUE_KEY_ID_seq"', 1, false);


--
-- TOC entry 5677 (class 0 OID 19736)
-- Dependencies: 463
-- Data for Name: AO_E8B6CC_PR_PARTICIPANT; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_E8B6CC_PR_PARTICIPANT" ("APPROVED", "DOMAIN_ID", "ID", "PULL_REQUEST_ID", "ROLE", "USERNAME") FROM stdin;
\.


--
-- TOC entry 5901 (class 0 OID 0)
-- Dependencies: 462
-- Name: AO_E8B6CC_PR_PARTICIPANT_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_E8B6CC_PR_PARTICIPANT_ID_seq"', 1, false);


--
-- TOC entry 5685 (class 0 OID 19809)
-- Dependencies: 471
-- Data for Name: AO_E8B6CC_PR_TO_COMMIT; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_E8B6CC_PR_TO_COMMIT" ("COMMIT_ID", "DOMAIN_ID", "ID", "REQUEST_ID") FROM stdin;
\.


--
-- TOC entry 5902 (class 0 OID 0)
-- Dependencies: 470
-- Name: AO_E8B6CC_PR_TO_COMMIT_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_E8B6CC_PR_TO_COMMIT_ID_seq"', 1, false);


--
-- TOC entry 5657 (class 0 OID 19606)
-- Dependencies: 443
-- Data for Name: AO_E8B6CC_PULL_REQUEST; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_E8B6CC_PULL_REQUEST" ("AUTHOR", "COMMENT_COUNT", "CREATED_ON", "DESTINATION_BRANCH", "DOMAIN_ID", "EXECUTED_BY", "ID", "LAST_STATUS", "NAME", "REMOTE_ID", "SOURCE_BRANCH", "SOURCE_REPO", "TO_REPOSITORY_ID", "UPDATED_ON", "URL") FROM stdin;
\.


--
-- TOC entry 5903 (class 0 OID 0)
-- Dependencies: 442
-- Name: AO_E8B6CC_PULL_REQUEST_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_E8B6CC_PULL_REQUEST_ID_seq"', 1, false);


--
-- TOC entry 5649 (class 0 OID 19531)
-- Dependencies: 435
-- Data for Name: AO_E8B6CC_REPOSITORY_MAPPING; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_E8B6CC_REPOSITORY_MAPPING" ("ACTIVITY_LAST_SYNC", "DELETED", "FORK", "FORK_OF_NAME", "FORK_OF_OWNER", "FORK_OF_SLUG", "ID", "LAST_CHANGESET_NODE", "LAST_COMMIT_DATE", "LINKED", "LOGO", "NAME", "ORGANIZATION_ID", "SLUG", "SMARTCOMMITS_ENABLED", "UPDATE_LINK_AUTHORISED") FROM stdin;
\.


--
-- TOC entry 5904 (class 0 OID 0)
-- Dependencies: 434
-- Name: AO_E8B6CC_REPOSITORY_MAPPING_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_E8B6CC_REPOSITORY_MAPPING_ID_seq"', 1, false);


--
-- TOC entry 5651 (class 0 OID 19550)
-- Dependencies: 437
-- Data for Name: AO_E8B6CC_REPO_TO_CHANGESET; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_E8B6CC_REPO_TO_CHANGESET" ("CHANGESET_ID", "ID", "REPOSITORY_ID") FROM stdin;
\.


--
-- TOC entry 5905 (class 0 OID 0)
-- Dependencies: 436
-- Name: AO_E8B6CC_REPO_TO_CHANGESET_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_E8B6CC_REPO_TO_CHANGESET_ID_seq"', 1, false);


--
-- TOC entry 5675 (class 0 OID 19721)
-- Dependencies: 461
-- Data for Name: AO_E8B6CC_REPO_TO_PROJECT; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_E8B6CC_REPO_TO_PROJECT" ("ID", "PROJECT_KEY", "REPOSITORY_ID") FROM stdin;
\.


--
-- TOC entry 5906 (class 0 OID 0)
-- Dependencies: 460
-- Name: AO_E8B6CC_REPO_TO_PROJECT_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_E8B6CC_REPO_TO_PROJECT_ID_seq"', 1, false);


--
-- TOC entry 5669 (class 0 OID 19682)
-- Dependencies: 455
-- Data for Name: AO_E8B6CC_SYNC_AUDIT_LOG; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_E8B6CC_SYNC_AUDIT_LOG" ("END_DATE", "EXC_TRACE", "FIRST_REQUEST_DATE", "FLIGHT_TIME_MS", "ID", "NUM_REQUESTS", "REPO_ID", "START_DATE", "SYNC_STATUS", "SYNC_TYPE", "TOTAL_ERRORS") FROM stdin;
\.


--
-- TOC entry 5907 (class 0 OID 0)
-- Dependencies: 454
-- Name: AO_E8B6CC_SYNC_AUDIT_LOG_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_E8B6CC_SYNC_AUDIT_LOG_ID_seq"', 1, false);


--
-- TOC entry 5663 (class 0 OID 19643)
-- Dependencies: 449
-- Data for Name: AO_E8B6CC_SYNC_EVENT; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_E8B6CC_SYNC_EVENT" ("EVENT_CLASS", "EVENT_DATE", "EVENT_JSON", "ID", "REPO_ID", "SCHEDULED_SYNC") FROM stdin;
\.


--
-- TOC entry 5908 (class 0 OID 0)
-- Dependencies: 448
-- Name: AO_E8B6CC_SYNC_EVENT_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_E8B6CC_SYNC_EVENT_ID_seq"', 1, false);


--
-- TOC entry 5721 (class 0 OID 20088)
-- Dependencies: 507
-- Data for Name: AO_ED669C_SEEN_ASSERTIONS; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."AO_ED669C_SEEN_ASSERTIONS" ("ASSERTION_ID", "EXPIRY_TIMESTAMP", "ID") FROM stdin;
\.


--
-- TOC entry 5909 (class 0 OID 0)
-- Dependencies: 506
-- Name: AO_ED669C_SEEN_ASSERTIONS_ID_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public."AO_ED669C_SEEN_ASSERTIONS_ID_seq"', 1, false);


--
-- TOC entry 5401 (class 0 OID 16421)
-- Dependencies: 187
-- Data for Name: app_user; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.app_user (id, user_key, lower_user_name) FROM stdin;
10000	JIRAUSER10000	admin
\.


--
-- TOC entry 5402 (class 0 OID 16429)
-- Dependencies: 188
-- Data for Name: audit_changed_value; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.audit_changed_value (id, log_id, name, delta_from, delta_to) FROM stdin;
10000	10001	Permission		Create Shared Objects
10001	10001	Group		jira-software-users
10002	10002	Permission		Bulk Change
10003	10002	Group		jira-software-users
10004	10003	Permission		Manage Group Filter Subscriptions
10005	10003	Group		jira-software-users
10006	10004	Permission		Browse Users
10007	10004	Group		jira-software-users
10008	10005	Organisation	\N	Atlassian
10009	10005	Date Purchased	\N	05/Mar/20
10010	10005	License Type	\N	Jira Software (Data Center): Evaluation
10011	10005	Server ID	\N	BPEO-L57U-WLAQ-YMG6
10012	10005	Support Entitlement Number (SEN)	\N	SEN-L15099173
10013	10005	User Limit	\N	Unlimited
10014	10005	jira-software	\N	-1
10015	10006	Username	\N	admin
10016	10006	Full name	\N	Admin
10017	10006	Email	\N	ssmith@atlassian.com
10018	10006	Active / Inactive	\N	Active
10019	10008	Permission		Manage Group Filter Subscriptions
10020	10008	Group		jira-administrators
10021	10009	Permission		Create Shared Objects
10022	10009	Group		jira-administrators
10023	10010	Permission		Browse Users
10024	10010	Group		jira-administrators
10025	10011	Permission		Bulk Change
10026	10011	Group		jira-administrators
10100	10100	Name	\N	Rank
10101	10100	Description	\N	Global rank field for Jira Software use only.
10102	10100	Type	\N	Global Rank
10103	10101	Name	\N	Sprint
10104	10101	Description	\N	Jira Software sprint field
10105	10101	Type	\N	Jira Sprint Field
10106	10102	Name	\N	Epic Link
10107	10102	Description	\N	Choose an epic to assign this issue to.
10108	10102	Type	\N	Epic Link Relationship
10109	10103	Name	\N	Epic Status
10110	10103	Description	\N	Epic Status field for Jira Software use only.
10111	10103	Type	\N	Status of Epic
10112	10104	Name	\N	Epic Name
10113	10104	Description	\N	Provide a short name to identify this epic.
10114	10104	Type	\N	Name of Epic
10115	10105	Name	\N	Epic Color
10116	10105	Description	\N	Epic Colour field for Jira Software use only.
10117	10105	Type	\N	Color of Epic
10118	10108	Name	\N	Story Points
10119	10108	Description	\N	Measurement of complexity and/or size of a requirement.
10120	10108	Type	\N	Number Field
10121	10109	Permission		Manage Sprints
10122	10109	Type		Project Role
10123	10109	Value		Administrators
10124	10110	Permission	Manage Sprints	
10125	10110	Type	Project Role	
10126	10110	Value	Administrators	
10127	10111	Permission		Manage Sprints
10128	10111	Type		Project Role
10129	10111	Value		Administrators
10130	10112	Name	\N	Software Simplified Workflow for Project TESR
10131	10112	Description	\N	Generated by JIRA Software version 8.5.3. This workflow is managed internally by Jira Software. Do not manually modify this workflow.
10132	10112	Status	\N	To Do
10133	10112	Status	\N	In Progress
10134	10112	Status	\N	Done
10135	10112	Transition	\N	Create (To Do)
10136	10112	Transition	\N	To Do (To Do)
10137	10112	Transition	\N	In Progress (In Progress)
10138	10112	Transition	\N	Done (Done)
10139	10113	Name	\N	TESR: Software Simplified Workflow Scheme
10140	10113	Description	\N	Generated by JIRA Software version 8.5.3. This workflow scheme is managed internally by Jira Software. Do not manually modify this workflow scheme.
10141	10115	Name		Filter for TESR board
10142	10115	Description		\N
10143	10115	Owner		JIRAUSER10000
10144	10115	Shared With	[]	[Project: tesr (VIEW)]
10145	10115	JQL Query		{project = "TESR"} order by Rank ASC
10146	10117	Name	\N	Default software scheme
10147	10117	Description	\N	Default scheme for Software projects.
10148	10118	Permission		Browse Projects
10149	10118	Type		Application access
10150	10119	Permission		Create Issues
10151	10119	Type		Application access
10152	10120	Permission		Edit Issues
10153	10120	Type		Application access
10154	10121	Permission		Assign Issues
10155	10121	Type		Application access
10156	10122	Permission		Resolve Issues
10157	10122	Type		Application access
10158	10123	Permission		Add Comments
10159	10123	Type		Application access
10160	10124	Permission		Delete Issues
10161	10124	Type		Project Role
10162	10124	Value		Administrators
10163	10125	Permission		Assignable User
10164	10125	Type		Application access
10165	10126	Permission		Close Issues
10166	10126	Type		Application access
10167	10127	Permission		Create Attachments
10168	10127	Type		Application access
10169	10128	Permission		Work On Issues
10170	10128	Type		Application access
10171	10129	Permission		Link Issues
10172	10129	Type		Application access
10173	10130	Permission		Administer Projects
10174	10130	Type		Project Role
10175	10130	Value		Administrators
10176	10131	Permission		Move Issues
10177	10131	Type		Application access
10178	10132	Permission		Schedule Issues
10179	10132	Type		Application access
10180	10133	Permission		Modify Reporter
10181	10133	Type		Project Role
10182	10133	Value		Administrators
10183	10134	Permission		View Voters and Watchers
10184	10134	Type		Application access
10185	10135	Permission		Manage Watchers
10186	10135	Type		Project Role
10187	10135	Value		Administrators
10188	10136	Permission		Edit All Comments
10189	10136	Type		Project Role
10190	10136	Value		Administrators
10191	10137	Permission		Edit Own Comments
10192	10137	Type		Application access
10193	10138	Permission		Delete All Comments
10194	10138	Type		Project Role
10195	10138	Value		Administrators
10196	10139	Permission		Delete Own Comments
10197	10139	Type		Application access
10198	10140	Permission		Delete All Attachments
10199	10140	Type		Project Role
10200	10140	Value		Administrators
10201	10141	Permission		Delete Own Attachments
10202	10141	Type		Application access
10203	10142	Permission		Edit Own Worklogs
10204	10142	Type		Application access
10205	10143	Permission		Edit All Worklogs
10206	10143	Type		Project Role
10207	10143	Value		Administrators
10208	10144	Permission		Delete Own Worklogs
10209	10144	Type		Application access
10210	10145	Permission		Delete All Worklogs
10211	10145	Type		Project Role
10212	10145	Value		Administrators
10213	10146	Permission		View Read-Only Workflow
10214	10146	Type		Application access
10215	10147	Permission		Transition Issues
10216	10147	Type		Application access
10217	10148	Permission		View Development Tools
10218	10148	Type		Application access
10219	10149	Permission		Manage Sprints
10220	10149	Type		Application access
10221	10152	Name	\N	tesr
10222	10152	Key	\N	TESR
10223	10152	Description	\N	
10224	10152	Project Lead	\N	admin
10225	10152	Default Assignee	\N	Unassigned
10226	10153	Users	\N	JIRAUSER10000
10227	10154	Name	\N	Version 1.0
10228	10154	Release date	\N	2020-02-27
10229	10156	Name	\N	Version 2.0
10230	10156	Release date	\N	2020-03-12
10231	10157	Name	\N	Version 3.0
\.


--
-- TOC entry 5403 (class 0 OID 16437)
-- Dependencies: 189
-- Data for Name: audit_item; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.audit_item (id, log_id, object_type, object_id, object_name, object_parent_id, object_parent_name) FROM stdin;
10000	10007	USER	JIRAUSER10000	admin	1	Jira Internal Directory
10001	10012	USER	JIRAUSER10000	admin	1	Jira Internal Directory
10100	10114	SCHEME	10100	TESR: Software Simplified Workflow Scheme	\N	\N
10101	10115	USER	JIRAUSER10000	admin	1	Jira Internal Directory
10102	10115	PROJECT	10000	tesr	\N	\N
10103	10116	BOARD	1	TESR board		
10104	10150	SCHEME	0	Default Permission Scheme	\N	\N
10105	10151	SCHEME	10000	Default software scheme	\N	\N
10106	10152	USER	JIRAUSER10000	admin	1	Jira Internal Directory
10107	10153	PROJECT	10000	tesr	\N	\N
10108	10154	PROJECT	10000	tesr	\N	\N
10109	10155	PROJECT	10000	tesr	\N	\N
10110	10156	PROJECT	10000	tesr	\N	\N
10111	10157	PROJECT	10000	tesr	\N	\N
\.


--
-- TOC entry 5404 (class 0 OID 16445)
-- Dependencies: 190
-- Data for Name: audit_log; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.audit_log (id, remote_address, created, author_key, summary, category, object_type, object_id, object_name, object_parent_id, object_parent_name, author_type, event_source_name, description, long_description, search_field) FROM stdin;
10000	10.0.152.28	2020-03-05 22:54:16.867+00	\N	Group created	group management	GROUP	\N	jira-software-users	1	Jira Internal Directory	0		\N		10.0.152.28 group created management jira-software-users jira internal directory
10001	10.0.152.28	2020-03-05 22:54:16.894+00	\N	Global permission added	permissions	PERMISSIONS	\N	Global Permissions	\N	\N	0		\N		10.0.152.28 global permission added permissions create shared objects jira-software-users
10002	10.0.152.28	2020-03-05 22:54:16.9+00	\N	Global permission added	permissions	PERMISSIONS	\N	Global Permissions	\N	\N	0		\N		10.0.152.28 global permission added permissions bulk change jira-software-users
10003	10.0.152.28	2020-03-05 22:54:16.904+00	\N	Global permission added	permissions	PERMISSIONS	\N	Global Permissions	\N	\N	0		\N		10.0.152.28 global permission added permissions manage group filter subscriptions jira-software-users
10004	10.0.152.28	2020-03-05 22:54:16.907+00	\N	Global permission added	permissions	PERMISSIONS	\N	Global Permissions	\N	\N	0		\N		10.0.152.28 global permission added permissions browse users jira-software-users
10005	10.0.152.28	2020-03-05 22:54:16.914+00	\N	New license added	system	LICENSE	0	SEN-L15099173	0	License SEN	0		\N		10.0.152.28 new license added system sen-l15099173 sen atlassian 05/mar/20 jira software (data center): evaluation bpeo-l57u-wlaq-ymg6 unlimited -1
10006	10.0.152.28	2020-03-05 22:56:40.544+00	\N	User created	user management	USER	JIRAUSER10000	admin	1	Jira Internal Directory	0		\N		10.0.152.28 user created management admin jira internal directory ssmith@atlassian.com active
10007	10.0.152.28	2020-03-05 22:56:40.57+00	\N	User added to group	group management	GROUP	\N	jira-administrators	1	Jira Internal Directory	0		\N		10.0.152.28 user added to group management jira-administrators jira internal directory admin
10008	10.0.152.28	2020-03-05 22:56:40.589+00	\N	Global permission added	permissions	PERMISSIONS	\N	Global Permissions	\N	\N	0		\N		10.0.152.28 global permission added permissions manage group filter subscriptions jira-administrators
10009	10.0.152.28	2020-03-05 22:56:40.593+00	\N	Global permission added	permissions	PERMISSIONS	\N	Global Permissions	\N	\N	0		\N		10.0.152.28 global permission added permissions create shared objects jira-administrators
10010	10.0.152.28	2020-03-05 22:56:40.596+00	\N	Global permission added	permissions	PERMISSIONS	\N	Global Permissions	\N	\N	0		\N		10.0.152.28 global permission added permissions browse users jira-administrators
10011	10.0.152.28	2020-03-05 22:56:40.599+00	\N	Global permission added	permissions	PERMISSIONS	\N	Global Permissions	\N	\N	0		\N		10.0.152.28 global permission added permissions bulk change jira-administrators
10012	10.0.152.28	2020-03-05 22:56:40.603+00	\N	User added to group	group management	GROUP	\N	jira-software-users	1	Jira Internal Directory	0		\N		10.0.152.28 user added to group management jira-software-users jira internal directory admin
10100	10.0.152.28	2020-03-05 22:58:16.229+00	\N	Custom field created	fields	CUSTOM_FIELD	customfield_10100	Rank	\N	\N	0		\N		10.0.152.28 custom field created fields rank global for jira software use only.
10101	10.0.152.28	2020-03-05 22:58:16.289+00	\N	Custom field created	fields	CUSTOM_FIELD	customfield_10101	Sprint	\N	\N	0		\N		10.0.152.28 custom field created fields sprint jira software
10102	10.0.152.28	2020-03-05 22:58:16.332+00	\N	Custom field created	fields	CUSTOM_FIELD	customfield_10102	Epic Link	\N	\N	0		\N		10.0.152.28 custom field created fields epic link choose an to assign this issue to. relationship
10103	10.0.152.28	2020-03-05 22:58:16.379+00	\N	Custom field created	fields	CUSTOM_FIELD	customfield_10103	Epic Status	\N	\N	0		\N		10.0.152.28 custom field created fields epic status for jira software use only. of
10104	10.0.152.28	2020-03-05 22:58:16.41+00	\N	Custom field created	fields	CUSTOM_FIELD	customfield_10104	Epic Name	\N	\N	0		\N		10.0.152.28 custom field created fields epic name provide a short to identify this epic. of
10105	10.0.152.28	2020-03-05 22:58:16.463+00	\N	Custom field created	fields	CUSTOM_FIELD	customfield_10105	Epic Color	\N	\N	0		\N		10.0.152.28 custom field created fields epic color colour for jira software use only. of
10106	10.0.152.28	2020-03-05 22:58:18.618+00	\N	Issue type created	issue types	ISSUE_TYPE	10000	Epic	\N	\N	0		\N		10.0.152.28 issue type created types epic
10107	10.0.152.28	2020-03-05 22:58:18.653+00	\N	Issue type created	issue types	ISSUE_TYPE	10001	Story	\N	\N	0		\N		10.0.152.28 issue type created types story
10108	10.0.152.28	2020-03-05 22:58:18.675+00	\N	Custom field created	fields	CUSTOM_FIELD	customfield_10106	Story Points	\N	\N	0		\N		10.0.152.28 custom field created fields story points measurement of complexity and/or size a requirement. number
10109	10.0.152.28	2020-03-05 22:58:19.104+00	\N	Permission scheme updated	permissions	SCHEME	0	Default Permission Scheme	\N	\N	0		\N		10.0.152.28 permission scheme updated permissions default manage sprints project role administrators
10110	10.0.152.28	2020-03-05 22:58:19.117+00	\N	Permission scheme updated	permissions	SCHEME	0	Default Permission Scheme	\N	\N	0		\N		10.0.152.28 permission scheme updated permissions default manage sprints project role administrators
10111	10.0.152.28	2020-03-05 22:58:19.121+00	\N	Permission scheme updated	permissions	SCHEME	0	Default Permission Scheme	\N	\N	0		\N		10.0.152.28 permission scheme updated permissions default manage sprints project role administrators
10112	10.0.152.28	2020-03-05 22:58:54.886+00	JIRAUSER10000	Workflow created	workflows	WORKFLOW	Software Simplified Workflow for Project TESR	Software Simplified Workflow for Project TESR	\N	\N	1		\N		admin 10.0.152.28 workflow created workflows software simplified for project tesr generated by jira version 8.5.3. this is managed internally software. do not manually modify workflow. to in progress done create (to do) (in progress) (done)
10113	10.0.152.28	2020-03-05 22:58:54.907+00	JIRAUSER10000	Workflow scheme created	workflows	SCHEME	10100	TESR: Software Simplified Workflow Scheme	\N	\N	1		\N		admin 10.0.152.28 workflow scheme created workflows tesr: software simplified generated by jira version 8.5.3. this is managed internally software. do not manually modify scheme.
10114	10.0.152.28	2020-03-05 22:58:54.927+00	JIRAUSER10000	Workflow scheme added to project	workflows	PROJECT	10000	tesr	\N	\N	1		\N		admin 10.0.152.28 workflow scheme added to project workflows tesr tesr: software simplified
10115	10.0.152.28	2020-03-05 22:58:55.057+00	JIRAUSER10000	Filter created	filters	FILTER	10000	Filter for TESR board	\N	\N	1		\N		admin 10.0.152.28 filter created filters for tesr board jira internal directory jirauser10000 [] [project: (view)] {project = "tesr"} order by rank asc
10116	10.0.152.28	2020-03-05 22:58:55.208+00	JIRAUSER10000	Board created	boards 	BOARD	1	TESR board			1		\N		admin 10.0.152.28 board created boards tesr
10117	10.0.152.28	2020-03-05 22:58:55.277+00	JIRAUSER10000	Permission scheme created	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme created permissions default software for projects.
10118	10.0.152.28	2020-03-05 22:58:55.284+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software browse projects application access
10119	10.0.152.28	2020-03-05 22:58:55.292+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software create issues application access
10120	10.0.152.28	2020-03-05 22:58:55.296+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software edit issues application access
10121	10.0.152.28	2020-03-05 22:58:55.3+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software assign issues application access
10122	10.0.152.28	2020-03-05 22:58:55.303+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software resolve issues application access
10123	10.0.152.28	2020-03-05 22:58:55.306+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software add comments application access
10124	10.0.152.28	2020-03-05 22:58:55.31+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software delete issues project role administrators
10125	10.0.152.28	2020-03-05 22:58:55.313+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software assignable user application access
10126	10.0.152.28	2020-03-05 22:58:55.317+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software close issues application access
10127	10.0.152.28	2020-03-05 22:58:55.32+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software create attachments application access
10128	10.0.152.28	2020-03-05 22:58:55.324+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software work on issues application access
10129	10.0.152.28	2020-03-05 22:58:55.327+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software link issues application access
10130	10.0.152.28	2020-03-05 22:58:55.331+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software administer projects project role administrators
10131	10.0.152.28	2020-03-05 22:58:55.335+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software move issues application access
10132	10.0.152.28	2020-03-05 22:58:55.338+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software schedule issues application access
10133	10.0.152.28	2020-03-05 22:58:55.342+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software modify reporter project role administrators
10134	10.0.152.28	2020-03-05 22:58:55.346+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software view voters and watchers application access
10135	10.0.152.28	2020-03-05 22:58:55.35+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software manage watchers project role administrators
10136	10.0.152.28	2020-03-05 22:58:55.353+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software edit all comments project role administrators
10137	10.0.152.28	2020-03-05 22:58:55.356+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software edit own comments application access
10138	10.0.152.28	2020-03-05 22:58:55.36+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software delete all comments project role administrators
10139	10.0.152.28	2020-03-05 22:58:55.364+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software delete own comments application access
10140	10.0.152.28	2020-03-05 22:58:55.368+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software delete all attachments project role administrators
10141	10.0.152.28	2020-03-05 22:58:55.374+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software delete own attachments application access
10142	10.0.152.28	2020-03-05 22:58:55.378+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software edit own worklogs application access
10143	10.0.152.28	2020-03-05 22:58:55.382+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software edit all worklogs project role administrators
10144	10.0.152.28	2020-03-05 22:58:55.386+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software delete own worklogs application access
10145	10.0.152.28	2020-03-05 22:58:55.39+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software delete all worklogs project role administrators
10146	10.0.152.28	2020-03-05 22:58:55.394+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software view read-only workflow application access
10147	10.0.152.28	2020-03-05 22:58:55.397+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software transition issues application access
10148	10.0.152.28	2020-03-05 22:58:55.401+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software view development tools application access
10149	10.0.152.28	2020-03-05 22:58:55.405+00	JIRAUSER10000	Permission scheme updated	permissions	SCHEME	10000	Default software scheme	\N	\N	1		\N		admin 10.0.152.28 permission scheme updated permissions default software manage sprints application access
10150	10.0.152.28	2020-03-05 22:58:55.415+00	JIRAUSER10000	Permission scheme removed from project	permissions	PROJECT	10000	tesr	\N	\N	1		\N		admin 10.0.152.28 permission scheme removed from project permissions tesr default
10151	10.0.152.28	2020-03-05 22:58:55.429+00	JIRAUSER10000	Permission scheme added to project	permissions	PROJECT	10000	tesr	\N	\N	1		\N		admin 10.0.152.28 permission scheme added to project permissions tesr default software
10152	10.0.152.28	2020-03-05 22:58:55.454+00	JIRAUSER10000	Project created	projects	PROJECT	10000	tesr	\N	\N	1		\N		admin 10.0.152.28 project created projects tesr jira internal directory unassigned
10153	10.0.152.28	2020-03-05 22:58:55.614+00	JIRAUSER10000	Project roles changed	projects	PROJECT_ROLE	10100	Developers	\N	\N	1		\N		admin 10.0.152.28 project roles changed projects developers tesr jirauser10000
10154	10.0.152.28	2020-03-05 22:58:55.631+00	JIRAUSER10000	Project version created	projects	VERSION	10000	Version 1.0	\N	\N	1		\N		admin 10.0.152.28 project version created projects 1.0 tesr 2020-02-27
10155	10.0.152.28	2020-03-05 22:58:55.644+00	JIRAUSER10000	Project version released	projects	VERSION	10000	Version 1.0	\N	\N	1		\N		admin 10.0.152.28 project version released projects 1.0 tesr
10156	10.0.152.28	2020-03-05 22:58:55.65+00	JIRAUSER10000	Project version created	projects	VERSION	10001	Version 2.0	\N	\N	1		\N		admin 10.0.152.28 project version created projects 2.0 tesr 2020-03-12
10157	10.0.152.28	2020-03-05 22:58:55.654+00	JIRAUSER10000	Project version created	projects	VERSION	10002	Version 3.0	\N	\N	1		\N		admin 10.0.152.28 project version created projects 3.0 tesr
\.


--
-- TOC entry 5405 (class 0 OID 16453)
-- Dependencies: 191
-- Data for Name: avatar; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.avatar (id, filename, contenttype, avatartype, owner, systemavatar) FROM stdin;
10000	codegeist.png	image/png	project	\N	1
10001	bird.svg	image/svg+xml	project	\N	1
10002	jm_black.png	image/png	project	\N	1
10321	development_task.svg	image/svg+xml	issuetype	\N	1
10003	jm_brown.png	image/png	project	\N	1
10322	feedback.svg	image/svg+xml	issuetype	\N	1
10004	jm_orange.png	image/png	project	\N	1
10323	request_access.svg	image/svg+xml	issuetype	\N	1
10005	jm_red.png	image/png	project	\N	1
10324	default.svg	image/svg+xml	project	\N	1
10006	jm_white.png	image/png	project	\N	1
10325	code.svg	image/svg+xml	project	\N	1
10007	jm_yellow.png	image/png	project	\N	1
10326	coffee.svg	image/svg+xml	project	\N	1
10008	monster.png	image/png	project	\N	1
10009	nature.svg	image/svg+xml	project	\N	1
10327	design.svg	image/svg+xml	project	\N	1
10010	koala.svg	image/svg+xml	project	\N	1
10328	drill.svg	image/svg+xml	project	\N	1
10011	rocket.svg	image/svg+xml	project	\N	1
10329	food.svg	image/svg+xml	project	\N	1
10100	Avatar-1.png	image/png	user	\N	1
10330	notes.svg	image/svg+xml	project	\N	1
10101	Avatar-2.png	image/png	user	\N	1
10331	red-flag.svg	image/svg+xml	project	\N	1
10102	Avatar-3.png	image/png	user	\N	1
10332	science.svg	image/svg+xml	project	\N	1
10103	Avatar-4.png	image/png	user	\N	1
10333	support.svg	image/svg+xml	project	\N	1
10104	Avatar-5.png	image/png	user	\N	1
10334	bull.svg	image/svg+xml	user	\N	1
10105	Avatar-6.png	image/png	user	\N	1
10335	cat.svg	image/svg+xml	user	\N	1
10106	Avatar-7.png	image/png	user	\N	1
10336	dog.svg	image/svg+xml	user	\N	1
10107	Avatar-8.png	image/png	user	\N	1
10337	female_1.svg	image/svg+xml	user	\N	1
10108	Avatar-9.png	image/png	user	\N	1
10338	female_2.svg	image/svg+xml	user	\N	1
10109	Avatar-10.png	image/png	user	\N	1
10339	female_3.svg	image/svg+xml	user	\N	1
10110	Avatar-11.png	image/png	user	\N	1
10340	female_4.svg	image/svg+xml	user	\N	1
10111	Avatar-12.png	image/png	user	\N	1
10341	ghost.svg	image/svg+xml	user	\N	1
10112	Avatar-13.png	image/png	user	\N	1
10342	male_1.svg	image/svg+xml	user	\N	1
10113	Avatar-14.png	image/png	user	\N	1
10114	Avatar-15.png	image/png	user	\N	1
10343	male_2.svg	image/svg+xml	user	\N	1
10115	Avatar-16.png	image/png	user	\N	1
10344	male_3.svg	image/svg+xml	user	\N	1
10116	Avatar-17.png	image/png	user	\N	1
10345	male_4.svg	image/svg+xml	user	\N	1
10117	Avatar-18.png	image/png	user	\N	1
10346	male_5.svg	image/svg+xml	user	\N	1
10118	Avatar-19.png	image/png	user	\N	1
10347	male_6.svg	image/svg+xml	user	\N	1
10119	Avatar-20.png	image/png	user	\N	1
10348	male_8.svg	image/svg+xml	user	\N	1
10120	Avatar-21.png	image/png	user	\N	1
10349	owl.svg	image/svg+xml	user	\N	1
10121	Avatar-22.png	image/png	user	\N	1
10350	pirate.svg	image/svg+xml	user	\N	1
10122	Avatar-default.svg	image/svg+xml	user	\N	1
10123	Avatar-unknown.png	image/png	user	\N	1
10351	robot.svg	image/svg+xml	user	\N	1
10200	cloud.svg	image/svg+xml	project	\N	1
10352	vampire.svg	image/svg+xml	user	\N	1
10201	spanner.svg	image/svg+xml	project	\N	1
10202	cd.svg	image/svg+xml	project	\N	1
10203	money.svg	image/svg+xml	project	\N	1
10204	mouse-hand.svg	image/svg+xml	project	\N	1
10205	yeti.svg	image/svg+xml	project	\N	1
10206	power.svg	image/svg+xml	project	\N	1
10207	refresh.svg	image/svg+xml	project	\N	1
10208	phone.svg	image/svg+xml	project	\N	1
10209	settings.svg	image/svg+xml	project	\N	1
10210	storm.svg	image/svg+xml	project	\N	1
10211	plane.svg	image/svg+xml	project	\N	1
10300	genericissue.svg	image/svg+xml	issuetype	\N	1
10303	bug.svg	image/svg+xml	issuetype	\N	1
10304	defect.svg	image/svg+xml	issuetype	\N	1
10306	documentation.svg	image/svg+xml	issuetype	\N	1
10307	epic.svg	image/svg+xml	issuetype	\N	1
10308	exclamation.svg	image/svg+xml	issuetype	\N	1
10309	design_task.svg	image/svg+xml	issuetype	\N	1
10310	improvement.svg	image/svg+xml	issuetype	\N	1
10311	newfeature.svg	image/svg+xml	issuetype	\N	1
10312	remove_feature.svg	image/svg+xml	issuetype	\N	1
10313	requirement.svg	image/svg+xml	issuetype	\N	1
10314	sales.svg	image/svg+xml	issuetype	\N	1
10315	story.svg	image/svg+xml	issuetype	\N	1
10316	subtask.svg	image/svg+xml	issuetype	\N	1
10318	task.svg	image/svg+xml	issuetype	\N	1
10320	question.svg	image/svg+xml	issuetype	\N	1
10500	female_5.svg	image/svg+xml	user	\N	1
10501	female_6.svg	image/svg+xml	user	\N	1
10502	female_7.svg	image/svg+xml	user	\N	1
10503	female_8.svg	image/svg+xml	user	\N	1
10504	female_9.svg	image/svg+xml	user	\N	1
10505	male_7.svg	image/svg+xml	user	\N	1
10506	male_9.svg	image/svg+xml	user	\N	1
10507	pirate_female.svg	image/svg+xml	user	\N	1
10508	princess.svg	image/svg+xml	user	\N	1
10509	spectrum.svg	image/svg+xml	project	\N	1
\.


--
-- TOC entry 5406 (class 0 OID 16461)
-- Dependencies: 192
-- Data for Name: board; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.board (id, jql) FROM stdin;
\.


--
-- TOC entry 5407 (class 0 OID 16469)
-- Dependencies: 193
-- Data for Name: boardproject; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.boardproject (board_id, project_id) FROM stdin;
\.


--
-- TOC entry 5408 (class 0 OID 16474)
-- Dependencies: 194
-- Data for Name: changegroup; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.changegroup (id, issueid, author, created) FROM stdin;
10000	10010	JIRAUSER10000	2020-03-02 01:50:55.571+00
10001	10011	JIRAUSER10000	2020-03-02 23:15:55.572+00
10002	10009	JIRAUSER10000	2020-02-27 13:48:55.57+00
10003	10012	JIRAUSER10000	2020-02-27 13:48:55.572+00
10004	10013	JIRAUSER10000	2020-03-04 18:48:55.572+00
10005	10014	JIRAUSER10000	2020-02-27 13:48:55.573+00
10006	10014	JIRAUSER10000	2020-02-29 18:32:55.573+00
10007	10014	JIRAUSER10000	2020-03-03 04:24:55.573+00
10008	10015	JIRAUSER10000	2020-02-13 12:38:55.573+00
10009	10015	JIRAUSER10000	2020-02-27 13:48:55.573+00
10010	10015	JIRAUSER10000	2020-02-27 18:32:55.573+00
10011	10015	JIRAUSER10000	2020-02-28 20:20:55.573+00
10012	10016	JIRAUSER10000	2020-02-27 13:48:55.574+00
10013	10016	JIRAUSER10000	2020-02-28 00:32:55.574+00
10014	10016	JIRAUSER10000	2020-03-02 06:24:55.574+00
10015	10017	JIRAUSER10000	2020-02-13 12:38:55.574+00
10016	10017	JIRAUSER10000	2020-02-14 09:08:55.574+00
10017	10018	JIRAUSER10000	2020-02-13 12:38:55.574+00
10018	10018	JIRAUSER10000	2020-02-17 09:02:55.574+00
10019	10019	JIRAUSER10000	2020-02-13 12:38:55.574+00
10020	10019	JIRAUSER10000	2020-02-18 21:37:55.574+00
10021	10020	JIRAUSER10000	2020-02-13 12:38:55.575+00
10022	10020	JIRAUSER10000	2020-02-21 04:15:55.575+00
10023	10021	JIRAUSER10000	2020-02-13 12:38:55.575+00
10024	10021	JIRAUSER10000	2020-02-23 19:35:55.575+00
10025	10022	JIRAUSER10000	2020-02-13 12:38:55.575+00
10026	10022	JIRAUSER10000	2020-02-25 21:13:55.575+00
\.


--
-- TOC entry 5409 (class 0 OID 16479)
-- Dependencies: 195
-- Data for Name: changeitem; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.changeitem (id, groupid, fieldtype, field, oldvalue, oldstring, newvalue, newstring) FROM stdin;
10000	10000	jira	status	10000	To Do	3	In Progress
10001	10001	jira	status	10000	To Do	3	In Progress
10002	10002	custom	Sprint	\N		1	Sample Sprint 2
10003	10003	custom	Sprint	\N		1	Sample Sprint 2
10004	10004	custom	Sprint	\N		1	Sample Sprint 2
10005	10005	custom	Sprint	\N		1	Sample Sprint 2
10006	10006	jira	status	10000	To Do	3	In Progress
10007	10007	jira	status	3	In Progress	10001	Done
10008	10008	custom	Sprint	\N		2	Sample Sprint 1
10009	10009	custom	Sprint	2	Sample Sprint 1	2, 1	Sample Sprint 1, Sample Sprint 2
10010	10010	jira	status	10000	To Do	3	In Progress
10011	10011	jira	status	3	In Progress	10001	Done
10012	10012	custom	Sprint	\N		1	Sample Sprint 2
10013	10013	jira	status	10000	To Do	3	In Progress
10014	10014	jira	status	3	In Progress	10001	Done
10015	10015	custom	Sprint	\N		2	Sample Sprint 1
10016	10016	jira	status	10000	To Do	10001	Done
10017	10017	custom	Sprint	\N		2	Sample Sprint 1
10018	10018	jira	status	10000	To Do	10001	Done
10019	10019	custom	Sprint	\N		2	Sample Sprint 1
10020	10020	jira	status	10000	To Do	10001	Done
10021	10021	custom	Sprint	\N		2	Sample Sprint 1
10022	10022	jira	status	10000	To Do	10001	Done
10023	10023	custom	Sprint	\N		2	Sample Sprint 1
10024	10024	jira	status	10000	To Do	10001	Done
10025	10025	custom	Sprint	\N		2	Sample Sprint 1
10026	10026	jira	status	10000	To Do	10001	Done
\.


--
-- TOC entry 5415 (class 0 OID 16512)
-- Dependencies: 201
-- Data for Name: clusteredjob; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.clusteredjob (id, job_id, job_runner_key, sched_type, interval_millis, first_run, cron_expression, time_zone, next_run, version, parameters) FROM stdin;
10110	com.atlassian.jira.service.JiraService:10001	com.atlassian.jira.service.DefaultServiceManager	C	\N	\N	0 50 10/12 * * ?	\N	1583751000000	7	\\xaced000573720037636f6d2e676f6f676c652e636f6d6d6f6e2e636f6c6c6563742e496d6d757461626c6542694d61702453657269616c697a6564466f726d000000000000000002000078720035636f6d2e676f6f676c652e636f6d6d6f6e2e636f6c6c6563742e496d6d757461626c654d61702453657269616c697a6564466f726d00000000000000000200025b00046b6579737400135b4c6a6176612f6c616e672f4f626a6563743b5b000676616c75657371007e00027870757200135b4c6a6176612e6c616e672e4f626a6563743b90ce589f1073296c020000787000000001740033636f6d2e61746c61737369616e2e6a6972612e736572766963652e536572766963654d616e616765723a7365727669636549647571007e0004000000017372000e6a6176612e6c616e672e4c6f6e673b8be490cc8f23df0200014a000576616c7565787200106a6176612e6c616e672e4e756d62657286ac951d0b94e08b02000078700000000000002711
10108	com.atlassian.jira.service.JiraService:10201	com.atlassian.jira.service.DefaultServiceManager	C	\N	\N	0 50 10/12 * * ?	\N	1583751000000	7	\\xaced000573720037636f6d2e676f6f676c652e636f6d6d6f6e2e636f6c6c6563742e496d6d757461626c6542694d61702453657269616c697a6564466f726d000000000000000002000078720035636f6d2e676f6f676c652e636f6d6d6f6e2e636f6c6c6563742e496d6d757461626c654d61702453657269616c697a6564466f726d00000000000000000200025b00046b6579737400135b4c6a6176612f6c616e672f4f626a6563743b5b000676616c75657371007e00027870757200135b4c6a6176612e6c616e672e4f626a6563743b90ce589f1073296c020000787000000001740033636f6d2e61746c61737369616e2e6a6972612e736572766963652e536572766963654d616e616765723a7365727669636549647571007e0004000000017372000e6a6176612e6c616e672e4c6f6e673b8be490cc8f23df0200014a000576616c7565787200106a6176612e6c616e672e4e756d62657286ac951d0b94e08b020000787000000000000027d9
10107	com.atlassian.jira.service.JiraService:10200	com.atlassian.jira.service.DefaultServiceManager	C	\N	\N	0 50 10/12 * * ?	\N	1583751000000	7	\\xaced000573720037636f6d2e676f6f676c652e636f6d6d6f6e2e636f6c6c6563742e496d6d757461626c6542694d61702453657269616c697a6564466f726d000000000000000002000078720035636f6d2e676f6f676c652e636f6d6d6f6e2e636f6c6c6563742e496d6d757461626c654d61702453657269616c697a6564466f726d00000000000000000200025b00046b6579737400135b4c6a6176612f6c616e672f4f626a6563743b5b000676616c75657371007e00027870757200135b4c6a6176612e6c616e672e4f626a6563743b90ce589f1073296c020000787000000001740033636f6d2e61746c61737369616e2e6a6972612e736572766963652e536572766963654d616e616765723a7365727669636549647571007e0004000000017372000e6a6176612e6c616e672e4c6f6e673b8be490cc8f23df0200014a000576616c7565787200106a6176612e6c616e672e4e756d62657286ac951d0b94e08b020000787000000000000027d8
10211	applink-status-analytics-job	com.atlassian.applinks.analytics.ApplinkStatusJob	I	86400000	\N	\N	\N	1583794699711	5	\N
10212	TruncateAlertsJobRunner	com.atlassian.diagnostics.internal.DefaultMonitoringService$TruncateAlertsJobRunner	I	86400000	1583535499711	\N	\N	1583794699711	4	\N
10204	com.atlassian.whisper.plugin.fetch.FetchJob	com.atlassian.whisper.plugin.fetch.FetchJob	I	21600000	1583452698932	\N	\N	1583733498933	14	\N
10202	com.atlassian.jira.plugins.inform.batching.cron.BatchNotificationJobSchedulerImpl	com.atlassian.jira.plugins.inform.batching.cron.BatchNotificationJobSchedulerImpl	I	300000	1583449398928	\N	\N	1583723898932	916	\\xaced000573720035636f6d2e676f6f676c652e636f6d6d6f6e2e636f6c6c6563742e496d6d757461626c654d61702453657269616c697a6564466f726d00000000000000000200025b00046b6579737400135b4c6a6176612f6c616e672f4f626a6563743b5b000676616c75657371007e00017870757200135b4c6a6176612e6c616e672e4f626a6563743b90ce589f1073296c0200007870000000027400116576656e74436f6e73756d65724e616d6574000d636c75737465724c6f636b49647571007e0003000000027400116d61696c4576656e74436f6e73756d6572740059636f6d2e61746c61737369616e2e6a6972612e706c7567696e732e696e666f726d2e6261746368696e672e63726f6e2e4f6e6365506572436c75737465724a6f6252756e6e65722e6d61696c4576656e74436f6e73756d6572
10101	analytics-collection	com.atlassian.plugins.authentication.impl.analytics.StatisticsCollectionService	C	\N	\N	0 0 23 * * ?	\N	1583794800000	5	\N
10102	assertionId-cleanup	com.atlassian.plugins.authentication.impl.web.saml.AssertionValidationService	I	3600000	1583452492377	\N	\N	1583726092377	77	\N
10106	com.atlassian.jira.web.action.issue.DefaultTemporaryWebAttachmentsMonitor	com.atlassian.jira.web.action.issue.DefaultTemporaryWebAttachmentsMonitor	I	3600000	1583452504080	\N	\N	1583726104080	77	\N
10206	com.atlassian.jira.plugins.inform.events.cleanup.CleanupJobScheduler	com.atlassian.jira.plugins.inform.events.cleanup.CleanupJobScheduler	C	\N	\N	0 0 0 1/1 * ? *	\N	1583798400000	5	\N
10104	data-provider-cleanup	com.atlassian.jira.plugin.devstatus.provider.DataProviderCleanupJob	I	1800000	1583448897962	\N	\N	1583724304527	154	\N
10213	com.atlassian.jira.plugins.dvcs.scheduler.DvcsScheduler:job	com.atlassian.jira.plugins.dvcs.scheduler.DvcsScheduler	I	3600000	1583451323339	\N	\N	1583724923339	77	\N
10007	com.atlassian.jira.service.JiraService:10002	com.atlassian.jira.service.DefaultServiceManager	C	\N	\N	0 0 0 * * ?	\N	1583798400000	5	\\xaced000573720037636f6d2e676f6f676c652e636f6d6d6f6e2e636f6c6c6563742e496d6d757461626c6542694d61702453657269616c697a6564466f726d000000000000000002000078720035636f6d2e676f6f676c652e636f6d6d6f6e2e636f6c6c6563742e496d6d757461626c654d61702453657269616c697a6564466f726d00000000000000000200025b00046b6579737400135b4c6a6176612f6c616e672f4f626a6563743b5b000676616c75657371007e00027870757200135b4c6a6176612e6c616e672e4f626a6563743b90ce589f1073296c020000787000000001740033636f6d2e61746c61737369616e2e6a6972612e736572766963652e536572766963654d616e616765723a7365727669636549647571007e0004000000017372000e6a6176612e6c616e672e4c6f6e673b8be490cc8f23df0200014a000576616c7565787200106a6176612e6c616e672e4e756d62657286ac951d0b94e08b02000078700000000000002712
10103	data-provider-poll	com.atlassian.jira.plugin.devstatus.provider.DevSummaryUpdateJob	I	300000	1583448897797	\N	\N	1583724004527	918	\N
10200	CompatibilityPluginScheduler.JobId.LEXO_RANK_SCHEDULER_JOB	CompatibilityPluginScheduler.JobRunnerKey.com.atlassian.greenhopper.service.lexorank.balance.LexoRankBalancePluginJob	I	60000	1583449097849	\N	\N	1583723777857	4579	\N
10205	com.atlassian.jira.plugin.ext.bamboo.service.PlanStatusUpdateJob-job	com.atlassian.jira.plugin.ext.bamboo.service.PlanStatusUpdateJob	I	60000	1583449098937	\N	\N	1583723779029	4579	\N
10209	CompatibilityPluginScheduler.JobId.hipchatUpdateGlancesDataJob	CompatibilityPluginScheduler.JobRunnerKey.com.atlassian.jira.plugins.hipchat.service.connect.UpdateGlancesDataJobHandler	I	60000	1583449119646	\N	\N	1583723799646	4579	\N
10105	com.atlassian.diagnostics.internal.analytics.DailyAlertAnalyticsJob	DailyAlertAnalyticsJob	C	\N	\N	0 19 * * * ?	\N	1583723940000	77	\N
10217	PluginUpdateCheckJob-job	PluginUpdateCheckJob-runner	I	86400000	1583457035437	\N	\N	1583802635437	5	\N
10201	com.atlassian.jira.analytics.scheduler.AnalyticsScheduler	com.atlassian.jira.analytics.scheduler.AnalyticsScheduler	I	604800000	1583535498914	\N	\N	1584140298914	2	\N
10208	CompatibilityPluginScheduler.JobId.hipchatInstallGlancesJob	CompatibilityPluginScheduler.JobRunnerKey.com.atlassian.jira.plugins.hipchat.service.connect.InstallGlancesJobHandler	I	3600000	1583449120232	\N	\N	1583726320232	78	\N
10207	CompatibilityPluginScheduler.JobId.hipchatRefreshConnectionStatusJob	CompatibilityPluginScheduler.JobRunnerKey.com.atlassian.jira.plugins.hipchat.service.ping.RefreshConnectionStatusJobHandler	I	3600000	1583449126060	\N	\N	1583726326060	78	\N
10215	LocalPluginLicenseNotificationJob-job	LocalPluginLicenseNotificationJob-runner	I	86400000	1583449099745	\N	\N	1583794699747	5	\N
10219	Service Provider Session Remover	com.atlassian.oauth.serviceprovider.internal.ExpiredSessionRemover	I	28800000	1583477899768	\N	\N	1583737099769	10	\N
10218	InstanceTopologyJob-job	InstanceTopologyJob-runner	I	86400000	1583504539451	\N	\N	1583763739451	4	\N
10109	com.atlassian.jira.service.JiraService:10202	com.atlassian.jira.service.DefaultServiceManager	C	\N	\N	0 * * * * ?	\N	1583723760000	4581	\\xaced000573720037636f6d2e676f6f676c652e636f6d6d6f6e2e636f6c6c6563742e496d6d757461626c6542694d61702453657269616c697a6564466f726d000000000000000002000078720035636f6d2e676f6f676c652e636f6d6d6f6e2e636f6c6c6563742e496d6d757461626c654d61702453657269616c697a6564466f726d00000000000000000200025b00046b6579737400135b4c6a6176612f6c616e672f4f626a6563743b5b000676616c75657371007e00027870757200135b4c6a6176612e6c616e672e4f626a6563743b90ce589f1073296c020000787000000001740033636f6d2e61746c61737369616e2e6a6972612e736572766963652e536572766963654d616e616765723a7365727669636549647571007e0004000000017372000e6a6176612e6c616e672e4c6f6e673b8be490cc8f23df0200014a000576616c7565787200106a6176612e6c616e672e4e756d62657286ac951d0b94e08b020000787000000000000027da
10203	com.atlassian.jira.plugins.inform.batching.cron.BatchNotificationJobSchedulerImpl.mentions	com.atlassian.jira.plugins.inform.batching.cron.BatchNotificationJobSchedulerImpl	I	60000	1583449158931	\N	\N	1583723778942	4578	\\xaced000573720035636f6d2e676f6f676c652e636f6d6d6f6e2e636f6c6c6563742e496d6d757461626c654d61702453657269616c697a6564466f726d00000000000000000200025b00046b6579737400135b4c6a6176612f6c616e672f4f626a6563743b5b000676616c75657371007e00017870757200135b4c6a6176612e6c616e672e4f626a6563743b90ce589f1073296c0200007870000000027400116576656e74436f6e73756d65724e616d6574000d636c75737465724c6f636b49647571007e0003000000027400186d61696c4d656e74696f6e4576656e74436f6e73756d6572740060636f6d2e61746c61737369616e2e6a6972612e706c7567696e732e696e666f726d2e6261746368696e672e63726f6e2e4f6e6365506572436c75737465724a6f6252756e6e65722e6d61696c4d656e74696f6e4576656e74436f6e73756d6572
10216	PluginRequestCheckJob-job	PluginRequestCheckJob-runner	I	3600000	1583449099749	\N	\N	1583726299752	78	\N
\.


--
-- TOC entry 5410 (class 0 OID 16487)
-- Dependencies: 196
-- Data for Name: clusterlockstatus; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.clusterlockstatus (id, lock_name, locked_by_node, update_time) FROM stdin;
10007	ao-plugin.upgrade.com.atlassian.jira.jira-invite-user-plugin	\N	1583448901666
10202	com.atlassian.greenhopper.manager.issue.issuetypes.StoryIssueTypeProvider	\N	1583449098660
10128	com.atlassian.jira.index.request.DefaultReindexRequestManager.processRequests	\N	1583449095940
10008	ao-plugin.upgrade.com.atlassian.jira.mail-plugin	\N	1583448901709
10019	ao-plugin.upgrade.com.atlassian.labs.hipchat.hipchat-for-jira-plugin	\N	1583448903628
10009	ao-plugin.upgrade.com.atlassian.jira.plugins.jira-transition-triggers-plugin	\N	1583448901903
10211	sal.upgrade.com.atlassian.plugins.atlassian-whitelist-core-plugin	\N	1583449099171
10011	ao-plugin.upgrade.com.atlassian.jira.plugins.jira-dvcs-connector-plugin	\N	1583448902969
10020	ao-plugin.upgrade.com.atlassian.plugins.atlassian-whitelist-core-plugin	\N	1583448903668
10001	ao-plugin.upgrade.com.atlassian.jira.plugins.project-templates-plugin	\N	1583448900104
10012	ao-plugin.upgrade.com.atlassian.jira.plugins.jira-development-integration-plugin	\N	1583448903103
10002	ao-plugin.upgrade.com.atlassian.jira.jira-projects-plugin	\N	1583448900297
10026	com.atlassian.jira.config.DefaultReindexMessageManager.messageLock	\N	1583449098688
10130	com.atlassian.jira.util.index.IndexingCounterManagerImpl.counter	\N	1583449096067
10003	ao-plugin.upgrade.com.atlassian.plugins.atlassian-nav-links-plugin	\N	1583448900563
10021	ao-plugin.upgrade.com.atlassian.plugins.authentication.atlassian-authentication-plugin	\N	1583448903707
10004	ao-plugin.upgrade.com.atlassian.webhooks.atlassian-webhooks-plugin	\N	1583448900614
10013	ao-plugin.upgrade.com.atlassian.whisper.atlassian-whisper-plugin	\N	1583448903201
10005	ao-plugin.upgrade.com.pyxis.greenhopper.jira	\N	1583448901582
10025	com.atlassian.jira.upgrade.UpgradeService	\N	1583449096075
10022	ao-plugin.upgrade.com.atlassian.streams.thirdparty-plugin	\N	1583448903874
10006	ao-plugin.upgrade.com.atlassian.jira.diagnostics-plugin	\N	1583448901627
10203	com.atlassian.greenhopper.manager.issue.fields.StoryPointsCustomFieldProvider	\N	1583449098689
10014	ao-plugin.upgrade.com.atlassian.jira.plugins.inform.event-plugin	\N	1583448903301
10023	ao-plugin.upgrade.com.atlassian.troubleshooting.plugin-jira	\N	1583448904005
10010	com.atlassian.jira.upgrade.PluginUpgradeManager	\N	1583448690063
10015	ao-plugin.upgrade.com.atlassian.jira.mobile.jira-mobile-rest	\N	1583448903369
10225	com.atlassian.jira.config.DefaultResolutionManager.creationLock	\N	1583449134613
10016	ao-plugin.upgrade.com.atlassian.jira.plugins.jira-optimizer-plugin	\N	1583448903413
10000	com.atlassian.jira.cluster.zdu.DefaultClusterUpgradeStateManager.clusterUpgradeState	\N	1583449100076
10205	sal.upgrade.com.atlassian.sal.jira	\N	1583449098990
10017	ao-plugin.upgrade.com.atlassian.jira.plugins.webhooks.jira-webhooks-plugin	\N	1583448903461
10212	sal.upgrade.com.atlassian.jira.plugin.ext.bamboo	\N	1583449099177
10206	sal.upgrade.com.atlassian.jira.gadgets	\N	1583449099033
10018	ao-plugin.upgrade.com.atlassian.plugins.base-hipchat-integration-plugin-api	\N	1583448903540
10024	com.atlassian.jira.service.DefaultServiceManager.updateLock	\N	1583448904137
10100	com.atlassian.jira.crowd.embedded.ofbiz.EagerOfBizGroupCache.loadGroupCacheLock	\N	1583448904672
10126	com.atlassian.jira.crowd.embedded.ofbiz.EagerOfBizUserCache.loadUserCacheLock	\N	1583448999934
10218	sal.upgrade.com.atlassian.upm.atlassian-universal-plugin-manager-plugin	\N	1583449099611
10207	sal.upgrade.com.atlassian.jira.plugins.jira-bitbucket-connector-plugin	\N	1583449099041
10200	com.atlassian.greenhopper.manager.issue.issuetypes.EpicIssueTypeProvider	\N	1583449098625
10213	sal.upgrade.com.atlassian.jira.jira-mail-plugin	\N	1583449099188
10201	com.atlassian.jira.config.DefaultIssueTypeManager.creationLock	\N	1583449098657
10208	sal.upgrade.com.pyxis.greenhopper.jira	\N	1583449099139
10209	sal.upgrade.com.atlassian.crowd.embedded.admin	\N	1583449099147
10214	sal.upgrade.com.atlassian.plugins.atlassian-nav-links-plugin	\N	1583449099197
10210	sal.upgrade.com.atlassian.labs.hipchat.hipchat-for-jira-plugin	\N	1583449099153
10228	com.atlassian.jira.plugins.inform.batching.cron.OncePerClusterJobRunner.mailEventConsumer	\N	1583723598936
10219	sal.upgrade.com.atlassian.jira.lookandfeel	\N	1583449099645
10215	sal.upgrade.com.atlassian.jira.plugins.jira-workflow-designer	\N	1583449099205
10216	sal.upgrade.com.atlassian.jira.plugins.webhooks.jira-webhooks-plugin	\N	1583449099218
10217	sal.upgrade.com.atlassian.jirawallboard.atlassian-wallboard-plugin	\N	1583449099225
10220	sal.upgrade.com.atlassian.jira.project-templates-plugin	\N	1583449099652
10204	com.atlassian.sal.jira.upgrade	\N	1583449099653
10227	com.atlassian.jira.plugins.inform.batching.cron.OncePerClusterJobRunner.mailMentionEventConsumer	\N	1583723718947
10221	com.atlassian.upm.impl.PluginManagerPluginAsynchronousTaskStatusStoreImpl	\N	1583449099744
10226	com.atlassian.jira.config.DefaultStatusManager.creationLock	\N	1583449134857
10222	com.atlassian.upm.request.PluginSettingsPluginRequestStore	\N	1583722699754
10224	com.atlassian.jira.plugins.dvcs.service.MessageExecutor.processMessage	\N	1583449100052
10223	com.atlassian.upm.notification.PluginSettingsNotificationCache	\N	1583722699759
\.


--
-- TOC entry 5411 (class 0 OID 16492)
-- Dependencies: 197
-- Data for Name: clustermessage; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.clustermessage (id, source_node, destination_node, claimed_by_node, message, message_time) FROM stdin;
\.


--
-- TOC entry 5412 (class 0 OID 16497)
-- Dependencies: 198
-- Data for Name: clusternode; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.clusternode (node_id, node_state, "timestamp", ip, cache_listener_port, node_build_number, node_version) FROM stdin;
i-0832bd612c9f98710	ACTIVE	1583448874154	ip-10-0-9-198.ap-southeast-2.compute.internal	40001	805003	8.5.3
\.


--
-- TOC entry 5413 (class 0 OID 16502)
-- Dependencies: 199
-- Data for Name: clusternodeheartbeat; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.clusternodeheartbeat (node_id, heartbeat_time, database_time) FROM stdin;
i-0832bd612c9f98710	1583723704047	1583723704046
\.


--
-- TOC entry 5414 (class 0 OID 16507)
-- Dependencies: 200
-- Data for Name: clusterupgradestate; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.clusterupgradestate (id, database_time, cluster_build_number, cluster_version, state, order_number) FROM stdin;
\.


--
-- TOC entry 5416 (class 0 OID 16520)
-- Dependencies: 202
-- Data for Name: columnlayout; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.columnlayout (id, username, searchrequest) FROM stdin;
10000	\N	\N
\.


--
-- TOC entry 5417 (class 0 OID 16525)
-- Dependencies: 203
-- Data for Name: columnlayoutitem; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.columnlayoutitem (id, columnlayout, fieldidentifier, horizontalposition) FROM stdin;
10000	10000	issuekey	0
10001	10000	status	1
10002	10000	created	2
10003	10000	updated	3
10004	10000	customfield_10000	4
\.


--
-- TOC entry 5418 (class 0 OID 16530)
-- Dependencies: 204
-- Data for Name: component; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.component (id, project, cname, description, url, lead, assigneetype, archived) FROM stdin;
\.


--
-- TOC entry 5419 (class 0 OID 16538)
-- Dependencies: 205
-- Data for Name: configurationcontext; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.configurationcontext (id, projectcategory, project, customfield, fieldconfigscheme) FROM stdin;
10000	\N	\N	issuetype	10000
10100	\N	\N	customfield_10000	10100
10101	\N	\N	priority	10101
10200	\N	\N	customfield_10100	10200
10201	\N	\N	customfield_10101	10201
10202	\N	\N	customfield_10102	10202
10203	\N	\N	customfield_10104	10203
10204	\N	\N	customfield_10103	10204
10205	\N	\N	customfield_10105	10205
10206	\N	\N	customfield_10106	10206
10207	\N	10000	issuetype	10207
\.


--
-- TOC entry 5420 (class 0 OID 16543)
-- Dependencies: 206
-- Data for Name: customfield; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.customfield (id, cfkey, customfieldtypekey, customfieldsearcherkey, cfname, description, defaultvalue, fieldtype, project, issuetype) FROM stdin;
10000	\N	com.atlassian.jira.plugins.jira-development-integration-plugin:devsummary	com.atlassian.jira.plugins.jira-development-integration-plugin:devsummarysearcher	Development	Development Summary Field for Jira Software use only.	\N	\N	\N	\N
10100	\N	com.pyxis.greenhopper.jira:gh-lexo-rank	com.pyxis.greenhopper.jira:gh-lexo-rank-searcher	Rank	Global rank field for Jira Software use only.	\N	\N	\N	\N
10101	\N	com.pyxis.greenhopper.jira:gh-sprint	com.pyxis.greenhopper.jira:gh-sprint-searcher	Sprint	Jira Software sprint field	\N	\N	\N	\N
10102	\N	com.pyxis.greenhopper.jira:gh-epic-link	com.pyxis.greenhopper.jira:gh-epic-link-searcher	Epic Link	Choose an epic to assign this issue to.	\N	\N	\N	\N
10103	\N	com.pyxis.greenhopper.jira:gh-epic-status	com.pyxis.greenhopper.jira:gh-epic-status-searcher	Epic Status	Epic Status field for Jira Software use only.	\N	\N	\N	\N
10104	\N	com.pyxis.greenhopper.jira:gh-epic-label	com.pyxis.greenhopper.jira:gh-epic-label-searcher	Epic Name	Provide a short name to identify this epic.	\N	\N	\N	\N
10105	\N	com.pyxis.greenhopper.jira:gh-epic-color	com.pyxis.greenhopper.jira:gh-epic-color-searcher	Epic Color	Epic Colour field for Jira Software use only.	\N	\N	\N	\N
10106	\N	com.atlassian.jira.plugin.system.customfieldtypes:float	com.atlassian.jira.plugin.system.customfieldtypes:exactnumber	Story Points	Measurement of complexity and/or size of a requirement.	\N	\N	\N	\N
\.


--
-- TOC entry 5421 (class 0 OID 16551)
-- Dependencies: 207
-- Data for Name: customfieldoption; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.customfieldoption (id, customfield, customfieldconfig, parentoptionid, sequence, customvalue, optiontype, disabled) FROM stdin;
10000	10103	10204	\N	0	To Do	\N	N
10001	10103	10204	\N	1	In Progress	\N	N
10002	10103	10204	\N	2	Done	\N	N
\.


--
-- TOC entry 5422 (class 0 OID 16556)
-- Dependencies: 208
-- Data for Name: customfieldvalue; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.customfieldvalue (id, issue, customfield, updated, parentkey, stringvalue, numbervalue, textvalue, datevalue, valuetype) FROM stdin;
10000	10000	10106	1583449135739	\N	\N	2	\N	\N	\N
10001	10001	10106	1583449136187	\N	\N	2	\N	\N	\N
10002	10002	10106	1583449136236	\N	\N	5	\N	\N	\N
10003	10003	10106	1583449136282	\N	\N	5	\N	\N	\N
10004	10004	10106	1583449136330	\N	\N	1	\N	\N	\N
10005	10005	10106	1583449136370	\N	\N	1	\N	\N	\N
10006	10008	10106	1583449136523	\N	\N	3	\N	\N	\N
10007	10009	10106	1583449136748	\N	\N	5	\N	\N	\N
10008	10009	10101	1583449136752	\N	1	\N	\N	\N	\N
10009	10012	10101	1583449136975	\N	1	\N	\N	\N	\N
10010	10013	10106	1583449137045	\N	\N	3	\N	\N	\N
10011	10013	10101	1583449137047	\N	1	\N	\N	\N	\N
10012	10014	10106	1583449137115	\N	\N	4	\N	\N	\N
10013	10014	10101	1583449137117	\N	1	\N	\N	\N	\N
10014	10015	10106	1583449137206	\N	\N	2	\N	\N	\N
10015	10015	10101	1583449137208	\N	1	\N	\N	\N	\N
10016	10015	10101	1583449137208	\N	2	\N	\N	\N	\N
10017	10016	10101	1583449137311	\N	1	\N	\N	\N	\N
10018	10017	10106	1583449137394	\N	\N	3	\N	\N	\N
10019	10017	10101	1583449137397	\N	2	\N	\N	\N	\N
10020	10018	10106	1583449137466	\N	\N	5	\N	\N	\N
10021	10018	10101	1583449137470	\N	2	\N	\N	\N	\N
10022	10019	10106	1583449137543	\N	\N	3	\N	\N	\N
10023	10019	10101	1583449137546	\N	2	\N	\N	\N	\N
10024	10020	10106	1583449137647	\N	\N	1	\N	\N	\N
10025	10020	10101	1583449137650	\N	2	\N	\N	\N	\N
10026	10021	10106	1583449137759	\N	\N	2	\N	\N	\N
10027	10021	10101	1583449137762	\N	2	\N	\N	\N	\N
10028	10022	10106	1583449137849	\N	\N	2	\N	\N	\N
10029	10022	10101	1583449137852	\N	2	\N	\N	\N	\N
\.


--
-- TOC entry 5400 (class 0 OID 16413)
-- Dependencies: 186
-- Data for Name: cwd_application; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.cwd_application (id, application_name, lower_application_name, created_date, updated_date, active, description, application_type, credential) FROM stdin;
1	crowd-embedded	crowd-embedded	2013-02-28 11:57:51.302+00	2013-02-28 11:57:51.302+00	1		CROWD	X
\.


--
-- TOC entry 5539 (class 0 OID 17381)
-- Dependencies: 325
-- Data for Name: cwd_application_address; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.cwd_application_address (application_id, remote_address, encoded_address_binary, remote_address_mask) FROM stdin;
\.


--
-- TOC entry 5424 (class 0 OID 16572)
-- Dependencies: 210
-- Data for Name: cwd_directory; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.cwd_directory (id, directory_name, lower_directory_name, created_date, updated_date, active, description, impl_class, lower_impl_class, directory_type, directory_position) FROM stdin;
1	Jira Internal Directory	jira internal directory	2013-02-28 11:57:51.308+00	2013-02-28 11:57:51.308+00	1	Jira default internal directory	com.atlassian.crowd.directory.InternalDirectory	com.atlassian.crowd.directory.internaldirectory	INTERNAL	0
\.


--
-- TOC entry 5425 (class 0 OID 16580)
-- Dependencies: 211
-- Data for Name: cwd_directory_attribute; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.cwd_directory_attribute (directory_id, attribute_name, attribute_value) FROM stdin;
1	user_encryption_method	atlassian-security
\.


--
-- TOC entry 5426 (class 0 OID 16588)
-- Dependencies: 212
-- Data for Name: cwd_directory_operation; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.cwd_directory_operation (directory_id, operation_type) FROM stdin;
1	CREATE_GROUP
1	CREATE_ROLE
1	CREATE_USER
1	DELETE_GROUP
1	DELETE_ROLE
1	DELETE_USER
1	UPDATE_GROUP
1	UPDATE_GROUP_ATTRIBUTE
1	UPDATE_ROLE
1	UPDATE_ROLE_ATTRIBUTE
1	UPDATE_USER
1	UPDATE_USER_ATTRIBUTE
\.


--
-- TOC entry 5456 (class 0 OID 16804)
-- Dependencies: 242
-- Data for Name: cwd_group; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.cwd_group (id, group_name, lower_group_name, active, local, created_date, updated_date, description, lower_description, group_type, directory_id) FROM stdin;
10000	jira-administrators	jira-administrators	1	0	2013-02-28 11:57:51.326+00	2013-02-28 11:57:51.326+00		\N	GROUP	1
10010	jira-software-users	jira-software-users	1	0	2020-03-05 22:54:16.855+00	2020-03-05 22:54:16.855+00	\N	\N	GROUP	1
\.


--
-- TOC entry 5457 (class 0 OID 16812)
-- Dependencies: 243
-- Data for Name: cwd_group_attributes; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.cwd_group_attributes (id, group_id, directory_id, attribute_name, attribute_value, lower_attribute_value) FROM stdin;
\.


--
-- TOC entry 5486 (class 0 OID 17023)
-- Dependencies: 272
-- Data for Name: cwd_membership; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.cwd_membership (id, parent_id, child_id, membership_type, group_type, parent_name, lower_parent_name, child_name, lower_child_name, directory_id) FROM stdin;
10000	10000	10000	GROUP_USER	\N	jira-administrators	jira-administrators	admin	admin	1
10001	10010	10000	GROUP_USER	\N	jira-software-users	jira-software-users	admin	admin	1
\.


--
-- TOC entry 5559 (class 0 OID 17529)
-- Dependencies: 345
-- Data for Name: cwd_user; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.cwd_user (id, directory_id, user_name, lower_user_name, active, created_date, updated_date, first_name, lower_first_name, last_name, lower_last_name, display_name, lower_display_name, email_address, lower_email_address, credential, deleted_externally, external_id) FROM stdin;
10000	1	admin	admin	1	2020-03-05 22:56:40.031+00	2020-03-05 22:56:40.031+00			Admin	admin	Admin	admin	ssmith@atlassian.com	ssmith@atlassian.com	{PKCS5S2}pZvZhcRtAuLkjd79fMRD3aCnn5jsjkbUWHQJqmdnW4Emh+C+MCAIQrecpS+UNUOx	\N	e353e9ef-d604-4994-b0fe-d88f7939f441
\.


--
-- TOC entry 5561 (class 0 OID 17542)
-- Dependencies: 347
-- Data for Name: cwd_user_attributes; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.cwd_user_attributes (id, user_id, directory_id, attribute_name, attribute_value, lower_attribute_value) FROM stdin;
10000	10000	1	requiresPasswordChange	false	false
10001	10000	1	invalidPasswordAttempts	0	0
10002	10000	1	passwordLastChanged	1583449000293	1583449000293
10003	10000	1	password.reset.request.expiry	1583535400554	1583535400554
10004	10000	1	password.reset.request.token	181cac47dafe85d4128e8d8df1c558687bb8c3de	181cac47dafe85d4128e8d8df1c558687bb8c3de
10100	10000	1	login.currentFailedCount	0	0
10101	10000	1	login.lastLoginMillis	1583449100092	1583449100092
10102	10000	1	login.count	1	1
\.


--
-- TOC entry 5423 (class 0 OID 16564)
-- Dependencies: 209
-- Data for Name: deadletter; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.deadletter (id, message_id, last_seen, mail_server_id, folder_name) FROM stdin;
\.


--
-- TOC entry 5428 (class 0 OID 16601)
-- Dependencies: 214
-- Data for Name: draftworkflowscheme; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.draftworkflowscheme (id, name, description, workflow_scheme_id, last_modified_date, last_modified_user) FROM stdin;
\.


--
-- TOC entry 5429 (class 0 OID 16609)
-- Dependencies: 215
-- Data for Name: draftworkflowschemeentity; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.draftworkflowschemeentity (id, scheme, workflow, issuetype) FROM stdin;
\.


--
-- TOC entry 5430 (class 0 OID 16617)
-- Dependencies: 216
-- Data for Name: entity_property; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.entity_property (id, entity_name, entity_id, property_key, created, updated, json_value) FROM stdin;
10000	ProjectProperty	10000	searchRequests	2020-03-05 22:58:55.44+00	2020-03-05 22:58:55.44+00	{"ids":[]}
\.


--
-- TOC entry 5431 (class 0 OID 16625)
-- Dependencies: 217
-- Data for Name: entity_property_index_document; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.entity_property_index_document (id, plugin_key, module_key, entity_key, updated, document) FROM stdin;
10000	com.atlassian.jira.plugins.jira-development-integration-plugin	jira-issue-fusion-jql	IssueProperty	2020-03-05 22:51:31.682+00	<?xml version="1.0" encoding="UTF-8"?>\n<index-document-configuration entity-key="IssueProperty"><key property-key="development"><extract path="totalBuilds" type="NUMBER"/><extract path="failingbuilds" type="NUMBER"/><extract path="passedbuilds" type="NUMBER"/><extract path="buildstatus" type="STRING"/><extract path="openprs" type="NUMBER"/><extract path="mergedprs" type="NUMBER"/><extract path="declinedprs" type="NUMBER"/><extract path="prs" type="NUMBER"/><extract path="prstatus" type="STRING"/><extract path="reviews" type="NUMBER"/><extract path="openreviews" type="NUMBER"/><extract path="commits" type="NUMBER"/><extract path="totalDeployments" type="NUMBER"/><extract path="deployed" type="NUMBER"/><extract path="notDeployed" type="NUMBER"/><extract path="environment" type="STRING"/><extract path="totalBranches" type="NUMBER"/></key></index-document-configuration>
\.


--
-- TOC entry 5432 (class 0 OID 16633)
-- Dependencies: 218
-- Data for Name: entity_translation; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.entity_translation (id, entity_name, entity_id, locale, trans_name, trans_desc) FROM stdin;
\.


--
-- TOC entry 5434 (class 0 OID 16649)
-- Dependencies: 220
-- Data for Name: external_entities; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.external_entities (id, name, entitytype) FROM stdin;
\.


--
-- TOC entry 5435 (class 0 OID 16657)
-- Dependencies: 221
-- Data for Name: externalgadget; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.externalgadget (id, gadget_xml) FROM stdin;
\.


--
-- TOC entry 5436 (class 0 OID 16665)
-- Dependencies: 222
-- Data for Name: favouriteassociations; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.favouriteassociations (id, username, entitytype, entityid, sequence) FROM stdin;
\.


--
-- TOC entry 5437 (class 0 OID 16670)
-- Dependencies: 223
-- Data for Name: feature; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.feature (id, feature_name, feature_type, user_key) FROM stdin;
10001	com.atlassian.jira.projects.issuenavigator	site	
10000	com.atlassian.jira.projects.ProjectCentricNavigation.Switch	site	
10100	mail.batching.enabled	site	\N
10101	com.atlassian.jira.agile.darkfeature.kanplan.enabled	site	
10102	com.atlassian.jira.agile.darkfeature.kanplan.epics.and.versions.enabled	site	
10103	com.atlassian.jira.agile.darkfeature.sprint.goal.enabled	site	
10104	com.atlassian.jira.agile.darkfeature.edit.closed.sprint.enabled	site	
10105	com.atlassian.jira.agile.darkfeature.splitissue	site	
\.


--
-- TOC entry 5438 (class 0 OID 16678)
-- Dependencies: 224
-- Data for Name: fieldconfigscheme; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.fieldconfigscheme (id, configname, description, fieldid, customfield) FROM stdin;
10000	Default Issue Type Scheme	Default issue type scheme is the list of global issue types. All newly created issue types will automatically be added to this scheme.	issuetype	\N
10100	Default Configuration Scheme for Development	Default configuration scheme generated by Jira	customfield_10000	\N
10101	Default priority scheme	This is default priority scheme used by all projects without any other scheme assigned	priority	\N
10200	Default Configuration Scheme for Rank	Default configuration scheme generated by Jira	customfield_10100	\N
10201	Default Configuration Scheme for Sprint	Default configuration scheme generated by Jira	customfield_10101	\N
10202	Default Configuration Scheme for Epic Link	Default configuration scheme generated by Jira	customfield_10102	\N
10203	Default Configuration Scheme for Epic Name	Default configuration scheme generated by Jira	customfield_10104	\N
10204	Default Configuration Scheme for Epic Status	Default configuration scheme generated by Jira	customfield_10103	\N
10205	Default Configuration Scheme for Epic Color	Default configuration scheme generated by Jira	customfield_10105	\N
10206	Default Configuration Scheme for Story Points	Default configuration scheme generated by Jira	customfield_10106	\N
10207	TESR: Scrum Issue Type Scheme	\N	issuetype	\N
\.


--
-- TOC entry 5439 (class 0 OID 16686)
-- Dependencies: 225
-- Data for Name: fieldconfigschemeissuetype; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.fieldconfigschemeissuetype (id, issuetype, fieldconfigscheme, fieldconfiguration) FROM stdin;
10100	\N	10000	10000
10200	\N	10100	10100
10201	\N	10101	10101
10300	\N	10200	10200
10301	\N	10201	10201
10302	\N	10202	10202
10303	10000	10203	10203
10304	10000	10204	10204
10305	10000	10205	10205
10306	10000	10206	10206
10307	10001	10206	10206
10310	\N	10207	10207
\.


--
-- TOC entry 5440 (class 0 OID 16691)
-- Dependencies: 226
-- Data for Name: fieldconfiguration; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.fieldconfiguration (id, configname, description, fieldid, customfield) FROM stdin;
10000	Default Configuration for Issue Type	Default configuration generated by Jira	issuetype	\N
10100	Default Configuration for Development	Default configuration generated by Jira	customfield_10000	\N
10101	Default configuration for priority	Default configuration generated by Jira	priority	\N
10200	Default Configuration for Rank	Default configuration generated by JIRA	customfield_10100	\N
10201	Default Configuration for Sprint	Default configuration generated by JIRA	customfield_10101	\N
10202	Default Configuration for Epic Link	Default configuration generated by JIRA	customfield_10102	\N
10203	Default Configuration for Epic Name	Default configuration generated by JIRA	customfield_10104	\N
10204	Default Configuration for Epic Status	Default configuration generated by JIRA	customfield_10103	\N
10205	Default Configuration for Epic Color	Default configuration generated by JIRA	customfield_10105	\N
10206	Default Configuration for Story Points	Default configuration generated by JIRA	customfield_10106	\N
10207	Default Configuration for Issue Type	Default configuration generated by JIRA	issuetype	\N
\.


--
-- TOC entry 5441 (class 0 OID 16699)
-- Dependencies: 227
-- Data for Name: fieldlayout; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.fieldlayout (id, name, description, layout_type, layoutscheme) FROM stdin;
10000	Default Field Configuration	The default field configuration	default	\N
\.


--
-- TOC entry 5442 (class 0 OID 16707)
-- Dependencies: 228
-- Data for Name: fieldlayoutitem; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.fieldlayoutitem (id, fieldlayout, fieldidentifier, description, verticalposition, ishidden, isrequired, renderertype) FROM stdin;
10122	10000	archivedby	\N	\N	false	false	jira-text-renderer
10123	10000	archiveddate	\N	\N	false	false	jira-text-renderer
10124	10000	customfield_10000	Development Summary Field for Jira Software use only.	\N	false	false	jira-text-renderer
10125	10000	summary	\N	\N	false	true	jira-text-renderer
10126	10000	issuetype	\N	\N	false	true	jira-text-renderer
10127	10000	security	\N	\N	false	false	jira-text-renderer
10128	10000	priority	\N	\N	false	false	jira-text-renderer
10129	10000	duedate	\N	\N	false	false	jira-text-renderer
10130	10000	components	\N	\N	false	false	frother-control-renderer
10131	10000	versions	\N	\N	false	false	frother-control-renderer
10132	10000	fixVersions	\N	\N	false	false	frother-control-renderer
10133	10000	assignee	\N	\N	false	false	jira-text-renderer
10134	10000	reporter	\N	\N	false	true	jira-text-renderer
10135	10000	environment	For example operating system, software platform and/or hardware specifications (include as appropriate for the issue).	\N	false	false	atlassian-wiki-renderer
10136	10000	description	\N	\N	false	false	atlassian-wiki-renderer
10137	10000	timetracking	An estimate of how much work remains until this issue will be resolved.<br>The format of this is ' *w *d *h *m ' (representing weeks, days, hours and minutes - where * can be any number)<br>Examples: 4d, 5h 30m, 60m and 3w.<br>	\N	false	false	jira-text-renderer
10138	10000	resolution	\N	\N	false	false	jira-text-renderer
10139	10000	attachment	\N	\N	false	false	jira-text-renderer
10140	10000	comment	\N	\N	false	false	atlassian-wiki-renderer
10141	10000	labels	\N	\N	false	false	jira-text-renderer
10142	10000	worklog	Allows work to be logged whilst creating, editing or transitioning issues.	\N	false	false	atlassian-wiki-renderer
10143	10000	issuelinks	\N	\N	false	false	jira-text-renderer
10145	10000	customfield_10100	Global rank field for Jira Software use only.	\N	false	false	jira-text-renderer
10146	10000	customfield_10101	Jira Software sprint field	\N	false	false	jira-text-renderer
10147	10000	customfield_10102	Choose an epic to assign this issue to.	\N	false	false	jira-text-renderer
10148	10000	customfield_10103	Epic Status field for Jira Software use only.	\N	false	false	jira-text-renderer
10144	10000	customfield_10104	Provide a short name to identify this epic.	\N	false	true	jira-text-renderer
\.


--
-- TOC entry 5443 (class 0 OID 16715)
-- Dependencies: 229
-- Data for Name: fieldlayoutscheme; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.fieldlayoutscheme (id, name, description) FROM stdin;
\.


--
-- TOC entry 5444 (class 0 OID 16723)
-- Dependencies: 230
-- Data for Name: fieldlayoutschemeassociation; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.fieldlayoutschemeassociation (id, issuetype, project, fieldlayoutscheme) FROM stdin;
\.


--
-- TOC entry 5445 (class 0 OID 16728)
-- Dependencies: 231
-- Data for Name: fieldlayoutschemeentity; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.fieldlayoutschemeentity (id, scheme, issuetype, fieldlayout) FROM stdin;
\.


--
-- TOC entry 5446 (class 0 OID 16733)
-- Dependencies: 232
-- Data for Name: fieldscreen; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.fieldscreen (id, name, description) FROM stdin;
1	Default Screen	Allows to update all system fields.
2	Workflow Screen	This screen is used in the workflow and enables you to assign issues
3	Resolve Issue Screen	Allows to set resolution, change fix versions and assign an issue.
10000	TESR: Scrum Default Issue Screen	
10001	TESR: Scrum Bug Screen	
\.


--
-- TOC entry 5447 (class 0 OID 16741)
-- Dependencies: 233
-- Data for Name: fieldscreenlayoutitem; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.fieldscreenlayoutitem (id, fieldidentifier, sequence, fieldscreentab) FROM stdin;
10000	summary	0	10000
10001	issuetype	1	10000
10002	security	2	10000
10003	priority	3	10000
10004	duedate	4	10000
10005	components	5	10000
10006	versions	6	10000
10007	fixVersions	7	10000
10008	assignee	8	10000
10009	reporter	9	10000
10010	environment	10	10000
10011	description	11	10000
10012	timetracking	12	10000
10013	attachment	13	10000
10014	assignee	0	10001
10015	resolution	0	10002
10016	fixVersions	1	10002
10017	assignee	2	10002
10018	worklog	3	10002
10100	labels	14	10000
10212	summary	0	10101
10213	issuetype	1	10101
10214	reporter	2	10101
10215	components	3	10101
10216	description	4	10101
10217	fixVersions	5	10101
10218	priority	6	10101
10219	labels	7	10101
10220	security	8	10101
10221	environment	9	10101
10222	attachment	10	10101
10223	versions	11	10101
10224	issuelinks	12	10101
10225	assignee	13	10101
10226	customfield_10104	0	10100
10200	summary	1	10100
10201	issuetype	2	10100
10202	reporter	3	10100
10203	components	4	10100
10204	description	5	10100
10205	fixVersions	6	10100
10206	priority	7	10100
10207	labels	8	10100
10208	security	9	10100
10209	attachment	10	10100
10210	issuelinks	11	10100
10211	assignee	12	10100
10227	customfield_10102	13	10100
10228	customfield_10102	14	10101
10229	customfield_10101	14	10100
10230	customfield_10101	15	10101
\.


--
-- TOC entry 5448 (class 0 OID 16746)
-- Dependencies: 234
-- Data for Name: fieldscreenscheme; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.fieldscreenscheme (id, name, description) FROM stdin;
1	Default Screen Scheme	Default Screen Scheme
10000	TESR: Scrum Default Screen Scheme	
10001	TESR: Scrum Bug Screen Scheme	
\.


--
-- TOC entry 5449 (class 0 OID 16754)
-- Dependencies: 235
-- Data for Name: fieldscreenschemeitem; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.fieldscreenschemeitem (id, operation, fieldscreen, fieldscreenscheme) FROM stdin;
10000	\N	1	1
10100	\N	10000	10000
10101	\N	10001	10001
\.


--
-- TOC entry 5450 (class 0 OID 16759)
-- Dependencies: 236
-- Data for Name: fieldscreentab; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.fieldscreentab (id, name, description, sequence, fieldscreen) FROM stdin;
10000	Field Tab	\N	0	1
10001	Field Tab	\N	0	2
10002	Field Tab	\N	0	3
10100	Field Tab	\N	0	10000
10101	Field Tab	\N	0	10001
\.


--
-- TOC entry 5451 (class 0 OID 16767)
-- Dependencies: 237
-- Data for Name: fileattachment; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.fileattachment (id, issueid, mimetype, filename, created, filesize, author, zip, thumbnailable) FROM stdin;
\.


--
-- TOC entry 5452 (class 0 OID 16775)
-- Dependencies: 238
-- Data for Name: filtersubscription; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.filtersubscription (id, filter_i_d, username, groupname, last_run, email_on_empty) FROM stdin;
\.


--
-- TOC entry 5453 (class 0 OID 16780)
-- Dependencies: 239
-- Data for Name: gadgetuserpreference; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.gadgetuserpreference (id, portletconfiguration, userprefkey, userprefvalue) FROM stdin;
10000	10002	isConfigured	true
10001	10003	keys	__all_projects__
10002	10003	isConfigured	true
10003	10003	title	Your Company Jira
10004	10003	numofentries	5
\.


--
-- TOC entry 5454 (class 0 OID 16788)
-- Dependencies: 240
-- Data for Name: genericconfiguration; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.genericconfiguration (id, datatype, datakey, xmlvalue) FROM stdin;
10000	DefaultValue	10000	<string>1</string>
10100	DefaultValue	10207	<string>10001</string>
10101	DefaultValue	10204	<long>10000</long>
\.


--
-- TOC entry 5455 (class 0 OID 16796)
-- Dependencies: 241
-- Data for Name: globalpermissionentry; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.globalpermissionentry (id, permission, group_id) FROM stdin;
10000	ADMINISTER	jira-administrators
10006	SYSTEM_ADMIN	jira-administrators
10100	CREATE_SHARED_OBJECTS	jira-software-users
10101	BULK_CHANGE	jira-software-users
10102	MANAGE_GROUP_FILTER_SUBSCRIPTIONS	jira-software-users
10103	USER_PICKER	jira-software-users
10104	MANAGE_GROUP_FILTER_SUBSCRIPTIONS	jira-administrators
10105	CREATE_SHARED_OBJECTS	jira-administrators
10106	USER_PICKER	jira-administrators
10107	BULK_CHANGE	jira-administrators
\.


--
-- TOC entry 5499 (class 0 OID 17112)
-- Dependencies: 285
-- Data for Name: groupbase; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.groupbase (id, groupname) FROM stdin;
\.


--
-- TOC entry 5459 (class 0 OID 16828)
-- Dependencies: 245
-- Data for Name: issue_field_option; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.issue_field_option (id, option_id, field_key, option_value, properties) FROM stdin;
\.


--
-- TOC entry 5460 (class 0 OID 16836)
-- Dependencies: 246
-- Data for Name: issue_field_option_scope; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.issue_field_option_scope (id, option_id, entity_id, scope_type) FROM stdin;
\.


--
-- TOC entry 5461 (class 0 OID 16844)
-- Dependencies: 247
-- Data for Name: issuelink; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.issuelink (id, linktype, source, destination, sequence) FROM stdin;
10000	10100	10005	10006	0
10001	10100	10009	10010	0
10002	10100	10009	10011	1
\.


--
-- TOC entry 5462 (class 0 OID 16849)
-- Dependencies: 248
-- Data for Name: issuelinktype; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.issuelinktype (id, linkname, inward, outward, pstyle) FROM stdin;
10100	jira_subtask_link	jira_subtask_inward	jira_subtask_outward	jira_subtask
10000	Blocks	is blocked by	blocks	\N
10001	Cloners	is cloned by	clones	\N
10002	Duplicate	is duplicated by	duplicates	\N
10003	Relates	relates to	relates to	\N
10200	Epic-Story Link	has Epic	is Epic of	jira_gh_epic_story
\.


--
-- TOC entry 5463 (class 0 OID 16857)
-- Dependencies: 249
-- Data for Name: issuesecurityscheme; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.issuesecurityscheme (id, name, description, defaultlevel) FROM stdin;
\.


--
-- TOC entry 5551 (class 0 OID 17471)
-- Dependencies: 337
-- Data for Name: issuestatus; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.issuestatus (id, sequence, pname, description, iconurl, statuscategory) FROM stdin;
1	1	Open	The issue is open and ready for the assignee to start work on it.	/images/icons/statuses/open.png	2
3	3	In Progress	This issue is being actively worked on at the moment by the assignee.	/images/icons/statuses/inprogress.png	4
4	4	Reopened	This issue was once resolved, but the resolution was deemed incorrect. From here issues are either marked assigned or resolved.	/images/icons/statuses/reopened.png	2
5	5	Resolved	A resolution has been taken, and it is awaiting verification by reporter. From here issues are either reopened, or are closed.	/images/icons/statuses/resolved.png	3
6	6	Closed	The issue is considered finished, the resolution is correct. Issues which are closed can be reopened.	/images/icons/statuses/closed.png	3
10000	7	To Do		/	2
10001	8	Done		/	3
\.


--
-- TOC entry 5464 (class 0 OID 16865)
-- Dependencies: 250
-- Data for Name: issuetype; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.issuetype (id, sequence, pname, pstyle, description, iconurl, avatar) FROM stdin;
10000	\N	Epic	\N	Created by Jira Software - do not edit or delete. Issue type for a big user story that needs to be broken down.	/images/icons/issuetypes/epic.svg	\N
10001	\N	Story	\N	Created by Jira Software - do not edit or delete. Issue type for a user story.	/images/icons/issuetypes/story.svg	\N
10002	\N	Task		A task that needs to be done.	\N	10318
10003	\N	Sub-task	jira_subtask	The sub-task of the issue	\N	10316
10004	\N	Bug		A problem which impairs or prevents the functions of the product.	\N	10303
\.


--
-- TOC entry 5465 (class 0 OID 16873)
-- Dependencies: 251
-- Data for Name: issuetypescreenscheme; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.issuetypescreenscheme (id, name, description) FROM stdin;
1	Default Issue Type Screen Scheme	The default issue type screen scheme
10000	TESR: Scrum Issue Type Screen Scheme	
\.


--
-- TOC entry 5466 (class 0 OID 16881)
-- Dependencies: 252
-- Data for Name: issuetypescreenschemeentity; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.issuetypescreenschemeentity (id, issuetype, scheme, fieldscreenscheme) FROM stdin;
10000	\N	1	1
10100	\N	10000	10000
10101	10004	10000	10001
\.


--
-- TOC entry 5399 (class 0 OID 16405)
-- Dependencies: 185
-- Data for Name: jiraaction; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.jiraaction (id, issueid, author, actiontype, actionlevel, rolelevel, actionbody, created, updateauthor, updated, actionnum) FROM stdin;
10000	10006	JIRAUSER10000	comment	\N	\N	Hello	2020-03-05 22:58:56.432+00	JIRAUSER10000	2020-03-05 22:58:56.432+00	\N
10001	10009	JIRAUSER10000	comment	\N	\N	Joined Sample Sprint 2 7 days 9 hours 10 minutes ago	2020-02-27 13:48:55.571+00	JIRAUSER10000	2020-02-27 13:48:55.571+00	\N
10002	10010	JIRAUSER10000	comment	\N	\N	To Do to In Progress 3 days 21 hours 8 minutes ago	2020-03-02 01:50:55.571+00	JIRAUSER10000	2020-03-02 01:50:55.571+00	\N
10003	10011	JIRAUSER10000	comment	\N	\N	To Do to In Progress 2 days 23 hours 43 minutes ago	2020-03-02 23:15:55.572+00	JIRAUSER10000	2020-03-02 23:15:55.572+00	\N
10004	10012	JIRAUSER10000	comment	\N	\N	Joined Sample Sprint 2 7 days 9 hours 10 minutes ago	2020-02-27 13:48:55.572+00	JIRAUSER10000	2020-02-27 13:48:55.572+00	\N
10005	10013	JIRAUSER10000	comment	\N	\N	Joined Sample Sprint 2 1 days 4 hours 10 minutes ago	2020-03-04 18:48:55.573+00	JIRAUSER10000	2020-03-04 18:48:55.573+00	\N
10006	10014	JIRAUSER10000	comment	\N	\N	Joined Sample Sprint 2 7 days 9 hours 10 minutes ago	2020-03-03 04:24:55.573+00	JIRAUSER10000	2020-03-03 04:24:55.573+00	\N
10007	10014	JIRAUSER10000	comment	\N	\N	To Do to In Progress 5 days 4 hours 26 minutes ago\r\nIn Progress to Done 2 days 18 hours 34 minutes ago	2020-03-03 04:24:55.573+00	JIRAUSER10000	2020-03-03 04:24:55.573+00	\N
10008	10015	JIRAUSER10000	comment	\N	\N	Created 28 days 5 hours 14 minutes ago\r\nJoined Sample Sprint 1 21 days 10 hours 20 minutes ago	2020-02-28 20:20:55.573+00	JIRAUSER10000	2020-02-28 20:20:55.573+00	\N
10009	10015	JIRAUSER10000	comment	\N	\N	Joined Sample Sprint 2 7 days 9 hours 10 minutes ago	2020-02-28 20:20:55.573+00	JIRAUSER10000	2020-02-28 20:20:55.573+00	\N
10010	10015	JIRAUSER10000	comment	\N	\N	To Do to In Progress 7 days 4 hours 26 minutes ago\r\nIn Progress to Done 6 days 2 hours 38 minutes ago	2020-02-28 20:20:55.573+00	JIRAUSER10000	2020-02-28 20:20:55.573+00	\N
10011	10016	JIRAUSER10000	comment	\N	\N	Joined Sample Sprint 2 7 days 9 hours 10 minutes ago	2020-03-02 06:24:55.574+00	JIRAUSER10000	2020-03-02 06:24:55.574+00	\N
10012	10016	JIRAUSER10000	comment	\N	\N	To Do to In Progress 6 days 22 hours 26 minutes ago\r\nIn Progress to Done 3 days 16 hours 34 minutes ago	2020-03-02 06:24:55.574+00	JIRAUSER10000	2020-03-02 06:24:55.574+00	\N
10013	10017	JIRAUSER10000	comment	\N	\N	Joined Sample Sprint 1 21 days 10 hours 20 minutes ago\r\nTo Do to Done 20 days 13 hours 50 minutes ago	2020-02-14 09:08:55.574+00	JIRAUSER10000	2020-02-14 09:08:55.574+00	\N
10014	10018	JIRAUSER10000	comment	\N	\N	Joined Sample Sprint 1 21 days 10 hours 20 minutes ago\r\nTo Do to Done 17 days 13 hours 56 minutes ago	2020-02-17 09:02:55.574+00	JIRAUSER10000	2020-02-17 09:02:55.574+00	\N
10015	10019	JIRAUSER10000	comment	\N	\N	Joined Sample Sprint 1 21 days 10 hours 20 minutes ago\r\nTo Do to Done 16 days 1 hours 21 minutes ago	2020-02-18 21:37:55.575+00	JIRAUSER10000	2020-02-18 21:37:55.575+00	\N
10016	10020	JIRAUSER10000	comment	\N	\N	Joined Sample Sprint 1 21 days 10 hours 20 minutes ago\r\nTo Do to Done 13 days 18 hours 43 minutes ago	2020-02-21 04:15:55.575+00	JIRAUSER10000	2020-02-21 04:15:55.575+00	\N
10017	10021	JIRAUSER10000	comment	\N	\N	Joined Sample Sprint 1 21 days 10 hours 20 minutes ago\r\nTo Do to Done 11 days 3 hours 23 minutes ago	2020-02-23 19:35:55.575+00	JIRAUSER10000	2020-02-23 19:35:55.575+00	\N
10018	10022	JIRAUSER10000	comment	\N	\N	Joined Sample Sprint 1 21 days 10 hours 20 minutes ago\r\nTo Do to Done 9 days 1 hours 45 minutes ago	2020-02-25 21:13:55.575+00	JIRAUSER10000	2020-02-25 21:13:55.575+00	\N
\.


--
-- TOC entry 5427 (class 0 OID 16593)
-- Dependencies: 213
-- Data for Name: jiradraftworkflows; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.jiradraftworkflows (id, parentname, descriptor) FROM stdin;
\.


--
-- TOC entry 5433 (class 0 OID 16641)
-- Dependencies: 219
-- Data for Name: jiraeventtype; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.jiraeventtype (id, template_id, name, description, event_type) FROM stdin;
1	\N	Issue Created	This is the 'issue created' event.	jira.system.event.type
2	\N	Issue Updated	This is the 'issue updated' event.	jira.system.event.type
3	\N	Issue Assigned	This is the 'issue assigned' event.	jira.system.event.type
4	\N	Issue Resolved	This is the 'issue resolved' event.	jira.system.event.type
5	\N	Issue Closed	This is the 'issue closed' event.	jira.system.event.type
6	\N	Issue Commented	This is the 'issue commented' event.	jira.system.event.type
7	\N	Issue Reopened	This is the 'issue reopened' event.	jira.system.event.type
8	\N	Issue Deleted	This is the 'issue deleted' event.	jira.system.event.type
9	\N	Issue Moved	This is the 'issue moved' event.	jira.system.event.type
10	\N	Work Logged On Issue	This is the 'work logged on issue' event.	jira.system.event.type
11	\N	Work Started On Issue	This is the 'work started on issue' event.	jira.system.event.type
12	\N	Work Stopped On Issue	This is the 'work stopped on issue' event.	jira.system.event.type
13	\N	Generic Event	This is the 'generic event' event.	jira.system.event.type
14	\N	Issue Comment Edited	This is the 'issue comment edited' event.	jira.system.event.type
15	\N	Issue Worklog Updated	This is the 'issue worklog updated' event.	jira.system.event.type
16	\N	Issue Worklog Deleted	This is the 'issue worklog deleted' event.	jira.system.event.type
17	\N	Issue Comment Deleted	This is the 'issue comment deleted' event.	jira.system.event.type
18	\N	Issue Archived	This is the 'issue archived' event	jira.system.event.type
19	\N	Issue Restored	This is the 'issue restored' event	jira.system.event.type
\.


--
-- TOC entry 5458 (class 0 OID 16820)
-- Dependencies: 244
-- Data for Name: jiraissue; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.jiraissue (id, pkey, issuenum, project, reporter, assignee, creator, issuetype, summary, description, environment, priority, resolution, issuestatus, created, updated, duedate, resolutiondate, votes, watches, timeoriginalestimate, timeestimate, timespent, workflow_id, security, fixfor, component, archived, archivedby, archiveddate) FROM stdin;
10000	\N	1	10000	JIRAUSER10000	\N	JIRAUSER10000	10001	As an Agile team, I'd like to learn about Scrum >> Click the "TESR-1" link at the left of this row to see detail in the Description tab on the right	*About Scrum*\n\nScrum is an iterative approach to Agile software development. The methodology has been around since the 1980s but was popularised by Jeff Sutherland and Ken Schwaber. \n\nScrum breaks the development of a product down in to discrete iterations (termed Sprints) that each deliver functionality that could potentially be shipped to users.\n\nThe Scrum Alliance offers an excellent [introduction to Scrum|http://www.scrumalliance.org/resources/47] that provides an overview of key Scrum concepts, stakeholders, processes and artefacts.	\N	3	\N	10000	2020-03-05 22:58:55.696+00	2020-03-05 22:58:55.696+00	\N	\N	0	0	\N	\N	\N	10000	\N	\N	\N	N	\N	\N
10001	\N	2	10000	JIRAUSER10000	\N	JIRAUSER10000	10001	As a product owner, I'd like to express work in terms of actual user problems, aka User Stories, and place them in the backlog >> Try creating a new story with the "+ Create Issue" button (top right of screen)	When you click "+ Create Issue" you will be asked for the correct project (select "tesr") and Issue Type (select "Story").\n\n*About User Stories*\n\nThe Scrum methodology drops traditional software requirement statements in favour of real world problems expressed as User Stories. Stories describe the task a particular user is trying to achieve and its value. They are typically of the form "As a (role) I want (something) so that (benefit)". This approach focuses the team on the core user need rather than on implementation details. \n\nStories are "placeholders for a conversation" -- they do not need to be especially detailed since it is expected that the team will work together to resolve ambiguity as the story is developed. \n\nStories to be implemented in the future are stored in the Product Backlog. The backlog is ranked by the Product Owner so that the next items to be completed are at the top.	\N	3	\N	10000	2020-03-05 22:58:56.179+00	2020-03-05 22:58:56.179+00	\N	\N	0	0	\N	\N	\N	10001	\N	\N	\N	N	\N	\N
10002	\N	3	10000	JIRAUSER10000	\N	JIRAUSER10000	10001	As a product owner, I'd like to rank stories in the backlog so I can communicate the proposed implementation order >> Try dragging this story up above the previous story	*About the Product Backlog*\n\nThe backlog is the source of truth for the order of work to be completed. It is expected that the Product Owner will work with the team to make sure that the backlog represents the current approach to delivering the product. JIRA Software makes it easy to prioritise (rank) Stories by dragging them up and down the backlog.	\N	3	\N	10000	2020-03-05 22:58:56.227+00	2020-03-05 22:58:56.227+00	\N	\N	0	0	\N	\N	\N	10002	\N	\N	\N	N	\N	\N
10003	\N	4	10000	JIRAUSER10000	\N	JIRAUSER10000	10001	As a team, I'd like to estimate the effort of a story in Story Points so we can understand the work remaining >> Try setting the Story Points for this story in the "Estimate" field	This story is estimated at 5 Story Points (as shown in the "Estimate" field at the top right of the Detail View). \n\nTry updating the Story Point estimate to 4 by clicking on the "Estimate" then typing.\n\n*Estimating using Story Points*\n\nBecause the traditional process of estimating tasks in weeks or days is often wildly inaccurate, many Scrum teams estimate in Story Points instead. Story Points exist merely as a way to estimate a task's difficulty compared to some other task (for example, a 10-point story would probably take double the effort of a 5-point story). As teams mature with Scrum they tend to achieve a consistent number of Story Points from Sprint to Sprint -- this is termed the team's _velocity_. This allows the Product Owner to use the velocity to predict how many Sprints it will take to deliver parts of the backlog. \n\nMany teams use Planning Poker to achieve consensus on Story Point estimates.\n\n*Using Other Estimation Units*\n\nYou can configure JIRA Software to use time-based estimates if you wish. In the configuration for the board, on the "Estimation" tab, select "Original Time Estimate" as your Estimation Statistic. If you also wish to track the time used during the Sprint, select "Remaining Estimate and Time Spent" to enable Time Tracking in JIRA Software.	\N	3	\N	10000	2020-03-05 22:58:56.273+00	2020-03-05 22:58:56.273+00	\N	\N	0	0	\N	\N	\N	10003	\N	\N	\N	N	\N	\N
10004	\N	5	10000	JIRAUSER10000	\N	JIRAUSER10000	10001	As a team, I'd like to commit to a set of stories to be completed in a sprint (or iteration) >> Click "Create Sprint" then drag the footer down to select issues for a sprint (you can't start a sprint at the moment because one is already active)	*Starting a Sprint*\n\nDuring the Planning Meeting the team will examine the stories at the top of the backlog and determine which they can commit to completing during the coming sprint. Based on this information the Product Owner might break down stories into smaller stories, adjust story priorities or otherwise work with the team to define the ideal sprint outcome. When the sprint is started the stories are moved into the sprint backlog.	\N	3	\N	10000	2020-03-05 22:58:56.321+00	2020-03-05 22:58:56.321+00	\N	\N	0	0	\N	\N	\N	10004	\N	\N	\N	N	\N	\N
10005	\N	6	10000	JIRAUSER10000	\N	JIRAUSER10000	10001	As a scrum master, I'd like to break stories down into tasks we can track during the sprint >> Try creating a task by clicking the Sub-Tasks tab in the Detail View on the right	*Task Breakdown*\n\nMany teams choose to break down user stories into a set of tasks needed to implement the story. They then update the status of these tasks during a sprint to track progress. The completion of the last task signals the end of the story. \n\nYou can add sub-tasks to a story on the sub-task tab (folder icon) above.	\N	3	\N	10000	2020-03-05 22:58:56.362+00	2020-03-05 22:58:56.362+00	\N	\N	0	0	\N	\N	\N	10005	\N	\N	\N	N	\N	\N
10006	\N	7	10000	JIRAUSER10000	JIRAUSER10000	JIRAUSER10000	10003	This is a sample task. Tasks are used to break down the steps to implement a user story	\N	\N	3	\N	10000	2020-03-05 22:58:56.399+00	2020-03-05 22:58:56.399+00	\N	\N	0	0	\N	\N	\N	10006	\N	\N	\N	N	\N	\N
10007	\N	8	10000	JIRAUSER10000	\N	JIRAUSER10000	10004	As a product owner, I'd like to include bugs, tasks and other issue types in my backlog >> Bugs like this one will also appear in your backlog but they are not normally estimated	*Estimation of Bugs*\n\nScrum teams do not normally apply story point estimates to bugs because bugs are considered to be part of the ongoing work that the team must deal with (i.e the overhead). If you view the story points completed in a sprint as a measure of progress, then bugs also have no value because they do not deliver anything additional to the customer. \n\nHowever, you can apply estimates to bugs if you wish by configuring the "Story Points" field to apply to other Issue Types (by default it only applies to Stories and Epics). Some more information on this is in the [documentation|https://confluence.atlassian.com/display/GH/Story+Point].	\N	3	\N	10000	2020-03-05 22:58:56.481+00	2020-03-05 22:58:56.481+00	\N	\N	0	0	\N	\N	\N	10007	\N	\N	\N	N	\N	\N
10008	\N	9	10000	JIRAUSER10000	\N	JIRAUSER10000	10001	As a developer, I'd like to update story status during the sprint >> Click the Active sprints link at the top right of the screen to go to the Active sprints where the current Sprint's items can be updated	\N	\N	3	\N	10000	2020-03-05 22:58:56.515+00	2020-03-05 22:58:56.515+00	\N	\N	0	0	\N	\N	\N	10008	\N	\N	\N	N	\N	\N
10009	\N	10	10000	JIRAUSER10000	JIRAUSER10000	JIRAUSER10000	10001	As a developer, I can update story and task status with drag and drop (click the triangle at far left of this story to show sub-tasks)	\N	\N	3	\N	3	2020-02-27 13:48:55.571+00	2020-02-27 13:48:55.571+00	\N	\N	0	0	\N	\N	\N	10009	\N	\N	\N	N	\N	\N
10010	\N	11	10000	JIRAUSER10000	JIRAUSER10000	JIRAUSER10000	10003	Update task status by dragging and dropping from column to column >> Try dragging this task to "Done"	\N	\N	3	\N	3	2020-03-02 01:50:55.571+00	2020-03-02 01:50:55.571+00	\N	\N	0	0	\N	\N	\N	10010	\N	\N	\N	N	\N	\N
10011	\N	12	10000	JIRAUSER10000	\N	JIRAUSER10000	10003	When the last task is done, the story can be automatically closed >> Drag this task to "Done" too	\N	\N	3	\N	3	2020-03-02 23:15:55.572+00	2020-03-02 23:15:55.572+00	\N	\N	0	0	\N	\N	\N	10011	\N	\N	\N	N	\N	\N
10012	\N	13	10000	JIRAUSER10000	JIRAUSER10000	JIRAUSER10000	10004	As a developer, I can update details on an item using the Detail View >> Click the "TESR-13" link at the top of this card to open the detail view	*Editing using the Detail View*\n\nMany of the fields in the detail view can be inline edited by simply clicking on them. \n\nFor other fields you can open Quick Edit, select "Edit" from the cog drop-down.	\N	3	\N	10000	2020-02-27 13:48:55.572+00	2020-02-27 13:48:55.572+00	\N	\N	0	0	\N	\N	\N	10012	\N	\N	\N	N	\N	\N
10013	\N	14	10000	JIRAUSER10000	JIRAUSER10000	JIRAUSER10000	10001	As a user, I can find important items on the board by using the customisable "Quick Filters" above >> Try clicking the "Only My Issues" Quick Filter above	*Creating Quick Filters*\n\nYou can add your own Quick Filters in the board configuration (select *Board > Configure*)	\N	3	\N	10000	2020-03-04 18:48:55.573+00	2020-03-04 18:48:55.573+00	\N	\N	0	0	\N	\N	\N	10013	\N	\N	\N	N	\N	\N
10014	\N	15	10000	JIRAUSER10000	\N	JIRAUSER10000	10001	As a scrum master, I can see the progress of a sprint via the Burndown Chart >> Click "Reports" to view the Burndown Chart	\N	\N	3	10000	10001	2020-02-27 13:48:55.573+00	2020-03-03 04:24:55.573+00	\N	2020-03-03 04:24:55.573+00	0	0	\N	\N	\N	10014	\N	\N	\N	N	\N	\N
10015	\N	16	10000	JIRAUSER10000	\N	JIRAUSER10000	10001	As a team, we can finish the sprint by clicking the cog icon next to the sprint name above the "To Do" column then selecting "Complete Sprint" >> Try closing this sprint now	\N	\N	3	10000	10001	2020-02-06 17:44:55.573+00	2020-02-28 20:20:55.573+00	\N	2020-02-28 20:20:55.573+00	0	0	\N	\N	\N	10015	\N	\N	\N	N	\N	\N
10016	\N	17	10000	JIRAUSER10000	JIRAUSER10000	JIRAUSER10000	10004	Instructions for deleting this sample board and project are in the description for this issue >> Click the "TESR-17" link and read the description tab of the detail view for more	*To delete this Sample Project _(must be performed by a user with Administration rights)_* \n- Open the administration interface to the projects page by using the keyboard shortcut 'g' then 'g' and typing 'Projects' in to the search dialog\n- Select the "Delete" link for the "tesr" project\n\n*To delete the Sample Project workflow and workflow scheme _(must be performed by a user with Administration rights)_* \n- Open the administration interface to the workflow schemes page by using the keyboard shortcut 'g' then 'g' and typing 'Workflow Schemes' in to the search dialog\n- Select the "Delete" link for the "TESR: Software Simplified Workflow Scheme" workflow scheme\n- Go to the workflows page by using the keyboard shortcut 'g' then 'g' and typing 'Workflows' in to the search dialog(_OnDemand users should select the second match for Workflows_)\n- Expand the "Inactive" section\n- Select the "Delete" link for the "Software Simplified Workflow  for Project TESR" workflow\n\n*To delete this Board _(must be performed by the owner of this Board or an Administrator)_*\n- Click the "Tools" cog at the top right of this board\n- Select "Delete"	\N	3	10000	10001	2020-02-27 13:48:55.574+00	2020-03-02 06:24:55.574+00	\N	2020-03-02 06:24:55.574+00	0	0	\N	\N	\N	10016	\N	\N	\N	N	\N	\N
10017	\N	18	10000	JIRAUSER10000	JIRAUSER10000	JIRAUSER10000	10001	As a user, I'd like a historical story to show in reports	\N	\N	3	10000	10001	2020-02-13 12:38:55.574+00	2020-02-14 09:08:55.574+00	\N	2020-02-14 09:08:55.574+00	0	0	\N	\N	\N	10017	\N	\N	\N	N	\N	\N
10018	\N	19	10000	JIRAUSER10000	JIRAUSER10000	JIRAUSER10000	10001	As a user, I'd like a historical story to show in reports	\N	\N	3	10000	10001	2020-02-13 12:38:55.574+00	2020-02-17 09:02:55.574+00	\N	2020-02-17 09:02:55.574+00	0	0	\N	\N	\N	10018	\N	\N	\N	N	\N	\N
10019	\N	20	10000	JIRAUSER10000	JIRAUSER10000	JIRAUSER10000	10001	As a user, I'd like a historical story to show in reports	\N	\N	3	10000	10001	2020-02-13 12:38:55.575+00	2020-02-18 21:37:55.575+00	\N	2020-02-18 21:37:55.575+00	0	0	\N	\N	\N	10019	\N	\N	\N	N	\N	\N
10020	\N	21	10000	JIRAUSER10000	JIRAUSER10000	JIRAUSER10000	10001	As a user, I'd like a historical story to show in reports	\N	\N	3	10000	10001	2020-02-13 12:38:55.575+00	2020-02-21 04:15:55.575+00	\N	2020-02-21 04:15:55.575+00	0	0	\N	\N	\N	10020	\N	\N	\N	N	\N	\N
10021	\N	22	10000	JIRAUSER10000	JIRAUSER10000	JIRAUSER10000	10001	As a user, I'd like a historical story to show in reports	\N	\N	3	10000	10001	2020-02-13 12:38:55.575+00	2020-02-23 19:35:55.575+00	\N	2020-02-23 19:35:55.575+00	0	0	\N	\N	\N	10021	\N	\N	\N	N	\N	\N
10022	\N	23	10000	JIRAUSER10000	JIRAUSER10000	JIRAUSER10000	10001	As a user, I'd like a historical story to show in reports	\N	\N	3	10000	10001	2020-02-13 12:38:55.575+00	2020-02-25 21:13:55.575+00	\N	2020-02-25 21:13:55.575+00	0	0	\N	\N	\N	10022	\N	\N	\N	N	\N	\N
\.


--
-- TOC entry 5513 (class 0 OID 17200)
-- Dependencies: 299
-- Data for Name: jiraperms; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.jiraperms (id, permtype, projectid, groupname) FROM stdin;
\.


--
-- TOC entry 5569 (class 0 OID 17594)
-- Dependencies: 355
-- Data for Name: jiraworkflows; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.jiraworkflows (id, workflowname, creatorname, descriptor, islocked) FROM stdin;
10000	classic default workflow	\N	<?xml version="1.0" encoding="UTF-8"?>\n<!DOCTYPE workflow PUBLIC "-//OpenSymphony Group//DTD OSWorkflow 2.8//EN" "http://www.opensymphony.com/osworkflow/workflow_2_8.dtd">\n<workflow>\n  <meta name="jira.description">The classic Jira default workflow</meta>\n  <initial-actions>\n    <action id="1" name="Create Issue">\n      <meta name="opsbar-sequence">0</meta>\n      <meta name="jira.i18n.title">common.forms.create</meta>\n      <validators>\n        <validator name="" type="class">\n          <arg name="class.name">com.atlassian.jira.workflow.validator.PermissionValidator</arg>\n          <arg name="permission">Create Issue</arg>\n        </validator>\n      </validators>\n      <results>\n        <unconditional-result old-status="Finished" status="Open" step="1">\n          <post-functions>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.issue.IssueCreateFunction</arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.event.FireIssueEventFunction</arg>\n              <arg name="eventTypeId">1</arg>\n            </function>\n          </post-functions>\n        </unconditional-result>\n      </results>\n    </action>\n  </initial-actions>\n  <common-actions>\n    <action id="2" name="Close Issue" view="resolveissue">\n      <meta name="opsbar-sequence">60</meta>\n      <meta name="jira.i18n.submit">closeissue.close</meta>\n      <meta name="jira.i18n.description">closeissue.desc</meta>\n      <meta name="jira.i18n.title">closeissue.title</meta>\n      <restrict-to>\n        <conditions type="AND">\n          <condition type="class">\n            <arg name="class.name">com.atlassian.jira.workflow.condition.PermissionCondition</arg>\n            <arg name="permission">Resolve Issue</arg>\n          </condition>\n          <condition type="class">\n            <arg name="class.name">com.atlassian.jira.workflow.condition.PermissionCondition</arg>\n            <arg name="permission">Close Issue</arg>\n          </condition>\n        </conditions>\n      </restrict-to>\n      <results>\n        <unconditional-result old-status="Finished" status="Closed" step="6">\n          <post-functions>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.issue.UpdateIssueStatusFunction</arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.misc.CreateCommentFunction</arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.issue.GenerateChangeHistoryFunction</arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.issue.IssueReindexFunction</arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.event.FireIssueEventFunction</arg>\n              <arg name="eventTypeId">5</arg>\n            </function>\n          </post-functions>\n        </unconditional-result>\n      </results>\n    </action>\n    <action id="3" name="Reopen Issue" view="commentassign">\n      <meta name="opsbar-sequence">80</meta>\n      <meta name="jira.i18n.submit">issue.operations.reopen.issue</meta>\n      <meta name="jira.i18n.description">issue.operations.reopen.description</meta>\n      <meta name="jira.i18n.title">issue.operations.reopen.issue</meta>\n      <restrict-to>\n        <conditions>\n          <condition type="class">\n            <arg name="class.name">com.atlassian.jira.workflow.condition.PermissionCondition</arg>\n            <arg name="permission">Resolve Issue</arg>\n          </condition>\n        </conditions>\n      </restrict-to>\n      <results>\n        <unconditional-result old-status="Finished" status="Reopened" step="5">\n          <post-functions>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.issue.UpdateIssueFieldFunction</arg>\n              <arg name="field.value"></arg>\n              <arg name="field.name">resolution</arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.issue.UpdateIssueStatusFunction</arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.misc.CreateCommentFunction</arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.issue.GenerateChangeHistoryFunction</arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.issue.IssueReindexFunction</arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.event.FireIssueEventFunction</arg>\n              <arg name="eventTypeId">7</arg>\n            </function>\n          </post-functions>\n        </unconditional-result>\n      </results>\n    </action>\n    <action id="4" name="Start Progress">\n      <meta name="opsbar-sequence">20</meta>\n      <meta name="jira.i18n.title">startprogress.title</meta>\n      <restrict-to>\n        <conditions>\n          <condition type="class">\n            <arg name="class.name">com.atlassian.jira.workflow.condition.AllowOnlyAssignee</arg>\n          </condition>\n        </conditions>\n      </restrict-to>\n      <results>\n        <unconditional-result old-status="Finished" status="Underway" step="3">\n          <post-functions>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.issue.UpdateIssueFieldFunction</arg>\n              <arg name="field.value"></arg>\n              <arg name="field.name">resolution</arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.issue.UpdateIssueStatusFunction</arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.misc.CreateCommentFunction</arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.issue.GenerateChangeHistoryFunction</arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.issue.IssueReindexFunction</arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.event.FireIssueEventFunction</arg>\n              <arg name="eventTypeId">11</arg>\n            </function>\n          </post-functions>\n        </unconditional-result>\n      </results>\n    </action>\n    <action id="5" name="Resolve Issue" view="resolveissue">\n      <meta name="opsbar-sequence">40</meta>\n      <meta name="jira.i18n.submit">resolveissue.resolve</meta>\n      <meta name="jira.i18n.description">resolveissue.desc.line1</meta>\n      <meta name="jira.i18n.title">resolveissue.title</meta>\n      <restrict-to>\n        <conditions>\n          <condition type="class">\n            <arg name="class.name">com.atlassian.jira.workflow.condition.PermissionCondition</arg>\n            <arg name="permission">Resolve Issue</arg>\n          </condition>\n        </conditions>\n      </restrict-to>\n      <results>\n        <unconditional-result old-status="Finished" status="Resolved" step="4">\n          <post-functions>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.issue.UpdateIssueStatusFunction</arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.misc.CreateCommentFunction</arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.issue.GenerateChangeHistoryFunction</arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.issue.IssueReindexFunction</arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.event.FireIssueEventFunction</arg>\n              <arg name="eventTypeId">4</arg>\n            </function>\n          </post-functions>\n        </unconditional-result>\n      </results>\n    </action>\n  </common-actions>\n  <steps>\n    <step id="1" name="Open">\n      <meta name="jira.status.id">1</meta>\n      <actions>\n<common-action id="4" />\n<common-action id="5" />\n<common-action id="2" />\n      </actions>\n    </step>\n    <step id="3" name="In Progress">\n      <meta name="jira.status.id">3</meta>\n      <actions>\n<common-action id="5" />\n<common-action id="2" />\n        <action id="301" name="Stop Progress">\n          <meta name="opsbar-sequence">20</meta>\n          <meta name="jira.i18n.title">stopprogress.title</meta>\n          <restrict-to>\n            <conditions>\n              <condition type="class">\n                <arg name="class.name">com.atlassian.jira.workflow.condition.AllowOnlyAssignee</arg>\n              </condition>\n            </conditions>\n          </restrict-to>\n          <results>\n            <unconditional-result old-status="Finished" status="Assigned" step="1">\n              <post-functions>\n                <function type="class">\n                  <arg name="class.name">com.atlassian.jira.workflow.function.issue.UpdateIssueFieldFunction</arg>\n                  <arg name="field.value"></arg>\n                  <arg name="field.name">resolution</arg>\n                </function>\n                <function type="class">\n                  <arg name="class.name">com.atlassian.jira.workflow.function.issue.UpdateIssueStatusFunction</arg>\n                </function>\n                <function type="class">\n                  <arg name="class.name">com.atlassian.jira.workflow.function.misc.CreateCommentFunction</arg>\n                </function>\n                <function type="class">\n                  <arg name="class.name">com.atlassian.jira.workflow.function.issue.GenerateChangeHistoryFunction</arg>\n                </function>\n                <function type="class">\n                  <arg name="class.name">com.atlassian.jira.workflow.function.issue.IssueReindexFunction</arg>\n                </function>\n                <function type="class">\n                  <arg name="class.name">com.atlassian.jira.workflow.function.event.FireIssueEventFunction</arg>\n                  <arg name="eventTypeId">12</arg>\n                </function>\n              </post-functions>\n            </unconditional-result>\n          </results>\n        </action>\n      </actions>\n    </step>\n    <step id="4" name="Resolved">\n      <meta name="jira.status.id">5</meta>\n      <actions>\n<common-action id="3" />\n        <action id="701" name="Close Issue" view="commentassign">\n          <meta name="opsbar-sequence">60</meta>\n          <meta name="jira.i18n.submit">closeissue.close</meta>\n          <meta name="jira.i18n.description">closeissue.desc</meta>\n          <meta name="jira.i18n.title">closeissue.title</meta>\n          <meta name="jira.description">Closing an issue indicates there is no more work to be done on it, and it has been verified as complete.</meta>\n          <restrict-to>\n            <conditions>\n              <condition type="class">\n                <arg name="class.name">com.atlassian.jira.workflow.condition.PermissionCondition</arg>\n                <arg name="permission">Close Issue</arg>\n              </condition>\n            </conditions>\n          </restrict-to>\n          <results>\n            <unconditional-result old-status="Finished" status="Closed" step="6">\n              <post-functions>\n                <function type="class">\n                  <arg name="class.name">com.atlassian.jira.workflow.function.issue.UpdateIssueStatusFunction</arg>\n                </function>\n                <function type="class">\n                  <arg name="class.name">com.atlassian.jira.workflow.function.misc.CreateCommentFunction</arg>\n                </function>\n                <function type="class">\n                  <arg name="class.name">com.atlassian.jira.workflow.function.issue.GenerateChangeHistoryFunction</arg>\n                </function>\n                <function type="class">\n                  <arg name="class.name">com.atlassian.jira.workflow.function.issue.IssueReindexFunction</arg>\n                </function>\n                <function type="class">\n                  <arg name="class.name">com.atlassian.jira.workflow.function.event.FireIssueEventFunction</arg>\n                  <arg name="eventTypeId">5</arg>\n                </function>\n              </post-functions>\n            </unconditional-result>\n          </results>\n        </action>\n      </actions>\n    </step>\n    <step id="5" name="Reopened">\n      <meta name="jira.status.id">4</meta>\n      <actions>\n<common-action id="5" />\n<common-action id="2" />\n<common-action id="4" />\n      </actions>\n    </step>\n    <step id="6" name="Closed">\n      <meta name="jira.status.id">6</meta>\n      <meta name="jira.issue.editable">false</meta>\n      <actions>\n<common-action id="3" />\n      </actions>\n    </step>\n  </steps>\n</workflow>\n	\N
10100	Software Simplified Workflow for Project TESR	\N	<?xml version="1.0" encoding="UTF-8"?>\n<!DOCTYPE workflow PUBLIC "-//OpenSymphony Group//DTD OSWorkflow 2.8//EN" "http://www.opensymphony.com/osworkflow/workflow_2_8.dtd">\n<workflow>\n  <meta name="jira.description">Generated by JIRA Software version 8.5.3. This workflow is managed internally by Jira Software. Do not manually modify this workflow.</meta>\n  <meta name="jira.update.author.key">JIRAUSER10000</meta>\n  <meta name="jira.updated.date">1583449134865</meta>\n  <meta name="jira.update.author.name">admin</meta>\n  <meta name="gh.version">8.5.3</meta>\n  <initial-actions>\n    <action id="1" name="Create">\n      <meta name="jira.i18n.title">common.forms.create</meta>\n      <validators>\n        <validator name="" type="class">\n          <arg name="permission">Create Issue</arg>\n          <arg name="class.name">com.atlassian.jira.workflow.validator.PermissionValidator</arg>\n        </validator>\n      </validators>\n      <results>\n        <unconditional-result old-status="null" status="To Do" step="1">\n          <post-functions>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.issue.IssueCreateFunction</arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.issue.IssueReindexFunction</arg>\n            </function>\n            <function type="class">\n              <arg name="eventTypeId">1</arg>\n              <arg name="class.name">com.atlassian.jira.workflow.function.event.FireIssueEventFunction\n                            </arg>\n            </function>\n          </post-functions>\n        </unconditional-result>\n      </results>\n    </action>\n  </initial-actions>\n  <global-actions>\n    <action id="11" name="To Do">\n      <meta name="jira.description"></meta>\n      <meta name="jira.i18n.title">gh.workflow.preset.todo</meta>\n      <results>\n        <unconditional-result old-status="Not Done" status="Done" step="1">\n          <post-functions>\n            <function type="class">\n              <arg name="field.name">resolution</arg>\n              <arg name="field.value"></arg>\n              <arg name="class.name">com.atlassian.jira.workflow.function.issue.UpdateIssueFieldFunction\n                            </arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.issue.UpdateIssueStatusFunction\n                            </arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.misc.CreateCommentFunction</arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">\n                                com.atlassian.jira.workflow.function.issue.GenerateChangeHistoryFunction\n                            </arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.issue.IssueReindexFunction</arg>\n            </function>\n            <function type="class">\n              <arg name="eventTypeId">13</arg>\n              <arg name="class.name">com.atlassian.jira.workflow.function.event.FireIssueEventFunction\n                            </arg>\n            </function>\n          </post-functions>\n        </unconditional-result>\n      </results>\n    </action>\n    <action id="21" name="In Progress">\n      <meta name="jira.description"></meta>\n      <meta name="jira.i18n.title">gh.workflow.preset.inprogress</meta>\n      <results>\n        <unconditional-result old-status="Not Done" status="Done" step="6">\n          <post-functions>\n            <function type="class">\n              <arg name="field.name">resolution</arg>\n              <arg name="field.value"></arg>\n              <arg name="class.name">com.atlassian.jira.workflow.function.issue.UpdateIssueFieldFunction\n                            </arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.issue.UpdateIssueStatusFunction\n                            </arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.misc.CreateCommentFunction</arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">\n                                com.atlassian.jira.workflow.function.issue.GenerateChangeHistoryFunction\n                            </arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.issue.IssueReindexFunction</arg>\n            </function>\n            <function type="class">\n              <arg name="eventTypeId">13</arg>\n              <arg name="class.name">com.atlassian.jira.workflow.function.event.FireIssueEventFunction\n                            </arg>\n            </function>\n          </post-functions>\n        </unconditional-result>\n      </results>\n    </action>\n    <action id="31" name="Done">\n      <meta name="jira.description"></meta>\n      <meta name="jira.i18n.title">gh.workflow.preset.done</meta>\n      <results>\n        <unconditional-result old-status="Not Done" status="Done" step="11">\n          <post-functions>\n            <function type="class">\n              <arg name="field.name">resolution</arg>\n              <arg name="field.value">10000</arg>\n              <arg name="class.name">com.atlassian.jira.workflow.function.issue.UpdateIssueFieldFunction\n                            </arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.issue.UpdateIssueStatusFunction\n                            </arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.misc.CreateCommentFunction</arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">\n                                com.atlassian.jira.workflow.function.issue.GenerateChangeHistoryFunction\n                            </arg>\n            </function>\n            <function type="class">\n              <arg name="class.name">com.atlassian.jira.workflow.function.issue.IssueReindexFunction</arg>\n            </function>\n            <function type="class">\n              <arg name="eventTypeId">13</arg>\n              <arg name="class.name">com.atlassian.jira.workflow.function.event.FireIssueEventFunction\n                            </arg>\n            </function>\n          </post-functions>\n        </unconditional-result>\n      </results>\n    </action>\n  </global-actions>\n  <steps>\n    <step id="1" name="To Do">\n      <meta name="jira.status.id">10000</meta>\n    </step>\n    <step id="6" name="In Progress">\n      <meta name="jira.status.id">3</meta>\n    </step>\n    <step id="11" name="Done">\n      <meta name="jira.status.id">10001</meta>\n    </step>\n  </steps>\n</workflow>\n	\N
\.


--
-- TOC entry 5572 (class 0 OID 17618)
-- Dependencies: 358
-- Data for Name: jiraworkflowstatuses; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.jiraworkflowstatuses (id, status, parentname) FROM stdin;
\.


--
-- TOC entry 5467 (class 0 OID 16886)
-- Dependencies: 253
-- Data for Name: jquartz_blob_triggers; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.jquartz_blob_triggers (sched_name, trigger_name, trigger_group, blob_data) FROM stdin;
\.


--
-- TOC entry 5468 (class 0 OID 16894)
-- Dependencies: 254
-- Data for Name: jquartz_calendars; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.jquartz_calendars (sched_name, calendar_name, calendar) FROM stdin;
\.


--
-- TOC entry 5469 (class 0 OID 16902)
-- Dependencies: 255
-- Data for Name: jquartz_cron_triggers; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.jquartz_cron_triggers (sched_name, trigger_name, trigger_group, cron_expression, time_zone_id) FROM stdin;
\.


--
-- TOC entry 5470 (class 0 OID 16910)
-- Dependencies: 256
-- Data for Name: jquartz_fired_triggers; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.jquartz_fired_triggers (sched_name, entry_id, trigger_name, trigger_group, is_volatile, instance_name, fired_time, sched_time, priority, state, job_name, job_group, is_stateful, is_nonconcurrent, is_update_data, requests_recovery) FROM stdin;
\.


--
-- TOC entry 5471 (class 0 OID 16918)
-- Dependencies: 257
-- Data for Name: jquartz_job_details; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.jquartz_job_details (sched_name, job_name, job_group, description, job_class_name, is_durable, is_volatile, is_stateful, is_nonconcurrent, is_update_data, requests_recovery, job_data) FROM stdin;
\.


--
-- TOC entry 5472 (class 0 OID 16926)
-- Dependencies: 258
-- Data for Name: jquartz_job_listeners; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.jquartz_job_listeners (job_name, job_group, job_listener) FROM stdin;
\.


--
-- TOC entry 5473 (class 0 OID 16934)
-- Dependencies: 259
-- Data for Name: jquartz_locks; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.jquartz_locks (sched_name, lock_name) FROM stdin;
\N	TRIGGER_ACCESS
\N	JOB_ACCESS
\N	CALENDAR_ACCESS
\N	STATE_ACCESS
\N	MISFIRE_ACCESS
\.


--
-- TOC entry 5474 (class 0 OID 16939)
-- Dependencies: 260
-- Data for Name: jquartz_paused_trigger_grps; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.jquartz_paused_trigger_grps (sched_name, trigger_group) FROM stdin;
\.


--
-- TOC entry 5475 (class 0 OID 16944)
-- Dependencies: 261
-- Data for Name: jquartz_scheduler_state; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.jquartz_scheduler_state (sched_name, instance_name, last_checkin_time, checkin_interval) FROM stdin;
\.


--
-- TOC entry 5477 (class 0 OID 16957)
-- Dependencies: 263
-- Data for Name: jquartz_simple_triggers; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.jquartz_simple_triggers (sched_name, trigger_name, trigger_group, repeat_count, repeat_interval, times_triggered) FROM stdin;
\.


--
-- TOC entry 5476 (class 0 OID 16949)
-- Dependencies: 262
-- Data for Name: jquartz_simprop_triggers; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.jquartz_simprop_triggers (sched_name, trigger_name, trigger_group, str_prop_1, str_prop_2, str_prop_3, int_prop_1, int_prop_2, long_prop_1, long_prop_2, dec_prop_1, dec_prop_2, bool_prop_1, bool_prop_2) FROM stdin;
\.


--
-- TOC entry 5478 (class 0 OID 16965)
-- Dependencies: 264
-- Data for Name: jquartz_trigger_listeners; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.jquartz_trigger_listeners (trigger_name, trigger_group, trigger_listener) FROM stdin;
\.


--
-- TOC entry 5479 (class 0 OID 16973)
-- Dependencies: 265
-- Data for Name: jquartz_triggers; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.jquartz_triggers (sched_name, trigger_name, trigger_group, job_name, job_group, is_volatile, description, next_fire_time, prev_fire_time, priority, trigger_state, trigger_type, start_time, end_time, calendar_name, misfire_instr, job_data) FROM stdin;
\.


--
-- TOC entry 5480 (class 0 OID 16981)
-- Dependencies: 266
-- Data for Name: label; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.label (id, fieldid, issue, label) FROM stdin;
\.


--
-- TOC entry 5481 (class 0 OID 16986)
-- Dependencies: 267
-- Data for Name: licenserolesdefault; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.licenserolesdefault (id, license_role_name) FROM stdin;
10000	jira-software
\.


--
-- TOC entry 5482 (class 0 OID 16991)
-- Dependencies: 268
-- Data for Name: licenserolesgroup; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.licenserolesgroup (id, license_role_name, group_id, primary_group) FROM stdin;
10000	jira-software	jira-software-users	Y
10001	jira-software	jira-administrators	N
\.


--
-- TOC entry 5483 (class 0 OID 16999)
-- Dependencies: 269
-- Data for Name: listenerconfig; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.listenerconfig (id, clazz, listenername) FROM stdin;
10000	com.atlassian.jira.event.listeners.mail.MailListener	Mail Listener
10001	com.atlassian.jira.event.listeners.history.IssueAssignHistoryListener	Issue Assignment Listener
10200	com.atlassian.jira.event.listeners.search.IssueIndexListener	Issue Index Listener
\.


--
-- TOC entry 5484 (class 0 OID 17007)
-- Dependencies: 270
-- Data for Name: mailserver; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.mailserver (id, name, description, mailfrom, prefix, smtp_port, protocol, server_type, servername, jndilocation, mailusername, mailpassword, istlsrequired, timeout, socks_port, socks_host) FROM stdin;
\.


--
-- TOC entry 5485 (class 0 OID 17015)
-- Dependencies: 271
-- Data for Name: managedconfigurationitem; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.managedconfigurationitem (id, item_id, item_type, managed, access_level, source, description_key) FROM stdin;
10000	customfield_10000	CUSTOM_FIELD	true	LOCKED	com.atlassian.jira.plugins.jira-development-integration-plugin:reserved-field-locked	devstatus.customfield.locked.desc
10100	customfield_10100	CUSTOM_FIELD	true	LOCKED	com.pyxis.greenhopper.jira:reference-select-locked	gh.customfield.locked.desc
10101	customfield_10101	CUSTOM_FIELD	true	LOCKED	com.pyxis.greenhopper.jira:reference-select-locked	gh.customfield.locked.desc
10102	customfield_10102	CUSTOM_FIELD	true	LOCKED	com.pyxis.greenhopper.jira:reference-select-locked	gh.customfield.locked.desc
10103	customfield_10103	CUSTOM_FIELD	true	LOCKED	com.pyxis.greenhopper.jira:reference-select-locked	gh.customfield.locked.desc
10104	customfield_10104	CUSTOM_FIELD	true	LOCKED	com.pyxis.greenhopper.jira:reference-select-locked	gh.customfield.locked.desc
10105	customfield_10105	CUSTOM_FIELD	true	LOCKED	com.pyxis.greenhopper.jira:reference-select-locked	gh.customfield.locked.desc
\.


--
-- TOC entry 5502 (class 0 OID 17130)
-- Dependencies: 288
-- Data for Name: membershipbase; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.membershipbase (id, user_name, group_name) FROM stdin;
\.


--
-- TOC entry 5487 (class 0 OID 17031)
-- Dependencies: 273
-- Data for Name: moved_issue_key; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.moved_issue_key (id, old_issue_key, issue_id) FROM stdin;
\.


--
-- TOC entry 5488 (class 0 OID 17036)
-- Dependencies: 274
-- Data for Name: nodeassociation; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.nodeassociation (source_node_id, source_node_entity, sink_node_id, sink_node_entity, association_type, sequence) FROM stdin;
10000	Project	10000	NotificationScheme	ProjectScheme	\N
10000	Project	10000	IssueTypeScreenScheme	ProjectScheme	\N
10000	Project	10100	WorkflowScheme	ProjectScheme	\N
10000	Project	10000	PermissionScheme	ProjectScheme	\N
10000	Issue	10001	Version	IssueFixVersion	\N
10001	Issue	10001	Version	IssueFixVersion	\N
10002	Issue	10002	Version	IssueFixVersion	\N
10003	Issue	10002	Version	IssueFixVersion	\N
10007	Issue	10001	Version	IssueFixVersion	\N
10009	Issue	10001	Version	IssueFixVersion	\N
10010	Issue	10001	Version	IssueFixVersion	\N
10011	Issue	10001	Version	IssueFixVersion	\N
10012	Issue	10001	Version	IssueFixVersion	\N
10017	Issue	10001	Version	IssueFixVersion	\N
10018	Issue	10001	Version	IssueFixVersion	\N
10019	Issue	10000	Version	IssueFixVersion	\N
10020	Issue	10000	Version	IssueFixVersion	\N
10021	Issue	10000	Version	IssueFixVersion	\N
10022	Issue	10000	Version	IssueFixVersion	\N
\.


--
-- TOC entry 5489 (class 0 OID 17041)
-- Dependencies: 275
-- Data for Name: nodeindexcounter; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.nodeindexcounter (id, node_id, sending_node_id, index_operation_id) FROM stdin;
10000	i-0832bd612c9f98710	i-0832bd612c9f98710	10130
\.


--
-- TOC entry 5490 (class 0 OID 17046)
-- Dependencies: 276
-- Data for Name: notification; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.notification (id, scheme, event, event_type_id, template_id, notif_type, notif_parameter) FROM stdin;
10000	10000	\N	1	\N	Current_Assignee	\N
10001	10000	\N	1	\N	Current_Reporter	\N
10002	10000	\N	1	\N	All_Watchers	\N
10003	10000	\N	2	\N	Current_Assignee	\N
10004	10000	\N	2	\N	Current_Reporter	\N
10005	10000	\N	2	\N	All_Watchers	\N
10006	10000	\N	3	\N	Current_Assignee	\N
10007	10000	\N	3	\N	Current_Reporter	\N
10008	10000	\N	3	\N	All_Watchers	\N
10009	10000	\N	4	\N	Current_Assignee	\N
10010	10000	\N	4	\N	Current_Reporter	\N
10011	10000	\N	4	\N	All_Watchers	\N
10012	10000	\N	5	\N	Current_Assignee	\N
10013	10000	\N	5	\N	Current_Reporter	\N
10014	10000	\N	5	\N	All_Watchers	\N
10015	10000	\N	6	\N	Current_Assignee	\N
10016	10000	\N	6	\N	Current_Reporter	\N
10017	10000	\N	6	\N	All_Watchers	\N
10018	10000	\N	7	\N	Current_Assignee	\N
10019	10000	\N	7	\N	Current_Reporter	\N
10020	10000	\N	7	\N	All_Watchers	\N
10021	10000	\N	8	\N	Current_Assignee	\N
10022	10000	\N	8	\N	Current_Reporter	\N
10023	10000	\N	8	\N	All_Watchers	\N
10024	10000	\N	9	\N	Current_Assignee	\N
10025	10000	\N	9	\N	Current_Reporter	\N
10026	10000	\N	9	\N	All_Watchers	\N
10027	10000	\N	10	\N	Current_Assignee	\N
10028	10000	\N	10	\N	Current_Reporter	\N
10029	10000	\N	10	\N	All_Watchers	\N
10030	10000	\N	11	\N	Current_Assignee	\N
10031	10000	\N	11	\N	Current_Reporter	\N
10032	10000	\N	11	\N	All_Watchers	\N
10033	10000	\N	12	\N	Current_Assignee	\N
10034	10000	\N	12	\N	Current_Reporter	\N
10035	10000	\N	12	\N	All_Watchers	\N
10036	10000	\N	13	\N	Current_Assignee	\N
10037	10000	\N	13	\N	Current_Reporter	\N
10038	10000	\N	13	\N	All_Watchers	\N
10100	10000	\N	14	\N	Current_Assignee	\N
10101	10000	\N	14	\N	Current_Reporter	\N
10102	10000	\N	14	\N	All_Watchers	\N
10103	10000	\N	15	\N	Current_Assignee	\N
10104	10000	\N	15	\N	Current_Reporter	\N
10105	10000	\N	15	\N	All_Watchers	\N
10106	10000	\N	16	\N	Current_Assignee	\N
10107	10000	\N	16	\N	Current_Reporter	\N
10108	10000	\N	16	\N	All_Watchers	\N
10200	10000	\N	18	\N	Current_Assignee	\N
10201	10000	\N	18	\N	Current_Reporter	\N
10202	10000	\N	18	\N	All_Watchers	\N
10203	10000	\N	19	\N	Current_Assignee	\N
10204	10000	\N	19	\N	Current_Reporter	\N
10205	10000	\N	19	\N	All_Watchers	\N
\.


--
-- TOC entry 5491 (class 0 OID 17051)
-- Dependencies: 277
-- Data for Name: notificationinstance; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.notificationinstance (id, notificationtype, source, emailaddress, messageid) FROM stdin;
\.


--
-- TOC entry 5492 (class 0 OID 17059)
-- Dependencies: 278
-- Data for Name: notificationscheme; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.notificationscheme (id, name, description) FROM stdin;
10000	Default Notification Scheme	\N
\.


--
-- TOC entry 5493 (class 0 OID 17067)
-- Dependencies: 279
-- Data for Name: oauthconsumer; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.oauthconsumer (id, created, consumername, consumer_key, consumerservice, public_key, private_key, description, callback, signature_method, shared_secret) FROM stdin;
\.


--
-- TOC entry 5494 (class 0 OID 17075)
-- Dependencies: 280
-- Data for Name: oauthconsumertoken; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.oauthconsumertoken (id, created, token_key, token, token_secret, token_type, consumer_key) FROM stdin;
\.


--
-- TOC entry 5495 (class 0 OID 17083)
-- Dependencies: 281
-- Data for Name: oauthspconsumer; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.oauthspconsumer (id, created, consumer_key, consumername, public_key, description, callback, two_l_o_allowed, executing_two_l_o_user, two_l_o_impersonation_allowed, three_l_o_allowed) FROM stdin;
\.


--
-- TOC entry 5496 (class 0 OID 17091)
-- Dependencies: 282
-- Data for Name: oauthsptoken; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.oauthsptoken (id, created, token, token_secret, token_type, consumer_key, username, ttl, spauth, callback, spverifier, spversion, session_handle, session_creation_time, session_last_renewal_time, session_time_to_live) FROM stdin;
\.


--
-- TOC entry 5512 (class 0 OID 17195)
-- Dependencies: 298
-- Data for Name: optionconfiguration; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.optionconfiguration (id, fieldid, optionid, fieldconfig, sequence) FROM stdin;
10200	priority	1	10101	1
10201	priority	2	10101	2
10202	priority	3	10101	3
10203	priority	4	10101	4
10204	priority	5	10101	5
10300	issuetype	10000	10000	0
10301	issuetype	10001	10000	1
10306	issuetype	10002	10207	0
10307	issuetype	10003	10207	1
10308	issuetype	10001	10207	2
10309	issuetype	10004	10207	3
10310	issuetype	10000	10207	4
\.


--
-- TOC entry 5497 (class 0 OID 17099)
-- Dependencies: 283
-- Data for Name: os_currentstep; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.os_currentstep (id, entry_id, step_id, action_id, owner, start_date, due_date, finish_date, status, caller) FROM stdin;
10000	10000	1	0		2020-03-05 22:58:55.721+00	\N	\N	To Do	\N
10001	10001	1	0		2020-03-05 22:58:56.182+00	\N	\N	To Do	\N
10002	10002	1	0		2020-03-05 22:58:56.229+00	\N	\N	To Do	\N
10003	10003	1	0		2020-03-05 22:58:56.276+00	\N	\N	To Do	\N
10004	10004	1	0		2020-03-05 22:58:56.323+00	\N	\N	To Do	\N
10005	10005	1	0		2020-03-05 22:58:56.364+00	\N	\N	To Do	\N
10006	10006	1	0		2020-03-05 22:58:56.401+00	\N	\N	To Do	\N
10007	10007	1	0		2020-03-05 22:58:56.484+00	\N	\N	To Do	\N
10008	10008	1	0		2020-03-05 22:58:56.518+00	\N	\N	To Do	\N
10009	10009	6	0		2020-03-05 22:58:56.743+00	\N	\N	To Do	\N
10010	10010	6	0		2020-03-05 22:58:56.789+00	\N	\N	To Do	\N
10011	10011	6	0		2020-03-05 22:58:56.867+00	\N	\N	To Do	\N
10012	10012	1	0		2020-03-05 22:58:56.969+00	\N	\N	To Do	\N
10013	10013	1	0		2020-03-05 22:58:57.04+00	\N	\N	To Do	\N
10014	10014	11	0		2020-03-05 22:58:57.11+00	\N	\N	To Do	\N
10015	10015	11	0		2020-03-05 22:58:57.201+00	\N	\N	To Do	\N
10016	10016	11	0		2020-03-05 22:58:57.305+00	\N	\N	To Do	\N
10017	10017	11	0		2020-03-05 22:58:57.39+00	\N	\N	To Do	\N
10018	10018	11	0		2020-03-05 22:58:57.46+00	\N	\N	To Do	\N
10019	10019	11	0		2020-03-05 22:58:57.538+00	\N	\N	To Do	\N
10020	10020	11	0		2020-03-05 22:58:57.64+00	\N	\N	To Do	\N
10021	10021	11	0		2020-03-05 22:58:57.747+00	\N	\N	To Do	\N
10022	10022	11	0		2020-03-05 22:58:57.843+00	\N	\N	To Do	\N
\.


--
-- TOC entry 5498 (class 0 OID 17107)
-- Dependencies: 284
-- Data for Name: os_currentstep_prev; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.os_currentstep_prev (id, previous_id) FROM stdin;
\.


--
-- TOC entry 5500 (class 0 OID 17117)
-- Dependencies: 286
-- Data for Name: os_historystep; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.os_historystep (id, entry_id, step_id, action_id, owner, start_date, due_date, finish_date, status, caller) FROM stdin;
\.


--
-- TOC entry 5501 (class 0 OID 17125)
-- Dependencies: 287
-- Data for Name: os_historystep_prev; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.os_historystep_prev (id, previous_id) FROM stdin;
\.


--
-- TOC entry 5511 (class 0 OID 17190)
-- Dependencies: 297
-- Data for Name: os_wfentry; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.os_wfentry (id, name, initialized, state) FROM stdin;
10000	Software Simplified Workflow for Project TESR	\N	1
10001	Software Simplified Workflow for Project TESR	\N	1
10002	Software Simplified Workflow for Project TESR	\N	1
10003	Software Simplified Workflow for Project TESR	\N	1
10004	Software Simplified Workflow for Project TESR	\N	1
10005	Software Simplified Workflow for Project TESR	\N	1
10006	Software Simplified Workflow for Project TESR	\N	1
10007	Software Simplified Workflow for Project TESR	\N	1
10008	Software Simplified Workflow for Project TESR	\N	1
10009	Software Simplified Workflow for Project TESR	\N	1
10010	Software Simplified Workflow for Project TESR	\N	1
10011	Software Simplified Workflow for Project TESR	\N	1
10012	Software Simplified Workflow for Project TESR	\N	1
10013	Software Simplified Workflow for Project TESR	\N	1
10014	Software Simplified Workflow for Project TESR	\N	1
10015	Software Simplified Workflow for Project TESR	\N	1
10016	Software Simplified Workflow for Project TESR	\N	1
10017	Software Simplified Workflow for Project TESR	\N	1
10018	Software Simplified Workflow for Project TESR	\N	1
10019	Software Simplified Workflow for Project TESR	\N	1
10020	Software Simplified Workflow for Project TESR	\N	1
10021	Software Simplified Workflow for Project TESR	\N	1
10022	Software Simplified Workflow for Project TESR	\N	1
\.


--
-- TOC entry 5514 (class 0 OID 17205)
-- Dependencies: 300
-- Data for Name: permissionscheme; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.permissionscheme (id, name, description) FROM stdin;
0	Default Permission Scheme	This is the default Permission Scheme. Any new projects that are created will be assigned this scheme.
10000	Default software scheme	Default scheme for Software projects.
\.


--
-- TOC entry 5515 (class 0 OID 17213)
-- Dependencies: 301
-- Data for Name: permissionschemeattribute; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.permissionschemeattribute (id, scheme, attribute_key, attribute_value) FROM stdin;
\.


--
-- TOC entry 5516 (class 0 OID 17221)
-- Dependencies: 302
-- Data for Name: pluginstate; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.pluginstate (pluginkey, pluginenabled) FROM stdin;
\.


--
-- TOC entry 5517 (class 0 OID 17226)
-- Dependencies: 303
-- Data for Name: pluginversion; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.pluginversion (id, pluginname, pluginkey, pluginversion, created) FROM stdin;
10000	Jira Projects Plugin	com.atlassian.jira.jira-projects-plugin	5.2.0	2020-03-05 22:51:31.722+00
10001	Jira Software Application	com.atlassian.jira.jira-software-application	8.5.3	2020-03-05 22:51:31.726+00
10002	Atlassian OAuth Consumer Plugin	com.atlassian.oauth.consumer	4.0.2	2020-03-05 22:51:31.727+00
10003	Atlassian Jira - Plugins - My Jira Home	com.atlassian.jira.jira-my-home-plugin	8.5.3	2020-03-05 22:51:31.728+00
10004	JIRA Software English (United States) Language Pack	tac.jira software.languages.en_US	8.6.0.v20190920191956	2020-03-05 22:51:31.729+00
10005	Project Creation Plugin SPI for JIRA	com.atlassian.plugins.jira-project-creation	4.0.0-74bdd7a6fa	2020-03-05 22:51:31.731+00
10006	Atlassian Embedded Crowd - Administration Plugin	com.atlassian.crowd.embedded.admin	2.3.4-j11-2	2020-03-05 22:51:31.732+00
10007	Analytics Client Plugin	com.atlassian.analytics.analytics-client	5.7.2	2020-03-05 22:51:31.733+00
10008	JIRA Software Chinese (China) Language Pack	tac.jira software.languages.zh_CN	8.6.0.v20190920191956	2020-03-05 22:51:31.734+00
10009	Jira Index Analyzer	com.atlassian.jira.plugins.jira-index-analyzer-plugin	8.5.3	2020-03-05 22:51:31.735+00
10010	Jira Help Tips plugin	com.atlassian.plugins.helptips.jira-help-tips	8.5.3	2020-03-05 22:51:31.736+00
10011	Atlassian Navigation Links Plugin	com.atlassian.plugins.atlassian-nav-links-plugin	5.0.0	2020-03-05 22:51:31.737+00
10012	JIRA Core Romanian (Romania) Language Pack	tac.jira core.languages.ro_RO	8.6.0.v20190920192957	2020-03-05 22:51:31.738+00
10013	Atlassian Jira - Plugins - Issue Web Link	com.atlassian.jira.jira-issue-link-web-plugin	8.5.3	2020-03-05 22:51:31.739+00
10014	JIRA Core Slovak (Slovakia) Language Pack	tac.jira core.languages.sk_SK	8.6.0.v20190920192957	2020-03-05 22:51:31.74+00
10015	Atlassian Hipchat Integration Plugin Core	com.atlassian.plugins.base-hipchat-integration-plugin-api	8.4.2	2020-03-05 22:51:31.741+00
10016	Whisper Messages Plugin	whisper.messages	1.0	2020-03-05 22:51:31.742+00
10017	Project Role Actors Plugin	com.atlassian.jira.plugin.system.projectroleactors	1.0	2020-03-05 22:51:31.743+00
10018	Atlassian Jira - Plugins - OAuth Service Provider SPI	com.atlassian.jira.oauth.serviceprovider	8.5.3	2020-03-05 22:51:31.744+00
10019	Keyboard Shortcuts Plugin	jira.keyboard.shortcuts	1.0	2020-03-05 22:51:31.745+00
10020	Bitbucket Importer Plugin for JIM	com.atlassian.jira.plugins.jira-importers-bitbucket-plugin	3.1.1	2020-03-05 22:51:31.746+00
10021	Atlassian Remote Event Common Plugin	com.atlassian.plugins.atlassian-remote-event-common-plugin	6.1.0	2020-03-05 22:51:31.747+00
10022	Jira GitHub Issue Importer	com.atlassian.jira.plugins.jira-importers-github-plugin	3.1.0	2020-03-05 22:51:31.748+00
10023	JIRA Password Policy Plugin	com.atlassian.jira.plugins.jira-password-policy-plugin	2.1.0	2020-03-05 22:51:31.749+00
10024	ROME: RSS/Atom syndication and publishing tools	com.springsource.com.sun.syndication-0.9.0	0.9.0	2020-03-05 22:51:31.75+00
10025	JIRA iCalendar Plugin	com.atlassian.jira.extra.jira-ical-feed	1.5.0	2020-03-05 22:51:31.751+00
10026	Jira Drag and Drop Attachment Plugin	com.atlassian.jira.plugins.jira-dnd-attachment-plugin	5.0.2	2020-03-05 22:51:31.752+00
10027	Atlassian Jira - Plugins - Post setup announcements plugin	com.atlassian.jira.jira-postsetup-announcements-plugin	8.5.3	2020-03-05 22:51:31.753+00
10028	Streams Inline Actions Plugin	com.atlassian.streams.actions	8.1.7	2020-03-05 22:51:31.754+00
10029	JIRA Software Spanish (Spain) Language Pack	tac.jira software.languages.es_ES	8.6.0.v20190920191956	2020-03-05 22:51:31.755+00
10030	JIRA Core Swedish (Sweden) Language Pack	tac.jira core.languages.sv_SE	8.6.0.v20190920192957	2020-03-05 22:51:31.757+00
10031	Comment Panel Plugin	com.atlassian.jira.plugin.system.comment-panel	1.0	2020-03-05 22:51:31.758+00
10032	Atlassian Jira - Plugins - Development Integration Plugin	com.atlassian.jira.plugins.jira-development-integration-plugin	5.5.3	2020-03-05 22:51:31.759+00
10033	Atlassian Remote Event Consumer Plugin	com.atlassian.plugins.atlassian-remote-event-consumer-plugin	6.1.0	2020-03-05 22:51:31.76+00
10034	scala-2.11-provider-plugin	com.atlassian.scala.plugins.scala-2.11-provider-plugin	0.13	2020-03-05 22:51:31.761+00
10035	JIRA Workflow Transition Tabs	com.atlassian.jira.plugin.system.workfloweditor.transition.tabs	1.0	2020-03-05 22:51:31.762+00
10036	Jira inform - batching plugin	com.atlassian.jira.plugins.inform.batching-plugin	1.3.3	2020-03-05 22:51:31.763+00
10037	JIRA Core Czech (Czech Republic) Language Pack	tac.jira core.languages.cs_CZ	8.6.0.v20190920192957	2020-03-05 22:51:31.764+00
10038	Renderer Plugin	com.atlassian.jira.plugin.system.jirarenderers	1.0	2020-03-05 22:51:31.765+00
10039	Atlassian Gadgets OAuth Service Provider Plugin	com.atlassian.gadgets.oauth.serviceprovider	4.3.10	2020-03-05 22:51:31.766+00
10040	JIRA Software Finnish (Finland) Language Pack	tac.jira software.languages.fi_FI	8.6.0.v20190920191956	2020-03-05 22:51:31.767+00
10041	JIRA Core Japanese (Japan) Language Pack	tac.jira core.languages.ja_JP	8.6.0.v20190920192957	2020-03-05 22:51:31.768+00
10042	JIRA Core Italian (Italy) Language Pack	tac.jira core.languages.it_IT	8.6.0.v20190920192957	2020-03-05 22:51:31.769+00
10043	Jira for Software Plugin	com.atlassian.jira.plugins.jira-software-plugin	8.5.3	2020-03-05 22:51:31.771+00
10044	JIRA Core Polish (Poland) Language Pack	tac.jira core.languages.pl_PL	8.6.0.v20190920192957	2020-03-05 22:51:31.772+00
10045	Atlassian Jira - Plugins - Admin Navigation Component	com.atlassian.jira.jira-admin-navigation-plugin	8.5.3	2020-03-05 22:51:31.773+00
10046	JIRA Software Danish (Denmark) Language Pack	tac.jira software.languages.da_DK	8.6.0.v20190920191956	2020-03-05 22:51:31.774+00
10047	JIRA Core German (Germany) Language Pack	tac.jira core.languages.de_DE	8.6.0.v20190920192957	2020-03-05 22:51:31.775+00
10048	Atlassian Plugins - Web Resources - Implementation Plugin	com.atlassian.plugins.atlassian-plugins-webresource-plugin	4.1.3	2020-03-05 22:51:31.776+00
10049	Preset Filters Sections	jira.webfragments.preset.filters	1.0	2020-03-05 22:51:31.777+00
10050	Atlassian Jira - Plugins - Project Config Plugin	com.atlassian.jira.jira-project-config-plugin	8.5.3	2020-03-05 22:51:31.778+00
10051	Crowd System Password Encoders	crowd.system.passwordencoders	1.0	2020-03-05 22:51:31.779+00
10052	Atlassian UI Plugin	com.atlassian.auiplugin	8.5.0	2020-03-05 22:51:31.78+00
10053	Atlassian Jira - Plugins - Share Content Component	com.atlassian.jira.jira-share-plugin	8.5.3	2020-03-05 22:51:31.781+00
10054	Atlassian Jira - Plugins - Statistics plugin	com.atlassian.jira.jira-statistics-plugin	8.5.3	2020-03-05 22:51:31.782+00
10055	Atlassian Jira - Plugins - Remote Jira Link	com.atlassian.jira.jira-issue-link-remote-jira-plugin	8.5.3	2020-03-05 22:51:31.783+00
10056	Functional Extensions Guava Inter-Ops	io.atlassian.fugue.guava-4.7.2	4.7.2	2020-03-05 22:51:31.784+00
10057	Remote Link Aggregator Plugin	com.atlassian.plugins.remote-link-aggregator-plugin	3.0.0	2020-03-05 22:51:31.785+00
10058	Atlassian HealthCheck Common Module	com.atlassian.healthcheck.atlassian-healthcheck	6.0.0	2020-03-05 22:51:31.787+00
10059	Workbox - Common Plugin	com.atlassian.mywork.mywork-common-plugin	7.0.1	2020-03-05 22:51:31.788+00
10060	Jira Workflow Sharing Plugin	com.atlassian.jira.plugins.workflow.sharing.jira-workflow-sharing-plugin	2.2.3	2020-03-05 22:51:31.789+00
10061	Entity property conditions	system.entity.property.conditions	1.0	2020-03-05 22:51:31.79+00
10062	org.osgi:org.osgi.service.cm	org.osgi.service.cm-1.5.0.201505202024	1.5.0.201505202024	2020-03-05 22:51:31.791+00
10063	JIRA Software Korean (South Korea) Language Pack	tac.jira software.languages.ko_KR	8.6.0.v20190920191956	2020-03-05 22:51:31.792+00
10064	Atlassian Jira - Plugins - APDEX	com.atlassian.jira.jira-apdex-plugin	8.5.3	2020-03-05 22:51:31.793+00
10065	JQL Functions	jira.jql.function	1.0	2020-03-05 22:51:31.794+00
10066	Atlassian Soy - Plugin	com.atlassian.soy.soy-template-plugin	5.0.0	2020-03-05 22:51:31.795+00
10067	JIRA Software Icelandic (Iceland) Language Pack	tac.jira software.languages.is_IS	8.6.0.v20190920191956	2020-03-05 22:51:31.796+00
10068	Mobile Plugin for Jira	com.atlassian.jira.mobile.jira-mobile-rest	1.0.5	2020-03-05 22:51:31.797+00
10069	atlassian-failure-cache-plugin	com.atlassian.atlassian-failure-cache-plugin	2.0.0	2020-03-05 22:51:31.798+00
10070	Atlassian Jira - Plugins - View Issue Panels	com.atlassian.jira.jira-view-issue-plugin	8.5.3	2020-03-05 22:51:31.8+00
10071	JIRA Footer	jira.footer	1.0	2020-03-05 22:51:31.801+00
10072	Applinks - Plugin - Core	com.atlassian.applinks.applinks-plugin	7.0.0-MNSTR-2473	2020-03-05 22:51:31.802+00
10073	Atlassian Whitelist API Plugin	com.atlassian.plugins.atlassian-whitelist-api-plugin-4.0.7	4.0.7	2020-03-05 22:51:31.803+00
10074	Jira inform - batchers	com.atlassian.jira.plugins.inform.batchers	1.3.3	2020-03-05 22:51:31.804+00
10075	jira-optimizer-plugin	com.atlassian.jira.plugins.jira-optimizer-plugin	2.0.13	2020-03-05 22:51:31.805+00
10076	Analytics Whitelist Plugin	com.atlassian.analytics.analytics-whitelist	3.68	2020-03-05 22:51:31.806+00
10077	Atlassian Jira - Plugins - Invite User	com.atlassian.jira.jira-invite-user-plugin	2.2.6	2020-03-05 22:51:31.807+00
10078	JIRA Software Norwegian (Norway) Language Pack	tac.jira software.languages.no_NO	8.6.0.v20190920191956	2020-03-05 22:51:31.808+00
10079	Applinks - Plugin - Basic Authentication	com.atlassian.applinks.applinks-basicauth-plugin	7.0.0-MNSTR-2473	2020-03-05 22:51:31.809+00
10080	Atlassian Jira - Plugins - Quick Search	com.atlassian.jira.plugins.jira-quicksearch-plugin	8.5.3	2020-03-05 22:51:31.81+00
10081	User Profile Links	jira.webfragments.user.profile.links	1.0	2020-03-05 22:51:31.811+00
10082	JIRA Software Italian (Italy) Language Pack	tac.jira software.languages.it_IT	8.6.0.v20190920191956	2020-03-05 22:51:31.812+00
10083	Admin Menu Sections	jira.webfragments.admin	1.0	2020-03-05 22:51:31.813+00
10084	wiki-editor	com.atlassian.jira.plugins.jira-wiki-editor	4.1.8	2020-03-05 22:51:31.814+00
10085	Atlassian OAuth Service Provider SPI	com.atlassian.oauth.atlassian-oauth-service-provider-spi	4.0.2	2020-03-05 22:51:31.816+00
10086	Top Navigation Bar	jira.top.navigation.bar	1.0	2020-03-05 22:51:31.817+00
10087	Apache Apache HttpClient OSGi bundle	org.apache.httpcomponents.httpclient-4.5.5	4.5.5	2020-03-05 22:51:31.818+00
10088	Applinks - Plugin - Trusted Apps	com.atlassian.applinks.applinks-trustedapps-plugin	7.0.0-MNSTR-2473	2020-03-05 22:51:31.819+00
10089	Wiki Renderer Macros Plugin	com.atlassian.jira.plugin.system.renderers.wiki.macros	1.0	2020-03-05 22:51:31.82+00
10090	Issue Views Plugin	jira.issueviews	1.0	2020-03-05 22:51:31.821+00
10091	JIRA Core Icelandic (Iceland) Language Pack	tac.jira core.languages.is_IS	8.6.0.v20190920192957	2020-03-05 22:51:31.822+00
10092	Streams SPI	com.atlassian.streams.streams-spi	8.1.7	2020-03-05 22:51:31.823+00
10093	Jira inform - event plugin	com.atlassian.jira.plugins.inform.event-plugin	1.3.3	2020-03-05 22:51:31.824+00
10094	Applinks - Plugin - CORS	com.atlassian.applinks.applinks-cors-plugin	7.0.0-MNSTR-2473	2020-03-05 22:51:31.826+00
10095	Streams Third Party Provider Plugin	com.atlassian.streams.streams-thirdparty-plugin	8.1.7	2020-03-05 22:51:31.827+00
10096	Atlassian OAuth Service Provider Plugin	com.atlassian.oauth.serviceprovider	4.0.2	2020-03-05 22:51:31.828+00
10097	Atlassian Jira - Plugins - Common AppLinks Based Issue Link Plugin	com.atlassian.jira.jira-issue-link-applinks-common-plugin	8.5.3	2020-03-05 22:51:31.829+00
10098	Functional Extensions	io.atlassian.fugue-4.7.2	4.7.2	2020-03-05 22:51:31.83+00
10099	jira-webresources-plugin	jira.webresources	1.0	2020-03-05 22:51:31.831+00
10100	Embedded Gadgets Plugin	com.atlassian.gadgets.embedded	4.3.10	2020-03-05 22:51:31.832+00
10101	Atlassian Jira - Plugins - Core Reports	com.atlassian.jira.jira-core-reports-plugin	8.5.3	2020-03-05 22:51:31.835+00
10102	Streams Plugin	com.atlassian.streams	8.1.7	2020-03-05 22:51:31.836+00
10103	Browse Project Operations Sections	jira.webfragments.browse.project.links	1.0	2020-03-05 22:51:31.837+00
10104	Jira Issue Collector Plugin	com.atlassian.jira.collector.plugin.jira-issue-collector-plugin	3.1.0	2020-03-05 22:51:31.838+00
10105	Atlassian Whitelist Core Plugin	com.atlassian.plugins.atlassian-whitelist-core-plugin	4.0.7	2020-03-05 22:51:31.839+00
10106	JIRA Core Russian (Russia) Language Pack	tac.jira core.languages.ru_RU	8.6.0.v20190920192957	2020-03-05 22:51:31.841+00
10107	ICU4J	com.atlassian.bundles.icu4j-3.8.0.1	3.8.0.1	2020-03-05 22:51:31.842+00
10108	Streams Core Plugin	com.atlassian.streams.core	8.1.7	2020-03-05 22:51:31.843+00
10109	Atlassian Jira - Plugins - WebHooks Plugin	com.atlassian.jira.plugins.webhooks.jira-webhooks-plugin	8.5.3	2020-03-05 22:51:31.844+00
10110	Issue Operations Plugin	com.atlassian.jira.plugin.system.issueoperations	1.0	2020-03-05 22:51:31.845+00
10111	Gadget Directory Plugin	com.atlassian.gadgets.directory	4.3.10	2020-03-05 22:51:31.846+00
10112	Attach Image for JIRA	com.atlassian.plugins.jira-html5-attach-images	4.0.0	2020-03-05 22:51:31.847+00
10113	Jira Monitoring Plugin	com.atlassian.jira.jira-monitoring-plugin	05.7.3	2020-03-05 22:51:31.848+00
10114	Atlassian Hipchat Integration Plugin	com.atlassian.plugins.base-hipchat-integration-plugin	8.4.2	2020-03-05 22:51:31.849+00
10115	Atlassian Jira - Plugins - Project Centric Issue Navigator	com.atlassian.jira.jira-projects-issue-navigator	9.1.16	2020-03-05 22:51:31.85+00
10116	jira-importers-plugin	com.atlassian.jira.plugins.jira-importers-plugin	9.1.2	2020-03-05 22:51:31.851+00
10117	Atlassian Plugins - JavaScript libraries	com.atlassian.plugin.jslibs	1.4.1	2020-03-05 22:51:31.852+00
10118	JIRA Software Portuguese (Brazil) Language Pack	tac.jira software.languages.pt_BR	8.6.0.v20190920191956	2020-03-05 22:51:31.854+00
10119	Jira Time Zone Detection plugin	com.atlassian.jira.jira-tzdetect-plugin	3.0.3	2020-03-05 22:51:31.855+00
10120	Hipchat for Jira	com.atlassian.labs.hipchat.hipchat-for-jira-plugin	8.4.2	2020-03-05 22:51:31.856+00
10121	Atlassian Jira - Plugins - Diagnostics Plugin	com.atlassian.jira.diagnostics	8.5.3	2020-03-05 22:51:31.857+00
10122	JIRA Core Dutch (Netherlands) Language Pack	tac.jira core.languages.nl_NL	8.6.0.v20190920192957	2020-03-05 22:51:31.858+00
10123	JIRA Core Estonian (Estonia) Language Pack	tac.jira core.languages.et_EE	8.6.0.v20190920192957	2020-03-05 22:51:31.859+00
10124	JSON Library	com.atlassian.bundles.json-20070829.0.0.1	20070829.0.0.1	2020-03-05 22:51:31.86+00
10125	JDOM DOM Processor	com.springsource.org.jdom-1.0.0	1.0.0	2020-03-05 22:51:31.861+00
10126	JIRA Core Chinese (China) Language Pack	tac.jira core.languages.zh_CN	8.6.0.v20190920192957	2020-03-05 22:51:31.862+00
10127	Atlassian Jira - Plugins - Feedback Plugin	com.atlassian.feedback.jira-feedback-plugin	8.5.3	2020-03-05 22:51:31.863+00
10128	ActiveObjects Plugin - OSGi Bundle	com.atlassian.activeobjects.activeobjects-plugin	3.0.1	2020-03-05 22:51:31.864+00
10129	Atlassian Jira - Plugins - Header Plugin	com.atlassian.jira.jira-header-plugin	8.5.3	2020-03-05 22:51:31.865+00
10130	Issue Tab Panels Plugin	com.atlassian.jira.plugin.system.issuetabpanels	1.0	2020-03-05 22:51:31.866+00
10131	JIRA Feature Keys	jira.feature.keys	1.0	2020-03-05 22:51:31.867+00
10132	JIRA Streams Inline Actions Plugin	com.atlassian.streams.jira.inlineactions	8.1.7	2020-03-05 22:51:31.868+00
10133	Atlassian Jira - Plugins - Application Properties	com.atlassian.jira.jira-application-properties-plugin	8.5.3	2020-03-05 22:51:31.869+00
10134	Atlassian Jira - Plugins - Gadgets Plugin	com.atlassian.jira.gadgets	8.5.3	2020-03-05 22:51:31.871+00
10135	Atlassian Jira - Plugins - Analytics whitelist	com.atlassian.jira.jira-analytics-whitelist-plugin	8.5.3	2020-03-05 22:51:31.872+00
10136	jira-inline-issue-create-plugin	com.atlassian.jira.plugins.inline-create.jira-inline-issue-create-plugin	2.0.16	2020-03-05 22:51:31.873+00
10137	Functional Extensions Scala Inter-Ops	io.atlassian.fugue.scala-4.7.2	4.7.2	2020-03-05 22:51:31.874+00
10138	Workbox - JIRA Provider Plugin	com.atlassian.mywork.mywork-jira-provider-plugin	7.0.1	2020-03-05 22:51:31.875+00
10139	JIRA Core Korean (South Korea) Language Pack	tac.jira core.languages.ko_KR	8.6.0.v20190920192957	2020-03-05 22:51:31.876+00
10140	Content Link Resolvers Plugin	com.atlassian.jira.plugin.wiki.contentlinkresolvers	1.0	2020-03-05 22:51:31.877+00
10141	Atlassian Application Manager plugin	com.atlassian.upm.upm-application-plugin	4.0.8	2020-03-05 22:51:31.878+00
10142	JIRA Core Spanish (Spain) Language Pack	tac.jira core.languages.es_ES	8.6.0.v20190920192957	2020-03-05 22:51:31.879+00
10143	JIRA Software Russian (Russia) Language Pack	tac.jira software.languages.ru_RU	8.6.0.v20190920191956	2020-03-05 22:51:31.88+00
10144	JIRA Software Swedish (Sweden) Language Pack	tac.jira software.languages.sv_SE	8.6.0.v20190920191956	2020-03-05 22:51:31.881+00
10145	Atlassian JIRA - Plugins - Credits Plugin	com.atlassian.jira.jira-credits-plugin	8.5.3	2020-03-05 22:51:31.882+00
10146	Jira Mobile	com.atlassian.jira.mobile	3.2.8	2020-03-05 22:51:31.883+00
10147	Atlassian Jira - Plugins - Onboarding	com.atlassian.jira.jira-onboarding-assets-plugin	8.5.3	2020-03-05 22:51:31.884+00
10148	FishEye Plugin	com.atlassian.jirafisheyeplugin	8.5.3	2020-03-05 22:51:31.885+00
10149	JIRA Software French (France) Language Pack	tac.jira software.languages.fr_FR	8.6.0.v20190920191956	2020-03-05 22:51:31.886+00
10150	Atlassian JIRA - Admin Helper Plugin	com.atlassian.jira.plugins.jira-admin-helper-plugin	5.0.1	2020-03-05 22:51:31.887+00
10151	Atlassian browser metrics plugin	com.atlassian.plugins.browser.metrics.browser-metrics-plugin	7.0.1	2020-03-05 22:51:31.888+00
10152	JIRA Software German (Germany) Language Pack	tac.jira software.languages.de_DE	8.6.0.v20190920191956	2020-03-05 22:51:31.889+00
10153	Atlassian Jira - Plugins - Global Issue Navigator	com.atlassian.jira.jira-issue-nav-plugin	9.1.16	2020-03-05 22:51:31.89+00
10154	User Navigation Bar Sections	jira.webfragments.user.navigation.bar	1.0	2020-03-05 22:51:31.891+00
10155	Atlassian Troubleshooting and Support Tools	com.atlassian.troubleshooting.plugin-jira	1.19.1	2020-03-05 22:51:31.892+00
10156	Neko HTML	com.atlassian.bundles.nekohtml-1.9.12.1	1.9.12.1	2020-03-05 22:51:31.893+00
10157	JIRA Global Permissions	jira.system.global.permissions	1.0	2020-03-05 22:51:31.894+00
10158	JIRA Software Slovak (Slovakia) Language Pack	tac.jira software.languages.sk_SK	8.6.0.v20190920191956	2020-03-05 22:51:31.895+00
10159	Atlassian Template Renderer API	com.atlassian.templaterenderer.api	4.0.0	2020-03-05 22:51:31.896+00
10160	JIRA Core Danish (Denmark) Language Pack	tac.jira core.languages.da_DK	8.6.0.v20190920192957	2020-03-05 22:51:31.897+00
10161	Atlassian JIRA - Plugins - File viewer plugin	com.atlassian.jira.jira-fileviewer-plugin	8.0.0	2020-03-05 22:51:31.898+00
10162	Atlassian Pretty URLs Plugin	com.atlassian.prettyurls.atlassian-pretty-urls-plugin	3.0.0	2020-03-05 22:51:31.899+00
10163	JIRA Attachment Archive File Processors	jira.system.attachment.processors	1.0	2020-03-05 22:51:31.901+00
10164	Atlassian Jira - Plugins - Admin Upgrades	com.atlassian.jira.jira-admin-updates-plugin	8.5.3	2020-03-05 22:51:31.902+00
10165	jira-ui	com.atlassian.jira.plugins.jira-ui	0.2.2	2020-03-05 22:51:31.903+00
10166	Atlassian Universal Plugin Manager Plugin	com.atlassian.upm.atlassian-universal-plugin-manager-plugin	4.0.8	2020-03-05 22:51:31.904+00
10167	SAML for Atlassian Data Center	com.atlassian.plugins.authentication.atlassian-authentication-plugin	3.1.1	2020-03-05 22:51:31.905+00
10168	Atlassian Whitelist UI Plugin	com.atlassian.plugins.atlassian-whitelist-ui-plugin	4.0.7	2020-03-05 22:51:31.906+00
10169	JIRA Core Portuguese (Brazil) Language Pack	tac.jira core.languages.pt_BR	8.6.0.v20190920192957	2020-03-05 22:51:31.907+00
10170	Atlassian Jira - Plugins - Frontend Features	com.atlassian.jira.jira-frontend-plugin	8.5.3	2020-03-05 22:51:31.908+00
10171	Redmine Importers Plugin for JIM	com.atlassian.jira.plugins.jira-importers-redmine-plugin	2.2.0	2020-03-05 22:51:31.909+00
10172	jira-capabilities-plugin	jira.capabilities	1.0	2020-03-05 22:51:31.91+00
10173	Atlassian JIRA - Plugins - Quick Edit Plugin	com.atlassian.jira.jira-quick-edit-plugin	4.0.0	2020-03-05 22:51:31.911+00
10174	Soy Function Plugin	com.atlassian.jira.plugin.system.soyfunction	1.0	2020-03-05 22:51:31.912+00
10175	Universal Plugin Manager - Role-Based Licensing Implementation Plugin	com.atlassian.upm.role-based-licensing-plugin	4.0.8	2020-03-05 22:51:31.913+00
10176	Jira Core Project Templates Plugin	com.atlassian.jira-core-project-templates	7.0.4	2020-03-05 22:51:31.914+00
10177	Atlassian OAuth Admin Plugin	com.atlassian.oauth.admin	4.0.2	2020-03-05 22:51:31.915+00
10178	Atlassian REST - Module Types	com.atlassian.plugins.rest.atlassian-rest-module	6.0.2	2020-03-05 22:51:31.916+00
10179	Crowd REST API	crowd-rest-application-management	1.0	2020-03-05 22:51:31.917+00
10180	jira-issue-nav-components	com.atlassian.jira.jira-issue-nav-components	9.1.16	2020-03-05 22:51:31.918+00
10181	jquery	com.atlassian.plugins.jquery	2.2.4.7	2020-03-05 22:51:31.919+00
10182	Atlassian Jira - Plugins - Auditing Plugin [Audit Log]	com.atlassian.jira.plugins.jira-auditing-plugin	8.5.3	2020-03-05 22:51:31.92+00
10183	JIRA Remote Link Aggregator Plugin	com.atlassian.plugins.jira-remote-link-aggregator-plugin	3.0.0	2020-03-05 22:51:31.921+00
10184	Streams API	com.atlassian.streams.streams-api	8.1.7	2020-03-05 22:51:31.923+00
10185	Atlassian Jira - Plugins - Atlassian Notifications Plugin	com.atlassian.jira.jira-whisper-plugin	8.5.3	2020-03-05 22:51:31.924+00
10186	Atlassian HTTP Client, Apache HTTP components impl	com.atlassian.httpclient.atlassian-httpclient-plugin	2.0.0	2020-03-05 22:51:31.925+00
10187	Jira DVCS Connector Plugin	com.atlassian.jira.plugins.jira-bitbucket-connector-plugin	5.2.6	2020-03-05 22:51:31.926+00
10188	JIRA Software Hungarian (Hungary) Language Pack	tac.jira software.languages.hu_HU	8.6.0.v20190920191956	2020-03-05 22:51:31.927+00
10189	Asana Importers Plugin for JIM	com.atlassian.jira.plugins.jira-importers-asana-plugin	2.1.0	2020-03-05 22:51:31.928+00
10190	Atlassian Awareness Capability	com.atlassian.plugins.atlassian-awareness-capability	0.0.6	2020-03-05 22:51:31.929+00
10191	Atlassian Plugins - Web Resources REST	com.atlassian.plugins.atlassian-plugins-webresource-rest	4.1.3	2020-03-05 22:51:31.93+00
10192	Custom Field Types & Searchers	com.atlassian.jira.plugin.system.customfieldtypes	1.0	2020-03-05 22:51:31.931+00
10193	Project Creation Capability Product REST Plugin	com.atlassian.plugins.atlassian-project-creation-plugin	4.0.0-74bdd7a6fa	2020-03-05 22:51:31.932+00
10194	Atlassian Jira - Plugins - REST Plugin	com.atlassian.jira.rest	8.5.3	2020-03-05 22:51:31.933+00
10195	Atlassian Spring Scanner Runtime	com.atlassian.plugin.atlassian-spring-scanner-runtime	2.1.13	2020-03-05 22:51:31.934+00
10196	JIRA Software Czech (Czech Republic) Language Pack	tac.jira software.languages.cs_CZ	8.6.0.v20190920191956	2020-03-05 22:51:31.935+00
10197	Opensocial Plugin	com.atlassian.gadgets.opensocial	4.3.10	2020-03-05 22:51:31.936+00
10198	Atlassian Jira - Plugins - Confluence Link	com.atlassian.jira.jira-issue-link-confluence-plugin	8.5.3	2020-03-05 22:51:31.937+00
10199	ROME, RSS and atOM utilitiEs for Java	rome.rome-1.0	1.0	2020-03-05 22:51:31.938+00
10200	User Format	jira.user.format	1.0	2020-03-05 22:51:31.94+00
10201	JIRA Core Finnish (Finland) Language Pack	tac.jira core.languages.fi_FI	8.6.0.v20190920192957	2020-03-05 22:51:31.943+00
10202	JIRA Software Japanese (Japan) Language Pack	tac.jira software.languages.ja_JP	8.6.0.v20190920191956	2020-03-05 22:51:31.944+00
10203	View Project Operations Sections	jira.webfragments.view.project.operations	1.0	2020-03-05 22:51:31.945+00
10204	atlassian-servlet-plugin	com.atlassian.web.atlassian-servlet-plugin	5.2.0	2020-03-05 22:51:31.946+00
10205	Apache Apache HttpCore OSGi bundle	org.apache.httpcomponents.httpcore-4.4.9	4.4.9	2020-03-05 22:51:31.947+00
10206	Crowd REST API	crowd-rest-plugin	1.0	2020-03-05 22:51:31.948+00
10207	JIRA Software Polish (Poland) Language Pack	tac.jira software.languages.pl_PL	8.6.0.v20190920191956	2020-03-05 22:51:31.949+00
10208	Atlassian OAuth Consumer SPI	com.atlassian.oauth.atlassian-oauth-consumer-spi	4.0.2	2020-03-05 22:51:31.95+00
10209	Atlassian Jira - Plugins - Post-Upgrade Landing Page	com.atlassian.jira.plugins.jira-post-upgrade-landing-page-plugin	8.5.3	2020-03-05 22:51:31.951+00
10210	JIRA Core Hungarian (Hungary) Language Pack	tac.jira core.languages.hu_HU	8.6.0.v20190920192957	2020-03-05 22:51:31.952+00
10211	Atlassian Spring Scanner Annotations	com.atlassian.plugin.atlassian-spring-scanner-annotation	2.1.13	2020-03-05 22:51:31.953+00
10212	jackson-module-scala-2.10-provider	com.atlassian.scala.plugins.jackson-module-scala-2.10-provider-plugin	0.5	2020-03-05 22:51:31.954+00
10213	User anonymization in Jira Core	com.atlassian.jira.user.anonymize	1.0	2020-03-05 22:51:31.955+00
10214	Rich Text Editor for JIRA	com.atlassian.jira.plugins.jira-editor-plugin	4.1.8	2020-03-05 22:51:31.956+00
10215	Web Resources Plugin Jira Core	jira.core	1.0	2020-03-05 22:51:31.957+00
10216	Workflow Plugin	com.atlassian.jira.plugin.system.workflow	1.0	2020-03-05 22:51:31.958+00
10217	jira-importers-trello-plugin	com.atlassian.jira.plugins.jira-importers-trello-plugin	3.1.0	2020-03-05 22:51:31.959+00
10218	JIRA Software Dutch (Netherlands) Language Pack	tac.jira software.languages.nl_NL	8.6.0.v20190920191956	2020-03-05 22:51:31.96+00
10219	Icon Types Plugin	jira.icontypes	1.0	2020-03-05 22:51:31.961+00
10220	JIRA Core Norwegian (Norway) Language Pack	tac.jira core.languages.no_NO	8.6.0.v20190920192957	2020-03-05 22:51:31.962+00
10221	Atlassian WebHooks Plugin	com.atlassian.webhooks.atlassian-webhooks-plugin	3.3.0	2020-03-05 22:51:31.963+00
10222	Help Paths Plugin	jira.help.paths	1.0	2020-03-05 22:51:31.964+00
10223	Jira Base URL Plugin	com.atlassian.jira.jira-baseurl-plugin	3.2.0	2020-03-05 22:51:31.965+00
10224	Atlassian Jira - Plugins - Look And Feel Logo Upload Plugin	com.atlassian.jira.lookandfeel	8.5.3	2020-03-05 22:51:31.966+00
10225	Issue Status Plugin	com.atlassian.plugins.issue-status-plugin	2.0.2	2020-03-05 22:51:31.967+00
10226	Gadget Spec Publisher Plugin	com.atlassian.gadgets.publisher	4.3.10	2020-03-05 22:51:31.968+00
10227	Atlassian Jira - Plugins - OAuth Consumer SPI	com.atlassian.jira.oauth.consumer	8.5.3	2020-03-05 22:51:31.969+00
10228	JIRA Agile	com.pyxis.greenhopper.jira	8.5.3	2020-03-05 22:51:31.971+00
10229	JIRA Software Estonian (Estonia) Language Pack	tac.jira software.languages.et_EE	8.6.0.v20190920191956	2020-03-05 22:51:31.972+00
10230	JIRA Core French (France) Language Pack	tac.jira core.languages.fr_FR	8.6.0.v20190920192957	2020-03-05 22:51:31.973+00
10231	Renderer Component Factories Plugin	com.atlassian.jira.plugin.wiki.renderercomponentfactories	1.0	2020-03-05 22:51:31.974+00
10232	Atlassian LESS Transformer Plugin	com.atlassian.plugins.less-transformer-plugin	4.0.0	2020-03-05 22:51:31.975+00
10233	Pocketknife Feature Flags Plugin	com.atlassian.pocketknife.featureflags-plugin	0.5.4	2020-03-05 22:51:31.976+00
10234	Functional Optics Library	io.atlassian.fugue.optics-4.7.2	4.7.2	2020-03-05 22:51:31.977+00
10235	Functional Extensions Retry Inter-Ops	io.atlassian.fugue.retry-4.7.2	4.7.2	2020-03-05 22:51:31.978+00
10236	Jira Bamboo Plugin	com.atlassian.jira.plugin.ext.bamboo	8.5.3	2020-03-05 22:51:31.979+00
10237	Atlassian Jira - Plugins - Transition Trigger Plugin	com.atlassian.jira.plugins.jira-transition-triggers-plugin	8.5.3	2020-03-05 22:51:31.98+00
10238	Atlassian Front-End Runtime Plugin	com.atlassian.frontend.atlassian-frontend-runtime-plugin	0.3.0	2020-03-05 22:51:31.981+00
10239	Atlassian Bot Session Killer	com.atlassian.labs.atlassian-bot-killer	1.7.9	2020-03-05 22:51:31.982+00
10240	User Profile Panels	jira.user.profile.panels	1.0	2020-03-05 22:51:31.983+00
10241	scala-2.10-provider-plugin	com.atlassian.scala.plugins.scala-2.10-provider-plugin	0.14	2020-03-05 22:51:31.984+00
10242	JIRA Software Romanian (Romania) Language Pack	tac.jira software.languages.ro_RO	8.6.0.v20190920191956	2020-03-05 22:51:31.985+00
10243	Atlassian Jira - Plugins - ActiveObjects SPI	com.atlassian.jira.jira-activeobjects-spi-plugin	8.5.3	2020-03-05 22:51:31.986+00
10244	JIRA Project Permissions	jira.system.project.permissions	1.0	2020-03-05 22:51:31.987+00
10245	Wallboard Plugin	com.atlassian.jirawallboard.atlassian-wallboard-plugin	3.0.3	2020-03-05 22:51:31.988+00
10246	Atlassian Jira - Plugins - User Profile Plugin	com.atlassian.jira.jira-user-profile-plugin	4.0.3	2020-03-05 22:51:31.989+00
10247	Project Templates Plugin	com.atlassian.jira.project-templates-plugin	7.0.4	2020-03-05 22:51:31.99+00
10248	Atlassian Notifications	com.atlassian.whisper.atlassian-whisper-plugin	2.0.6	2020-03-05 22:51:31.991+00
10249	Filter Deletion Warning Plugin	jira.filter.deletion.warning	1.0	2020-03-05 22:51:31.992+00
10250	JIRA Software Monitor Plugin	com.atlassian.jira.plugins.jira-software-monitor-plugin	8.5.3	2020-03-05 22:51:31.993+00
10251	English (United States) Language Pack	com.atlassian.jira.jira-languages.en_US	8.5.3	2020-03-05 22:51:31.995+00
10252	Atlassian Jira - Plugins - LESS integration	com.atlassian.jira.jira-less-integration	8.5.3	2020-03-05 22:51:31.996+00
10253	Atlassian Jira - Plugins - SAL Plugin	com.atlassian.sal.jira	8.5.3	2020-03-05 22:51:31.997+00
10254	Atlassian Diagnostics - Plugin	com.atlassian.diagnostics.atlassian-diagnostics-plugin	1.1.10	2020-03-05 22:51:31.998+00
10255	Atlassian Template Renderer Velocity 1.6 Plugin	com.atlassian.templaterenderer.atlassian-template-renderer-velocity1.6-plugin	4.0.0	2020-03-05 22:51:31.999+00
10256	Atlassian Jira - Plugins - Mail Plugin	com.atlassian.jira.jira-mail-plugin	11.0.5	2020-03-05 22:51:32.001+00
10257	Atlassian Jira - Plugins - Closure Template Renderer	com.atlassian.jira.jira-soy-plugin	8.5.3	2020-03-05 22:51:32.002+00
10258	Jira Workflow Designer Plugin	com.atlassian.jira.plugins.jira-workflow-designer	8.0.6	2020-03-05 22:51:32.003+00
10259	Gadget Dashboard Plugin	com.atlassian.gadgets.dashboard	4.3.10	2020-03-05 22:51:32.004+00
10260	Atlassian - Administration - Quick Search - JIRA	com.atlassian.administration.atlassian-admin-quicksearch-jira	2.3.3	2020-03-05 22:51:32.005+00
10261	Web Panel Plugin	jira.webpanels	1.0	2020-03-05 22:51:32.006+00
10262	Applinks - Plugin - OAuth	com.atlassian.applinks.applinks-oauth-plugin	7.0.0-MNSTR-2473	2020-03-05 22:51:32.007+00
10263	JIRA browser metrics integration plugin	com.atlassian.jira.plugins.jira-browser-metrics	2.0.14	2020-03-05 22:51:32.008+00
10264	JIRA Activity Stream Plugin	com.atlassian.streams.streams-jira-plugin	8.1.7	2020-03-05 22:51:32.009+00
10265	English (United Kingdom) Language Pack	com.atlassian.jira.jira-languages.en_UK	8.5.3	2020-03-05 22:51:32.01+00
10266	Static Assets (CDN) Plugin	com.atlassian.plugins.static-assets-url	1.0.5	2020-03-05 22:51:32.011+00
10267	JIRA Usage Hints	jira.usage.hints	1.0	2020-03-05 22:51:32.012+00
\.


--
-- TOC entry 5518 (class 0 OID 17234)
-- Dependencies: 304
-- Data for Name: portalpage; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.portalpage (id, username, pagename, description, sequence, fav_count, layout, ppversion) FROM stdin;
10000	\N	System Dashboard	\N	0	0	AA	0
\.


--
-- TOC entry 5519 (class 0 OID 17242)
-- Dependencies: 305
-- Data for Name: portletconfiguration; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.portletconfiguration (id, portalpage, portlet_id, column_number, positionseq, gadget_xml, color, dashboard_module_complete_key) FROM stdin;
10000	10000	\N	0	0	\N	\N	com.atlassian.jira.gadgets:introduction-dashboard-item
10002	10000	\N	1	0	rest/gadgets/1.0/g/com.atlassian.jira.gadgets:assigned-to-me-gadget/gadgets/assigned-to-me-gadget.xml	\N	\N
10003	10000	\N	1	1	rest/gadgets/1.0/g/com.atlassian.streams.streams-jira-plugin:activitystream-gadget/gadgets/activitystream-gadget.xml	\N	\N
\.


--
-- TOC entry 5520 (class 0 OID 17250)
-- Dependencies: 306
-- Data for Name: priority; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.priority (id, sequence, pname, description, iconurl, status_color) FROM stdin;
1	1	Highest	This problem will block progress.	/images/icons/priorities/highest.png	#ff7452
2	2	High	Serious problem that could block progress.	/images/icons/priorities/high.png	#ff8f73
3	3	Medium	Has the potential to affect progress.	/images/icons/priorities/medium.png	#ffab00
4	4	Low	Minor problem or easily worked around.	/images/icons/priorities/low.png	#0065ff
5	5	Lowest	Trivial problem with little or no impact on progress.	/images/icons/priorities/lowest.png	#2684ff
\.


--
-- TOC entry 5521 (class 0 OID 17258)
-- Dependencies: 307
-- Data for Name: productlicense; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.productlicense (id, license) FROM stdin;
10000	AAABjQ0ODAoPeNp9kk9PwkAQxe/9FJt40cM2BS0GkiZi2RhMW5CixsTLWAdZU7bN7Bbl29s/EFAqx92dN+83b/ZsvixYCMQcl3V6A/dq4PSZH89Z1+k61gchqmWW50h2IBNUGsW7NDJTnojmYjadjWNhRcXqDWmyeNRI2uMdy8+UgcREsEJP65U0yxswKWgtQdlJtrI+JYF9JJsWlCxB4wgMepU/dy6507O2zvNNjnVLfxKGYuaPh8HuSXznkjYHuivuuDsMEYJM2zlipDXSeOTdTsWEB+71I38Ohg/8JbzrNZA5Ze9FYuzqwHW2MF9AaJdd5Ro9QwWeKiuBwEdlkJrSuHjTCcm8DnAv/j/ZlvzbUigHLE0UqOSfJE4wHm1h61OGEoxHsYh40HGdfr9zfdm0ORpKRF5LWbtbbIAq5QJSjdaEPkBJDfXYw91mLJ+wvvr7D9KG7KkErQTdX/HURDlJvV3LCPdR35cMLN4ysPNqAtaMcPE6YGINaVEbNuRHyz2R/CHBoW7fszn/AB2dLg0wLAIUCRImOsLjnhv2BCuun+PgIzzo3r4CFFwbChJ6B8xDxq6poRgm6h9DVv8TX02j7
\.


--
-- TOC entry 5522 (class 0 OID 17266)
-- Dependencies: 308
-- Data for Name: project; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.project (id, pname, url, lead, description, pkey, pcounter, assigneetype, avatar, originalkey, projecttype) FROM stdin;
10000	tesr	\N	JIRAUSER10000	\N	TESR	23	3	10324	TESR	software
\.


--
-- TOC entry 5525 (class 0 OID 17287)
-- Dependencies: 311
-- Data for Name: project_key; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.project_key (id, project_id, project_key) FROM stdin;
10000	10000	TESR
\.


--
-- TOC entry 5523 (class 0 OID 17274)
-- Dependencies: 309
-- Data for Name: projectcategory; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.projectcategory (id, cname, description) FROM stdin;
\.


--
-- TOC entry 5524 (class 0 OID 17282)
-- Dependencies: 310
-- Data for Name: projectchangedtime; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.projectchangedtime (project_id, issue_changed_time) FROM stdin;
\.


--
-- TOC entry 5526 (class 0 OID 17292)
-- Dependencies: 312
-- Data for Name: projectrole; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.projectrole (id, name, description) FROM stdin;
10002	Administrators	A project role that represents administrators in a project
10100	Developers	\N
\.


--
-- TOC entry 5527 (class 0 OID 17300)
-- Dependencies: 313
-- Data for Name: projectroleactor; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.projectroleactor (id, pid, projectroleid, roletype, roletypeparameter) FROM stdin;
10002	\N	10002	atlassian-group-role-actor	jira-administrators
10100	10000	10002	atlassian-group-role-actor	jira-administrators
10101	10000	10100	atlassian-user-role-actor	JIRAUSER10000
\.


--
-- TOC entry 5566 (class 0 OID 17573)
-- Dependencies: 352
-- Data for Name: projectversion; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.projectversion (id, project, vname, description, sequence, released, archived, url, startdate, releasedate) FROM stdin;
10000	10000	Version 1.0	\N	1	true	\N	\N	\N	2020-02-27 11:18:55.569+00
10001	10000	Version 2.0	\N	2	\N	\N	\N	\N	2020-03-12 11:18:55.569+00
10002	10000	Version 3.0	\N	3	\N	\N	\N	\N	\N
\.


--
-- TOC entry 5503 (class 0 OID 17138)
-- Dependencies: 289
-- Data for Name: propertydata; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.propertydata (id, propertyvalue) FROM stdin;
\.


--
-- TOC entry 5504 (class 0 OID 17143)
-- Dependencies: 290
-- Data for Name: propertydate; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.propertydate (id, propertyvalue) FROM stdin;
10304	2020-03-05 22:58:18.68+00
\.


--
-- TOC entry 5505 (class 0 OID 17148)
-- Dependencies: 291
-- Data for Name: propertydecimal; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.propertydecimal (id, propertyvalue) FROM stdin;
\.


--
-- TOC entry 5506 (class 0 OID 17153)
-- Dependencies: 292
-- Data for Name: propertyentry; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.propertyentry (id, entity_name, entity_id, property_key, propertytype) FROM stdin;
6	jira.properties	1	jira.avatar.user.anonymous.id	5
7	jira.properties	1	jira.scheme.default.issue.type	5
9	jira.properties	1	jira.whitelist.disabled	1
10	jira.properties	1	jira.whitelist.rules	6
11	jira.properties	1	jira.option.timetracking	1
12	jira.properties	1	jira.timetracking.estimates.legacy.behaviour	1
1	jira.properties	1	jira.version.patched	5
2	jira.properties	1	jira.avatar.issuetype.subtask.default.id	5
3	jira.properties	1	jira.avatar.default.id	5
13	jira.properties	1	jira.version	5
4	jira.properties	1	jira.avatar.issuetype.default.id	5
14	jira.properties	1	jira.downgrade.minimum.version	5
5	jira.properties	1	jira.avatar.user.default.id	5
15	jira.properties	1	jira.option.allowunassigned	1
16	jira.properties	1	jira.path.index.use.default.directory	1
21	com.atlassian.jira.plugins.jira-workflow-designer	1	jira.workflow.layout:8a6044147cf2c19c02d099279cfbfd47	6
22	jira.properties	1	jira.onboarding.app_user.id.threshold	5
23	jira.properties	1	post.migration.page.displayed	1
10100	jira.properties	1	webwork.i18n.encoding	5
10101	jira.properties	1	jira.sid.key	5
10102	BambooServerProperties	1	bamboo.config.version	2
10103	GreenHopper.Run.History	1	lastRunVersion	6
10104	GreenHopper.Run.History	2	2020-03-05T22:51:22.957Z	6
10105	jira.properties	1	AO_550953_#	5
10106	jira.properties	1	AO_60DB71_#	5
10107	jira.properties	1	AO_E8B6CC_#	5
10108	jira.properties	1	dvcs.connector.bitbucket.url	5
10109	jira.properties	1	dvcs.connector.github.url	5
10110	jira.plugin.devstatus.datastorage	1	upgrade.state	5
10111	jira.properties	1	AO_4AEACD_#	5
10112	jira.properties	1	AO_587B34_#	5
10113	jira.properties	1	com.atlassian.jira.util.index.IndexingCounterManagerImpl.counterValue	3
10114	jira.properties	1	jira.monitoring.jmx.enabled	1
10115	ServiceConfig	10200	RETENTION_PERIOD	5
10116	ServiceConfig	10201	RETENTION_PERIOD	5
10117	jira.properties	1	jvm.system.timezone	5
10118	jira.properties	1	jira.webresource.superbatch.flushcounter	5
10119	jira.properties	1	jira.i18n.language.index	5
10120	jira.properties	1	jira.scheme.default.priority	5
10123	com.atlassian.jira.user.flag.FlagDismissalServiceImpl	1	com.atlassian.jira.flag.resets	6
10200	jira.properties	1	jira.webresource.flushcounter	5
10201	jira.properties	1	jira.mail.send.disabled	1
10202	jira.properties	1	mailsetting.jira.mail.send.disabled.modifiedBy	5
10203	jira.properties	1	mailsetting.jira.mail.send.disabled.modifiedDate	5
10204	jira.properties	1	jira.title	5
10205	jira.properties	1	jira.baseurl	5
10206	jira.properties	1	jira.mode	5
10207	jira.properties	1	jira.path.attachments	5
10208	jira.properties	1	jira.path.attachments.use.default.directory	1
10209	jira.properties	1	jira.option.allowattachments	1
10210	ServiceConfig	10001	USE_DEFAULT_DIRECTORY	5
10211	jira.properties	1	jira.path.backup	5
10212	jira.properties	1	jira.setup	5
10213	jira.properties	1	jira.initial.build.number	5
10214	jira.properties	1	jira.option.user.externalmanagement	1
10215	jira.properties	1	jira.option.voting	1
10216	jira.properties	1	jira.option.watching	1
10217	jira.properties	1	jira.option.issuelinking	1
10218	jira.properties	1	jira.option.emailvisible	5
10300	jira.properties	1	jira.option.allowsubtasks	1
10301	GreenHopper.properties	1	GreenHopper.LexoRank.Default.customfield.id	3
10302	GreenHopper.properties	1	GreenHopper.Sprint.Default.customfield.id	3
10303	admin.message.manager	1	user	5
10304	admin.message.manager	1	time	7
10305	admin.message.manager	1	task	5
10306	GreenHopper.properties	1	GreenHopper.Sprint.Index.Check.Necessary	5
10307	GreenHopper.properties	1	GreenHopper.EpicLink.Default.customfield.id	3
10308	GreenHopper.properties	1	GreenHopper.EpicStatus.Default.customfield.id	3
10309	GreenHopper.properties	1	GreenHopper.EpicLabel.Default.customfield.id	3
10310	GreenHopper.properties	1	GreenHopper.EpicColor.Default.customfield.id	3
10311	GreenHopper.properties	1	GreenHopper.JIRA60.Version.Migration	1
10312	GreenHopper.properties	1	JIRA.Software.Simplified.Workflow.Upgraded	1
10313	GreenHopper.properties	1	GreenHopper.Epic.Default.issuetype.id	5
10314	GreenHopper.properties	1	GreenHopper.Story.Default.issuetype.id	5
10315	GreenHopper.properties	1	GreenHopper.StoryPoints.Default.customfield.id	3
10316	GreenHopper.properties	1	GreenHopper.Epic.Default.linktype.id	3
10317	jira.properties	1	com.atlassian.upm.log.PluginSettingsAuditLogService:log:upm_audit_log_v3	5
10318	jira.properties	1	com.atlassian.sal.jira:build	5
10319	jira.properties	1	com.atlassian.plugins.atlassian-whitelist-api-plugin:whitelist.enabled	5
10320	jira.properties	1	com.atlassian.jira.gadgets:build	5
10321	jira.properties	1	com.atlassian.jira.plugins.jira-bitbucket-connector-plugin:build	5
10322	GreenHopper.UpgradeHistory	1	2020-03-05T22:58:19.042Z	6
10323	GreenHopper.UpgradeHistory	2	2020-03-05T22:58:19.047Z	6
10324	GreenHopper.properties	1	GreenHopper.Upgrade.Latest.Upgraded.Version	3
10325	jira.properties	1	com.pyxis.greenhopper.jira:build	5
10326	GreenHopper.UpgradeHistory	1	2020-03-05T22:58:19.053Z	6
10327	GreenHopper.UpgradeHistory	2	2020-03-05T22:58:19.067Z	6
10328	GreenHopper.UpgradeHistory	1	2020-03-05T22:58:19.073Z	6
10329	GreenHopper.UpgradeHistory	2	2020-03-05T22:58:19.076Z	6
10330	GreenHopper.UpgradeHistory	1	2020-03-05T22:58:19.082Z	6
10331	GreenHopper.UpgradeHistory	2	2020-03-05T22:58:19.105Z	6
10332	GreenHopper.UpgradeHistory	1	2020-03-05T22:58:19.111Z	6
10333	GreenHopper.UpgradeHistory	2	2020-03-05T22:58:19.123Z	6
10334	GreenHopper.UpgradeHistory	1	2020-03-05T22:58:19.128Z	6
10335	GreenHopper.UpgradeHistory	2	2020-03-05T22:58:19.131Z	6
10336	jira.properties	1	com.atlassian.crowd.embedded.admin:build	5
10337	jira.properties	1	com.atlassian.labs.hipchat.hipchat-for-jira-plugin:build	5
10338	jira.properties	1	com.atlassian.plugins.atlassian-whitelist-core-plugin:build	5
10339	jira.properties	1	com.atlassian.jira.plugin.ext.bamboo:build	5
10340	jira.properties	1	com.atlassian.jira.jira-mail-plugin:build	5
10341	jira.properties	1	com.atlassian.plugins.custom_apps.hasCustomOrder	5
10342	jira.properties	1	com.atlassian.plugins.atlassian-nav-links-plugin:build	5
10343	jira.properties	1	com.atlassian.jira.plugins.jira-workflow-designer:build	5
10344	jira.properties	1	com.atlassian.jira.plugins.webhooks.jira-webhooks-plugin:build	5
10345	jira.properties	1	com.atlassian.jirawallboard.atlassian-wallboard-plugin:build	5
10346	jira.properties	1	com.atlassian.upm.atlassian-universal-plugin-manager-plugin:build	5
10347	jira.properties	1	com.atlassian.jira.lookandfeel:isDefaultFavicon	5
10348	jira.properties	1	com.atlassian.jira.lookandfeel:usingCustomFavicon	5
10349	jira.properties	1	com.atlassian.jira.lookandfeel:customDefaultFaviconURL	5
10350	jira.properties	1	com.atlassian.jira.lookandfeel:customDefaultFaviconHiresURL	5
10351	jira.properties	1	com.atlassian.jira.lookandfeel:faviconWidth	5
10352	jira.properties	1	com.atlassian.jira.lookandfeel:faviconHeight	5
10353	jira.properties	1	com.atlassian.jira.lookandfeel:build	5
10354	jira.properties	1	com.atlassian.jira.project-templates-plugin:build	5
10355	jira.properties	1	com.atlassian.analytics.client.configuration..policy_acknowledged	5
10356	jira.properties	1	com.atlassian.analytics.client.configuration.uuid	5
10357	jira.properties	1	com.atlassian.analytics.client.configuration.serverid	5
10358	jira.properties	1	jira-header-plugin.studio-tab-migration-complete	5
10359	jira.properties	1	com.atlassian.streams.InlineActivityStream:loaded.from.jira.projects	5
10360	jira.properties	1	com.atlassian.upm:notifications:notification-edition.mismatch	5
10361	jira.properties	1	com.atlassian.upm:notifications:notification-evaluation.expired	5
10362	jira.properties	1	com.atlassian.upm:notifications:notification-evaluation.nearlyexpired	5
10363	jira.properties	1	com.atlassian.analytics.client.configuration..analytics_enabled	5
10364	jira.properties	1	com.atlassian.upm:notifications:notification-maintenance.expired	5
10365	jira.properties	1	com.atlassian.upm:notifications:notification-maintenance.nearlyexpired	5
10366	jira.properties	1	com.atlassian.upm:notifications:notification-license.expired	5
10367	jira.properties	1	com.atlassian.upm:notifications:notification-license.nearlyexpired	5
10368	jira.properties	1	com.atlassian.upm:notifications:notification-plugin.request	5
10369	fisheye-jira-plugin.properties	1	Upgrade Conditions Applied	5
10370	fisheye-jira-plugin.properties	1	FISH-375-fixed	5
10371	fisheye-jira-plugin.properties	1	fisheye.ual.migration.complete	5
10372	fisheye-jira-plugin.properties	1	fisheye.ual.crucible.enabled.property.fix.complete	5
10373	fisheye-jira-plugin.properties	1	Perforce Update Applied	5
10374	ApplicationUser	10000	jira.onboarding.first.use.flow.started	5
10375	jira.properties	1	com.atlassian.jira.onboarding.postsetup.AppPropertiesPostSetupAnnouncementStore.all	6
10376	jira.properties	1	com.atlassian.upm:notifications:notification-update	5
10377	jira.properties	1	com.atlassian.analytics.client.configuration..logged_base_analytics_data	5
10378	ApplicationUser	10000	newsletter.signup.first.view	3
10379	ApplicationUser	10000	jira.onboarding.first.use.flow.current.sequence	5
10380	ApplicationUser	10000	jira.onboarding.first.use.flow.completed	1
10381	jira.properties	1	com.pyxis.greenhopper.jira:default.permission.scheme.id	5
10382	ApplicationUser	10000	com.atlassian.jira.jira-projects-plugin:release-page.badge	2
10383	user.format.mapping	1	profileLink	5
10384	ApplicationUser	10000	last-visited-item.10000	5
10385	ApplicationUser	10000	com.atlassian.jira.flag.dismissals	6
10386	jira.properties	1	com.atlassian.whisper.plugin:ETag:messages	5
10387	jira.properties	1	com.atlassian.whisper.plugin:ETag:usermessages	5
10388	jira.properties	1	com.atlassian.whisper.plugin:last-successful-fetch-time	5
10389	jira.properties	1	com.atlassian.analytics.client.upload.last_date	5
\.


--
-- TOC entry 5507 (class 0 OID 17161)
-- Dependencies: 293
-- Data for Name: propertynumber; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.propertynumber (id, propertyvalue) FROM stdin;
9	0
11	1
12	0
15	1
16	1
23	1
10102	22
10201	0
10208	1
10209	1
10114	0
10214	0
10215	1
10216	1
10217	1
10113	1
10300	1
10301	10100
10302	10101
10307	10102
10308	10103
10309	10104
10310	10105
10311	1
10312	1
10315	10106
10316	10200
10324	51
10378	1583449111758
10380	1
10382	0
\.


--
-- TOC entry 5508 (class 0 OID 17166)
-- Dependencies: 294
-- Data for Name: propertystring; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.propertystring (id, propertyvalue) FROM stdin;
10204	Jira
10205	http://jirae-loadb-tm0dx5wikit8-1323618516.ap-southeast-2.elb.amazonaws.com
10206	private
10207	/media/atl/jira/shared/data/attachments
10211	/media/atl/jira/shared/export
10115	2880m
10116	2880m
10210	true
10117	Etc/UTC
10118	4
10212	true
10213	805003
10218	show
1	805003
13	8.5.3
14	7.1.2
10306	true
10313	10000
10314	10001
2	10316
3	10011
4	10300
5	10122
6	10123
7	10000
22	0
10100	UTF-8
10101	BPEO-L57U-WLAQ-YMG6
10105	1
10106	1
10303	
10305	Story Points Field Creation
10317	#java.util.List\n{"userKey":"JIRA","date":1583449098939,"i18nKey":"upm.auditLog.upm.startup","entryType":"UPM_STARTUP","params":[]}
10318	2
10320	1
10321	2
10108	https://bitbucket.org
10109	https://github.com
10107	16
10110	UPGRADED
10111	1
10112	4
10119	english-moderate-stemming
10120	10101
10360	#java.util.List\n
10361	#java.util.List\n
10202	
10203	1583448692460
10362	#java.util.List\n
10364	#java.util.List\n
10325	51
10336	3
10337	1
10319	true
10338	4
10339	1
10340	2
10341	false
10342	1
10343	1
10344	3
10345	6086
10346	5
10347	false
10348	false
10349	/favicon.ico
10350	/images/64jira.png
10351	64
10352	64
10200	6411e0087192541a09d88223fb51a6a0
10353	1
10354	2001
10355	true
10356	02f9776b-e6b8-4f3f-badc-dda928de002d
10357	BPEO-L57U-WLAQ-YMG6
10358	migrated
10359	true
10363	true
10369	1
10370	1
10371	1
10372	1
10373	1
10374	cyoaFirstUseFlow
10377	true
10379	cyoa:return
10381	10000
10383	jira.user.format:profile-link-user-format
10384	com.pyxis.greenhopper.jira:project-sidebar-plan-scrum
10386	W/"da39a3ee5e6b4b0d3255bfef95601890afd80709"
10365	#java.util.List\n
10387	W/"da39a3ee5e6b4b0d3255bfef95601890afd80709"
10366	#java.util.List\n
10367	#java.util.List\n
10388	1583711900150
10389	3/9/20 12:45 AM
10376	#java.util.List\ncom.atlassian.troubleshooting.plugin-jira
10368	#java.util.List\n
\.


--
-- TOC entry 5509 (class 0 OID 17174)
-- Dependencies: 295
-- Data for Name: propertytext; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.propertytext (id, propertyvalue) FROM stdin;
10	http://www.atlassian.com/*\n
21	{\n    "edgeMap": {\n        "1DEDB66F-FE5C-EDFD-54D0-4D19CDC8CECA": {\n            "actionId": 5,\n            "allPoints": [\n                {\n                    "positiveController": null,\n                    "x": 1806.5,\n                    "y": 434.0\n                },\n                {\n                    "positiveController": null,\n                    "x": 1801.0,\n                    "y": 115.0\n                }\n            ],\n            "controlPoints": [],\n            "endNodeId": "6DA64EEB-08FE-2870-C90C-4D19CDA2F72D",\n            "endPoint": {\n                "positiveController": null,\n                "x": 1801.0,\n                "y": 115.0\n            },\n            "endStepId": 4,\n            "id": "1DEDB66F-FE5C-EDFD-54D0-4D19CDC8CECA",\n            "label": "Resolve Issue",\n            "labelPoint": {\n                "positiveController": null,\n                "x": 1776.85,\n                "y": 355.25\n            },\n            "lineType": "straight",\n            "startNodeId": "A8B1A431-AC3A-6DCD-BFF0-4D19CDBCAADB",\n            "startPoint": {\n                "positiveController": null,\n                "x": 1806.5,\n                "y": 434.0\n            },\n            "startStepId": 5\n        },\n        "3DF7CEC8-9FBC-C0D0-AFB1-4D19CE6EA230": {\n            "actionId": 2,\n            "allPoints": [\n                {\n                    "positiveController": null,\n                    "x": 1469.5,\n                    "y": 113.0\n                },\n                {\n                    "positiveController": null,\n                    "x": 1614.0,\n                    "y": 226.0\n                }\n            ],\n            "controlPoints": [],\n            "endNodeId": "1C846CFB-4F0D-2F40-D0AE-4D19CDAF5D34",\n            "endPoint": {\n                "positiveController": null,\n                "x": 1614.0,\n                "y": 226.0\n            },\n            "endStepId": 6,\n            "id": "3DF7CEC8-9FBC-C0D0-AFB1-4D19CE6EA230",\n            "label": "Close Issue",\n            "labelPoint": {\n                "positiveController": null,\n                "x": 1492.25,\n                "y": 154.25\n            },\n            "lineType": "straight",\n            "startNodeId": "778534F4-7595-88B6-45E1-4D19CD518712",\n            "startPoint": {\n                "positiveController": null,\n                "x": 1469.5,\n                "y": 113.0\n            },\n            "startStepId": 1\n        },\n        "483797F1-1BF4-5E0F-86C6-4D19CE6023A2": {\n            "actionId": 5,\n            "allPoints": [\n                {\n                    "positiveController": null,\n                    "x": 1469.5,\n                    "y": 113.0\n                },\n                {\n                    "positiveController": null,\n                    "x": 1763.0,\n                    "y": 113.0\n                }\n            ],\n            "controlPoints": [],\n            "endNodeId": "6DA64EEB-08FE-2870-C90C-4D19CDA2F72D",\n            "endPoint": {\n                "positiveController": null,\n                "x": 1763.0,\n                "y": 113.0\n            },\n            "endStepId": 4,\n            "id": "483797F1-1BF4-5E0F-86C6-4D19CE6023A2",\n            "label": "Resolve Issue",\n            "labelPoint": {\n                "positiveController": null,\n                "x": 1551.0,\n                "y": 104.0\n            },\n            "lineType": "straight",\n            "startNodeId": "778534F4-7595-88B6-45E1-4D19CD518712",\n            "startPoint": {\n                "positiveController": null,\n                "x": 1469.5,\n                "y": 113.0\n            },\n            "startStepId": 1\n        },\n        "517D7F32-20FB-309E-8639-4D19CE2ACB54": {\n            "actionId": 5,\n            "allPoints": [\n                {\n                    "positiveController": null,\n                    "x": 1434.0,\n                    "y": 435.0\n                },\n                {\n                    "positiveController": {\n                        "positiveController": null,\n                        "x": 0.0,\n                        "y": 0.0\n                    },\n                    "x": 1435.0,\n                    "y": 490.0\n                },\n                {\n                    "positiveController": {\n                        "positiveController": null,\n                        "x": 0.0,\n                        "y": 0.0\n                    },\n                    "x": 1947.0,\n                    "y": 494.0\n                },\n                {\n                    "positiveController": {\n                        "positiveController": null,\n                        "x": 0.0,\n                        "y": 0.0\n                    },\n                    "x": 1950.0,\n                    "y": 118.0\n                },\n                {\n                    "positiveController": null,\n                    "x": 1763.0,\n                    "y": 113.0\n                }\n            ],\n            "controlPoints": [\n                {\n                    "positiveController": {\n                        "positiveController": null,\n                        "x": 0.0,\n                        "y": 0.0\n                    },\n                    "x": 1435.0,\n                    "y": 490.0\n                },\n                {\n                    "positiveController": {\n                        "positiveController": null,\n                        "x": 0.0,\n                        "y": 0.0\n                    },\n                    "x": 1947.0,\n                    "y": 494.0\n                },\n                {\n                    "positiveController": {\n                        "positiveController": null,\n                        "x": 0.0,\n                        "y": 0.0\n                    },\n                    "x": 1950.0,\n                    "y": 118.0\n                }\n            ],\n            "endNodeId": "6DA64EEB-08FE-2870-C90C-4D19CDA2F72D",\n            "endPoint": {\n                "positiveController": null,\n                "x": 1763.0,\n                "y": 113.0\n            },\n            "endStepId": 4,\n            "id": "517D7F32-20FB-309E-8639-4D19CE2ACB54",\n            "label": "Resolve Issue",\n            "labelPoint": {\n                "positiveController": null,\n                "x": 1631.25,\n                "y": 479.5\n            },\n            "lineType": "poly",\n            "startNodeId": "0740FFFA-2AA1-C90A-38ED-4D19CD61899B",\n            "startPoint": {\n                "positiveController": null,\n                "x": 1434.0,\n                "y": 435.0\n            },\n            "startStepId": 3\n        },\n        "58BD4605-5FB9-84EA-6952-4D19CE7B454B": {\n            "actionId": 1,\n            "allPoints": [\n                {\n                    "positiveController": null,\n                    "x": 1470.0,\n                    "y": 16.0\n                },\n                {\n                    "positiveController": null,\n                    "x": 1469.5,\n                    "y": 113.0\n                }\n            ],\n            "controlPoints": [],\n            "endNodeId": "778534F4-7595-88B6-45E1-4D19CD518712",\n            "endPoint": {\n                "positiveController": null,\n                "x": 1469.5,\n                "y": 113.0\n            },\n            "endStepId": 1,\n            "id": "58BD4605-5FB9-84EA-6952-4D19CE7B454B",\n            "label": "Create Issue",\n            "labelPoint": {\n                "positiveController": null,\n                "x": 1475.5,\n                "y": 48.5\n            },\n            "lineType": "straight",\n            "startNodeId": "15174530-AE75-04E0-1D9D-4D19CD200835",\n            "startPoint": {\n                "positiveController": null,\n                "x": 1470.0,\n                "y": 16.0\n            },\n            "startStepId": 1\n        },\n        "92D3DEFD-13AC-06A7-E5D8-4D19CE537791": {\n            "actionId": 4,\n            "allPoints": [\n                {\n                    "positiveController": null,\n                    "x": 1439.5,\n                    "y": 116.0\n                },\n                {\n                    "positiveController": {\n                        "positiveController": null,\n                        "x": 0.0,\n                        "y": 0.0\n                    },\n                    "x": 1393.0,\n                    "y": 116.0\n                },\n                {\n                    "positiveController": null,\n                    "x": 1390.0,\n                    "y": 434.0\n                }\n            ],\n            "controlPoints": [\n                {\n                    "positiveController": {\n                        "positiveController": null,\n                        "x": 0.0,\n                        "y": 0.0\n                    },\n                    "x": 1393.0,\n                    "y": 116.0\n                }\n            ],\n            "endNodeId": "0740FFFA-2AA1-C90A-38ED-4D19CD61899B",\n            "endPoint": {\n                "positiveController": null,\n                "x": 1390.0,\n                "y": 434.0\n            },\n            "endStepId": 3,\n            "id": "92D3DEFD-13AC-06A7-E5D8-4D19CE537791",\n            "label": "Start Progress",\n            "labelPoint": {\n                "positiveController": null,\n                "x": 1323.65,\n                "y": 193.75\n            },\n            "lineType": "poly",\n            "startNodeId": "778534F4-7595-88B6-45E1-4D19CD518712",\n            "startPoint": {\n                "positiveController": null,\n                "x": 1439.5,\n                "y": 116.0\n            },\n            "startStepId": 1\n        },\n        "C049EE11-C5BB-F93B-36C3-4D19CDF12B8F": {\n            "actionId": 3,\n            "allPoints": [\n                {\n                    "positiveController": null,\n                    "x": 1677.0,\n                    "y": 227.0\n                },\n                {\n                    "positiveController": {\n                        "positiveController": null,\n                        "x": 0.0,\n                        "y": 0.0\n                    },\n                    "x": 1767.05,\n                    "y": 230.05\n                },\n                {\n                    "positiveController": null,\n                    "x": 1773.5,\n                    "y": 425.0\n                }\n            ],\n            "controlPoints": [\n                {\n                    "positiveController": {\n                        "positiveController": null,\n                        "x": 0.0,\n                        "y": 0.0\n                    },\n                    "x": 1767.05,\n                    "y": 230.05\n                }\n            ],\n            "endNodeId": "A8B1A431-AC3A-6DCD-BFF0-4D19CDBCAADB",\n            "endPoint": {\n                "positiveController": null,\n                "x": 1773.5,\n                "y": 425.0\n            },\n            "endStepId": 5,\n            "id": "C049EE11-C5BB-F93B-36C3-4D19CDF12B8F",\n            "label": "Reopen Issue",\n            "labelPoint": {\n                "positiveController": null,\n                "x": 1703.85,\n                "y": 218.5\n            },\n            "lineType": "poly",\n            "startNodeId": "1C846CFB-4F0D-2F40-D0AE-4D19CDAF5D34",\n            "startPoint": {\n                "positiveController": null,\n                "x": 1677.0,\n                "y": 227.0\n            },\n            "startStepId": 6\n        },\n        "C9EA1792-2332-8B56-A04D-4D19CD725367": {\n            "actionId": 301,\n            "allPoints": [\n                {\n                    "positiveController": null,\n                    "x": 1465.0,\n                    "y": 436.0\n                },\n                {\n                    "positiveController": null,\n                    "x": 1469.5,\n                    "y": 113.0\n                }\n            ],\n            "controlPoints": [],\n            "endNodeId": "778534F4-7595-88B6-45E1-4D19CD518712",\n            "endPoint": {\n                "positiveController": null,\n                "x": 1469.5,\n                "y": 113.0\n            },\n            "endStepId": 1,\n            "id": "C9EA1792-2332-8B56-A04D-4D19CD725367",\n            "label": "Stop Progress",\n            "labelPoint": {\n                "positiveController": null,\n                "x": 1407.8,\n                "y": 308.5\n            },\n            "lineType": "straight",\n            "startNodeId": "0740FFFA-2AA1-C90A-38ED-4D19CD61899B",\n            "startPoint": {\n                "positiveController": null,\n                "x": 1465.0,\n                "y": 436.0\n            },\n            "startStepId": 3\n        },\n        "CAF37138-6321-E03A-8E41-4D19CDD7DC78": {\n            "actionId": 2,\n            "allPoints": [\n                {\n                    "positiveController": null,\n                    "x": 1764.5,\n                    "y": 430.0\n                },\n                {\n                    "positiveController": null,\n                    "x": 1614.0,\n                    "y": 226.0\n                }\n            ],\n            "controlPoints": [],\n            "endNodeId": "1C846CFB-4F0D-2F40-D0AE-4D19CDAF5D34",\n            "endPoint": {\n                "positiveController": null,\n                "x": 1614.0,\n                "y": 226.0\n            },\n            "endStepId": 6,\n            "id": "CAF37138-6321-E03A-8E41-4D19CDD7DC78",\n            "label": "Close Issue",\n            "labelPoint": {\n                "positiveController": null,\n                "x": 1677.65,\n                "y": 365.0\n            },\n            "lineType": "straight",\n            "startNodeId": "A8B1A431-AC3A-6DCD-BFF0-4D19CDBCAADB",\n            "startPoint": {\n                "positiveController": null,\n                "x": 1764.5,\n                "y": 430.0\n            },\n            "startStepId": 5\n        },\n        "E1F8462A-8B0A-87EA-4F70-4D19CE423C83": {\n            "actionId": 2,\n            "allPoints": [\n                {\n                    "positiveController": null,\n                    "x": 1488.0,\n                    "y": 430.0\n                },\n                {\n                    "positiveController": null,\n                    "x": 1614.0,\n                    "y": 226.0\n                }\n            ],\n            "controlPoints": [],\n            "endNodeId": "1C846CFB-4F0D-2F40-D0AE-4D19CDAF5D34",\n            "endPoint": {\n                "positiveController": null,\n                "x": 1614.0,\n                "y": 226.0\n            },\n            "endStepId": 6,\n            "id": "E1F8462A-8B0A-87EA-4F70-4D19CE423C83",\n            "label": "Close Issue",\n            "labelPoint": {\n                "positiveController": null,\n                "x": 1492.0,\n                "y": 345.0\n            },\n            "lineType": "straight",\n            "startNodeId": "0740FFFA-2AA1-C90A-38ED-4D19CD61899B",\n            "startPoint": {\n                "positiveController": null,\n                "x": 1488.0,\n                "y": 430.0\n            },\n            "startStepId": 3\n        },\n        "E27D8EB8-8E49-430B-8FCB-4D19CE127171": {\n            "actionId": 3,\n            "allPoints": [\n                {\n                    "positiveController": null,\n                    "x": 1840.0,\n                    "y": 130.0\n                },\n                {\n                    "positiveController": null,\n                    "x": 1846.5,\n                    "y": 428.0\n                }\n            ],\n            "controlPoints": [],\n            "endNodeId": "A8B1A431-AC3A-6DCD-BFF0-4D19CDBCAADB",\n            "endPoint": {\n                "positiveController": null,\n                "x": 1846.5,\n                "y": 428.0\n            },\n            "endStepId": 5,\n            "id": "E27D8EB8-8E49-430B-8FCB-4D19CE127171",\n            "label": "Reopen Issue",\n            "labelPoint": {\n                "positiveController": null,\n                "x": 1814.05,\n                "y": 169.5\n            },\n            "lineType": "straight",\n            "startNodeId": "6DA64EEB-08FE-2870-C90C-4D19CDA2F72D",\n            "startPoint": {\n                "positiveController": null,\n                "x": 1840.0,\n                "y": 130.0\n            },\n            "startStepId": 4\n        },\n        "F79E742D-A9E4-0124-D7D4-4D19CDE48C9C": {\n            "actionId": 4,\n            "allPoints": [\n                {\n                    "positiveController": null,\n                    "x": 1806.5,\n                    "y": 434.0\n                },\n                {\n                    "positiveController": null,\n                    "x": 1434.0,\n                    "y": 435.0\n                }\n            ],\n            "controlPoints": [],\n            "endNodeId": "0740FFFA-2AA1-C90A-38ED-4D19CD61899B",\n            "endPoint": {\n                "positiveController": null,\n                "x": 1434.0,\n                "y": 435.0\n            },\n            "endStepId": 3,\n            "id": "F79E742D-A9E4-0124-D7D4-4D19CDE48C9C",\n            "label": "Start Progress",\n            "labelPoint": {\n                "positiveController": null,\n                "x": 1607.25,\n                "y": 423.5\n            },\n            "lineType": "straight",\n            "startNodeId": "A8B1A431-AC3A-6DCD-BFF0-4D19CDBCAADB",\n            "startPoint": {\n                "positiveController": null,\n                "x": 1806.5,\n                "y": 434.0\n            },\n            "startStepId": 5\n        },\n        "FD6BA267-475B-70B3-8AA4-4D19CE00BCD1": {\n            "actionId": 701,\n            "allPoints": [\n                {\n                    "positiveController": null,\n                    "x": 1763.0,\n                    "y": 113.0\n                },\n                {\n                    "positiveController": null,\n                    "x": 1614.0,\n                    "y": 226.0\n                }\n            ],\n            "controlPoints": [],\n            "endNodeId": "1C846CFB-4F0D-2F40-D0AE-4D19CDAF5D34",\n            "endPoint": {\n                "positiveController": null,\n                "x": 1614.0,\n                "y": 226.0\n            },\n            "endStepId": 6,\n            "id": "FD6BA267-475B-70B3-8AA4-4D19CE00BCD1",\n            "label": "Close Issue",\n            "labelPoint": {\n                "positiveController": null,\n                "x": 1635.75,\n                "y": 152.25\n            },\n            "lineType": "straight",\n            "startNodeId": "6DA64EEB-08FE-2870-C90C-4D19CDA2F72D",\n            "startPoint": {\n                "positiveController": null,\n                "x": 1763.0,\n                "y": 113.0\n            },\n            "startStepId": 4\n        }\n    },\n    "nodeMap": {\n        "0740FFFA-2AA1-C90A-38ED-4D19CD61899B": {\n            "id": "0740FFFA-2AA1-C90A-38ED-4D19CD61899B",\n            "inLinkIds": [\n                "F79E742D-A9E4-0124-D7D4-4D19CDE48C9C",\n                "92D3DEFD-13AC-06A7-E5D8-4D19CE537791"\n            ],\n            "isInitialAction": false,\n            "label": "In Progress",\n            "outLinkIds": [\n                "C9EA1792-2332-8B56-A04D-4D19CD725367",\n                "517D7F32-20FB-309E-8639-4D19CE2ACB54",\n                "E1F8462A-8B0A-87EA-4F70-4D19CE423C83"\n            ],\n            "rect": {\n                "height": 45.0,\n                "positiveController": null,\n                "width": 146.0,\n                "x": 1373.0,\n                "y": 419.0\n            },\n            "stepId": 3\n        },\n        "15174530-AE75-04E0-1D9D-4D19CD200835": {\n            "id": "15174530-AE75-04E0-1D9D-4D19CD200835",\n            "inLinkIds": [],\n            "isInitialAction": true,\n            "label": "Create Issue",\n            "outLinkIds": [\n                "58BD4605-5FB9-84EA-6952-4D19CE7B454B"\n            ],\n            "rect": {\n                "height": 45.0,\n                "positiveController": null,\n                "width": 157.0,\n                "x": 1405.0,\n                "y": 0.0\n            },\n            "stepId": 1\n        },\n        "1C846CFB-4F0D-2F40-D0AE-4D19CDAF5D34": {\n            "id": "1C846CFB-4F0D-2F40-D0AE-4D19CDAF5D34",\n            "inLinkIds": [\n                "CAF37138-6321-E03A-8E41-4D19CDD7DC78",\n                "E1F8462A-8B0A-87EA-4F70-4D19CE423C83",\n                "FD6BA267-475B-70B3-8AA4-4D19CE00BCD1",\n                "3DF7CEC8-9FBC-C0D0-AFB1-4D19CE6EA230"\n            ],\n            "isInitialAction": false,\n            "label": "Closed",\n            "outLinkIds": [\n                "C049EE11-C5BB-F93B-36C3-4D19CDF12B8F"\n            ],\n            "rect": {\n                "height": 45.0,\n                "positiveController": null,\n                "width": 120.0,\n                "x": 1569.0,\n                "y": 210.0\n            },\n            "stepId": 6\n        },\n        "6DA64EEB-08FE-2870-C90C-4D19CDA2F72D": {\n            "id": "6DA64EEB-08FE-2870-C90C-4D19CDA2F72D",\n            "inLinkIds": [\n                "517D7F32-20FB-309E-8639-4D19CE2ACB54",\n                "1DEDB66F-FE5C-EDFD-54D0-4D19CDC8CECA",\n                "483797F1-1BF4-5E0F-86C6-4D19CE6023A2"\n            ],\n            "isInitialAction": false,\n            "label": "Resolved",\n            "outLinkIds": [\n                "FD6BA267-475B-70B3-8AA4-4D19CE00BCD1",\n                "E27D8EB8-8E49-430B-8FCB-4D19CE127171"\n            ],\n            "rect": {\n                "height": 44.0,\n                "positiveController": null,\n                "width": 137.0,\n                "x": 1709.0,\n                "y": 97.0\n            },\n            "stepId": 4\n        },\n        "778534F4-7595-88B6-45E1-4D19CD518712": {\n            "id": "778534F4-7595-88B6-45E1-4D19CD518712",\n            "inLinkIds": [\n                "C9EA1792-2332-8B56-A04D-4D19CD725367",\n                "58BD4605-5FB9-84EA-6952-4D19CE7B454B"\n            ],\n            "isInitialAction": false,\n            "label": "Open",\n            "outLinkIds": [\n                "92D3DEFD-13AC-06A7-E5D8-4D19CE537791",\n                "483797F1-1BF4-5E0F-86C6-4D19CE6023A2",\n                "3DF7CEC8-9FBC-C0D0-AFB1-4D19CE6EA230"\n            ],\n            "rect": {\n                "height": 45.0,\n                "positiveController": null,\n                "width": 106.0,\n                "x": 1429.5,\n                "y": 97.0\n            },\n            "stepId": 1\n        },\n        "A8B1A431-AC3A-6DCD-BFF0-4D19CDBCAADB": {\n            "id": "A8B1A431-AC3A-6DCD-BFF0-4D19CDBCAADB",\n            "inLinkIds": [\n                "E27D8EB8-8E49-430B-8FCB-4D19CE127171",\n                "C049EE11-C5BB-F93B-36C3-4D19CDF12B8F"\n            ],\n            "isInitialAction": false,\n            "label": "Reopened",\n            "outLinkIds": [\n                "1DEDB66F-FE5C-EDFD-54D0-4D19CDC8CECA",\n                "CAF37138-6321-E03A-8E41-4D19CDD7DC78",\n                "F79E742D-A9E4-0124-D7D4-4D19CDE48C9C"\n            ],\n            "rect": {\n                "height": 45.0,\n                "positiveController": null,\n                "width": 142.0,\n                "x": 1749.5,\n                "y": 418.0\n            },\n            "stepId": 5\n        }\n    },\n    "rootIds": [\n        "15174530-AE75-04E0-1D9D-4D19CD200835"\n    ],\n    "width": 1136\n}\n
10104	<map>\n  <entry>\n    <string>pluginVersion</string>\n    <string>8.5.3</string>\n  </entry>\n  <entry>\n    <string>ranOn</string>\n    <string>2020-03-05T22:51:22.948Z</string>\n  </entry>\n  <entry>\n    <string>buildDate</string>\n    <string>2020-01-09T07:24:54.920Z</string>\n  </entry>\n  <entry>\n    <string>latestUpgradeTaskRun</string>\n    <string>0</string>\n  </entry>\n  <entry>\n    <string>changeSet</string>\n    <string>316fc5132dcef952</string>\n  </entry>\n</map>
10103	<map>\n  <entry>\n    <string>pluginVersion</string>\n    <string>8.5.3</string>\n  </entry>\n  <entry>\n    <string>ranOn</string>\n    <string>2020-03-05T22:54:58.808Z</string>\n  </entry>\n  <entry>\n    <string>buildDate</string>\n    <string>2020-01-09T07:24:54.920Z</string>\n  </entry>\n  <entry>\n    <string>latestUpgradeTaskRun</string>\n    <string>0</string>\n  </entry>\n  <entry>\n    <string>changeSet</string>\n    <string>316fc5132dcef952</string>\n  </entry>\n</map>
10123	{"com.atlassian.jira.reindex.required":1583449098683}
10322	<map>\n  <entry>\n    <string>timeTaken</string>\n    <string>-1</string>\n  </entry>\n  <entry>\n    <string>pluginVersion</string>\n    <string>8.5.3</string>\n  </entry>\n  <entry>\n    <string>ranOn</string>\n    <string>2020-03-05T22:58:19.042Z</string>\n  </entry>\n  <entry>\n    <string>buildNumber</string>\n    <string>46</string>\n  </entry>\n  <entry>\n    <string>changeSet</string>\n    <string>316fc5132dcef952</string>\n  </entry>\n</map>
10323	<map>\n  <entry>\n    <string>timeTaken</string>\n    <string>3</string>\n  </entry>\n  <entry>\n    <string>pluginVersion</string>\n    <string>8.5.3</string>\n  </entry>\n  <entry>\n    <string>ranOn</string>\n    <string>2020-03-05T22:58:19.047Z</string>\n  </entry>\n  <entry>\n    <string>buildNumber</string>\n    <string>46</string>\n  </entry>\n  <entry>\n    <string>changeSet</string>\n    <string>316fc5132dcef952</string>\n  </entry>\n</map>
10326	<map>\n  <entry>\n    <string>timeTaken</string>\n    <string>-1</string>\n  </entry>\n  <entry>\n    <string>pluginVersion</string>\n    <string>8.5.3</string>\n  </entry>\n  <entry>\n    <string>ranOn</string>\n    <string>2020-03-05T22:58:19.053Z</string>\n  </entry>\n  <entry>\n    <string>buildNumber</string>\n    <string>47</string>\n  </entry>\n  <entry>\n    <string>changeSet</string>\n    <string>316fc5132dcef952</string>\n  </entry>\n</map>
10327	<map>\n  <entry>\n    <string>timeTaken</string>\n    <string>12</string>\n  </entry>\n  <entry>\n    <string>pluginVersion</string>\n    <string>8.5.3</string>\n  </entry>\n  <entry>\n    <string>ranOn</string>\n    <string>2020-03-05T22:58:19.067Z</string>\n  </entry>\n  <entry>\n    <string>buildNumber</string>\n    <string>47</string>\n  </entry>\n  <entry>\n    <string>changeSet</string>\n    <string>316fc5132dcef952</string>\n  </entry>\n</map>
10328	<map>\n  <entry>\n    <string>timeTaken</string>\n    <string>-1</string>\n  </entry>\n  <entry>\n    <string>pluginVersion</string>\n    <string>8.5.3</string>\n  </entry>\n  <entry>\n    <string>ranOn</string>\n    <string>2020-03-05T22:58:19.073Z</string>\n  </entry>\n  <entry>\n    <string>buildNumber</string>\n    <string>48</string>\n  </entry>\n  <entry>\n    <string>changeSet</string>\n    <string>316fc5132dcef952</string>\n  </entry>\n</map>
10329	<map>\n  <entry>\n    <string>timeTaken</string>\n    <string>2</string>\n  </entry>\n  <entry>\n    <string>pluginVersion</string>\n    <string>8.5.3</string>\n  </entry>\n  <entry>\n    <string>ranOn</string>\n    <string>2020-03-05T22:58:19.076Z</string>\n  </entry>\n  <entry>\n    <string>buildNumber</string>\n    <string>48</string>\n  </entry>\n  <entry>\n    <string>changeSet</string>\n    <string>316fc5132dcef952</string>\n  </entry>\n</map>
10330	<map>\n  <entry>\n    <string>timeTaken</string>\n    <string>-1</string>\n  </entry>\n  <entry>\n    <string>pluginVersion</string>\n    <string>8.5.3</string>\n  </entry>\n  <entry>\n    <string>ranOn</string>\n    <string>2020-03-05T22:58:19.082Z</string>\n  </entry>\n  <entry>\n    <string>buildNumber</string>\n    <string>49</string>\n  </entry>\n  <entry>\n    <string>changeSet</string>\n    <string>316fc5132dcef952</string>\n  </entry>\n</map>
10331	<map>\n  <entry>\n    <string>timeTaken</string>\n    <string>22</string>\n  </entry>\n  <entry>\n    <string>pluginVersion</string>\n    <string>8.5.3</string>\n  </entry>\n  <entry>\n    <string>ranOn</string>\n    <string>2020-03-05T22:58:19.105Z</string>\n  </entry>\n  <entry>\n    <string>buildNumber</string>\n    <string>49</string>\n  </entry>\n  <entry>\n    <string>changeSet</string>\n    <string>316fc5132dcef952</string>\n  </entry>\n</map>
10332	<map>\n  <entry>\n    <string>timeTaken</string>\n    <string>-1</string>\n  </entry>\n  <entry>\n    <string>pluginVersion</string>\n    <string>8.5.3</string>\n  </entry>\n  <entry>\n    <string>ranOn</string>\n    <string>2020-03-05T22:58:19.111Z</string>\n  </entry>\n  <entry>\n    <string>buildNumber</string>\n    <string>50</string>\n  </entry>\n  <entry>\n    <string>changeSet</string>\n    <string>316fc5132dcef952</string>\n  </entry>\n</map>
10333	<map>\n  <entry>\n    <string>timeTaken</string>\n    <string>11</string>\n  </entry>\n  <entry>\n    <string>pluginVersion</string>\n    <string>8.5.3</string>\n  </entry>\n  <entry>\n    <string>ranOn</string>\n    <string>2020-03-05T22:58:19.123Z</string>\n  </entry>\n  <entry>\n    <string>buildNumber</string>\n    <string>50</string>\n  </entry>\n  <entry>\n    <string>changeSet</string>\n    <string>316fc5132dcef952</string>\n  </entry>\n</map>
10334	<map>\n  <entry>\n    <string>timeTaken</string>\n    <string>-1</string>\n  </entry>\n  <entry>\n    <string>pluginVersion</string>\n    <string>8.5.3</string>\n  </entry>\n  <entry>\n    <string>ranOn</string>\n    <string>2020-03-05T22:58:19.128Z</string>\n  </entry>\n  <entry>\n    <string>buildNumber</string>\n    <string>51</string>\n  </entry>\n  <entry>\n    <string>changeSet</string>\n    <string>316fc5132dcef952</string>\n  </entry>\n</map>
10335	<map>\n  <entry>\n    <string>timeTaken</string>\n    <string>1</string>\n  </entry>\n  <entry>\n    <string>pluginVersion</string>\n    <string>8.5.3</string>\n  </entry>\n  <entry>\n    <string>ranOn</string>\n    <string>2020-03-05T22:58:19.131Z</string>\n  </entry>\n  <entry>\n    <string>buildNumber</string>\n    <string>51</string>\n  </entry>\n  <entry>\n    <string>changeSet</string>\n    <string>316fc5132dcef952</string>\n  </entry>\n</map>
10375	database.setup=AWAITS;app.properties.setup=FULLFILLED;create.user.mail.properties.setup=ANNOUNCE;mail.properties.setup=ANNOUNCE
10385	{"com.atlassian.jira.tzdetect.39600000,36000000":1583449206854}
\.


--
-- TOC entry 5528 (class 0 OID 17308)
-- Dependencies: 314
-- Data for Name: qrtz_calendars; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.qrtz_calendars (id, calendar_name, calendar) FROM stdin;
\.


--
-- TOC entry 5529 (class 0 OID 17316)
-- Dependencies: 315
-- Data for Name: qrtz_cron_triggers; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.qrtz_cron_triggers (id, trigger_id, cronexperssion) FROM stdin;
\.


--
-- TOC entry 5530 (class 0 OID 17321)
-- Dependencies: 316
-- Data for Name: qrtz_fired_triggers; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.qrtz_fired_triggers (id, entry_id, trigger_id, trigger_listener, fired_time, trigger_state) FROM stdin;
\.


--
-- TOC entry 5531 (class 0 OID 17329)
-- Dependencies: 317
-- Data for Name: qrtz_job_details; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.qrtz_job_details (id, job_name, job_group, class_name, is_durable, is_stateful, requests_recovery, job_data) FROM stdin;
\.


--
-- TOC entry 5532 (class 0 OID 17337)
-- Dependencies: 318
-- Data for Name: qrtz_job_listeners; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.qrtz_job_listeners (id, job, job_listener) FROM stdin;
\.


--
-- TOC entry 5533 (class 0 OID 17342)
-- Dependencies: 319
-- Data for Name: qrtz_simple_triggers; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.qrtz_simple_triggers (id, trigger_id, repeat_count, repeat_interval, times_triggered) FROM stdin;
\.


--
-- TOC entry 5534 (class 0 OID 17347)
-- Dependencies: 320
-- Data for Name: qrtz_trigger_listeners; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.qrtz_trigger_listeners (id, trigger_id, trigger_listener) FROM stdin;
\.


--
-- TOC entry 5535 (class 0 OID 17352)
-- Dependencies: 321
-- Data for Name: qrtz_triggers; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.qrtz_triggers (id, trigger_name, trigger_group, job, next_fire, trigger_state, trigger_type, start_time, end_time, calendar_name, misfire_instr) FROM stdin;
\.


--
-- TOC entry 5536 (class 0 OID 17360)
-- Dependencies: 322
-- Data for Name: reindex_component; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.reindex_component (id, request_id, affected_index, entity_type) FROM stdin;
10100	10000	ISSUE	NONE
10101	10000	COMMENT	NONE
10102	10000	CHANGEHISTORY	NONE
10103	10000	WORKLOG	NONE
\.


--
-- TOC entry 5537 (class 0 OID 17365)
-- Dependencies: 323
-- Data for Name: reindex_request; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.reindex_request (id, type, request_time, start_time, completion_time, status, execution_node_id, query) FROM stdin;
10000	IMMEDIATE	2020-03-05 22:51:32.318+00	2020-03-05 22:58:15.95+00	2020-03-05 22:58:16.066+00	COMPLETE	\N	\N
\.


--
-- TOC entry 5538 (class 0 OID 17373)
-- Dependencies: 324
-- Data for Name: remembermetoken; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.remembermetoken (id, created, token, username) FROM stdin;
\.


--
-- TOC entry 5540 (class 0 OID 17389)
-- Dependencies: 326
-- Data for Name: remotelink; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.remotelink (id, issueid, globalid, title, summary, url, iconurl, icontitle, relationship, resolved, statusname, statusdescription, statusiconurl, statusicontitle, statusiconlink, statuscategorykey, statuscategorycolorname, applicationtype, applicationname) FROM stdin;
\.


--
-- TOC entry 5541 (class 0 OID 17397)
-- Dependencies: 327
-- Data for Name: replicatedindexoperation; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.replicatedindexoperation (id, index_time, node_id, affected_index, entity_type, affected_ids, operation, filename) FROM stdin;
10130	2020-03-05 22:58:58.082+00	i-0832bd612c9f98710	ISSUE	NONE	10016,10017,10018,10019,10020,10021,10022,10000,10001,10002,10003,10004,10005,10006,10007,10008,10009,10010,10011,10012,10013,10014,10015	UPDATE_WITH_RELATED	
\.


--
-- TOC entry 5542 (class 0 OID 17405)
-- Dependencies: 328
-- Data for Name: resolution; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.resolution (id, sequence, pname, description, iconurl) FROM stdin;
10000	1	Done	Work has been completed on this issue.	\N
10001	2	Won't Do	This issue won't be actioned.	\N
10002	3	Duplicate	The problem is a duplicate of an existing issue.	\N
10003	4	Cannot Reproduce	All attempts at reproducing this issue failed, or not enough information was available to reproduce the issue. Reading the code produces no clues as to why this behavior would occur. If more information appears later, please reopen the issue.	\N
\.


--
-- TOC entry 5543 (class 0 OID 17413)
-- Dependencies: 329
-- Data for Name: rundetails; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.rundetails (id, job_id, start_time, run_duration, run_outcome, info_message) FROM stdin;
51200	assertionId-cleanup	2020-03-09 02:54:52.377+00	2	S	
44417	InstanceTopologyJob-job	2020-03-08 14:22:19.451+00	221	S	
51203	com.atlassian.jira.web.action.issue.DefaultTemporaryWebAttachmentsMonitor	2020-03-09 02:55:04.08+00	2	S	Removed 0 temporary attachments
51373	com.atlassian.jira.plugins.inform.batching.cron.BatchNotificationJobSchedulerImpl	2020-03-09 03:13:18.932+00	7	S	
51205	data-provider-cleanup	2020-03-09 02:55:04.527+00	3	S	
49851	com.atlassian.jira.cache.monitor.CacheStatisticsMonitor	2020-03-09 00:24:47.151+00	10	S	
51033	com.atlassian.jira.plugins.dvcs.scheduler.DvcsScheduler:job	2020-03-09 02:35:23.339+00	3	S	
49627	com.atlassian.jira.service.JiraService:10002	2020-03-09 00:00:00+00	67	S	
49629	com.atlassian.jira.plugins.inform.events.cleanup.CleanupJobScheduler	2020-03-09 00:00:00+00	80	S	
51263	com.atlassian.jira.apdex.impl.SendAnalyticsJobRunner	2020-03-09 03:01:00+00	0	S	
49067	applink-status-analytics-job	2020-03-08 22:58:19.711+00	3	S	
49686	JiraPluginScheduler:com.atlassian.analytics.client.upload.RemoteFilterRead:job	2020-03-09 00:06:00+00	1043	S	
49068	TruncateAlertsJobRunner	2020-03-08 22:58:19.711+00	3	S	
49069	Service Provider Session Remover	2020-03-08 22:58:19.769+00	2	S	
49070	LocalPluginLicenseNotificationJob-job	2020-03-08 22:58:19.747+00	46	S	
51385	com.atlassian.jira.service.JiraService:10000	2020-03-09 03:15:00+00	0	S	
51386	com.atlassian.jira.service.JiraService:10202	2020-03-09 03:15:00+00	23	S	
50032	JiraPluginScheduler:com.atlassian.analytics.client.upload.S3EventUploader:job	2020-03-09 00:45:00+00	2344	S	
51387	data-provider-poll	2020-03-09 03:15:04.527+00	2	S	
10104	BundledUpdateCheckJob-job	2020-03-05 22:58:19.745+00	3	S	
51388	com.atlassian.troubleshooting.jira.healthcheck.scheduler.impl.HeartbeatSchedulerImpl id=healthCheckScheduler	2020-03-09 03:15:04.532+00	0	S	
10107	com.atlassian.troubleshooting.jira.scheduler.CustomisationsCheckScheduler:job	2020-03-05 22:58:19.677+00	2374	S	
51389	CaesiumSchedulerService.RefreshJob	2020-03-09 03:15:04.534+00	0	S	
50271	PluginUpdateCheckJob-job	2020-03-09 01:10:35.437+00	6673	S	
51390	com.atlassian.jira.cache.EhCacheCompactor	2020-03-09 03:15:04.546+00	0	S	Purged 0 expired entries from a total of 25 entries in 23 caches.
51391	CompatibilityPluginScheduler.JobId.LEXO_RANK_SCHEDULER_JOB	2020-03-09 03:15:17.857+00	3	S	
51392	JiraPluginScheduler:LEXO_RANK_STATS_COLLECTOR_JOB	2020-03-09 03:15:17.864+00	0	S	
51393	com.atlassian.jira.plugins.inform.batching.cron.BatchNotificationJobSchedulerImpl.mentions	2020-03-09 03:15:18.942+00	8	S	
51394	com.atlassian.jira.plugin.ext.bamboo.service.PlanStatusUpdateJob-job	2020-03-09 03:15:19.029+00	1	S	
51395	CompatibilityPluginScheduler.JobId.hipchatUpdateGlancesDataJob	2020-03-09 03:15:39.646+00	2	S	
49085	analytics-collection	2020-03-08 23:00:00+00	10	S	
51235	JiraPluginScheduler:com.atlassian.jira.plugins.workflow.sharing.file.FileCleaningJob	2020-03-09 02:58:18.926+00	4	S	
49034	com.atlassian.jira.user.UserHistoryDatabaseCompactor	2020-03-08 22:55:04.531+00	2	S	
51239	PluginRequestCheckJob-job	2020-03-09 02:58:19.752+00	9	S	
51241	CompatibilityPluginScheduler.JobId.hipchatInstallGlancesJob	2020-03-09 02:58:40.232+00	1	S	
10136	com.atlassian.jira.plugins.software.monitor.ApplicationPluginsEnablerJobRunner	2020-03-05 23:01:02.415+00	4	S	
48981	com.atlassian.jira.service.JiraService:10201	2020-03-08 22:50:00.001+00	24	S	
51242	CompatibilityPluginScheduler.JobId.hipchatRefreshConnectionStatusJob	2020-03-09 02:58:46.06+00	2	S	
48983	com.atlassian.jira.service.JiraService:10200	2020-03-08 22:50:00+00	47	S	
50882	com.atlassian.diagnostics.internal.analytics.DailyAlertAnalyticsJob	2020-03-09 02:19:00+00	4	S	
48984	com.atlassian.jira.service.JiraService:10001	2020-03-08 22:50:00+00	2628	S	
51250	JiraPluginScheduler:com.atlassian.troubleshooting.healthcheck.scheduler.HealthCheckSchedulerImpl:job	2020-03-09 02:59:19.652+00	81	S	
23092	com.atlassian.jira.analytics.scheduler.AnalyticsScheduler	2020-03-06 22:58:18.914+00	202	S	
49613	com.atlassian.whisper.plugin.fetch.FetchJob	2020-03-08 23:58:18.933+00	1219	S	
\.


--
-- TOC entry 5544 (class 0 OID 17421)
-- Dependencies: 330
-- Data for Name: schemeissuesecurities; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.schemeissuesecurities (id, scheme, security, sec_type, sec_parameter) FROM stdin;
\.


--
-- TOC entry 5545 (class 0 OID 17429)
-- Dependencies: 331
-- Data for Name: schemeissuesecuritylevels; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.schemeissuesecuritylevels (id, name, description, scheme) FROM stdin;
\.


--
-- TOC entry 5546 (class 0 OID 17437)
-- Dependencies: 332
-- Data for Name: schemepermissions; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.schemepermissions (id, scheme, permission, perm_type, perm_parameter, permission_key) FROM stdin;
10025	0	32	projectrole	10002	MANAGE_WATCHERS
10026	0	34	projectrole	10002	EDIT_ALL_COMMENTS
10027	0	35	applicationRole		EDIT_OWN_COMMENTS
10028	0	36	projectrole	10002	DELETE_ALL_COMMENTS
10029	0	37	applicationRole		DELETE_OWN_COMMENTS
10030	0	38	projectrole	10002	DELETE_ALL_ATTACHMENTS
10031	0	39	applicationRole		DELETE_OWN_ATTACHMENTS
10033	0	29	applicationRole		VIEW_DEV_TOOLS
10101	\N	44	group	jira-administrators	\N
10200	0	45	applicationRole		VIEW_READONLY_WORKFLOW
10300	0	46	applicationRole		TRANSITION_ISSUES
10000	\N	0	group	jira-administrators	\N
10004	0	23	projectrole	10002	ADMINISTER_PROJECTS
10005	0	10	applicationRole		BROWSE_PROJECTS
10006	0	11	applicationRole		CREATE_ISSUES
10007	0	15	applicationRole		ADD_COMMENTS
10008	0	19	applicationRole		CREATE_ATTACHMENTS
10009	0	13	applicationRole		ASSIGN_ISSUES
10010	0	17	applicationRole		ASSIGNABLE_USER
10011	0	14	applicationRole		RESOLVE_ISSUES
10012	0	21	applicationRole		LINK_ISSUES
10013	0	12	applicationRole		EDIT_ISSUES
10014	0	16	projectrole	10002	DELETE_ISSUES
10015	0	18	applicationRole		CLOSE_ISSUES
10016	0	25	applicationRole		MOVE_ISSUES
10017	0	28	applicationRole		SCHEDULE_ISSUES
10018	0	30	projectrole	10002	MODIFY_REPORTER
10019	0	20	applicationRole		WORK_ON_ISSUES
10020	0	43	projectrole	10002	DELETE_ALL_WORKLOGS
10021	0	42	applicationRole		DELETE_OWN_WORKLOGS
10022	0	41	projectrole	10002	EDIT_ALL_WORKLOGS
10023	0	40	applicationRole		EDIT_OWN_WORKLOGS
10024	0	31	applicationRole		VIEW_VOTERS_AND_WATCHERS
10401	0	\N	projectrole	10002	MANAGE_SPRINTS_PERMISSION
10402	10000	\N	applicationRole	\N	BROWSE_PROJECTS
10403	10000	\N	applicationRole	\N	CREATE_ISSUES
10404	10000	\N	applicationRole	\N	EDIT_ISSUES
10405	10000	\N	applicationRole	\N	ASSIGN_ISSUES
10406	10000	\N	applicationRole	\N	RESOLVE_ISSUES
10407	10000	\N	applicationRole	\N	ADD_COMMENTS
10408	10000	\N	projectrole	10002	DELETE_ISSUES
10409	10000	\N	applicationRole	\N	ASSIGNABLE_USER
10410	10000	\N	applicationRole	\N	CLOSE_ISSUES
10411	10000	\N	applicationRole	\N	CREATE_ATTACHMENTS
10412	10000	\N	applicationRole	\N	WORK_ON_ISSUES
10413	10000	\N	applicationRole	\N	LINK_ISSUES
10414	10000	\N	projectrole	10002	ADMINISTER_PROJECTS
10415	10000	\N	applicationRole	\N	MOVE_ISSUES
10416	10000	\N	applicationRole	\N	SCHEDULE_ISSUES
10417	10000	\N	projectrole	10002	MODIFY_REPORTER
10418	10000	\N	applicationRole	\N	VIEW_VOTERS_AND_WATCHERS
10419	10000	\N	projectrole	10002	MANAGE_WATCHERS
10420	10000	\N	projectrole	10002	EDIT_ALL_COMMENTS
10421	10000	\N	applicationRole	\N	EDIT_OWN_COMMENTS
10422	10000	\N	projectrole	10002	DELETE_ALL_COMMENTS
10423	10000	\N	applicationRole	\N	DELETE_OWN_COMMENTS
10424	10000	\N	projectrole	10002	DELETE_ALL_ATTACHMENTS
10425	10000	\N	applicationRole	\N	DELETE_OWN_ATTACHMENTS
10426	10000	\N	applicationRole	\N	EDIT_OWN_WORKLOGS
10427	10000	\N	projectrole	10002	EDIT_ALL_WORKLOGS
10428	10000	\N	applicationRole	\N	DELETE_OWN_WORKLOGS
10429	10000	\N	projectrole	10002	DELETE_ALL_WORKLOGS
10430	10000	\N	applicationRole	\N	VIEW_READONLY_WORKFLOW
10431	10000	\N	applicationRole	\N	TRANSITION_ISSUES
10432	10000	\N	applicationRole	\N	VIEW_DEV_TOOLS
10433	10000	\N	applicationRole	\N	MANAGE_SPRINTS_PERMISSION
\.


--
-- TOC entry 5547 (class 0 OID 17445)
-- Dependencies: 333
-- Data for Name: searchrequest; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.searchrequest (id, filtername, authorname, description, username, groupname, projectid, reqcontent, fav_count, filtername_lower) FROM stdin;
10000	Filter for TESR board	JIRAUSER10000	\N	JIRAUSER10000	\N	\N	project = TESR ORDER BY Rank ASC	0	filter for tesr board
\.


--
-- TOC entry 5548 (class 0 OID 17453)
-- Dependencies: 334
-- Data for Name: sequence_value_item; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.sequence_value_item (seq_name, seq_id) FROM stdin;
ListenerConfig	10300
IndexBackupSequence	10100
PermissionScheme	10100
ProductLicense	10100
AuditChangedValue	10300
Group	10110
LicenseRoleGroup	10100
GlobalPermissionEntry	10200
EntityProperty	10100
ApplicationUser	10100
User	10100
ProjectRole	10200
Membership	10100
LicenseRoleDefault	10100
ReindexComponent	10200
TaskIdSequence	10100
OSPropertyEntry	10400
FieldLayoutItem	10200
CustomField	10200
FieldConfiguration	10300
FieldConfigScheme	10300
FieldConfigSchemeIssueType	10400
ConfigurationContext	10300
AuditLog	10200
EventType	10000
FieldLayout	10100
GadgetUserPreference	10100
NotificationScheme	10100
OAuthConsumer	10100
PortalPage	10100
PortletConfiguration	10100
Priority	10000
Version	10100
UpgradeTaskHistory	10100
UpgradeTaskHistoryAuditLog	10100
OSWorkflowEntry	10100
OSCurrentStep	10100
Issue	10100
ColumnLayout	10100
ColumnLayoutItem	10100
Feature	10200
ManagedConfigurationItem	10200
ServiceConfig	10300
EntityPropertyIndexDocument	10100
ClusteredJob	10300
PluginVersion	10300
UpgradeHistory	10200
CustomFieldValue	10100
ReindexRequest	10100
Avatar	10600
Notification	10300
UpgradeVersionHistory	10100
Action	10100
IssueLink	10100
ClusterLockStatus	10300
IssueType	10100
OptionConfiguration	10400
IssueLinkType	10300
SchemePermissions	10500
ClusterMessage	10300
UserAttribute	10200
NodeIndexCounter	10100
Project	10100
ProjectKey	10100
ProjectRoleActor	10200
GenericConfiguration	10200
FieldScreen	10100
FieldScreenTab	10200
FieldScreenLayoutItem	10300
FieldScreenScheme	10100
FieldScreenSchemeItem	10200
IssueTypeScreenScheme	10100
IssueTypeScreenSchemeEntity	10200
Resolution	10100
CustomFieldOption	10100
Status	10100
Workflow	10200
WorkflowScheme	10200
WorkflowSchemeEntity	10200
AuditItem	10200
SearchRequest	10100
SharePermissions	10200
ReplicatedIndexOperation	10200
ChangeGroup	10100
ChangeItem	10100
UserHistoryItem	10100
RunDetails	51400
\.


--
-- TOC entry 5549 (class 0 OID 17458)
-- Dependencies: 335
-- Data for Name: serviceconfig; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.serviceconfig (id, delaytime, clazz, servicename, cron_expression) FROM stdin;
10000	60000	com.atlassian.jira.service.services.mail.MailQueueService	Mail Queue Service	0 * * * * ?
10002	86400000	com.atlassian.jira.service.services.auditing.AuditLogCleaningService	Audit log cleaning service	0 0 0 * * ?
10200	43200000	com.atlassian.jira.service.services.index.ReplicatedIndexCleaningService	Replicated index flush service	0 50 10/12 * * ?
10201	43200000	com.atlassian.jira.service.services.cluster.ClusterMessageCleaningService	Cluster messaging flush service	0 50 10/12 * * ?
10202	60000	com.atlassian.jira.service.services.cluster.NodeStateCheckerService	Cluster node state check service	0 * * * * ?
10001	43200000	com.atlassian.jira.service.services.export.ExportService	Backup Service	0 50 10/12 * * ?
\.


--
-- TOC entry 5550 (class 0 OID 17466)
-- Dependencies: 336
-- Data for Name: sharepermissions; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.sharepermissions (id, entityid, entitytype, sharetype, param1, param2, rights) FROM stdin;
10000	10000	PortalPage	global	\N	\N	1
10100	10000	SearchRequest	project	10000	\N	1
\.


--
-- TOC entry 5552 (class 0 OID 17479)
-- Dependencies: 338
-- Data for Name: tempattachmentsmonitor; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.tempattachmentsmonitor (temporary_attachment_id, form_token, file_name, content_type, file_size, created_time) FROM stdin;
\.


--
-- TOC entry 5553 (class 0 OID 17487)
-- Dependencies: 339
-- Data for Name: trackback_ping; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.trackback_ping (id, issue, url, title, blogname, excerpt, created) FROM stdin;
\.


--
-- TOC entry 5554 (class 0 OID 17495)
-- Dependencies: 340
-- Data for Name: trustedapp; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.trustedapp (id, application_id, name, public_key, ip_match, url_match, timeout, created, created_by, updated, updated_by) FROM stdin;
\.


--
-- TOC entry 5555 (class 0 OID 17503)
-- Dependencies: 341
-- Data for Name: upgradehistory; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.upgradehistory (id, upgradeclass, targetbuild, status, downgradetaskrequired) FROM stdin;
10000	com.atlassian.jira.upgrade.tasks.UpgradeTask_Build70100	70100	complete	Y
10100	com.atlassian.jira.upgrade.tasks.UpgradeTask_Build70101	70101	complete	N
10101	com.atlassian.jira.upgrade.tasks.UpgradeTask_Build70102	70102	complete	N
10102	com.atlassian.jira.upgrade.tasks.UpgradeTask_Build71001	71001	complete	N
10103	com.atlassian.jira.upgrade.tasks.UpgradeTask_Build72001	72001	complete	N
10104	com.atlassian.jira.upgrade.tasks.UpgradeTask_Build72002	72002	complete	N
10105	com.atlassian.jira.upgrade.tasks.UpgradeTask_Build73002	73002	complete	N
\.


--
-- TOC entry 5556 (class 0 OID 17511)
-- Dependencies: 342
-- Data for Name: upgradetaskhistory; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.upgradetaskhistory (id, upgrade_task_factory_key, build_number, status, upgrade_type) FROM stdin;
10000	com.atlassian.jira.plugins.jira-bitbucket-connector-plugin	1	COMPLETED	SERVER
10001	com.atlassian.jira.plugins.jira-bitbucket-connector-plugin	2	COMPLETED	SERVER
10002	com.atlassian.jira.plugins.jira-development-integration-plugin	1	COMPLETED	SERVER
10003	com.atlassian.jira.plugins.jira-development-integration-plugin	2	COMPLETED	SERVER
10004	com.atlassian.jira.plugins.jira-development-integration-plugin	3	COMPLETED	SERVER
10005	com.atlassian.jira.plugins.inform.batching-plugin	1	COMPLETED	SERVER
10012	host	73010	COMPLETED	SERVER
10013	host	73011	COMPLETED	SERVER
10014	host	74002	COMPLETED	SERVER
10015	host	75002	COMPLETED	SERVER
10016	host	75003	COMPLETED	SERVER
10017	host	75004	COMPLETED	SERVER
10018	host	75005	COMPLETED	SERVER
10019	host	76001	COMPLETED	SERVER
10020	host	77001	COMPLETED	SERVER
10021	host	710001	COMPLETED	SERVER
10022	host	711001	COMPLETED	SERVER
10023	host	712001	COMPLETED	SERVER
10024	host	800000	COMPLETED	SERVER
10025	host	800003	COMPLETED	SERVER
10026	host	800004	COMPLETED	SERVER
10027	host	800005	COMPLETED	SERVER
10028	host	800006	COMPLETED	SERVER
10029	host	800007	COMPLETED	SERVER
10030	host	800009	COMPLETED	SERVER
10031	host	801000	COMPLETED	SERVER
10032	host	804000	COMPLETED	SERVER
\.


--
-- TOC entry 5557 (class 0 OID 17516)
-- Dependencies: 343
-- Data for Name: upgradetaskhistoryauditlog; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.upgradetaskhistoryauditlog (id, upgrade_task_factory_key, build_number, status, upgrade_type, timeperformed, action) FROM stdin;
10000	com.atlassian.jira.plugins.jira-bitbucket-connector-plugin	1	COMPLETED	SERVER	2020-03-05 22:51:28.796+00	upgrade
10001	com.atlassian.jira.plugins.jira-bitbucket-connector-plugin	2	COMPLETED	SERVER	2020-03-05 22:51:28.917+00	upgrade
10002	com.atlassian.jira.plugins.jira-development-integration-plugin	1	COMPLETED	SERVER	2020-03-05 22:51:29.248+00	upgrade
10003	com.atlassian.jira.plugins.jira-development-integration-plugin	2	COMPLETED	SERVER	2020-03-05 22:51:29.259+00	upgrade
10004	com.atlassian.jira.plugins.jira-development-integration-plugin	3	COMPLETED	SERVER	2020-03-05 22:51:29.582+00	upgrade
10005	com.atlassian.jira.plugins.inform.batching-plugin	1	COMPLETED	SERVER	2020-03-05 22:51:30.058+00	upgrade
10006	host	70101	COMPLETED	SERVER	2020-03-05 22:51:32.096+00	upgrade
10007	host	70102	COMPLETED	SERVER	2020-03-05 22:51:32.108+00	upgrade
10008	host	71001	COMPLETED	SERVER	2020-03-05 22:51:32.118+00	upgrade
10009	host	72001	COMPLETED	SERVER	2020-03-05 22:51:32.124+00	upgrade
10010	host	72002	COMPLETED	SERVER	2020-03-05 22:51:32.16+00	upgrade
10011	host	73002	COMPLETED	SERVER	2020-03-05 22:51:32.164+00	upgrade
10012	host	73010	COMPLETED	SERVER	2020-03-05 22:51:32.177+00	upgrade
10013	host	73011	COMPLETED	SERVER	2020-03-05 22:51:32.181+00	upgrade
10014	host	74002	COMPLETED	SERVER	2020-03-05 22:51:32.188+00	upgrade
10015	host	75002	COMPLETED	SERVER	2020-03-05 22:51:32.194+00	upgrade
10016	host	75003	COMPLETED	SERVER	2020-03-05 22:51:32.199+00	upgrade
10017	host	75004	COMPLETED	SERVER	2020-03-05 22:51:32.204+00	upgrade
10018	host	75005	COMPLETED	SERVER	2020-03-05 22:51:32.21+00	upgrade
10019	host	76001	COMPLETED	SERVER	2020-03-05 22:51:32.232+00	upgrade
10020	host	77001	COMPLETED	SERVER	2020-03-05 22:51:32.246+00	upgrade
10021	host	710001	COMPLETED	SERVER	2020-03-05 22:51:32.264+00	upgrade
10022	host	711001	COMPLETED	SERVER	2020-03-05 22:51:32.277+00	upgrade
10023	host	712001	COMPLETED	SERVER	2020-03-05 22:51:32.285+00	upgrade
10024	host	800000	COMPLETED	SERVER	2020-03-05 22:51:32.288+00	upgrade
10025	host	800003	COMPLETED	SERVER	2020-03-05 22:51:32.315+00	upgrade
10026	host	800004	COMPLETED	SERVER	2020-03-05 22:51:32.335+00	upgrade
10027	host	800005	COMPLETED	SERVER	2020-03-05 22:51:32.353+00	upgrade
10028	host	800006	COMPLETED	SERVER	2020-03-05 22:51:32.364+00	upgrade
10029	host	800007	COMPLETED	SERVER	2020-03-05 22:51:32.375+00	upgrade
10030	host	800009	COMPLETED	SERVER	2020-03-05 22:51:32.388+00	upgrade
10031	host	801000	COMPLETED	SERVER	2020-03-05 22:51:32.413+00	upgrade
10032	host	804000	COMPLETED	SERVER	2020-03-05 22:51:32.417+00	upgrade
\.


--
-- TOC entry 5558 (class 0 OID 17521)
-- Dependencies: 344
-- Data for Name: upgradeversionhistory; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.upgradeversionhistory (id, timeperformed, targetbuild, targetversion) FROM stdin;
10000	2020-03-05 22:51:32.422+00	804000	8.5.3
\.


--
-- TOC entry 5560 (class 0 OID 17537)
-- Dependencies: 346
-- Data for Name: userassociation; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.userassociation (source_name, sink_node_id, sink_node_entity, association_type, sequence, created) FROM stdin;
\.


--
-- TOC entry 5510 (class 0 OID 17182)
-- Dependencies: 296
-- Data for Name: userbase; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.userbase (id, username, password_hash) FROM stdin;
\.


--
-- TOC entry 5562 (class 0 OID 17550)
-- Dependencies: 348
-- Data for Name: userhistoryitem; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.userhistoryitem (id, entitytype, entityid, username, lastviewed, data) FROM stdin;
10002	Sprint	1	JIRAUSER10000	1583449140609	Sample Sprint 2
10001	Project	10000	JIRAUSER10000	1583449140647	\N
10000	RapidView	1	JIRAUSER10000	1583449143690	TESR board
\.


--
-- TOC entry 5563 (class 0 OID 17558)
-- Dependencies: 349
-- Data for Name: userpickerfilter; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.userpickerfilter (id, customfield, customfieldconfig, enabled) FROM stdin;
\.


--
-- TOC entry 5564 (class 0 OID 17563)
-- Dependencies: 350
-- Data for Name: userpickerfiltergroup; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.userpickerfiltergroup (id, userpickerfilter, groupname) FROM stdin;
\.


--
-- TOC entry 5565 (class 0 OID 17568)
-- Dependencies: 351
-- Data for Name: userpickerfilterrole; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.userpickerfilterrole (id, userpickerfilter, projectroleid) FROM stdin;
\.


--
-- TOC entry 5567 (class 0 OID 17581)
-- Dependencies: 353
-- Data for Name: versioncontrol; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.versioncontrol (id, vcsname, vcsdescription, vcstype) FROM stdin;
\.


--
-- TOC entry 5568 (class 0 OID 17589)
-- Dependencies: 354
-- Data for Name: votehistory; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.votehistory (id, issueid, votes, "timestamp") FROM stdin;
\.


--
-- TOC entry 5570 (class 0 OID 17602)
-- Dependencies: 356
-- Data for Name: workflowscheme; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.workflowscheme (id, name, description) FROM stdin;
10000	classic	classic
10100	TESR: Software Simplified Workflow Scheme	Generated by JIRA Software version 8.5.3. This workflow scheme is managed internally by Jira Software. Do not manually modify this workflow scheme.
\.


--
-- TOC entry 5571 (class 0 OID 17610)
-- Dependencies: 357
-- Data for Name: workflowschemeentity; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.workflowschemeentity (id, scheme, workflow, issuetype) FROM stdin;
10000	10000	classic default workflow	0
10100	10100	Software Simplified Workflow for Project TESR	0
\.


--
-- TOC entry 5573 (class 0 OID 17626)
-- Dependencies: 359
-- Data for Name: worklog; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.worklog (id, issueid, author, grouplevel, rolelevel, worklogbody, created, updateauthor, updated, startdate, timeworked) FROM stdin;
\.


--
-- TOC entry 5190 (class 2606 OID 19987)
-- Name: AO_0A5972_NOTIFICATION_SETTING AO_0A5972_NOTIFICATION_SETTING_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_0A5972_NOTIFICATION_SETTING"
    ADD CONSTRAINT "AO_0A5972_NOTIFICATION_SETTING_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5193 (class 2606 OID 19995)
-- Name: AO_0A5972_PUSH_REGISTRATION AO_0A5972_PUSH_REGISTRATION_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_0A5972_PUSH_REGISTRATION"
    ADD CONSTRAINT "AO_0A5972_PUSH_REGISTRATION_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5196 (class 2606 OID 20005)
-- Name: AO_0AC321_RECOMMENDATION_AO AO_0AC321_RECOMMENDATION_AO_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_0AC321_RECOMMENDATION_AO"
    ADD CONSTRAINT "AO_0AC321_RECOMMENDATION_AO_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5216 (class 2606 OID 20085)
-- Name: AO_21D670_WHITELIST_RULES AO_21D670_WHITELIST_RULES_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_21D670_WHITELIST_RULES"
    ADD CONSTRAINT "AO_21D670_WHITELIST_RULES_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5171 (class 2606 OID 19919)
-- Name: AO_21F425_MESSAGE_AO AO_21F425_MESSAGE_AO_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_21F425_MESSAGE_AO"
    ADD CONSTRAINT "AO_21F425_MESSAGE_AO_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5167 (class 2606 OID 19911)
-- Name: AO_21F425_MESSAGE_MAPPING_AO AO_21F425_MESSAGE_MAPPING_AO_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_21F425_MESSAGE_MAPPING_AO"
    ADD CONSTRAINT "AO_21F425_MESSAGE_MAPPING_AO_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5164 (class 2606 OID 19900)
-- Name: AO_21F425_USER_PROPERTY_AO AO_21F425_USER_PROPERTY_AO_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_21F425_USER_PROPERTY_AO"
    ADD CONSTRAINT "AO_21F425_USER_PROPERTY_AO_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 4974 (class 2606 OID 19066)
-- Name: AO_38321B_CUSTOM_CONTENT_LINK AO_38321B_CUSTOM_CONTENT_LINK_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_38321B_CUSTOM_CONTENT_LINK"
    ADD CONSTRAINT "AO_38321B_CUSTOM_CONTENT_LINK_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5053 (class 2606 OID 19445)
-- Name: AO_3B1893_LOOP_DETECTION AO_3B1893_LOOP_DETECTION_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_3B1893_LOOP_DETECTION"
    ADD CONSTRAINT "AO_3B1893_LOOP_DETECTION_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5239 (class 2606 OID 20196)
-- Name: AO_4789DD_HEALTH_CHECK_STATUS AO_4789DD_HEALTH_CHECK_STATUS_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_4789DD_HEALTH_CHECK_STATUS"
    ADD CONSTRAINT "AO_4789DD_HEALTH_CHECK_STATUS_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5241 (class 2606 OID 20207)
-- Name: AO_4789DD_PROPERTIES AO_4789DD_PROPERTIES_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_4789DD_PROPERTIES"
    ADD CONSTRAINT "AO_4789DD_PROPERTIES_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5248 (class 2606 OID 20229)
-- Name: AO_4789DD_READ_NOTIFICATIONS AO_4789DD_READ_NOTIFICATIONS_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_4789DD_READ_NOTIFICATIONS"
    ADD CONSTRAINT "AO_4789DD_READ_NOTIFICATIONS_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5243 (class 2606 OID 20219)
-- Name: AO_4789DD_TASK_MONITOR AO_4789DD_TASK_MONITOR_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_4789DD_TASK_MONITOR"
    ADD CONSTRAINT "AO_4789DD_TASK_MONITOR_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5198 (class 2606 OID 20016)
-- Name: AO_4AEACD_WEBHOOK_DAO AO_4AEACD_WEBHOOK_DAO_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_4AEACD_WEBHOOK_DAO"
    ADD CONSTRAINT "AO_4AEACD_WEBHOOK_DAO_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 4971 (class 2606 OID 19053)
-- Name: AO_550953_SHORTCUT AO_550953_SHORTCUT_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_550953_SHORTCUT"
    ADD CONSTRAINT "AO_550953_SHORTCUT_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5233 (class 2606 OID 20160)
-- Name: AO_563AEE_ACTIVITY_ENTITY AO_563AEE_ACTIVITY_ENTITY_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_563AEE_ACTIVITY_ENTITY"
    ADD CONSTRAINT "AO_563AEE_ACTIVITY_ENTITY_pkey" PRIMARY KEY ("ACTIVITY_ID");


--
-- TOC entry 5223 (class 2606 OID 20108)
-- Name: AO_563AEE_ACTOR_ENTITY AO_563AEE_ACTOR_ENTITY_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_563AEE_ACTOR_ENTITY"
    ADD CONSTRAINT "AO_563AEE_ACTOR_ENTITY_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5225 (class 2606 OID 20116)
-- Name: AO_563AEE_MEDIA_LINK_ENTITY AO_563AEE_MEDIA_LINK_ENTITY_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_563AEE_MEDIA_LINK_ENTITY"
    ADD CONSTRAINT "AO_563AEE_MEDIA_LINK_ENTITY_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5227 (class 2606 OID 20127)
-- Name: AO_563AEE_OBJECT_ENTITY AO_563AEE_OBJECT_ENTITY_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_563AEE_OBJECT_ENTITY"
    ADD CONSTRAINT "AO_563AEE_OBJECT_ENTITY_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5230 (class 2606 OID 20143)
-- Name: AO_563AEE_TARGET_ENTITY AO_563AEE_TARGET_ENTITY_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_563AEE_TARGET_ENTITY"
    ADD CONSTRAINT "AO_563AEE_TARGET_ENTITY_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5157 (class 2606 OID 19870)
-- Name: AO_575BF5_DEV_SUMMARY AO_575BF5_DEV_SUMMARY_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_575BF5_DEV_SUMMARY"
    ADD CONSTRAINT "AO_575BF5_DEV_SUMMARY_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5153 (class 2606 OID 19858)
-- Name: AO_575BF5_PROCESSED_COMMITS AO_575BF5_PROCESSED_COMMITS_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_575BF5_PROCESSED_COMMITS"
    ADD CONSTRAINT "AO_575BF5_PROCESSED_COMMITS_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5148 (class 2606 OID 19847)
-- Name: AO_575BF5_PROVIDER_ISSUE AO_575BF5_PROVIDER_ISSUE_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_575BF5_PROVIDER_ISSUE"
    ADD CONSTRAINT "AO_575BF5_PROVIDER_ISSUE_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5161 (class 2606 OID 19881)
-- Name: AO_575BF5_PROVIDER_SEQ_NO AO_575BF5_PROVIDER_SEQ_NO_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_575BF5_PROVIDER_SEQ_NO"
    ADD CONSTRAINT "AO_575BF5_PROVIDER_SEQ_NO_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5206 (class 2606 OID 20052)
-- Name: AO_587B34_GLANCE_CONFIG AO_587B34_GLANCE_CONFIG_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_587B34_GLANCE_CONFIG"
    ADD CONSTRAINT "AO_587B34_GLANCE_CONFIG_pkey" PRIMARY KEY ("ROOM_ID");


--
-- TOC entry 5208 (class 2606 OID 20065)
-- Name: AO_587B34_PROJECT_CONFIG AO_587B34_PROJECT_CONFIG_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_587B34_PROJECT_CONFIG"
    ADD CONSTRAINT "AO_587B34_PROJECT_CONFIG_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5200 (class 2606 OID 20028)
-- Name: AO_5FB9D7_AOHIP_CHAT_LINK AO_5FB9D7_AOHIP_CHAT_LINK_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_5FB9D7_AOHIP_CHAT_LINK"
    ADD CONSTRAINT "AO_5FB9D7_AOHIP_CHAT_LINK_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5202 (class 2606 OID 20039)
-- Name: AO_5FB9D7_AOHIP_CHAT_USER AO_5FB9D7_AOHIP_CHAT_USER_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_5FB9D7_AOHIP_CHAT_USER"
    ADD CONSTRAINT "AO_5FB9D7_AOHIP_CHAT_USER_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5002 (class 2606 OID 19171)
-- Name: AO_60DB71_AUDITENTRY AO_60DB71_AUDITENTRY_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_AUDITENTRY"
    ADD CONSTRAINT "AO_60DB71_AUDITENTRY_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5022 (class 2606 OID 19263)
-- Name: AO_60DB71_BOARDADMINS AO_60DB71_BOARDADMINS_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_BOARDADMINS"
    ADD CONSTRAINT "AO_60DB71_BOARDADMINS_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5010 (class 2606 OID 19202)
-- Name: AO_60DB71_CARDCOLOR AO_60DB71_CARDCOLOR_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_CARDCOLOR"
    ADD CONSTRAINT "AO_60DB71_CARDCOLOR_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5007 (class 2606 OID 19185)
-- Name: AO_60DB71_CARDLAYOUT AO_60DB71_CARDLAYOUT_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_CARDLAYOUT"
    ADD CONSTRAINT "AO_60DB71_CARDLAYOUT_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5043 (class 2606 OID 19380)
-- Name: AO_60DB71_COLUMNSTATUS AO_60DB71_COLUMNSTATUS_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_COLUMNSTATUS"
    ADD CONSTRAINT "AO_60DB71_COLUMNSTATUS_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5019 (class 2606 OID 19247)
-- Name: AO_60DB71_COLUMN AO_60DB71_COLUMN_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_COLUMN"
    ADD CONSTRAINT "AO_60DB71_COLUMN_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5013 (class 2606 OID 19216)
-- Name: AO_60DB71_DETAILVIEWFIELD AO_60DB71_DETAILVIEWFIELD_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_DETAILVIEWFIELD"
    ADD CONSTRAINT "AO_60DB71_DETAILVIEWFIELD_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5025 (class 2606 OID 19279)
-- Name: AO_60DB71_ESTIMATESTATISTIC AO_60DB71_ESTIMATESTATISTIC_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_ESTIMATESTATISTIC"
    ADD CONSTRAINT "AO_60DB71_ESTIMATESTATISTIC_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 4987 (class 2606 OID 19118)
-- Name: AO_60DB71_ISSUERANKINGLOG AO_60DB71_ISSUERANKINGLOG_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_ISSUERANKINGLOG"
    ADD CONSTRAINT "AO_60DB71_ISSUERANKINGLOG_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 4979 (class 2606 OID 19088)
-- Name: AO_60DB71_ISSUERANKING AO_60DB71_ISSUERANKING_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_ISSUERANKING"
    ADD CONSTRAINT "AO_60DB71_ISSUERANKING_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 4983 (class 2606 OID 19096)
-- Name: AO_60DB71_LEXORANKBALANCER AO_60DB71_LEXORANKBALANCER_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_LEXORANKBALANCER"
    ADD CONSTRAINT "AO_60DB71_LEXORANKBALANCER_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 4989 (class 2606 OID 19133)
-- Name: AO_60DB71_LEXORANK AO_60DB71_LEXORANK_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_LEXORANK"
    ADD CONSTRAINT "AO_60DB71_LEXORANK_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5046 (class 2606 OID 19398)
-- Name: AO_60DB71_NONWORKINGDAY AO_60DB71_NONWORKINGDAY_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_NONWORKINGDAY"
    ADD CONSTRAINT "AO_60DB71_NONWORKINGDAY_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5016 (class 2606 OID 19233)
-- Name: AO_60DB71_QUICKFILTER AO_60DB71_QUICKFILTER_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_QUICKFILTER"
    ADD CONSTRAINT "AO_60DB71_QUICKFILTER_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 4998 (class 2606 OID 19152)
-- Name: AO_60DB71_RANKABLEOBJECT AO_60DB71_RANKABLEOBJECT_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_RANKABLEOBJECT"
    ADD CONSTRAINT "AO_60DB71_RANKABLEOBJECT_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 4985 (class 2606 OID 19107)
-- Name: AO_60DB71_RAPIDVIEW AO_60DB71_RAPIDVIEW_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_RAPIDVIEW"
    ADD CONSTRAINT "AO_60DB71_RAPIDVIEW_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 4995 (class 2606 OID 19144)
-- Name: AO_60DB71_SPRINT AO_60DB71_SPRINT_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_SPRINT"
    ADD CONSTRAINT "AO_60DB71_SPRINT_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5037 (class 2606 OID 19337)
-- Name: AO_60DB71_STATSFIELD AO_60DB71_STATSFIELD_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_STATSFIELD"
    ADD CONSTRAINT "AO_60DB71_STATSFIELD_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5031 (class 2606 OID 19311)
-- Name: AO_60DB71_SUBQUERY AO_60DB71_SUBQUERY_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_SUBQUERY"
    ADD CONSTRAINT "AO_60DB71_SUBQUERY_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5040 (class 2606 OID 19354)
-- Name: AO_60DB71_SWIMLANE AO_60DB71_SWIMLANE_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_SWIMLANE"
    ADD CONSTRAINT "AO_60DB71_SWIMLANE_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5028 (class 2606 OID 19295)
-- Name: AO_60DB71_TRACKINGSTATISTIC AO_60DB71_TRACKINGSTATISTIC_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_TRACKINGSTATISTIC"
    ADD CONSTRAINT "AO_60DB71_TRACKINGSTATISTIC_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5000 (class 2606 OID 19160)
-- Name: AO_60DB71_VERSION AO_60DB71_VERSION_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_VERSION"
    ADD CONSTRAINT "AO_60DB71_VERSION_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5034 (class 2606 OID 19324)
-- Name: AO_60DB71_WORKINGDAYS AO_60DB71_WORKINGDAYS_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_WORKINGDAYS"
    ADD CONSTRAINT "AO_60DB71_WORKINGDAYS_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5186 (class 2606 OID 19963)
-- Name: AO_733371_EVENT_PARAMETER AO_733371_EVENT_PARAMETER_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_733371_EVENT_PARAMETER"
    ADD CONSTRAINT "AO_733371_EVENT_PARAMETER_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5180 (class 2606 OID 19947)
-- Name: AO_733371_EVENT_RECIPIENT AO_733371_EVENT_RECIPIENT_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_733371_EVENT_RECIPIENT"
    ADD CONSTRAINT "AO_733371_EVENT_RECIPIENT_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5173 (class 2606 OID 19933)
-- Name: AO_733371_EVENT AO_733371_EVENT_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_733371_EVENT"
    ADD CONSTRAINT "AO_733371_EVENT_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5051 (class 2606 OID 19432)
-- Name: AO_97EDAB_USERINVITATION AO_97EDAB_USERINVITATION_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_97EDAB_USERINVITATION"
    ADD CONSTRAINT "AO_97EDAB_USERINVITATION_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 4977 (class 2606 OID 19078)
-- Name: AO_A0B856_WEB_HOOK_LISTENER_AO AO_A0B856_WEB_HOOK_LISTENER_AO_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_A0B856_WEB_HOOK_LISTENER_AO"
    ADD CONSTRAINT "AO_A0B856_WEB_HOOK_LISTENER_AO_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 4969 (class 2606 OID 19042)
-- Name: AO_B9A0F0_APPLIED_TEMPLATE AO_B9A0F0_APPLIED_TEMPLATE_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_B9A0F0_APPLIED_TEMPLATE"
    ADD CONSTRAINT "AO_B9A0F0_APPLIED_TEMPLATE_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5049 (class 2606 OID 19421)
-- Name: AO_C16815_ALERT_AO AO_C16815_ALERT_AO_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_C16815_ALERT_AO"
    ADD CONSTRAINT "AO_C16815_ALERT_AO_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5055 (class 2606 OID 19459)
-- Name: AO_CFF990_AOTRANSITION_FAILURE AO_CFF990_AOTRANSITION_FAILURE_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_CFF990_AOTRANSITION_FAILURE"
    ADD CONSTRAINT "AO_CFF990_AOTRANSITION_FAILURE_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5087 (class 2606 OID 19597)
-- Name: AO_E8B6CC_BRANCH_HEAD_MAPPING AO_E8B6CC_BRANCH_HEAD_MAPPING_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_BRANCH_HEAD_MAPPING"
    ADD CONSTRAINT "AO_E8B6CC_BRANCH_HEAD_MAPPING_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5095 (class 2606 OID 19625)
-- Name: AO_E8B6CC_BRANCH AO_E8B6CC_BRANCH_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_BRANCH"
    ADD CONSTRAINT "AO_E8B6CC_BRANCH_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5065 (class 2606 OID 19517)
-- Name: AO_E8B6CC_CHANGESET_MAPPING AO_E8B6CC_CHANGESET_MAPPING_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_CHANGESET_MAPPING"
    ADD CONSTRAINT "AO_E8B6CC_CHANGESET_MAPPING_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5119 (class 2606 OID 19718)
-- Name: AO_E8B6CC_COMMIT AO_E8B6CC_COMMIT_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_COMMIT"
    ADD CONSTRAINT "AO_E8B6CC_COMMIT_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5106 (class 2606 OID 19661)
-- Name: AO_E8B6CC_GIT_HUB_EVENT AO_E8B6CC_GIT_HUB_EVENT_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_GIT_HUB_EVENT"
    ADD CONSTRAINT "AO_E8B6CC_GIT_HUB_EVENT_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5061 (class 2606 OID 19493)
-- Name: AO_E8B6CC_ISSUE_MAPPING_V2 AO_E8B6CC_ISSUE_MAPPING_V2_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_ISSUE_MAPPING_V2"
    ADD CONSTRAINT "AO_E8B6CC_ISSUE_MAPPING_V2_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5057 (class 2606 OID 19470)
-- Name: AO_E8B6CC_ISSUE_MAPPING AO_E8B6CC_ISSUE_MAPPING_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_ISSUE_MAPPING"
    ADD CONSTRAINT "AO_E8B6CC_ISSUE_MAPPING_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5130 (class 2606 OID 19760)
-- Name: AO_E8B6CC_ISSUE_TO_BRANCH AO_E8B6CC_ISSUE_TO_BRANCH_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_ISSUE_TO_BRANCH"
    ADD CONSTRAINT "AO_E8B6CC_ISSUE_TO_BRANCH_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5082 (class 2606 OID 19576)
-- Name: AO_E8B6CC_ISSUE_TO_CHANGESET AO_E8B6CC_ISSUE_TO_CHANGESET_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_ISSUE_TO_CHANGESET"
    ADD CONSTRAINT "AO_E8B6CC_ISSUE_TO_CHANGESET_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5138 (class 2606 OID 19799)
-- Name: AO_E8B6CC_MESSAGE_QUEUE_ITEM AO_E8B6CC_MESSAGE_QUEUE_ITEM_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_MESSAGE_QUEUE_ITEM"
    ADD CONSTRAINT "AO_E8B6CC_MESSAGE_QUEUE_ITEM_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5134 (class 2606 OID 19782)
-- Name: AO_E8B6CC_MESSAGE_TAG AO_E8B6CC_MESSAGE_TAG_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_MESSAGE_TAG"
    ADD CONSTRAINT "AO_E8B6CC_MESSAGE_TAG_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5116 (class 2606 OID 19706)
-- Name: AO_E8B6CC_MESSAGE AO_E8B6CC_MESSAGE_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_MESSAGE"
    ADD CONSTRAINT "AO_E8B6CC_MESSAGE_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5071 (class 2606 OID 19528)
-- Name: AO_E8B6CC_ORGANIZATION_MAPPING AO_E8B6CC_ORGANIZATION_MAPPING_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_ORGANIZATION_MAPPING"
    ADD CONSTRAINT "AO_E8B6CC_ORGANIZATION_MAPPING_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5110 (class 2606 OID 19674)
-- Name: AO_E8B6CC_ORG_TO_PROJECT AO_E8B6CC_ORG_TO_PROJECT_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_ORG_TO_PROJECT"
    ADD CONSTRAINT "AO_E8B6CC_ORG_TO_PROJECT_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5063 (class 2606 OID 19504)
-- Name: AO_E8B6CC_PROJECT_MAPPING_V2 AO_E8B6CC_PROJECT_MAPPING_V2_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_PROJECT_MAPPING_V2"
    ADD CONSTRAINT "AO_E8B6CC_PROJECT_MAPPING_V2_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5059 (class 2606 OID 19481)
-- Name: AO_E8B6CC_PROJECT_MAPPING AO_E8B6CC_PROJECT_MAPPING_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_PROJECT_MAPPING"
    ADD CONSTRAINT "AO_E8B6CC_PROJECT_MAPPING_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5098 (class 2606 OID 19640)
-- Name: AO_E8B6CC_PR_ISSUE_KEY AO_E8B6CC_PR_ISSUE_KEY_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_PR_ISSUE_KEY"
    ADD CONSTRAINT "AO_E8B6CC_PR_ISSUE_KEY_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5126 (class 2606 OID 19745)
-- Name: AO_E8B6CC_PR_PARTICIPANT AO_E8B6CC_PR_PARTICIPANT_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_PR_PARTICIPANT"
    ADD CONSTRAINT "AO_E8B6CC_PR_PARTICIPANT_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5143 (class 2606 OID 19815)
-- Name: AO_E8B6CC_PR_TO_COMMIT AO_E8B6CC_PR_TO_COMMIT_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_PR_TO_COMMIT"
    ADD CONSTRAINT "AO_E8B6CC_PR_TO_COMMIT_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5090 (class 2606 OID 19617)
-- Name: AO_E8B6CC_PULL_REQUEST AO_E8B6CC_PULL_REQUEST_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_PULL_REQUEST"
    ADD CONSTRAINT "AO_E8B6CC_PULL_REQUEST_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5074 (class 2606 OID 19540)
-- Name: AO_E8B6CC_REPOSITORY_MAPPING AO_E8B6CC_REPOSITORY_MAPPING_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_REPOSITORY_MAPPING"
    ADD CONSTRAINT "AO_E8B6CC_REPOSITORY_MAPPING_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5078 (class 2606 OID 19555)
-- Name: AO_E8B6CC_REPO_TO_CHANGESET AO_E8B6CC_REPO_TO_CHANGESET_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_REPO_TO_CHANGESET"
    ADD CONSTRAINT "AO_E8B6CC_REPO_TO_CHANGESET_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5123 (class 2606 OID 19726)
-- Name: AO_E8B6CC_REPO_TO_PROJECT AO_E8B6CC_REPO_TO_PROJECT_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_REPO_TO_PROJECT"
    ADD CONSTRAINT "AO_E8B6CC_REPO_TO_PROJECT_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5113 (class 2606 OID 19694)
-- Name: AO_E8B6CC_SYNC_AUDIT_LOG AO_E8B6CC_SYNC_AUDIT_LOG_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_SYNC_AUDIT_LOG"
    ADD CONSTRAINT "AO_E8B6CC_SYNC_AUDIT_LOG_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5103 (class 2606 OID 19652)
-- Name: AO_E8B6CC_SYNC_EVENT AO_E8B6CC_SYNC_EVENT_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_SYNC_EVENT"
    ADD CONSTRAINT "AO_E8B6CC_SYNC_EVENT_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 5218 (class 2606 OID 20094)
-- Name: AO_ED669C_SEEN_ASSERTIONS AO_ED669C_SEEN_ASSERTIONS_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_ED669C_SEEN_ASSERTIONS"
    ADD CONSTRAINT "AO_ED669C_SEEN_ASSERTIONS_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 4435 (class 2606 OID 16428)
-- Name: app_user pk_app_user; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.app_user
    ADD CONSTRAINT pk_app_user PRIMARY KEY (id);


--
-- TOC entry 4440 (class 2606 OID 16436)
-- Name: audit_changed_value pk_audit_changed_value; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.audit_changed_value
    ADD CONSTRAINT pk_audit_changed_value PRIMARY KEY (id);


--
-- TOC entry 4443 (class 2606 OID 16444)
-- Name: audit_item pk_audit_item; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.audit_item
    ADD CONSTRAINT pk_audit_item PRIMARY KEY (id);


--
-- TOC entry 4446 (class 2606 OID 16452)
-- Name: audit_log pk_audit_log; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.audit_log
    ADD CONSTRAINT pk_audit_log PRIMARY KEY (id);


--
-- TOC entry 4450 (class 2606 OID 16460)
-- Name: avatar pk_avatar; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.avatar
    ADD CONSTRAINT pk_avatar PRIMARY KEY (id);


--
-- TOC entry 4452 (class 2606 OID 16468)
-- Name: board pk_board; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.board
    ADD CONSTRAINT pk_board PRIMARY KEY (id);


--
-- TOC entry 4456 (class 2606 OID 16473)
-- Name: boardproject pk_boardproject; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.boardproject
    ADD CONSTRAINT pk_boardproject PRIMARY KEY (board_id, project_id);


--
-- TOC entry 4460 (class 2606 OID 16478)
-- Name: changegroup pk_changegroup; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.changegroup
    ADD CONSTRAINT pk_changegroup PRIMARY KEY (id);


--
-- TOC entry 4463 (class 2606 OID 16486)
-- Name: changeitem pk_changeitem; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.changeitem
    ADD CONSTRAINT pk_changeitem PRIMARY KEY (id);


--
-- TOC entry 4481 (class 2606 OID 16519)
-- Name: clusteredjob pk_clusteredjob; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.clusteredjob
    ADD CONSTRAINT pk_clusteredjob PRIMARY KEY (id);


--
-- TOC entry 4466 (class 2606 OID 16491)
-- Name: clusterlockstatus pk_clusterlockstatus; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.clusterlockstatus
    ADD CONSTRAINT pk_clusterlockstatus PRIMARY KEY (id);


--
-- TOC entry 4468 (class 2606 OID 16496)
-- Name: clustermessage pk_clustermessage; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.clustermessage
    ADD CONSTRAINT pk_clustermessage PRIMARY KEY (id);


--
-- TOC entry 4471 (class 2606 OID 16501)
-- Name: clusternode pk_clusternode; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.clusternode
    ADD CONSTRAINT pk_clusternode PRIMARY KEY (node_id);


--
-- TOC entry 4473 (class 2606 OID 16506)
-- Name: clusternodeheartbeat pk_clusternodeheartbeat; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.clusternodeheartbeat
    ADD CONSTRAINT pk_clusternodeheartbeat PRIMARY KEY (node_id);


--
-- TOC entry 4476 (class 2606 OID 16511)
-- Name: clusterupgradestate pk_clusterupgradestate; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.clusterupgradestate
    ADD CONSTRAINT pk_clusterupgradestate PRIMARY KEY (id);


--
-- TOC entry 4485 (class 2606 OID 16524)
-- Name: columnlayout pk_columnlayout; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.columnlayout
    ADD CONSTRAINT pk_columnlayout PRIMARY KEY (id);


--
-- TOC entry 4488 (class 2606 OID 16529)
-- Name: columnlayoutitem pk_columnlayoutitem; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.columnlayoutitem
    ADD CONSTRAINT pk_columnlayoutitem PRIMARY KEY (id);


--
-- TOC entry 4492 (class 2606 OID 16537)
-- Name: component pk_component; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.component
    ADD CONSTRAINT pk_component PRIMARY KEY (id);


--
-- TOC entry 4498 (class 2606 OID 16542)
-- Name: configurationcontext pk_configurationcontext; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.configurationcontext
    ADD CONSTRAINT pk_configurationcontext PRIMARY KEY (id);


--
-- TOC entry 4500 (class 2606 OID 16550)
-- Name: customfield pk_customfield; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.customfield
    ADD CONSTRAINT pk_customfield PRIMARY KEY (id);


--
-- TOC entry 4503 (class 2606 OID 16555)
-- Name: customfieldoption pk_customfieldoption; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.customfieldoption
    ADD CONSTRAINT pk_customfieldoption PRIMARY KEY (id);


--
-- TOC entry 4506 (class 2606 OID 16563)
-- Name: customfieldvalue pk_customfieldvalue; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.customfieldvalue
    ADD CONSTRAINT pk_customfieldvalue PRIMARY KEY (id);


--
-- TOC entry 4432 (class 2606 OID 16420)
-- Name: cwd_application pk_cwd_application; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cwd_application
    ADD CONSTRAINT pk_cwd_application PRIMARY KEY (id);


--
-- TOC entry 4862 (class 2606 OID 17388)
-- Name: cwd_application_address pk_cwd_application_address; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cwd_application_address
    ADD CONSTRAINT pk_cwd_application_address PRIMARY KEY (application_id, remote_address);


--
-- TOC entry 4515 (class 2606 OID 16579)
-- Name: cwd_directory pk_cwd_directory; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cwd_directory
    ADD CONSTRAINT pk_cwd_directory PRIMARY KEY (id);


--
-- TOC entry 4518 (class 2606 OID 16587)
-- Name: cwd_directory_attribute pk_cwd_directory_attribute; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cwd_directory_attribute
    ADD CONSTRAINT pk_cwd_directory_attribute PRIMARY KEY (directory_id, attribute_name);


--
-- TOC entry 4520 (class 2606 OID 16592)
-- Name: cwd_directory_operation pk_cwd_directory_operation; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cwd_directory_operation
    ADD CONSTRAINT pk_cwd_directory_operation PRIMARY KEY (directory_id, operation_type);


--
-- TOC entry 4610 (class 2606 OID 16811)
-- Name: cwd_group pk_cwd_group; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cwd_group
    ADD CONSTRAINT pk_cwd_group PRIMARY KEY (id);


--
-- TOC entry 4614 (class 2606 OID 16819)
-- Name: cwd_group_attributes pk_cwd_group_attributes; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cwd_group_attributes
    ADD CONSTRAINT pk_cwd_group_attributes PRIMARY KEY (id);


--
-- TOC entry 4720 (class 2606 OID 17030)
-- Name: cwd_membership pk_cwd_membership; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cwd_membership
    ADD CONSTRAINT pk_cwd_membership PRIMARY KEY (id);


--
-- TOC entry 4921 (class 2606 OID 17536)
-- Name: cwd_user pk_cwd_user; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cwd_user
    ADD CONSTRAINT pk_cwd_user PRIMARY KEY (id);


--
-- TOC entry 4930 (class 2606 OID 17549)
-- Name: cwd_user_attributes pk_cwd_user_attributes; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cwd_user_attributes
    ADD CONSTRAINT pk_cwd_user_attributes PRIMARY KEY (id);


--
-- TOC entry 4510 (class 2606 OID 16571)
-- Name: deadletter pk_deadletter; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.deadletter
    ADD CONSTRAINT pk_deadletter PRIMARY KEY (id);


--
-- TOC entry 4525 (class 2606 OID 16608)
-- Name: draftworkflowscheme pk_draftworkflowscheme; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.draftworkflowscheme
    ADD CONSTRAINT pk_draftworkflowscheme PRIMARY KEY (id);


--
-- TOC entry 4528 (class 2606 OID 16616)
-- Name: draftworkflowschemeentity pk_draftworkflowschemeentity; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.draftworkflowschemeentity
    ADD CONSTRAINT pk_draftworkflowschemeentity PRIMARY KEY (id);


--
-- TOC entry 4532 (class 2606 OID 16624)
-- Name: entity_property pk_entity_property; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.entity_property
    ADD CONSTRAINT pk_entity_property PRIMARY KEY (id);


--
-- TOC entry 4535 (class 2606 OID 16632)
-- Name: entity_property_index_document pk_entity_property_index_docum; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.entity_property_index_document
    ADD CONSTRAINT pk_entity_property_index_docum PRIMARY KEY (id);


--
-- TOC entry 4538 (class 2606 OID 16640)
-- Name: entity_translation pk_entity_translation; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.entity_translation
    ADD CONSTRAINT pk_entity_translation PRIMARY KEY (id);


--
-- TOC entry 4544 (class 2606 OID 16656)
-- Name: external_entities pk_external_entities; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.external_entities
    ADD CONSTRAINT pk_external_entities PRIMARY KEY (id);


--
-- TOC entry 4546 (class 2606 OID 16664)
-- Name: externalgadget pk_externalgadget; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.externalgadget
    ADD CONSTRAINT pk_externalgadget PRIMARY KEY (id);


--
-- TOC entry 4549 (class 2606 OID 16669)
-- Name: favouriteassociations pk_favouriteassociations; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.favouriteassociations
    ADD CONSTRAINT pk_favouriteassociations PRIMARY KEY (id);


--
-- TOC entry 4552 (class 2606 OID 16677)
-- Name: feature pk_feature; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature
    ADD CONSTRAINT pk_feature PRIMARY KEY (id);


--
-- TOC entry 4555 (class 2606 OID 16685)
-- Name: fieldconfigscheme pk_fieldconfigscheme; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fieldconfigscheme
    ADD CONSTRAINT pk_fieldconfigscheme PRIMARY KEY (id);


--
-- TOC entry 4559 (class 2606 OID 16690)
-- Name: fieldconfigschemeissuetype pk_fieldconfigschemeissuetype; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fieldconfigschemeissuetype
    ADD CONSTRAINT pk_fieldconfigschemeissuetype PRIMARY KEY (id);


--
-- TOC entry 4562 (class 2606 OID 16698)
-- Name: fieldconfiguration pk_fieldconfiguration; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fieldconfiguration
    ADD CONSTRAINT pk_fieldconfiguration PRIMARY KEY (id);


--
-- TOC entry 4564 (class 2606 OID 16706)
-- Name: fieldlayout pk_fieldlayout; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fieldlayout
    ADD CONSTRAINT pk_fieldlayout PRIMARY KEY (id);


--
-- TOC entry 4568 (class 2606 OID 16714)
-- Name: fieldlayoutitem pk_fieldlayoutitem; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fieldlayoutitem
    ADD CONSTRAINT pk_fieldlayoutitem PRIMARY KEY (id);


--
-- TOC entry 4570 (class 2606 OID 16722)
-- Name: fieldlayoutscheme pk_fieldlayoutscheme; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fieldlayoutscheme
    ADD CONSTRAINT pk_fieldlayoutscheme PRIMARY KEY (id);


--
-- TOC entry 4573 (class 2606 OID 16727)
-- Name: fieldlayoutschemeassociation pk_fieldlayoutschemeassociatio; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fieldlayoutschemeassociation
    ADD CONSTRAINT pk_fieldlayoutschemeassociatio PRIMARY KEY (id);


--
-- TOC entry 4577 (class 2606 OID 16732)
-- Name: fieldlayoutschemeentity pk_fieldlayoutschemeentity; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fieldlayoutschemeentity
    ADD CONSTRAINT pk_fieldlayoutschemeentity PRIMARY KEY (id);


--
-- TOC entry 4579 (class 2606 OID 16740)
-- Name: fieldscreen pk_fieldscreen; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fieldscreen
    ADD CONSTRAINT pk_fieldscreen PRIMARY KEY (id);


--
-- TOC entry 4583 (class 2606 OID 16745)
-- Name: fieldscreenlayoutitem pk_fieldscreenlayoutitem; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fieldscreenlayoutitem
    ADD CONSTRAINT pk_fieldscreenlayoutitem PRIMARY KEY (id);


--
-- TOC entry 4585 (class 2606 OID 16753)
-- Name: fieldscreenscheme pk_fieldscreenscheme; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fieldscreenscheme
    ADD CONSTRAINT pk_fieldscreenscheme PRIMARY KEY (id);


--
-- TOC entry 4587 (class 2606 OID 16758)
-- Name: fieldscreenschemeitem pk_fieldscreenschemeitem; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fieldscreenschemeitem
    ADD CONSTRAINT pk_fieldscreenschemeitem PRIMARY KEY (id);


--
-- TOC entry 4591 (class 2606 OID 16766)
-- Name: fieldscreentab pk_fieldscreentab; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fieldscreentab
    ADD CONSTRAINT pk_fieldscreentab PRIMARY KEY (id);


--
-- TOC entry 4594 (class 2606 OID 16774)
-- Name: fileattachment pk_fileattachment; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fileattachment
    ADD CONSTRAINT pk_fileattachment PRIMARY KEY (id);


--
-- TOC entry 4596 (class 2606 OID 16779)
-- Name: filtersubscription pk_filtersubscription; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.filtersubscription
    ADD CONSTRAINT pk_filtersubscription PRIMARY KEY (id);


--
-- TOC entry 4600 (class 2606 OID 16787)
-- Name: gadgetuserpreference pk_gadgetuserpreference; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.gadgetuserpreference
    ADD CONSTRAINT pk_gadgetuserpreference PRIMARY KEY (id);


--
-- TOC entry 4603 (class 2606 OID 16795)
-- Name: genericconfiguration pk_genericconfiguration; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.genericconfiguration
    ADD CONSTRAINT pk_genericconfiguration PRIMARY KEY (id);


--
-- TOC entry 4606 (class 2606 OID 16803)
-- Name: globalpermissionentry pk_globalpermissionentry; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.globalpermissionentry
    ADD CONSTRAINT pk_globalpermissionentry PRIMARY KEY (id);


--
-- TOC entry 4763 (class 2606 OID 17116)
-- Name: groupbase pk_groupbase; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.groupbase
    ADD CONSTRAINT pk_groupbase PRIMARY KEY (id);


--
-- TOC entry 4630 (class 2606 OID 16835)
-- Name: issue_field_option pk_issue_field_option; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.issue_field_option
    ADD CONSTRAINT pk_issue_field_option PRIMARY KEY (id);


--
-- TOC entry 4632 (class 2606 OID 16843)
-- Name: issue_field_option_scope pk_issue_field_option_scope; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.issue_field_option_scope
    ADD CONSTRAINT pk_issue_field_option_scope PRIMARY KEY (id);


--
-- TOC entry 4637 (class 2606 OID 16848)
-- Name: issuelink pk_issuelink; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.issuelink
    ADD CONSTRAINT pk_issuelink PRIMARY KEY (id);


--
-- TOC entry 4641 (class 2606 OID 16856)
-- Name: issuelinktype pk_issuelinktype; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.issuelinktype
    ADD CONSTRAINT pk_issuelinktype PRIMARY KEY (id);


--
-- TOC entry 4643 (class 2606 OID 16864)
-- Name: issuesecurityscheme pk_issuesecurityscheme; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.issuesecurityscheme
    ADD CONSTRAINT pk_issuesecurityscheme PRIMARY KEY (id);


--
-- TOC entry 4898 (class 2606 OID 17478)
-- Name: issuestatus pk_issuestatus; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.issuestatus
    ADD CONSTRAINT pk_issuestatus PRIMARY KEY (id);


--
-- TOC entry 4645 (class 2606 OID 16872)
-- Name: issuetype pk_issuetype; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.issuetype
    ADD CONSTRAINT pk_issuetype PRIMARY KEY (id);


--
-- TOC entry 4647 (class 2606 OID 16880)
-- Name: issuetypescreenscheme pk_issuetypescreenscheme; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.issuetypescreenscheme
    ADD CONSTRAINT pk_issuetypescreenscheme PRIMARY KEY (id);


--
-- TOC entry 4650 (class 2606 OID 16885)
-- Name: issuetypescreenschemeentity pk_issuetypescreenschemeentity; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.issuetypescreenschemeentity
    ADD CONSTRAINT pk_issuetypescreenschemeentity PRIMARY KEY (id);


--
-- TOC entry 4430 (class 2606 OID 16412)
-- Name: jiraaction pk_jiraaction; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.jiraaction
    ADD CONSTRAINT pk_jiraaction PRIMARY KEY (id);


--
-- TOC entry 4522 (class 2606 OID 16600)
-- Name: jiradraftworkflows pk_jiradraftworkflows; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.jiradraftworkflows
    ADD CONSTRAINT pk_jiradraftworkflows PRIMARY KEY (id);


--
-- TOC entry 4541 (class 2606 OID 16648)
-- Name: jiraeventtype pk_jiraeventtype; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.jiraeventtype
    ADD CONSTRAINT pk_jiraeventtype PRIMARY KEY (id);


--
-- TOC entry 4628 (class 2606 OID 16827)
-- Name: jiraissue pk_jiraissue; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.jiraissue
    ADD CONSTRAINT pk_jiraissue PRIMARY KEY (id);


--
-- TOC entry 4799 (class 2606 OID 17204)
-- Name: jiraperms pk_jiraperms; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.jiraperms
    ADD CONSTRAINT pk_jiraperms PRIMARY KEY (id);


--
-- TOC entry 4955 (class 2606 OID 17601)
-- Name: jiraworkflows pk_jiraworkflows; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.jiraworkflows
    ADD CONSTRAINT pk_jiraworkflows PRIMARY KEY (id);


--
-- TOC entry 4963 (class 2606 OID 17625)
-- Name: jiraworkflowstatuses pk_jiraworkflowstatuses; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.jiraworkflowstatuses
    ADD CONSTRAINT pk_jiraworkflowstatuses PRIMARY KEY (id);


--
-- TOC entry 4652 (class 2606 OID 16893)
-- Name: jquartz_blob_triggers pk_jquartz_blob_triggers; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.jquartz_blob_triggers
    ADD CONSTRAINT pk_jquartz_blob_triggers PRIMARY KEY (trigger_name, trigger_group);


--
-- TOC entry 4654 (class 2606 OID 16901)
-- Name: jquartz_calendars pk_jquartz_calendars; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.jquartz_calendars
    ADD CONSTRAINT pk_jquartz_calendars PRIMARY KEY (calendar_name);


--
-- TOC entry 4656 (class 2606 OID 16909)
-- Name: jquartz_cron_triggers pk_jquartz_cron_triggers; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.jquartz_cron_triggers
    ADD CONSTRAINT pk_jquartz_cron_triggers PRIMARY KEY (trigger_name, trigger_group);


--
-- TOC entry 4664 (class 2606 OID 16917)
-- Name: jquartz_fired_triggers pk_jquartz_fired_triggers; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.jquartz_fired_triggers
    ADD CONSTRAINT pk_jquartz_fired_triggers PRIMARY KEY (entry_id);


--
-- TOC entry 4668 (class 2606 OID 16925)
-- Name: jquartz_job_details pk_jquartz_job_details; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.jquartz_job_details
    ADD CONSTRAINT pk_jquartz_job_details PRIMARY KEY (job_name, job_group);


--
-- TOC entry 4670 (class 2606 OID 16933)
-- Name: jquartz_job_listeners pk_jquartz_job_listeners; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.jquartz_job_listeners
    ADD CONSTRAINT pk_jquartz_job_listeners PRIMARY KEY (job_name, job_group, job_listener);


--
-- TOC entry 4672 (class 2606 OID 16938)
-- Name: jquartz_locks pk_jquartz_locks; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.jquartz_locks
    ADD CONSTRAINT pk_jquartz_locks PRIMARY KEY (lock_name);


--
-- TOC entry 4674 (class 2606 OID 16943)
-- Name: jquartz_paused_trigger_grps pk_jquartz_paused_trigger_grps; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.jquartz_paused_trigger_grps
    ADD CONSTRAINT pk_jquartz_paused_trigger_grps PRIMARY KEY (trigger_group);


--
-- TOC entry 4676 (class 2606 OID 16948)
-- Name: jquartz_scheduler_state pk_jquartz_scheduler_state; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.jquartz_scheduler_state
    ADD CONSTRAINT pk_jquartz_scheduler_state PRIMARY KEY (instance_name);


--
-- TOC entry 4680 (class 2606 OID 16964)
-- Name: jquartz_simple_triggers pk_jquartz_simple_triggers; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.jquartz_simple_triggers
    ADD CONSTRAINT pk_jquartz_simple_triggers PRIMARY KEY (trigger_name, trigger_group);


--
-- TOC entry 4678 (class 2606 OID 16956)
-- Name: jquartz_simprop_triggers pk_jquartz_simprop_triggers; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.jquartz_simprop_triggers
    ADD CONSTRAINT pk_jquartz_simprop_triggers PRIMARY KEY (trigger_name, trigger_group);


--
-- TOC entry 4682 (class 2606 OID 16972)
-- Name: jquartz_trigger_listeners pk_jquartz_trigger_listeners; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.jquartz_trigger_listeners
    ADD CONSTRAINT pk_jquartz_trigger_listeners PRIMARY KEY (trigger_group, trigger_listener);


--
-- TOC entry 4696 (class 2606 OID 16980)
-- Name: jquartz_triggers pk_jquartz_triggers; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.jquartz_triggers
    ADD CONSTRAINT pk_jquartz_triggers PRIMARY KEY (trigger_name, trigger_group);


--
-- TOC entry 4702 (class 2606 OID 16985)
-- Name: label pk_label; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.label
    ADD CONSTRAINT pk_label PRIMARY KEY (id);


--
-- TOC entry 4705 (class 2606 OID 16990)
-- Name: licenserolesdefault pk_licenserolesdefault; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.licenserolesdefault
    ADD CONSTRAINT pk_licenserolesdefault PRIMARY KEY (id);


--
-- TOC entry 4708 (class 2606 OID 16998)
-- Name: licenserolesgroup pk_licenserolesgroup; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.licenserolesgroup
    ADD CONSTRAINT pk_licenserolesgroup PRIMARY KEY (id);


--
-- TOC entry 4710 (class 2606 OID 17006)
-- Name: listenerconfig pk_listenerconfig; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.listenerconfig
    ADD CONSTRAINT pk_listenerconfig PRIMARY KEY (id);


--
-- TOC entry 4712 (class 2606 OID 17014)
-- Name: mailserver pk_mailserver; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.mailserver
    ADD CONSTRAINT pk_mailserver PRIMARY KEY (id);


--
-- TOC entry 4715 (class 2606 OID 17022)
-- Name: managedconfigurationitem pk_managedconfigurationitem; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.managedconfigurationitem
    ADD CONSTRAINT pk_managedconfigurationitem PRIMARY KEY (id);


--
-- TOC entry 4772 (class 2606 OID 17137)
-- Name: membershipbase pk_membershipbase; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.membershipbase
    ADD CONSTRAINT pk_membershipbase PRIMARY KEY (id);


--
-- TOC entry 4724 (class 2606 OID 17035)
-- Name: moved_issue_key pk_moved_issue_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.moved_issue_key
    ADD CONSTRAINT pk_moved_issue_key PRIMARY KEY (id);


--
-- TOC entry 4728 (class 2606 OID 17040)
-- Name: nodeassociation pk_nodeassociation; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.nodeassociation
    ADD CONSTRAINT pk_nodeassociation PRIMARY KEY (source_node_id, source_node_entity, sink_node_id, sink_node_entity, association_type);


--
-- TOC entry 4731 (class 2606 OID 17045)
-- Name: nodeindexcounter pk_nodeindexcounter; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.nodeindexcounter
    ADD CONSTRAINT pk_nodeindexcounter PRIMARY KEY (id);


--
-- TOC entry 4734 (class 2606 OID 17050)
-- Name: notification pk_notification; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notification
    ADD CONSTRAINT pk_notification PRIMARY KEY (id);


--
-- TOC entry 4738 (class 2606 OID 17058)
-- Name: notificationinstance pk_notificationinstance; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notificationinstance
    ADD CONSTRAINT pk_notificationinstance PRIMARY KEY (id);


--
-- TOC entry 4740 (class 2606 OID 17066)
-- Name: notificationscheme pk_notificationscheme; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notificationscheme
    ADD CONSTRAINT pk_notificationscheme PRIMARY KEY (id);


--
-- TOC entry 4744 (class 2606 OID 17074)
-- Name: oauthconsumer pk_oauthconsumer; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.oauthconsumer
    ADD CONSTRAINT pk_oauthconsumer PRIMARY KEY (id);


--
-- TOC entry 4748 (class 2606 OID 17082)
-- Name: oauthconsumertoken pk_oauthconsumertoken; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.oauthconsumertoken
    ADD CONSTRAINT pk_oauthconsumertoken PRIMARY KEY (id);


--
-- TOC entry 4751 (class 2606 OID 17090)
-- Name: oauthspconsumer pk_oauthspconsumer; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.oauthspconsumer
    ADD CONSTRAINT pk_oauthspconsumer PRIMARY KEY (id);


--
-- TOC entry 4755 (class 2606 OID 17098)
-- Name: oauthsptoken pk_oauthsptoken; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.oauthsptoken
    ADD CONSTRAINT pk_oauthsptoken PRIMARY KEY (id);


--
-- TOC entry 4797 (class 2606 OID 17199)
-- Name: optionconfiguration pk_optionconfiguration; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.optionconfiguration
    ADD CONSTRAINT pk_optionconfiguration PRIMARY KEY (id);


--
-- TOC entry 4757 (class 2606 OID 17106)
-- Name: os_currentstep pk_os_currentstep; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.os_currentstep
    ADD CONSTRAINT pk_os_currentstep PRIMARY KEY (id);


--
-- TOC entry 4760 (class 2606 OID 17111)
-- Name: os_currentstep_prev pk_os_currentstep_prev; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.os_currentstep_prev
    ADD CONSTRAINT pk_os_currentstep_prev PRIMARY KEY (id, previous_id);


--
-- TOC entry 4766 (class 2606 OID 17124)
-- Name: os_historystep pk_os_historystep; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.os_historystep
    ADD CONSTRAINT pk_os_historystep PRIMARY KEY (id);


--
-- TOC entry 4768 (class 2606 OID 17129)
-- Name: os_historystep_prev pk_os_historystep_prev; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.os_historystep_prev
    ADD CONSTRAINT pk_os_historystep_prev PRIMARY KEY (id, previous_id);


--
-- TOC entry 4793 (class 2606 OID 17194)
-- Name: os_wfentry pk_os_wfentry; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.os_wfentry
    ADD CONSTRAINT pk_os_wfentry PRIMARY KEY (id);


--
-- TOC entry 4801 (class 2606 OID 17212)
-- Name: permissionscheme pk_permissionscheme; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.permissionscheme
    ADD CONSTRAINT pk_permissionscheme PRIMARY KEY (id);


--
-- TOC entry 4803 (class 2606 OID 17220)
-- Name: permissionschemeattribute pk_permissionschemeattribute; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.permissionschemeattribute
    ADD CONSTRAINT pk_permissionschemeattribute PRIMARY KEY (id);


--
-- TOC entry 4807 (class 2606 OID 17225)
-- Name: pluginstate pk_pluginstate; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pluginstate
    ADD CONSTRAINT pk_pluginstate PRIMARY KEY (pluginkey);


--
-- TOC entry 4809 (class 2606 OID 17233)
-- Name: pluginversion pk_pluginversion; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pluginversion
    ADD CONSTRAINT pk_pluginversion PRIMARY KEY (id);


--
-- TOC entry 4811 (class 2606 OID 17241)
-- Name: portalpage pk_portalpage; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.portalpage
    ADD CONSTRAINT pk_portalpage PRIMARY KEY (id);


--
-- TOC entry 4814 (class 2606 OID 17249)
-- Name: portletconfiguration pk_portletconfiguration; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.portletconfiguration
    ADD CONSTRAINT pk_portletconfiguration PRIMARY KEY (id);


--
-- TOC entry 4816 (class 2606 OID 17257)
-- Name: priority pk_priority; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.priority
    ADD CONSTRAINT pk_priority PRIMARY KEY (id);


--
-- TOC entry 4818 (class 2606 OID 17265)
-- Name: productlicense pk_productlicense; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.productlicense
    ADD CONSTRAINT pk_productlicense PRIMARY KEY (id);


--
-- TOC entry 4821 (class 2606 OID 17273)
-- Name: project pk_project; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.project
    ADD CONSTRAINT pk_project PRIMARY KEY (id);


--
-- TOC entry 4830 (class 2606 OID 17291)
-- Name: project_key pk_project_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.project_key
    ADD CONSTRAINT pk_project_key PRIMARY KEY (id);


--
-- TOC entry 4824 (class 2606 OID 17281)
-- Name: projectcategory pk_projectcategory; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.projectcategory
    ADD CONSTRAINT pk_projectcategory PRIMARY KEY (id);


--
-- TOC entry 4826 (class 2606 OID 17286)
-- Name: projectchangedtime pk_projectchangedtime; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.projectchangedtime
    ADD CONSTRAINT pk_projectchangedtime PRIMARY KEY (project_id);


--
-- TOC entry 4832 (class 2606 OID 17299)
-- Name: projectrole pk_projectrole; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.projectrole
    ADD CONSTRAINT pk_projectrole PRIMARY KEY (id);


--
-- TOC entry 4834 (class 2606 OID 17307)
-- Name: projectroleactor pk_projectroleactor; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.projectroleactor
    ADD CONSTRAINT pk_projectroleactor PRIMARY KEY (id);


--
-- TOC entry 4948 (class 2606 OID 17580)
-- Name: projectversion pk_projectversion; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.projectversion
    ADD CONSTRAINT pk_projectversion PRIMARY KEY (id);


--
-- TOC entry 4774 (class 2606 OID 17142)
-- Name: propertydata pk_propertydata; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.propertydata
    ADD CONSTRAINT pk_propertydata PRIMARY KEY (id);


--
-- TOC entry 4776 (class 2606 OID 17147)
-- Name: propertydate pk_propertydate; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.propertydate
    ADD CONSTRAINT pk_propertydate PRIMARY KEY (id);


--
-- TOC entry 4778 (class 2606 OID 17152)
-- Name: propertydecimal pk_propertydecimal; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.propertydecimal
    ADD CONSTRAINT pk_propertydecimal PRIMARY KEY (id);


--
-- TOC entry 4782 (class 2606 OID 17160)
-- Name: propertyentry pk_propertyentry; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.propertyentry
    ADD CONSTRAINT pk_propertyentry PRIMARY KEY (id);


--
-- TOC entry 4784 (class 2606 OID 17165)
-- Name: propertynumber pk_propertynumber; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.propertynumber
    ADD CONSTRAINT pk_propertynumber PRIMARY KEY (id);


--
-- TOC entry 4786 (class 2606 OID 17173)
-- Name: propertystring pk_propertystring; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.propertystring
    ADD CONSTRAINT pk_propertystring PRIMARY KEY (id);


--
-- TOC entry 4788 (class 2606 OID 17181)
-- Name: propertytext pk_propertytext; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.propertytext
    ADD CONSTRAINT pk_propertytext PRIMARY KEY (id);


--
-- TOC entry 4838 (class 2606 OID 17315)
-- Name: qrtz_calendars pk_qrtz_calendars; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qrtz_calendars
    ADD CONSTRAINT pk_qrtz_calendars PRIMARY KEY (calendar_name);


--
-- TOC entry 4840 (class 2606 OID 17320)
-- Name: qrtz_cron_triggers pk_qrtz_cron_triggers; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qrtz_cron_triggers
    ADD CONSTRAINT pk_qrtz_cron_triggers PRIMARY KEY (id);


--
-- TOC entry 4842 (class 2606 OID 17328)
-- Name: qrtz_fired_triggers pk_qrtz_fired_triggers; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qrtz_fired_triggers
    ADD CONSTRAINT pk_qrtz_fired_triggers PRIMARY KEY (entry_id);


--
-- TOC entry 4844 (class 2606 OID 17336)
-- Name: qrtz_job_details pk_qrtz_job_details; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qrtz_job_details
    ADD CONSTRAINT pk_qrtz_job_details PRIMARY KEY (id);


--
-- TOC entry 4846 (class 2606 OID 17341)
-- Name: qrtz_job_listeners pk_qrtz_job_listeners; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qrtz_job_listeners
    ADD CONSTRAINT pk_qrtz_job_listeners PRIMARY KEY (id);


--
-- TOC entry 4848 (class 2606 OID 17346)
-- Name: qrtz_simple_triggers pk_qrtz_simple_triggers; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qrtz_simple_triggers
    ADD CONSTRAINT pk_qrtz_simple_triggers PRIMARY KEY (id);


--
-- TOC entry 4850 (class 2606 OID 17351)
-- Name: qrtz_trigger_listeners pk_qrtz_trigger_listeners; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qrtz_trigger_listeners
    ADD CONSTRAINT pk_qrtz_trigger_listeners PRIMARY KEY (id);


--
-- TOC entry 4852 (class 2606 OID 17359)
-- Name: qrtz_triggers pk_qrtz_triggers; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qrtz_triggers
    ADD CONSTRAINT pk_qrtz_triggers PRIMARY KEY (id);


--
-- TOC entry 4855 (class 2606 OID 17364)
-- Name: reindex_component pk_reindex_component; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reindex_component
    ADD CONSTRAINT pk_reindex_component PRIMARY KEY (id);


--
-- TOC entry 4857 (class 2606 OID 17372)
-- Name: reindex_request pk_reindex_request; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reindex_request
    ADD CONSTRAINT pk_reindex_request PRIMARY KEY (id);


--
-- TOC entry 4859 (class 2606 OID 17380)
-- Name: remembermetoken pk_remembermetoken; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.remembermetoken
    ADD CONSTRAINT pk_remembermetoken PRIMARY KEY (id);


--
-- TOC entry 4864 (class 2606 OID 17396)
-- Name: remotelink pk_remotelink; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.remotelink
    ADD CONSTRAINT pk_remotelink PRIMARY KEY (id);


--
-- TOC entry 4869 (class 2606 OID 17404)
-- Name: replicatedindexoperation pk_replicatedindexoperation; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.replicatedindexoperation
    ADD CONSTRAINT pk_replicatedindexoperation PRIMARY KEY (id);


--
-- TOC entry 4871 (class 2606 OID 17412)
-- Name: resolution pk_resolution; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.resolution
    ADD CONSTRAINT pk_resolution PRIMARY KEY (id);


--
-- TOC entry 4873 (class 2606 OID 17420)
-- Name: rundetails pk_rundetails; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rundetails
    ADD CONSTRAINT pk_rundetails PRIMARY KEY (id);


--
-- TOC entry 4877 (class 2606 OID 17428)
-- Name: schemeissuesecurities pk_schemeissuesecurities; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.schemeissuesecurities
    ADD CONSTRAINT pk_schemeissuesecurities PRIMARY KEY (id);


--
-- TOC entry 4881 (class 2606 OID 17436)
-- Name: schemeissuesecuritylevels pk_schemeissuesecuritylevels; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.schemeissuesecuritylevels
    ADD CONSTRAINT pk_schemeissuesecuritylevels PRIMARY KEY (id);


--
-- TOC entry 4884 (class 2606 OID 17444)
-- Name: schemepermissions pk_schemepermissions; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.schemepermissions
    ADD CONSTRAINT pk_schemepermissions PRIMARY KEY (id);


--
-- TOC entry 4887 (class 2606 OID 17452)
-- Name: searchrequest pk_searchrequest; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.searchrequest
    ADD CONSTRAINT pk_searchrequest PRIMARY KEY (id);


--
-- TOC entry 4891 (class 2606 OID 17457)
-- Name: sequence_value_item pk_sequence_value_item; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sequence_value_item
    ADD CONSTRAINT pk_sequence_value_item PRIMARY KEY (seq_name);


--
-- TOC entry 4893 (class 2606 OID 17465)
-- Name: serviceconfig pk_serviceconfig; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.serviceconfig
    ADD CONSTRAINT pk_serviceconfig PRIMARY KEY (id);


--
-- TOC entry 4895 (class 2606 OID 17470)
-- Name: sharepermissions pk_sharepermissions; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sharepermissions
    ADD CONSTRAINT pk_sharepermissions PRIMARY KEY (id);


--
-- TOC entry 4902 (class 2606 OID 17486)
-- Name: tempattachmentsmonitor pk_tempattachmentsmonitor; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tempattachmentsmonitor
    ADD CONSTRAINT pk_tempattachmentsmonitor PRIMARY KEY (temporary_attachment_id);


--
-- TOC entry 4904 (class 2606 OID 17494)
-- Name: trackback_ping pk_trackback_ping; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.trackback_ping
    ADD CONSTRAINT pk_trackback_ping PRIMARY KEY (id);


--
-- TOC entry 4906 (class 2606 OID 17502)
-- Name: trustedapp pk_trustedapp; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.trustedapp
    ADD CONSTRAINT pk_trustedapp PRIMARY KEY (id);


--
-- TOC entry 4909 (class 2606 OID 17510)
-- Name: upgradehistory pk_upgradehistory; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.upgradehistory
    ADD CONSTRAINT pk_upgradehistory PRIMARY KEY (upgradeclass);


--
-- TOC entry 4911 (class 2606 OID 17515)
-- Name: upgradetaskhistory pk_upgradetaskhistory; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.upgradetaskhistory
    ADD CONSTRAINT pk_upgradetaskhistory PRIMARY KEY (id);


--
-- TOC entry 4913 (class 2606 OID 17520)
-- Name: upgradetaskhistoryauditlog pk_upgradetaskhistoryauditlog; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.upgradetaskhistoryauditlog
    ADD CONSTRAINT pk_upgradetaskhistoryauditlog PRIMARY KEY (id);


--
-- TOC entry 4915 (class 2606 OID 17528)
-- Name: upgradeversionhistory pk_upgradeversionhistory; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.upgradeversionhistory
    ADD CONSTRAINT pk_upgradeversionhistory PRIMARY KEY (targetbuild);


--
-- TOC entry 4925 (class 2606 OID 17541)
-- Name: userassociation pk_userassociation; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.userassociation
    ADD CONSTRAINT pk_userassociation PRIMARY KEY (source_name, sink_node_id, sink_node_entity, association_type);


--
-- TOC entry 4791 (class 2606 OID 17189)
-- Name: userbase pk_userbase; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.userbase
    ADD CONSTRAINT pk_userbase PRIMARY KEY (id);


--
-- TOC entry 4933 (class 2606 OID 17557)
-- Name: userhistoryitem pk_userhistoryitem; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.userhistoryitem
    ADD CONSTRAINT pk_userhistoryitem PRIMARY KEY (id);


--
-- TOC entry 4936 (class 2606 OID 17562)
-- Name: userpickerfilter pk_userpickerfilter; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.userpickerfilter
    ADD CONSTRAINT pk_userpickerfilter PRIMARY KEY (id);


--
-- TOC entry 4941 (class 2606 OID 17567)
-- Name: userpickerfiltergroup pk_userpickerfiltergroup; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.userpickerfiltergroup
    ADD CONSTRAINT pk_userpickerfiltergroup PRIMARY KEY (id);


--
-- TOC entry 4944 (class 2606 OID 17572)
-- Name: userpickerfilterrole pk_userpickerfilterrole; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.userpickerfilterrole
    ADD CONSTRAINT pk_userpickerfilterrole PRIMARY KEY (id);


--
-- TOC entry 4950 (class 2606 OID 17588)
-- Name: versioncontrol pk_versioncontrol; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.versioncontrol
    ADD CONSTRAINT pk_versioncontrol PRIMARY KEY (id);


--
-- TOC entry 4952 (class 2606 OID 17593)
-- Name: votehistory pk_votehistory; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.votehistory
    ADD CONSTRAINT pk_votehistory PRIMARY KEY (id);


--
-- TOC entry 4957 (class 2606 OID 17609)
-- Name: workflowscheme pk_workflowscheme; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.workflowscheme
    ADD CONSTRAINT pk_workflowscheme PRIMARY KEY (id);


--
-- TOC entry 4959 (class 2606 OID 17617)
-- Name: workflowschemeentity pk_workflowschemeentity; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.workflowschemeentity
    ADD CONSTRAINT pk_workflowschemeentity PRIMARY KEY (id);


--
-- TOC entry 4965 (class 2606 OID 17633)
-- Name: worklog pk_worklog; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.worklog
    ADD CONSTRAINT pk_worklog PRIMARY KEY (id);


--
-- TOC entry 5246 (class 2606 OID 20221)
-- Name: AO_4789DD_TASK_MONITOR u_ao_4789dd_task_mo1827547914; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_4789DD_TASK_MONITOR"
    ADD CONSTRAINT u_ao_4789dd_task_mo1827547914 UNIQUE ("TASK_ID");


--
-- TOC entry 5214 (class 2606 OID 20067)
-- Name: AO_587B34_PROJECT_CONFIG u_ao_587b34_project2070954277; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_587B34_PROJECT_CONFIG"
    ADD CONSTRAINT u_ao_587b34_project2070954277 UNIQUE ("NAME_UNIQUE_CONSTRAINT");


--
-- TOC entry 5221 (class 2606 OID 20096)
-- Name: AO_ED669C_SEEN_ASSERTIONS u_ao_ed669c_seen_as1055534769; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_ED669C_SEEN_ASSERTIONS"
    ADD CONSTRAINT u_ao_ed669c_seen_as1055534769 UNIQUE ("ASSERTION_ID");


--
-- TOC entry 4427 (class 1259 OID 17634)
-- Name: action_author_created; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX action_author_created ON public.jiraaction USING btree (author, created);


--
-- TOC entry 4428 (class 1259 OID 17635)
-- Name: action_issue; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX action_issue ON public.jiraaction USING btree (issueid);


--
-- TOC entry 4592 (class 1259 OID 17695)
-- Name: attach_issue; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX attach_issue ON public.fileattachment USING btree (issueid);


--
-- TOC entry 4447 (class 1259 OID 17643)
-- Name: avatar_filename_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX avatar_filename_index ON public.avatar USING btree (filename, avatartype, systemavatar);


--
-- TOC entry 4448 (class 1259 OID 17642)
-- Name: avatar_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX avatar_index ON public.avatar USING btree (avatartype, owner);


--
-- TOC entry 4501 (class 1259 OID 17664)
-- Name: cf_cfoption; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX cf_cfoption ON public.customfieldoption USING btree (customfield);


--
-- TOC entry 4939 (class 1259 OID 17816)
-- Name: cf_userpickerfiltergroup; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX cf_userpickerfiltergroup ON public.userpickerfiltergroup USING btree (userpickerfilter);


--
-- TOC entry 4942 (class 1259 OID 17817)
-- Name: cf_userpickerfilterrole; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX cf_userpickerfilterrole ON public.userpickerfilterrole USING btree (userpickerfilter);


--
-- TOC entry 4504 (class 1259 OID 17665)
-- Name: cfvalue_issue; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX cfvalue_issue ON public.customfieldvalue USING btree (issue, customfield);


--
-- TOC entry 4457 (class 1259 OID 17646)
-- Name: chggroup_author_created; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX chggroup_author_created ON public.changegroup USING btree (author, created);


--
-- TOC entry 4458 (class 1259 OID 17647)
-- Name: chggroup_issue_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX chggroup_issue_id ON public.changegroup USING btree (issueid, id);


--
-- TOC entry 4461 (class 1259 OID 17648)
-- Name: chgitem_group_field; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX chgitem_group_field ON public.changeitem USING btree (groupid, field);


--
-- TOC entry 4482 (class 1259 OID 17655)
-- Name: cl_searchrequest; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX cl_searchrequest ON public.columnlayout USING btree (searchrequest);


--
-- TOC entry 4483 (class 1259 OID 17656)
-- Name: cl_username; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX cl_username ON public.columnlayout USING btree (username);


--
-- TOC entry 4464 (class 1259 OID 17649)
-- Name: cluster_lock_name_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX cluster_lock_name_idx ON public.clusterlockstatus USING btree (lock_name);


--
-- TOC entry 4477 (class 1259 OID 20231)
-- Name: clusteredjob_jobid_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX clusteredjob_jobid_idx ON public.clusteredjob USING btree (job_id);


--
-- TOC entry 4478 (class 1259 OID 17653)
-- Name: clusteredjob_jrk_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX clusteredjob_jrk_idx ON public.clusteredjob USING btree (job_runner_key);


--
-- TOC entry 4479 (class 1259 OID 17654)
-- Name: clusteredjob_nextrun_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX clusteredjob_nextrun_idx ON public.clusteredjob USING btree (next_run);


--
-- TOC entry 4493 (class 1259 OID 17661)
-- Name: confcontext; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX confcontext ON public.configurationcontext USING btree (projectcategory, project, customfield);


--
-- TOC entry 4494 (class 1259 OID 17663)
-- Name: confcontextfieldconfigscheme; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX confcontextfieldconfigscheme ON public.configurationcontext USING btree (fieldconfigscheme);


--
-- TOC entry 4495 (class 1259 OID 17660)
-- Name: confcontextkey; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX confcontextkey ON public.configurationcontext USING btree (customfield);


--
-- TOC entry 4496 (class 1259 OID 17662)
-- Name: confcontextprojectkey; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX confcontextprojectkey ON public.configurationcontext USING btree (project, customfield);


--
-- TOC entry 4507 (class 1259 OID 17667)
-- Name: deadletter_lastseen; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX deadletter_lastseen ON public.deadletter USING btree (last_seen);


--
-- TOC entry 4508 (class 1259 OID 17666)
-- Name: deadletter_msg_server_folder; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX deadletter_msg_server_folder ON public.deadletter USING btree (message_id, mail_server_id, folder_name);


--
-- TOC entry 4526 (class 1259 OID 17673)
-- Name: draft_workflow_scheme; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX draft_workflow_scheme ON public.draftworkflowschemeentity USING btree (scheme);


--
-- TOC entry 4523 (class 1259 OID 17672)
-- Name: draft_workflow_scheme_parent; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX draft_workflow_scheme_parent ON public.draftworkflowscheme USING btree (workflow_scheme_id);


--
-- TOC entry 4529 (class 1259 OID 17674)
-- Name: entityproperty_id_name_key; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX entityproperty_id_name_key ON public.entity_property USING btree (entity_id, entity_name, property_key);


--
-- TOC entry 4530 (class 1259 OID 17675)
-- Name: entityproperty_key_name; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX entityproperty_key_name ON public.entity_property USING btree (property_key, entity_name);


--
-- TOC entry 4536 (class 1259 OID 17677)
-- Name: entitytranslation_locale; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX entitytranslation_locale ON public.entity_translation USING btree (locale);


--
-- TOC entry 4533 (class 1259 OID 17676)
-- Name: entpropindexdoc_module; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX entpropindexdoc_module ON public.entity_property_index_document USING btree (plugin_key, module_key);


--
-- TOC entry 4542 (class 1259 OID 17679)
-- Name: ext_entity_name; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX ext_entity_name ON public.external_entities USING btree (name);


--
-- TOC entry 4547 (class 1259 OID 17680)
-- Name: favourite_index; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX favourite_index ON public.favouriteassociations USING btree (username, entitytype, entityid);


--
-- TOC entry 4560 (class 1259 OID 17685)
-- Name: fc_fieldid; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX fc_fieldid ON public.fieldconfiguration USING btree (fieldid);


--
-- TOC entry 4553 (class 1259 OID 17682)
-- Name: fcs_fieldid; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX fcs_fieldid ON public.fieldconfigscheme USING btree (fieldid);


--
-- TOC entry 4556 (class 1259 OID 17683)
-- Name: fcs_issuetype; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX fcs_issuetype ON public.fieldconfigschemeissuetype USING btree (issuetype);


--
-- TOC entry 4557 (class 1259 OID 17684)
-- Name: fcs_scheme; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX fcs_scheme ON public.fieldconfigschemeissuetype USING btree (fieldconfigscheme);


--
-- TOC entry 4550 (class 1259 OID 17681)
-- Name: feature_id_userkey; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_id_userkey ON public.feature USING btree (id, user_key);


--
-- TOC entry 4794 (class 1259 OID 17776)
-- Name: fieldid_fieldconf; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX fieldid_fieldconf ON public.optionconfiguration USING btree (fieldid, fieldconfig);


--
-- TOC entry 4795 (class 1259 OID 17775)
-- Name: fieldid_optionid; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX fieldid_optionid ON public.optionconfiguration USING btree (fieldid, optionid);


--
-- TOC entry 4574 (class 1259 OID 17690)
-- Name: fieldlayout_layout; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX fieldlayout_layout ON public.fieldlayoutschemeentity USING btree (fieldlayout);


--
-- TOC entry 4575 (class 1259 OID 17689)
-- Name: fieldlayout_scheme; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX fieldlayout_scheme ON public.fieldlayoutschemeentity USING btree (scheme);


--
-- TOC entry 4580 (class 1259 OID 17691)
-- Name: fieldscitem_tab; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX fieldscitem_tab ON public.fieldscreenlayoutitem USING btree (fieldscreentab);


--
-- TOC entry 4581 (class 1259 OID 17692)
-- Name: fieldscreen_field; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX fieldscreen_field ON public.fieldscreenlayoutitem USING btree (fieldidentifier);


--
-- TOC entry 4648 (class 1259 OID 17721)
-- Name: fieldscreen_scheme; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX fieldscreen_scheme ON public.issuetypescreenschemeentity USING btree (fieldscreenscheme);


--
-- TOC entry 4589 (class 1259 OID 17694)
-- Name: fieldscreen_tab; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX fieldscreen_tab ON public.fieldscreentab USING btree (fieldscreen);


--
-- TOC entry 4571 (class 1259 OID 17688)
-- Name: fl_scheme_assoc; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX fl_scheme_assoc ON public.fieldlayoutschemeassociation USING btree (project, issuetype);


--
-- TOC entry 4764 (class 1259 OID 17769)
-- Name: historystep_entryid; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX historystep_entryid ON public.os_historystep USING btree (entry_id);


--
-- TOC entry 4827 (class 1259 OID 17783)
-- Name: idx_all_project_ids; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_all_project_ids ON public.project_key USING btree (project_id);


--
-- TOC entry 4828 (class 1259 OID 17782)
-- Name: idx_all_project_keys; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_all_project_keys ON public.project_key USING btree (project_key);


--
-- TOC entry 4441 (class 1259 OID 17640)
-- Name: idx_audit_item_log_id2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_audit_item_log_id2 ON public.audit_item USING btree (log_id);


--
-- TOC entry 4444 (class 1259 OID 17641)
-- Name: idx_audit_log_created; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_audit_log_created ON public.audit_log USING btree (created);


--
-- TOC entry 4453 (class 1259 OID 17645)
-- Name: idx_board_board_ids; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_board_board_ids ON public.boardproject USING btree (board_id);


--
-- TOC entry 4454 (class 1259 OID 17644)
-- Name: idx_board_project_ids; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_board_project_ids ON public.boardproject USING btree (project_id);


--
-- TOC entry 4438 (class 1259 OID 17639)
-- Name: idx_changed_value_log_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_changed_value_log_id ON public.audit_changed_value USING btree (log_id);


--
-- TOC entry 4486 (class 1259 OID 17657)
-- Name: idx_cli_fieldidentifier; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_cli_fieldidentifier ON public.columnlayoutitem USING btree (fieldidentifier);


--
-- TOC entry 4489 (class 1259 OID 17659)
-- Name: idx_component_name; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_component_name ON public.component USING btree (cname);


--
-- TOC entry 4490 (class 1259 OID 17658)
-- Name: idx_component_project; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_component_project ON public.component USING btree (project);


--
-- TOC entry 4511 (class 1259 OID 17669)
-- Name: idx_directory_active; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_directory_active ON public.cwd_directory USING btree (active);


--
-- TOC entry 4512 (class 1259 OID 17670)
-- Name: idx_directory_impl; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_directory_impl ON public.cwd_directory USING btree (lower_impl_class);


--
-- TOC entry 4513 (class 1259 OID 17671)
-- Name: idx_directory_type; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_directory_type ON public.cwd_directory USING btree (directory_type);


--
-- TOC entry 4916 (class 1259 OID 17807)
-- Name: idx_display_name; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_display_name ON public.cwd_user USING btree (lower_display_name);


--
-- TOC entry 4917 (class 1259 OID 17808)
-- Name: idx_email_address; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_email_address ON public.cwd_user USING btree (lower_email_address);


--
-- TOC entry 4918 (class 1259 OID 17805)
-- Name: idx_first_name; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_first_name ON public.cwd_user USING btree (lower_first_name);


--
-- TOC entry 4565 (class 1259 OID 17686)
-- Name: idx_fli_fieldidentifier; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_fli_fieldidentifier ON public.fieldlayoutitem USING btree (fieldidentifier);


--
-- TOC entry 4566 (class 1259 OID 17687)
-- Name: idx_fli_fieldlayout; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_fli_fieldlayout ON public.fieldlayoutitem USING btree (fieldlayout);


--
-- TOC entry 4607 (class 1259 OID 17701)
-- Name: idx_group_active; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_group_active ON public.cwd_group USING btree (lower_group_name, active);


--
-- TOC entry 4612 (class 1259 OID 17703)
-- Name: idx_group_attr_dir_name_lval; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_group_attr_dir_name_lval ON public.cwd_group_attributes USING btree (directory_id, attribute_name, lower_attribute_value);


--
-- TOC entry 4608 (class 1259 OID 17702)
-- Name: idx_group_dir_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_group_dir_id ON public.cwd_group USING btree (directory_id);


--
-- TOC entry 4919 (class 1259 OID 17806)
-- Name: idx_last_name; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_last_name ON public.cwd_user USING btree (lower_last_name);


--
-- TOC entry 4716 (class 1259 OID 17752)
-- Name: idx_mem_dir_child; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_mem_dir_child ON public.cwd_membership USING btree (lower_child_name, membership_type, directory_id);


--
-- TOC entry 4717 (class 1259 OID 17751)
-- Name: idx_mem_dir_parent; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_mem_dir_parent ON public.cwd_membership USING btree (lower_parent_name, membership_type, directory_id);


--
-- TOC entry 4718 (class 1259 OID 17750)
-- Name: idx_mem_dir_parent_child; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_mem_dir_parent_child ON public.cwd_membership USING btree (lower_parent_name, lower_child_name, membership_type, directory_id);


--
-- TOC entry 4722 (class 1259 OID 17753)
-- Name: idx_old_issue_key; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_old_issue_key ON public.moved_issue_key USING btree (old_issue_key);


--
-- TOC entry 4961 (class 1259 OID 17822)
-- Name: idx_parent_name; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_parent_name ON public.jiraworkflowstatuses USING btree (parentname);


--
-- TOC entry 4822 (class 1259 OID 17781)
-- Name: idx_project_category_name; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_project_category_name ON public.projectcategory USING btree (cname);


--
-- TOC entry 4819 (class 1259 OID 17780)
-- Name: idx_project_key; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_project_key ON public.project USING btree (pkey);


--
-- TOC entry 4657 (class 1259 OID 17723)
-- Name: idx_qrtz_ft_inst_job_req_rcvry; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_ft_inst_job_req_rcvry ON public.jquartz_fired_triggers USING btree (sched_name, instance_name, requests_recovery);


--
-- TOC entry 4658 (class 1259 OID 17724)
-- Name: idx_qrtz_ft_j_g; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_ft_j_g ON public.jquartz_fired_triggers USING btree (sched_name, job_name, job_group);


--
-- TOC entry 4659 (class 1259 OID 17725)
-- Name: idx_qrtz_ft_jg; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_ft_jg ON public.jquartz_fired_triggers USING btree (sched_name, job_group);


--
-- TOC entry 4660 (class 1259 OID 17726)
-- Name: idx_qrtz_ft_t_g; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_ft_t_g ON public.jquartz_fired_triggers USING btree (sched_name, trigger_name, trigger_group);


--
-- TOC entry 4661 (class 1259 OID 17727)
-- Name: idx_qrtz_ft_tg; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_ft_tg ON public.jquartz_fired_triggers USING btree (sched_name, trigger_group);


--
-- TOC entry 4662 (class 1259 OID 17722)
-- Name: idx_qrtz_ft_trig_inst_name; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_ft_trig_inst_name ON public.jquartz_fired_triggers USING btree (sched_name, instance_name);


--
-- TOC entry 4683 (class 1259 OID 17733)
-- Name: idx_qrtz_j_g; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_j_g ON public.jquartz_triggers USING btree (sched_name, trigger_group);


--
-- TOC entry 4665 (class 1259 OID 17729)
-- Name: idx_qrtz_j_grp; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_j_grp ON public.jquartz_job_details USING btree (sched_name, job_group);


--
-- TOC entry 4666 (class 1259 OID 17728)
-- Name: idx_qrtz_j_req_recovery; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_j_req_recovery ON public.jquartz_job_details USING btree (sched_name, requests_recovery);


--
-- TOC entry 4684 (class 1259 OID 17734)
-- Name: idx_qrtz_j_state; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_j_state ON public.jquartz_triggers USING btree (sched_name, trigger_state);


--
-- TOC entry 4685 (class 1259 OID 17732)
-- Name: idx_qrtz_t_c; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_t_c ON public.jquartz_triggers USING btree (sched_name, calendar_name);


--
-- TOC entry 4686 (class 1259 OID 17730)
-- Name: idx_qrtz_t_j; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_t_j ON public.jquartz_triggers USING btree (sched_name, job_name, job_group);


--
-- TOC entry 4687 (class 1259 OID 17731)
-- Name: idx_qrtz_t_jg; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_t_jg ON public.jquartz_triggers USING btree (sched_name, job_group);


--
-- TOC entry 4688 (class 1259 OID 17736)
-- Name: idx_qrtz_t_n_g_state; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_t_n_g_state ON public.jquartz_triggers USING btree (sched_name, trigger_group, trigger_state);


--
-- TOC entry 4689 (class 1259 OID 17735)
-- Name: idx_qrtz_t_n_state; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_t_n_state ON public.jquartz_triggers USING btree (sched_name, trigger_name, trigger_group, trigger_state);


--
-- TOC entry 4690 (class 1259 OID 17737)
-- Name: idx_qrtz_t_next_fire_time; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_t_next_fire_time ON public.jquartz_triggers USING btree (sched_name, next_fire_time);


--
-- TOC entry 4691 (class 1259 OID 17739)
-- Name: idx_qrtz_t_nft_misfire; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_t_nft_misfire ON public.jquartz_triggers USING btree (sched_name, misfire_instr, next_fire_time);


--
-- TOC entry 4692 (class 1259 OID 17738)
-- Name: idx_qrtz_t_nft_st; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_t_nft_st ON public.jquartz_triggers USING btree (sched_name, trigger_state, next_fire_time);


--
-- TOC entry 4693 (class 1259 OID 17740)
-- Name: idx_qrtz_t_nft_st_misfire; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_t_nft_st_misfire ON public.jquartz_triggers USING btree (sched_name, misfire_instr, next_fire_time, trigger_state);


--
-- TOC entry 4694 (class 1259 OID 17741)
-- Name: idx_qrtz_t_nft_st_misfire_grp; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_t_nft_st_misfire_grp ON public.jquartz_triggers USING btree (sched_name, misfire_instr, next_fire_time, trigger_group, trigger_state);


--
-- TOC entry 4853 (class 1259 OID 17786)
-- Name: idx_reindex_component_req_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_reindex_component_req_id ON public.reindex_component USING btree (request_id);


--
-- TOC entry 4899 (class 1259 OID 17801)
-- Name: idx_tam_by_created_time; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_tam_by_created_time ON public.tempattachmentsmonitor USING btree (created_time);


--
-- TOC entry 4900 (class 1259 OID 17800)
-- Name: idx_tam_by_form_token; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_tam_by_form_token ON public.tempattachmentsmonitor USING btree (form_token);


--
-- TOC entry 4928 (class 1259 OID 17811)
-- Name: idx_user_attr_dir_name_lval; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_user_attr_dir_name_lval ON public.cwd_user_attributes USING btree (directory_id, attribute_name, lower_attribute_value);


--
-- TOC entry 4945 (class 1259 OID 17818)
-- Name: idx_version_project; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_version_project ON public.projectversion USING btree (project);


--
-- TOC entry 4946 (class 1259 OID 17819)
-- Name: idx_version_sequence; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_version_sequence ON public.projectversion USING btree (sequence);


--
-- TOC entry 5191 (class 1259 OID 19996)
-- Name: index_ao_0a5972_not187736835; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_0a5972_not187736835 ON public."AO_0A5972_NOTIFICATION_SETTING" USING btree ("USER_KEY");


--
-- TOC entry 5194 (class 1259 OID 19997)
-- Name: index_ao_0a5972_pus2127422409; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_0a5972_pus2127422409 ON public."AO_0A5972_PUSH_REGISTRATION" USING btree ("USER_KEY");


--
-- TOC entry 5168 (class 1259 OID 19922)
-- Name: index_ao_21f425_mes1965715920; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_21f425_mes1965715920 ON public."AO_21F425_MESSAGE_MAPPING_AO" USING btree ("MESSAGE_ID");


--
-- TOC entry 5169 (class 1259 OID 19921)
-- Name: index_ao_21f425_mes223897723; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_21f425_mes223897723 ON public."AO_21F425_MESSAGE_MAPPING_AO" USING btree ("USER_HASH");


--
-- TOC entry 5165 (class 1259 OID 19920)
-- Name: index_ao_21f425_use1458667739; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_21f425_use1458667739 ON public."AO_21F425_USER_PROPERTY_AO" USING btree ("USER");


--
-- TOC entry 4975 (class 1259 OID 19067)
-- Name: index_ao_38321b_cus1828044926; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_38321b_cus1828044926 ON public."AO_38321B_CUSTOM_CONTENT_LINK" USING btree ("CONTENT_KEY");


--
-- TOC entry 5244 (class 1259 OID 20230)
-- Name: index_ao_4789dd_tas42846517; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_4789dd_tas42846517 ON public."AO_4789DD_TASK_MONITOR" USING btree ("TASK_MONITOR_KIND");


--
-- TOC entry 4972 (class 1259 OID 19054)
-- Name: index_ao_550953_sho1778115994; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_550953_sho1778115994 ON public."AO_550953_SHORTCUT" USING btree ("PROJECT_ID");


--
-- TOC entry 5234 (class 1259 OID 20183)
-- Name: index_ao_563aee_act1642652291; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_563aee_act1642652291 ON public."AO_563AEE_ACTIVITY_ENTITY" USING btree ("OBJECT_ID");


--
-- TOC entry 5235 (class 1259 OID 20184)
-- Name: index_ao_563aee_act1978295567; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_563aee_act1978295567 ON public."AO_563AEE_ACTIVITY_ENTITY" USING btree ("TARGET_ID");


--
-- TOC entry 5236 (class 1259 OID 20182)
-- Name: index_ao_563aee_act972488439; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_563aee_act972488439 ON public."AO_563AEE_ACTIVITY_ENTITY" USING btree ("ICON_ID");


--
-- TOC entry 5237 (class 1259 OID 20185)
-- Name: index_ao_563aee_act995325379; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_563aee_act995325379 ON public."AO_563AEE_ACTIVITY_ENTITY" USING btree ("ACTOR_ID");


--
-- TOC entry 5228 (class 1259 OID 20149)
-- Name: index_ao_563aee_obj696886343; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_563aee_obj696886343 ON public."AO_563AEE_OBJECT_ENTITY" USING btree ("IMAGE_ID");


--
-- TOC entry 5231 (class 1259 OID 20181)
-- Name: index_ao_563aee_tar521440921; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_563aee_tar521440921 ON public."AO_563AEE_TARGET_ENTITY" USING btree ("IMAGE_ID");


--
-- TOC entry 5158 (class 1259 OID 19888)
-- Name: index_ao_575bf5_dev567785983; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_575bf5_dev567785983 ON public."AO_575BF5_DEV_SUMMARY" USING btree ("ISSUE_ID");


--
-- TOC entry 5159 (class 1259 OID 19887)
-- Name: index_ao_575bf5_dev996442447; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_575bf5_dev996442447 ON public."AO_575BF5_DEV_SUMMARY" USING btree ("PROVIDER_SOURCE_ID");


--
-- TOC entry 5149 (class 1259 OID 19883)
-- Name: index_ao_575bf5_pro1117502689; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_575bf5_pro1117502689 ON public."AO_575BF5_PROVIDER_ISSUE" USING btree ("STALE_AFTER");


--
-- TOC entry 5150 (class 1259 OID 19882)
-- Name: index_ao_575bf5_pro1348521624; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_575bf5_pro1348521624 ON public."AO_575BF5_PROVIDER_ISSUE" USING btree ("PROVIDER_SOURCE_ID");


--
-- TOC entry 5154 (class 1259 OID 19885)
-- Name: index_ao_575bf5_pro1681808571; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_575bf5_pro1681808571 ON public."AO_575BF5_PROCESSED_COMMITS" USING btree ("COMMIT_HASH");


--
-- TOC entry 5151 (class 1259 OID 19884)
-- Name: index_ao_575bf5_pro741170824; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_575bf5_pro741170824 ON public."AO_575BF5_PROVIDER_ISSUE" USING btree ("ISSUE_ID");


--
-- TOC entry 5155 (class 1259 OID 19886)
-- Name: index_ao_575bf5_pro78019725; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_575bf5_pro78019725 ON public."AO_575BF5_PROCESSED_COMMITS" USING btree ("METADATA_HASH");


--
-- TOC entry 5162 (class 1259 OID 19889)
-- Name: index_ao_575bf5_pro996609822; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_575bf5_pro996609822 ON public."AO_575BF5_PROVIDER_SEQ_NO" USING btree ("PROVIDER_SOURCE_ID");


--
-- TOC entry 5209 (class 1259 OID 20072)
-- Name: index_ao_587b34_pro1732672724; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_587b34_pro1732672724 ON public."AO_587B34_PROJECT_CONFIG" USING btree ("ROOM_ID");


--
-- TOC entry 5210 (class 1259 OID 20071)
-- Name: index_ao_587b34_pro193829489; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_587b34_pro193829489 ON public."AO_587B34_PROJECT_CONFIG" USING btree ("CONFIGURATION_GROUP_ID");


--
-- TOC entry 5211 (class 1259 OID 20073)
-- Name: index_ao_587b34_pro2093917684; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_587b34_pro2093917684 ON public."AO_587B34_PROJECT_CONFIG" USING btree ("PROJECT_ID");


--
-- TOC entry 5212 (class 1259 OID 20074)
-- Name: index_ao_587b34_pro2115480362; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_587b34_pro2115480362 ON public."AO_587B34_PROJECT_CONFIG" USING btree ("NAME");


--
-- TOC entry 5203 (class 1259 OID 20045)
-- Name: index_ao_5fb9d7_aoh1981563178; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_5fb9d7_aoh1981563178 ON public."AO_5FB9D7_AOHIP_CHAT_USER" USING btree ("USER_KEY");


--
-- TOC entry 5204 (class 1259 OID 20046)
-- Name: index_ao_5fb9d7_aoh49772492; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_5fb9d7_aoh49772492 ON public."AO_5FB9D7_AOHIP_CHAT_USER" USING btree ("HIP_CHAT_LINK_ID");


--
-- TOC entry 5003 (class 1259 OID 19365)
-- Name: index_ao_60db71_aud137736645; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_60db71_aud137736645 ON public."AO_60DB71_AUDITENTRY" USING btree ("ENTITY_CLASS");


--
-- TOC entry 5004 (class 1259 OID 19366)
-- Name: index_ao_60db71_aud1756477597; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_60db71_aud1756477597 ON public."AO_60DB71_AUDITENTRY" USING btree ("CATEGORY");


--
-- TOC entry 5005 (class 1259 OID 19367)
-- Name: index_ao_60db71_aud604788536; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_60db71_aud604788536 ON public."AO_60DB71_AUDITENTRY" USING btree ("ENTITY_ID");


--
-- TOC entry 5023 (class 1259 OID 19387)
-- Name: index_ao_60db71_boa2110227660; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_60db71_boa2110227660 ON public."AO_60DB71_BOARDADMINS" USING btree ("RAPID_VIEW_ID");


--
-- TOC entry 5008 (class 1259 OID 19368)
-- Name: index_ao_60db71_car149237770; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_60db71_car149237770 ON public."AO_60DB71_CARDLAYOUT" USING btree ("RAPID_VIEW_ID");


--
-- TOC entry 5011 (class 1259 OID 19369)
-- Name: index_ao_60db71_car2031978979; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_60db71_car2031978979 ON public."AO_60DB71_CARDCOLOR" USING btree ("RAPID_VIEW_ID");


--
-- TOC entry 5044 (class 1259 OID 19407)
-- Name: index_ao_60db71_col1856623434; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_60db71_col1856623434 ON public."AO_60DB71_COLUMNSTATUS" USING btree ("COLUMN_ID");


--
-- TOC entry 5020 (class 1259 OID 19386)
-- Name: index_ao_60db71_col2098611346; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_60db71_col2098611346 ON public."AO_60DB71_COLUMN" USING btree ("RAPID_VIEW_ID");


--
-- TOC entry 5014 (class 1259 OID 19370)
-- Name: index_ao_60db71_det878495474; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_60db71_det878495474 ON public."AO_60DB71_DETAILVIEWFIELD" USING btree ("RAPID_VIEW_ID");


--
-- TOC entry 5026 (class 1259 OID 19388)
-- Name: index_ao_60db71_est1680565966; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_60db71_est1680565966 ON public."AO_60DB71_ESTIMATESTATISTIC" USING btree ("RAPID_VIEW_ID");


--
-- TOC entry 4980 (class 1259 OID 19173)
-- Name: index_ao_60db71_iss1616896230; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_60db71_iss1616896230 ON public."AO_60DB71_ISSUERANKING" USING btree ("ISSUE_ID");


--
-- TOC entry 4981 (class 1259 OID 19172)
-- Name: index_ao_60db71_iss1786461035; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_60db71_iss1786461035 ON public."AO_60DB71_ISSUERANKING" USING btree ("CUSTOM_FIELD_ID");


--
-- TOC entry 4990 (class 1259 OID 19361)
-- Name: index_ao_60db71_lex1569533973; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_60db71_lex1569533973 ON public."AO_60DB71_LEXORANK" USING btree ("FIELD_ID", "RANK");


--
-- TOC entry 4991 (class 1259 OID 19363)
-- Name: index_ao_60db71_lex1632828616; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_60db71_lex1632828616 ON public."AO_60DB71_LEXORANK" USING btree ("LOCK_HASH");


--
-- TOC entry 4992 (class 1259 OID 19360)
-- Name: index_ao_60db71_lex1694305086; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_60db71_lex1694305086 ON public."AO_60DB71_LEXORANK" USING btree ("FIELD_ID", "BUCKET", "RANK");


--
-- TOC entry 4993 (class 1259 OID 19362)
-- Name: index_ao_60db71_lex604083109; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_60db71_lex604083109 ON public."AO_60DB71_LEXORANK" USING btree ("ISSUE_ID");


--
-- TOC entry 5047 (class 1259 OID 19408)
-- Name: index_ao_60db71_non1145824037; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_60db71_non1145824037 ON public."AO_60DB71_NONWORKINGDAY" USING btree ("WORKING_DAYS_ID");


--
-- TOC entry 5017 (class 1259 OID 19371)
-- Name: index_ao_60db71_qui432573905; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_60db71_qui432573905 ON public."AO_60DB71_QUICKFILTER" USING btree ("RAPID_VIEW_ID");


--
-- TOC entry 4996 (class 1259 OID 19364)
-- Name: index_ao_60db71_spr1457658269; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_60db71_spr1457658269 ON public."AO_60DB71_SPRINT" USING btree ("SEQUENCE");


--
-- TOC entry 5038 (class 1259 OID 19405)
-- Name: index_ao_60db71_sta1907922871; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_60db71_sta1907922871 ON public."AO_60DB71_STATSFIELD" USING btree ("RAPID_VIEW_ID");


--
-- TOC entry 5032 (class 1259 OID 19390)
-- Name: index_ao_60db71_sub730851836; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_60db71_sub730851836 ON public."AO_60DB71_SUBQUERY" USING btree ("RAPID_VIEW_ID");


--
-- TOC entry 5041 (class 1259 OID 19406)
-- Name: index_ao_60db71_swi1429284592; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_60db71_swi1429284592 ON public."AO_60DB71_SWIMLANE" USING btree ("RAPID_VIEW_ID");


--
-- TOC entry 5029 (class 1259 OID 19389)
-- Name: index_ao_60db71_tra1711190333; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_60db71_tra1711190333 ON public."AO_60DB71_TRACKINGSTATISTIC" USING btree ("RAPID_VIEW_ID");


--
-- TOC entry 5035 (class 1259 OID 19404)
-- Name: index_ao_60db71_wor1205491794; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_60db71_wor1205491794 ON public."AO_60DB71_WORKINGDAYS" USING btree ("RAPID_VIEW_ID");


--
-- TOC entry 5181 (class 1259 OID 19972)
-- Name: index_ao_733371_eve1266474620; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_733371_eve1266474620 ON public."AO_733371_EVENT_RECIPIENT" USING btree ("CONSUMER_NAME");


--
-- TOC entry 5187 (class 1259 OID 19976)
-- Name: index_ao_733371_eve1423945899; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_733371_eve1423945899 ON public."AO_733371_EVENT_PARAMETER" USING btree ("NAME");


--
-- TOC entry 5182 (class 1259 OID 19971)
-- Name: index_ao_733371_eve1645451632; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_733371_eve1645451632 ON public."AO_733371_EVENT_RECIPIENT" USING btree ("EVENT_ID");


--
-- TOC entry 5183 (class 1259 OID 19974)
-- Name: index_ao_733371_eve525098581; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_733371_eve525098581 ON public."AO_733371_EVENT_RECIPIENT" USING btree ("SEND_DATE");


--
-- TOC entry 5174 (class 1259 OID 19969)
-- Name: index_ao_733371_eve673683319; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_733371_eve673683319 ON public."AO_733371_EVENT" USING btree ("EVENT_BUNDLE_ID");


--
-- TOC entry 5184 (class 1259 OID 19973)
-- Name: index_ao_733371_eve678699426; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_733371_eve678699426 ON public."AO_733371_EVENT_RECIPIENT" USING btree ("STATUS");


--
-- TOC entry 5188 (class 1259 OID 19975)
-- Name: index_ao_733371_eve704112384; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_733371_eve704112384 ON public."AO_733371_EVENT_PARAMETER" USING btree ("EVENT_ID");


--
-- TOC entry 5175 (class 1259 OID 19934)
-- Name: index_ao_733371_eve902883849; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_733371_eve902883849 ON public."AO_733371_EVENT" USING btree ("EVENT_TYPE");


--
-- TOC entry 5176 (class 1259 OID 19970)
-- Name: index_ao_733371_event_action; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_733371_event_action ON public."AO_733371_EVENT" USING btree ("ACTION");


--
-- TOC entry 5177 (class 1259 OID 19936)
-- Name: index_ao_733371_event_created; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_733371_event_created ON public."AO_733371_EVENT" USING btree ("CREATED");


--
-- TOC entry 5178 (class 1259 OID 19935)
-- Name: index_ao_733371_event_user_key; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_733371_event_user_key ON public."AO_733371_EVENT" USING btree ("USER_KEY");


--
-- TOC entry 5088 (class 1259 OID 19603)
-- Name: index_ao_e8b6cc_bra1368852151; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_bra1368852151 ON public."AO_E8B6CC_BRANCH_HEAD_MAPPING" USING btree ("REPOSITORY_ID");


--
-- TOC entry 5096 (class 1259 OID 19752)
-- Name: index_ao_e8b6cc_bra405461593; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_bra405461593 ON public."AO_E8B6CC_BRANCH" USING btree ("REPOSITORY_ID");


--
-- TOC entry 5066 (class 1259 OID 19544)
-- Name: index_ao_e8b6cc_cha1086340152; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_cha1086340152 ON public."AO_E8B6CC_CHANGESET_MAPPING" USING btree ("SMARTCOMMIT_AVAILABLE");


--
-- TOC entry 5067 (class 1259 OID 19541)
-- Name: index_ao_e8b6cc_cha1483243924; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_cha1483243924 ON public."AO_E8B6CC_CHANGESET_MAPPING" USING btree ("NODE");


--
-- TOC entry 5068 (class 1259 OID 19543)
-- Name: index_ao_e8b6cc_cha509722037; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_cha509722037 ON public."AO_E8B6CC_CHANGESET_MAPPING" USING btree ("RAW_NODE");


--
-- TOC entry 5069 (class 1259 OID 19542)
-- Name: index_ao_e8b6cc_cha897886051; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_cha897886051 ON public."AO_E8B6CC_CHANGESET_MAPPING" USING btree ("AUTHOR");


--
-- TOC entry 5120 (class 1259 OID 19806)
-- Name: index_ao_e8b6cc_com1308336834; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_com1308336834 ON public."AO_E8B6CC_COMMIT" USING btree ("DOMAIN_ID");


--
-- TOC entry 5121 (class 1259 OID 19805)
-- Name: index_ao_e8b6cc_commit_node; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_commit_node ON public."AO_E8B6CC_COMMIT" USING btree ("NODE");


--
-- TOC entry 5107 (class 1259 OID 19771)
-- Name: index_ao_e8b6cc_git1120895454; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_git1120895454 ON public."AO_E8B6CC_GIT_HUB_EVENT" USING btree ("GIT_HUB_ID");


--
-- TOC entry 5108 (class 1259 OID 19770)
-- Name: index_ao_e8b6cc_git1804640320; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_git1804640320 ON public."AO_E8B6CC_GIT_HUB_EVENT" USING btree ("REPOSITORY_ID");


--
-- TOC entry 5083 (class 1259 OID 19584)
-- Name: index_ao_e8b6cc_iss1229805759; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_iss1229805759 ON public."AO_E8B6CC_ISSUE_TO_CHANGESET" USING btree ("CHANGESET_ID");


--
-- TOC entry 5131 (class 1259 OID 19830)
-- Name: index_ao_e8b6cc_iss1325927291; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_iss1325927291 ON public."AO_E8B6CC_ISSUE_TO_BRANCH" USING btree ("BRANCH_ID");


--
-- TOC entry 5132 (class 1259 OID 19829)
-- Name: index_ao_e8b6cc_iss353204826; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_iss353204826 ON public."AO_E8B6CC_ISSUE_TO_BRANCH" USING btree ("ISSUE_KEY");


--
-- TOC entry 5084 (class 1259 OID 19585)
-- Name: index_ao_e8b6cc_iss417950110; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_iss417950110 ON public."AO_E8B6CC_ISSUE_TO_CHANGESET" USING btree ("ISSUE_KEY");


--
-- TOC entry 5085 (class 1259 OID 19586)
-- Name: index_ao_e8b6cc_iss648895902; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_iss648895902 ON public."AO_E8B6CC_ISSUE_TO_CHANGESET" USING btree ("PROJECT_KEY");


--
-- TOC entry 5117 (class 1259 OID 19774)
-- Name: index_ao_e8b6cc_mes1247039512; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_mes1247039512 ON public."AO_E8B6CC_MESSAGE" USING btree ("ADDRESS");


--
-- TOC entry 5135 (class 1259 OID 19831)
-- Name: index_ao_e8b6cc_mes1391090780; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_mes1391090780 ON public."AO_E8B6CC_MESSAGE_TAG" USING btree ("MESSAGE_ID");


--
-- TOC entry 5136 (class 1259 OID 19832)
-- Name: index_ao_e8b6cc_mes1648007831; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_mes1648007831 ON public."AO_E8B6CC_MESSAGE_TAG" USING btree ("TAG");


--
-- TOC entry 5139 (class 1259 OID 19833)
-- Name: index_ao_e8b6cc_mes344532677; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_mes344532677 ON public."AO_E8B6CC_MESSAGE_QUEUE_ITEM" USING btree ("MESSAGE_ID");


--
-- TOC entry 5140 (class 1259 OID 19835)
-- Name: index_ao_e8b6cc_mes59146529; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_mes59146529 ON public."AO_E8B6CC_MESSAGE_QUEUE_ITEM" USING btree ("STATE");


--
-- TOC entry 5141 (class 1259 OID 19834)
-- Name: index_ao_e8b6cc_mes60959905; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_mes60959905 ON public."AO_E8B6CC_MESSAGE_QUEUE_ITEM" USING btree ("QUEUE");


--
-- TOC entry 5072 (class 1259 OID 19545)
-- Name: index_ao_e8b6cc_org1513110290; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_org1513110290 ON public."AO_E8B6CC_ORGANIZATION_MAPPING" USING btree ("DVCS_TYPE");


--
-- TOC entry 5111 (class 1259 OID 19772)
-- Name: index_ao_e8b6cc_org1899590324; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_org1899590324 ON public."AO_E8B6CC_ORG_TO_PROJECT" USING btree ("ORGANIZATION_ID");


--
-- TOC entry 5144 (class 1259 OID 19837)
-- Name: index_ao_e8b6cc_pr_1045528152; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_pr_1045528152 ON public."AO_E8B6CC_PR_TO_COMMIT" USING btree ("REQUEST_ID");


--
-- TOC entry 5127 (class 1259 OID 19827)
-- Name: index_ao_e8b6cc_pr_1105917040; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_pr_1105917040 ON public."AO_E8B6CC_PR_PARTICIPANT" USING btree ("PULL_REQUEST_ID");


--
-- TOC entry 5145 (class 1259 OID 19838)
-- Name: index_ao_e8b6cc_pr_1458633226; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_pr_1458633226 ON public."AO_E8B6CC_PR_TO_COMMIT" USING btree ("COMMIT_ID");


--
-- TOC entry 5099 (class 1259 OID 19767)
-- Name: index_ao_e8b6cc_pr_1639282617; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_pr_1639282617 ON public."AO_E8B6CC_PR_ISSUE_KEY" USING btree ("DOMAIN_ID");


--
-- TOC entry 5100 (class 1259 OID 19768)
-- Name: index_ao_e8b6cc_pr_2106805302; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_pr_2106805302 ON public."AO_E8B6CC_PR_ISSUE_KEY" USING btree ("ISSUE_KEY");


--
-- TOC entry 5101 (class 1259 OID 19766)
-- Name: index_ao_e8b6cc_pr_281193494; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_pr_281193494 ON public."AO_E8B6CC_PR_ISSUE_KEY" USING btree ("PULL_REQUEST_ID");


--
-- TOC entry 5146 (class 1259 OID 19836)
-- Name: index_ao_e8b6cc_pr_685151049; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_pr_685151049 ON public."AO_E8B6CC_PR_TO_COMMIT" USING btree ("DOMAIN_ID");


--
-- TOC entry 5128 (class 1259 OID 19828)
-- Name: index_ao_e8b6cc_pr_758084799; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_pr_758084799 ON public."AO_E8B6CC_PR_PARTICIPANT" USING btree ("DOMAIN_ID");


--
-- TOC entry 5091 (class 1259 OID 19733)
-- Name: index_ao_e8b6cc_pul1230717024; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_pul1230717024 ON public."AO_E8B6CC_PULL_REQUEST" USING btree ("DOMAIN_ID");


--
-- TOC entry 5092 (class 1259 OID 19751)
-- Name: index_ao_e8b6cc_pul1448445182; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_pul1448445182 ON public."AO_E8B6CC_PULL_REQUEST" USING btree ("TO_REPOSITORY_ID");


--
-- TOC entry 5093 (class 1259 OID 19732)
-- Name: index_ao_e8b6cc_pul602811170; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_pul602811170 ON public."AO_E8B6CC_PULL_REQUEST" USING btree ("REMOTE_ID");


--
-- TOC entry 5079 (class 1259 OID 19582)
-- Name: index_ao_e8b6cc_rep1082901832; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_rep1082901832 ON public."AO_E8B6CC_REPO_TO_CHANGESET" USING btree ("REPOSITORY_ID");


--
-- TOC entry 5124 (class 1259 OID 19826)
-- Name: index_ao_e8b6cc_rep1928770529; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_rep1928770529 ON public."AO_E8B6CC_REPO_TO_PROJECT" USING btree ("REPOSITORY_ID");


--
-- TOC entry 5075 (class 1259 OID 19547)
-- Name: index_ao_e8b6cc_rep702725269; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_rep702725269 ON public."AO_E8B6CC_REPOSITORY_MAPPING" USING btree ("ORGANIZATION_ID");


--
-- TOC entry 5080 (class 1259 OID 19583)
-- Name: index_ao_e8b6cc_rep922992576; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_rep922992576 ON public."AO_E8B6CC_REPO_TO_CHANGESET" USING btree ("CHANGESET_ID");


--
-- TOC entry 5076 (class 1259 OID 19546)
-- Name: index_ao_e8b6cc_rep93578901; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_rep93578901 ON public."AO_E8B6CC_REPOSITORY_MAPPING" USING btree ("LINKED");


--
-- TOC entry 5114 (class 1259 OID 19773)
-- Name: index_ao_e8b6cc_syn203792807; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_syn203792807 ON public."AO_E8B6CC_SYNC_AUDIT_LOG" USING btree ("REPO_ID");


--
-- TOC entry 5104 (class 1259 OID 19769)
-- Name: index_ao_e8b6cc_syn493078035; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_e8b6cc_syn493078035 ON public."AO_E8B6CC_SYNC_EVENT" USING btree ("REPO_ID");


--
-- TOC entry 5219 (class 1259 OID 20097)
-- Name: index_ao_ed669c_see20117222; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_ao_ed669c_see20117222 ON public."AO_ED669C_SEEN_ASSERTIONS" USING btree ("EXPIRY_TIMESTAMP");


--
-- TOC entry 4616 (class 1259 OID 17711)
-- Name: issue_assignee; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX issue_assignee ON public.jiraissue USING btree (assignee);


--
-- TOC entry 4617 (class 1259 OID 17707)
-- Name: issue_created; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX issue_created ON public.jiraissue USING btree (created);


--
-- TOC entry 4618 (class 1259 OID 17709)
-- Name: issue_duedate; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX issue_duedate ON public.jiraissue USING btree (duedate);


--
-- TOC entry 4619 (class 1259 OID 17705)
-- Name: issue_proj_num; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX issue_proj_num ON public.jiraissue USING btree (issuenum, project);


--
-- TOC entry 4620 (class 1259 OID 17706)
-- Name: issue_proj_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX issue_proj_status ON public.jiraissue USING btree (project, issuestatus);


--
-- TOC entry 4621 (class 1259 OID 17712)
-- Name: issue_reporter; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX issue_reporter ON public.jiraissue USING btree (reporter);


--
-- TOC entry 4622 (class 1259 OID 17710)
-- Name: issue_resolutiondate; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX issue_resolutiondate ON public.jiraissue USING btree (resolutiondate);


--
-- TOC entry 4623 (class 1259 OID 17708)
-- Name: issue_updated; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX issue_updated ON public.jiraissue USING btree (updated);


--
-- TOC entry 4624 (class 1259 OID 17714)
-- Name: issue_votes; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX issue_votes ON public.jiraissue USING btree (votes);


--
-- TOC entry 4625 (class 1259 OID 17715)
-- Name: issue_watches; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX issue_watches ON public.jiraissue USING btree (watches);


--
-- TOC entry 4626 (class 1259 OID 17713)
-- Name: issue_workflow; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX issue_workflow ON public.jiraissue USING btree (workflow_id);


--
-- TOC entry 4633 (class 1259 OID 17717)
-- Name: issuelink_dest; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX issuelink_dest ON public.issuelink USING btree (destination);


--
-- TOC entry 4634 (class 1259 OID 17716)
-- Name: issuelink_src; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX issuelink_src ON public.issuelink USING btree (source);


--
-- TOC entry 4635 (class 1259 OID 17718)
-- Name: issuelink_type; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX issuelink_type ON public.issuelink USING btree (linktype);


--
-- TOC entry 4697 (class 1259 OID 17742)
-- Name: label_fieldissue; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX label_fieldissue ON public.label USING btree (issue, fieldid);


--
-- TOC entry 4698 (class 1259 OID 17743)
-- Name: label_fieldissuelabel; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX label_fieldissuelabel ON public.label USING btree (issue, fieldid, label);


--
-- TOC entry 4699 (class 1259 OID 17745)
-- Name: label_issue; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX label_issue ON public.label USING btree (issue);


--
-- TOC entry 4700 (class 1259 OID 17744)
-- Name: label_label; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX label_label ON public.label USING btree (label);


--
-- TOC entry 4703 (class 1259 OID 17746)
-- Name: licenseroledefault_index; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX licenseroledefault_index ON public.licenserolesdefault USING btree (license_role_name);


--
-- TOC entry 4706 (class 1259 OID 17747)
-- Name: licenserolegroup_index; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX licenserolegroup_index ON public.licenserolesgroup USING btree (license_role_name, group_id);


--
-- TOC entry 4638 (class 1259 OID 17719)
-- Name: linktypename; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX linktypename ON public.issuelinktype USING btree (linkname);


--
-- TOC entry 4639 (class 1259 OID 17720)
-- Name: linktypestyle; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX linktypestyle ON public.issuelinktype USING btree (pstyle);


--
-- TOC entry 4713 (class 1259 OID 17748)
-- Name: managedconfigitem_id_type_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX managedconfigitem_id_type_idx ON public.managedconfigurationitem USING btree (item_id, item_type);


--
-- TOC entry 4769 (class 1259 OID 17771)
-- Name: mshipbase_group; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX mshipbase_group ON public.membershipbase USING btree (group_name);


--
-- TOC entry 4770 (class 1259 OID 17770)
-- Name: mshipbase_user; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX mshipbase_user ON public.membershipbase USING btree (user_name);


--
-- TOC entry 4729 (class 1259 OID 17756)
-- Name: node_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX node_id_idx ON public.nodeindexcounter USING btree (node_id, sending_node_id);


--
-- TOC entry 4867 (class 1259 OID 17790)
-- Name: node_operation_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX node_operation_idx ON public.replicatedindexoperation USING btree (node_id, affected_index, operation, index_time);


--
-- TOC entry 4725 (class 1259 OID 17755)
-- Name: node_sink; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX node_sink ON public.nodeassociation USING btree (sink_node_id, sink_node_entity);


--
-- TOC entry 4726 (class 1259 OID 17754)
-- Name: node_source; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX node_source ON public.nodeassociation USING btree (source_node_id, source_node_entity);


--
-- TOC entry 4735 (class 1259 OID 17759)
-- Name: notif_messageid; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX notif_messageid ON public.notificationinstance USING btree (messageid);


--
-- TOC entry 4736 (class 1259 OID 17758)
-- Name: notif_source; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX notif_source ON public.notificationinstance USING btree (source);


--
-- TOC entry 4732 (class 1259 OID 17757)
-- Name: ntfctn_scheme; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX ntfctn_scheme ON public.notification USING btree (scheme);


--
-- TOC entry 4741 (class 1259 OID 17760)
-- Name: oauth_consumer_index; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX oauth_consumer_index ON public.oauthconsumer USING btree (consumer_key);


--
-- TOC entry 4742 (class 1259 OID 17761)
-- Name: oauth_consumer_service_index; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX oauth_consumer_service_index ON public.oauthconsumer USING btree (consumerservice);


--
-- TOC entry 4745 (class 1259 OID 17763)
-- Name: oauth_consumer_token_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX oauth_consumer_token_index ON public.oauthconsumertoken USING btree (token);


--
-- TOC entry 4746 (class 1259 OID 17762)
-- Name: oauth_consumer_token_key_index; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX oauth_consumer_token_key_index ON public.oauthconsumertoken USING btree (token_key);


--
-- TOC entry 4749 (class 1259 OID 17764)
-- Name: oauth_sp_consumer_index; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX oauth_sp_consumer_index ON public.oauthspconsumer USING btree (consumer_key);


--
-- TOC entry 4752 (class 1259 OID 17766)
-- Name: oauth_sp_consumer_key_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX oauth_sp_consumer_key_index ON public.oauthsptoken USING btree (consumer_key);


--
-- TOC entry 4753 (class 1259 OID 17765)
-- Name: oauth_sp_token_index; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX oauth_sp_token_index ON public.oauthsptoken USING btree (token);


--
-- TOC entry 4474 (class 1259 OID 17651)
-- Name: ordernumber_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX ordernumber_idx ON public.clusterupgradestate USING btree (order_number);


--
-- TOC entry 4761 (class 1259 OID 17768)
-- Name: osgroup_name; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX osgroup_name ON public.groupbase USING btree (groupname);


--
-- TOC entry 4779 (class 1259 OID 17772)
-- Name: osproperty_entid_name_propkey; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX osproperty_entid_name_propkey ON public.propertyentry USING btree (entity_id, entity_name, property_key);


--
-- TOC entry 4780 (class 1259 OID 17773)
-- Name: osproperty_propertykey; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX osproperty_propertykey ON public.propertyentry USING btree (property_key);


--
-- TOC entry 4789 (class 1259 OID 17774)
-- Name: osuser_name; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX osuser_name ON public.userbase USING btree (username);


--
-- TOC entry 4882 (class 1259 OID 17796)
-- Name: permission_key_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX permission_key_idx ON public.schemepermissions USING btree (permission_key);


--
-- TOC entry 4812 (class 1259 OID 17779)
-- Name: ppage_username; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX ppage_username ON public.portalpage USING btree (username);


--
-- TOC entry 4885 (class 1259 OID 17795)
-- Name: prmssn_scheme; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX prmssn_scheme ON public.schemepermissions USING btree (scheme);


--
-- TOC entry 4804 (class 1259 OID 17777)
-- Name: prmssn_scheme_attr_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX prmssn_scheme_attr_idx ON public.permissionschemeattribute USING btree (scheme);


--
-- TOC entry 4805 (class 1259 OID 17778)
-- Name: prmssn_scheme_attr_key_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX prmssn_scheme_attr_key_idx ON public.permissionschemeattribute USING btree (scheme, attribute_key);


--
-- TOC entry 4860 (class 1259 OID 17787)
-- Name: remembermetoken_username_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX remembermetoken_username_index ON public.remembermetoken USING btree (username);


--
-- TOC entry 4865 (class 1259 OID 17789)
-- Name: remotelink_globalid; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX remotelink_globalid ON public.remotelink USING btree (globalid);


--
-- TOC entry 4866 (class 1259 OID 17788)
-- Name: remotelink_issueid; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX remotelink_issueid ON public.remotelink USING btree (issueid, globalid);


--
-- TOC entry 4835 (class 1259 OID 17785)
-- Name: role_pid_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX role_pid_idx ON public.projectroleactor USING btree (pid);


--
-- TOC entry 4836 (class 1259 OID 17784)
-- Name: role_player_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX role_player_idx ON public.projectroleactor USING btree (projectroleid, pid);


--
-- TOC entry 4874 (class 1259 OID 17791)
-- Name: rundetails_jobid_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX rundetails_jobid_idx ON public.rundetails USING btree (job_id);


--
-- TOC entry 4875 (class 1259 OID 17792)
-- Name: rundetails_starttime_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX rundetails_starttime_idx ON public.rundetails USING btree (start_time);


--
-- TOC entry 4588 (class 1259 OID 17693)
-- Name: screenitem_scheme; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX screenitem_scheme ON public.fieldscreenschemeitem USING btree (fieldscreenscheme);


--
-- TOC entry 4888 (class 1259 OID 17798)
-- Name: searchrequest_filternamelower; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX searchrequest_filternamelower ON public.searchrequest USING btree (filtername_lower);


--
-- TOC entry 4878 (class 1259 OID 17793)
-- Name: sec_scheme; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX sec_scheme ON public.schemeissuesecurities USING btree (scheme);


--
-- TOC entry 4879 (class 1259 OID 17794)
-- Name: sec_security; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX sec_security ON public.schemeissuesecurities USING btree (security);


--
-- TOC entry 4896 (class 1259 OID 17799)
-- Name: share_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX share_index ON public.sharepermissions USING btree (entityid, entitytype);


--
-- TOC entry 4469 (class 1259 OID 17650)
-- Name: source_destination_node_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX source_destination_node_idx ON public.clustermessage USING btree (source_node, destination_node);


--
-- TOC entry 4889 (class 1259 OID 17797)
-- Name: sr_author; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX sr_author ON public.searchrequest USING btree (authorname);


--
-- TOC entry 4597 (class 1259 OID 17696)
-- Name: subscrpt_user; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX subscrpt_user ON public.filtersubscription USING btree (filter_i_d, username);


--
-- TOC entry 4598 (class 1259 OID 17697)
-- Name: subscrptn_group; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX subscrptn_group ON public.filtersubscription USING btree (filter_i_d, groupname);


--
-- TOC entry 4907 (class 1259 OID 17802)
-- Name: trustedapp_id; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX trustedapp_id ON public.trustedapp USING btree (application_id);


--
-- TOC entry 4604 (class 1259 OID 17699)
-- Name: type_key; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX type_key ON public.genericconfiguration USING btree (datatype, datakey);


--
-- TOC entry 4934 (class 1259 OID 17813)
-- Name: uh_type_user_entity; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uh_type_user_entity ON public.userhistoryitem USING btree (entitytype, username, entityid);


--
-- TOC entry 4433 (class 1259 OID 17636)
-- Name: uk_application_name; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uk_application_name ON public.cwd_application USING btree (lower_application_name);


--
-- TOC entry 4516 (class 1259 OID 17668)
-- Name: uk_directory_name; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_directory_name ON public.cwd_directory USING btree (lower_directory_name);


--
-- TOC entry 4539 (class 1259 OID 17678)
-- Name: uk_entitytranslation; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uk_entitytranslation ON public.entity_translation USING btree (entity_name, entity_id, locale);


--
-- TOC entry 4615 (class 1259 OID 17704)
-- Name: uk_group_attr_name_lval; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uk_group_attr_name_lval ON public.cwd_group_attributes USING btree (group_id, attribute_name, lower_attribute_value);


--
-- TOC entry 4611 (class 1259 OID 17700)
-- Name: uk_group_name_dir_id; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uk_group_name_dir_id ON public.cwd_group USING btree (lower_group_name, directory_id);


--
-- TOC entry 4436 (class 1259 OID 17638)
-- Name: uk_lower_user_name; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uk_lower_user_name ON public.app_user USING btree (lower_user_name);


--
-- TOC entry 4721 (class 1259 OID 17749)
-- Name: uk_mem_parent_child_type; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uk_mem_parent_child_type ON public.cwd_membership USING btree (parent_id, child_id, membership_type);


--
-- TOC entry 4931 (class 1259 OID 17812)
-- Name: uk_user_attr_name_lval; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_user_attr_name_lval ON public.cwd_user_attributes USING btree (user_id, attribute_name);


--
-- TOC entry 4922 (class 1259 OID 17804)
-- Name: uk_user_externalid_dir_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_user_externalid_dir_id ON public.cwd_user USING btree (external_id, directory_id);


--
-- TOC entry 4437 (class 1259 OID 17637)
-- Name: uk_user_key; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uk_user_key ON public.app_user USING btree (user_key);


--
-- TOC entry 4923 (class 1259 OID 17803)
-- Name: uk_user_name_dir_id; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uk_user_name_dir_id ON public.cwd_user USING btree (lower_user_name, directory_id);


--
-- TOC entry 4937 (class 1259 OID 17814)
-- Name: upf_customfield; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX upf_customfield ON public.userpickerfilter USING btree (customfield);


--
-- TOC entry 4938 (class 1259 OID 17815)
-- Name: upf_fieldconfigid; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX upf_fieldconfigid ON public.userpickerfilter USING btree (customfieldconfig);


--
-- TOC entry 4926 (class 1259 OID 17810)
-- Name: user_sink; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX user_sink ON public.userassociation USING btree (sink_node_id, sink_node_entity);


--
-- TOC entry 4927 (class 1259 OID 17809)
-- Name: user_source; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX user_source ON public.userassociation USING btree (source_name);


--
-- TOC entry 4601 (class 1259 OID 17698)
-- Name: userpref_portletconfiguration; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX userpref_portletconfiguration ON public.gadgetuserpreference USING btree (portletconfiguration);


--
-- TOC entry 4953 (class 1259 OID 17820)
-- Name: votehistory_issue_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX votehistory_issue_index ON public.votehistory USING btree (issueid);


--
-- TOC entry 4758 (class 1259 OID 17767)
-- Name: wf_entryid; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX wf_entryid ON public.os_currentstep USING btree (entry_id);


--
-- TOC entry 4960 (class 1259 OID 17821)
-- Name: workflow_scheme; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX workflow_scheme ON public.workflowschemeentity USING btree (scheme);


--
-- TOC entry 4966 (class 1259 OID 17824)
-- Name: worklog_author; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX worklog_author ON public.worklog USING btree (author);


--
-- TOC entry 4967 (class 1259 OID 17823)
-- Name: worklog_issue; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX worklog_issue ON public.worklog USING btree (issueid);


--
-- TOC entry 5281 (class 2606 OID 20161)
-- Name: AO_563AEE_ACTIVITY_ENTITY fk_ao_563aee_activity_entity_actor_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_563AEE_ACTIVITY_ENTITY"
    ADD CONSTRAINT fk_ao_563aee_activity_entity_actor_id FOREIGN KEY ("ACTOR_ID") REFERENCES public."AO_563AEE_ACTOR_ENTITY"("ID");


--
-- TOC entry 5280 (class 2606 OID 20166)
-- Name: AO_563AEE_ACTIVITY_ENTITY fk_ao_563aee_activity_entity_icon_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_563AEE_ACTIVITY_ENTITY"
    ADD CONSTRAINT fk_ao_563aee_activity_entity_icon_id FOREIGN KEY ("ICON_ID") REFERENCES public."AO_563AEE_MEDIA_LINK_ENTITY"("ID");


--
-- TOC entry 5279 (class 2606 OID 20171)
-- Name: AO_563AEE_ACTIVITY_ENTITY fk_ao_563aee_activity_entity_object_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_563AEE_ACTIVITY_ENTITY"
    ADD CONSTRAINT fk_ao_563aee_activity_entity_object_id FOREIGN KEY ("OBJECT_ID") REFERENCES public."AO_563AEE_OBJECT_ENTITY"("ID");


--
-- TOC entry 5278 (class 2606 OID 20176)
-- Name: AO_563AEE_ACTIVITY_ENTITY fk_ao_563aee_activity_entity_target_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_563AEE_ACTIVITY_ENTITY"
    ADD CONSTRAINT fk_ao_563aee_activity_entity_target_id FOREIGN KEY ("TARGET_ID") REFERENCES public."AO_563AEE_TARGET_ENTITY"("ID");


--
-- TOC entry 5276 (class 2606 OID 20128)
-- Name: AO_563AEE_OBJECT_ENTITY fk_ao_563aee_object_entity_image_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_563AEE_OBJECT_ENTITY"
    ADD CONSTRAINT fk_ao_563aee_object_entity_image_id FOREIGN KEY ("IMAGE_ID") REFERENCES public."AO_563AEE_MEDIA_LINK_ENTITY"("ID");


--
-- TOC entry 5277 (class 2606 OID 20144)
-- Name: AO_563AEE_TARGET_ENTITY fk_ao_563aee_target_entity_image_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_563AEE_TARGET_ENTITY"
    ADD CONSTRAINT fk_ao_563aee_target_entity_image_id FOREIGN KEY ("IMAGE_ID") REFERENCES public."AO_563AEE_MEDIA_LINK_ENTITY"("ID");


--
-- TOC entry 5275 (class 2606 OID 20040)
-- Name: AO_5FB9D7_AOHIP_CHAT_USER fk_ao_5fb9d7_aohip_chat_user_hip_chat_link_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_5FB9D7_AOHIP_CHAT_USER"
    ADD CONSTRAINT fk_ao_5fb9d7_aohip_chat_user_hip_chat_link_id FOREIGN KEY ("HIP_CHAT_LINK_ID") REFERENCES public."AO_5FB9D7_AOHIP_CHAT_LINK"("ID");


--
-- TOC entry 5254 (class 2606 OID 19264)
-- Name: AO_60DB71_BOARDADMINS fk_ao_60db71_boardadmins_rapid_view_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_BOARDADMINS"
    ADD CONSTRAINT fk_ao_60db71_boardadmins_rapid_view_id FOREIGN KEY ("RAPID_VIEW_ID") REFERENCES public."AO_60DB71_RAPIDVIEW"("ID");


--
-- TOC entry 5250 (class 2606 OID 19203)
-- Name: AO_60DB71_CARDCOLOR fk_ao_60db71_cardcolor_rapid_view_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_CARDCOLOR"
    ADD CONSTRAINT fk_ao_60db71_cardcolor_rapid_view_id FOREIGN KEY ("RAPID_VIEW_ID") REFERENCES public."AO_60DB71_RAPIDVIEW"("ID");


--
-- TOC entry 5249 (class 2606 OID 19186)
-- Name: AO_60DB71_CARDLAYOUT fk_ao_60db71_cardlayout_rapid_view_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_CARDLAYOUT"
    ADD CONSTRAINT fk_ao_60db71_cardlayout_rapid_view_id FOREIGN KEY ("RAPID_VIEW_ID") REFERENCES public."AO_60DB71_RAPIDVIEW"("ID");


--
-- TOC entry 5253 (class 2606 OID 19248)
-- Name: AO_60DB71_COLUMN fk_ao_60db71_column_rapid_view_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_COLUMN"
    ADD CONSTRAINT fk_ao_60db71_column_rapid_view_id FOREIGN KEY ("RAPID_VIEW_ID") REFERENCES public."AO_60DB71_RAPIDVIEW"("ID");


--
-- TOC entry 5261 (class 2606 OID 19381)
-- Name: AO_60DB71_COLUMNSTATUS fk_ao_60db71_columnstatus_column_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_COLUMNSTATUS"
    ADD CONSTRAINT fk_ao_60db71_columnstatus_column_id FOREIGN KEY ("COLUMN_ID") REFERENCES public."AO_60DB71_COLUMN"("ID");


--
-- TOC entry 5251 (class 2606 OID 19217)
-- Name: AO_60DB71_DETAILVIEWFIELD fk_ao_60db71_detailviewfield_rapid_view_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_DETAILVIEWFIELD"
    ADD CONSTRAINT fk_ao_60db71_detailviewfield_rapid_view_id FOREIGN KEY ("RAPID_VIEW_ID") REFERENCES public."AO_60DB71_RAPIDVIEW"("ID");


--
-- TOC entry 5255 (class 2606 OID 19280)
-- Name: AO_60DB71_ESTIMATESTATISTIC fk_ao_60db71_estimatestatistic_rapid_view_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_ESTIMATESTATISTIC"
    ADD CONSTRAINT fk_ao_60db71_estimatestatistic_rapid_view_id FOREIGN KEY ("RAPID_VIEW_ID") REFERENCES public."AO_60DB71_RAPIDVIEW"("ID");


--
-- TOC entry 5262 (class 2606 OID 19399)
-- Name: AO_60DB71_NONWORKINGDAY fk_ao_60db71_nonworkingday_working_days_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_NONWORKINGDAY"
    ADD CONSTRAINT fk_ao_60db71_nonworkingday_working_days_id FOREIGN KEY ("WORKING_DAYS_ID") REFERENCES public."AO_60DB71_WORKINGDAYS"("ID");


--
-- TOC entry 5252 (class 2606 OID 19234)
-- Name: AO_60DB71_QUICKFILTER fk_ao_60db71_quickfilter_rapid_view_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_QUICKFILTER"
    ADD CONSTRAINT fk_ao_60db71_quickfilter_rapid_view_id FOREIGN KEY ("RAPID_VIEW_ID") REFERENCES public."AO_60DB71_RAPIDVIEW"("ID");


--
-- TOC entry 5259 (class 2606 OID 19338)
-- Name: AO_60DB71_STATSFIELD fk_ao_60db71_statsfield_rapid_view_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_STATSFIELD"
    ADD CONSTRAINT fk_ao_60db71_statsfield_rapid_view_id FOREIGN KEY ("RAPID_VIEW_ID") REFERENCES public."AO_60DB71_RAPIDVIEW"("ID");


--
-- TOC entry 5257 (class 2606 OID 19312)
-- Name: AO_60DB71_SUBQUERY fk_ao_60db71_subquery_rapid_view_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_SUBQUERY"
    ADD CONSTRAINT fk_ao_60db71_subquery_rapid_view_id FOREIGN KEY ("RAPID_VIEW_ID") REFERENCES public."AO_60DB71_RAPIDVIEW"("ID");


--
-- TOC entry 5260 (class 2606 OID 19355)
-- Name: AO_60DB71_SWIMLANE fk_ao_60db71_swimlane_rapid_view_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_SWIMLANE"
    ADD CONSTRAINT fk_ao_60db71_swimlane_rapid_view_id FOREIGN KEY ("RAPID_VIEW_ID") REFERENCES public."AO_60DB71_RAPIDVIEW"("ID");


--
-- TOC entry 5256 (class 2606 OID 19296)
-- Name: AO_60DB71_TRACKINGSTATISTIC fk_ao_60db71_trackingstatistic_rapid_view_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_TRACKINGSTATISTIC"
    ADD CONSTRAINT fk_ao_60db71_trackingstatistic_rapid_view_id FOREIGN KEY ("RAPID_VIEW_ID") REFERENCES public."AO_60DB71_RAPIDVIEW"("ID");


--
-- TOC entry 5258 (class 2606 OID 19325)
-- Name: AO_60DB71_WORKINGDAYS fk_ao_60db71_workingdays_rapid_view_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_60DB71_WORKINGDAYS"
    ADD CONSTRAINT fk_ao_60db71_workingdays_rapid_view_id FOREIGN KEY ("RAPID_VIEW_ID") REFERENCES public."AO_60DB71_RAPIDVIEW"("ID");


--
-- TOC entry 5274 (class 2606 OID 19964)
-- Name: AO_733371_EVENT_PARAMETER fk_ao_733371_event_parameter_event_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_733371_EVENT_PARAMETER"
    ADD CONSTRAINT fk_ao_733371_event_parameter_event_id FOREIGN KEY ("EVENT_ID") REFERENCES public."AO_733371_EVENT"("ID");


--
-- TOC entry 5273 (class 2606 OID 19948)
-- Name: AO_733371_EVENT_RECIPIENT fk_ao_733371_event_recipient_event_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_733371_EVENT_RECIPIENT"
    ADD CONSTRAINT fk_ao_733371_event_recipient_event_id FOREIGN KEY ("EVENT_ID") REFERENCES public."AO_733371_EVENT"("ID");


--
-- TOC entry 5263 (class 2606 OID 19626)
-- Name: AO_E8B6CC_BRANCH fk_ao_e8b6cc_branch_repository_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_BRANCH"
    ADD CONSTRAINT fk_ao_e8b6cc_branch_repository_id FOREIGN KEY ("REPOSITORY_ID") REFERENCES public."AO_E8B6CC_REPOSITORY_MAPPING"("ID");


--
-- TOC entry 5264 (class 2606 OID 19662)
-- Name: AO_E8B6CC_GIT_HUB_EVENT fk_ao_e8b6cc_git_hub_event_repository_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_GIT_HUB_EVENT"
    ADD CONSTRAINT fk_ao_e8b6cc_git_hub_event_repository_id FOREIGN KEY ("REPOSITORY_ID") REFERENCES public."AO_E8B6CC_REPOSITORY_MAPPING"("ID");


--
-- TOC entry 5268 (class 2606 OID 19761)
-- Name: AO_E8B6CC_ISSUE_TO_BRANCH fk_ao_e8b6cc_issue_to_branch_branch_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_ISSUE_TO_BRANCH"
    ADD CONSTRAINT fk_ao_e8b6cc_issue_to_branch_branch_id FOREIGN KEY ("BRANCH_ID") REFERENCES public."AO_E8B6CC_BRANCH"("ID");


--
-- TOC entry 5270 (class 2606 OID 19800)
-- Name: AO_E8B6CC_MESSAGE_QUEUE_ITEM fk_ao_e8b6cc_message_queue_item_message_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_MESSAGE_QUEUE_ITEM"
    ADD CONSTRAINT fk_ao_e8b6cc_message_queue_item_message_id FOREIGN KEY ("MESSAGE_ID") REFERENCES public."AO_E8B6CC_MESSAGE"("ID");


--
-- TOC entry 5269 (class 2606 OID 19783)
-- Name: AO_E8B6CC_MESSAGE_TAG fk_ao_e8b6cc_message_tag_message_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_MESSAGE_TAG"
    ADD CONSTRAINT fk_ao_e8b6cc_message_tag_message_id FOREIGN KEY ("MESSAGE_ID") REFERENCES public."AO_E8B6CC_MESSAGE"("ID");


--
-- TOC entry 5265 (class 2606 OID 19675)
-- Name: AO_E8B6CC_ORG_TO_PROJECT fk_ao_e8b6cc_org_to_project_organization_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_ORG_TO_PROJECT"
    ADD CONSTRAINT fk_ao_e8b6cc_org_to_project_organization_id FOREIGN KEY ("ORGANIZATION_ID") REFERENCES public."AO_E8B6CC_ORGANIZATION_MAPPING"("ID");


--
-- TOC entry 5267 (class 2606 OID 19746)
-- Name: AO_E8B6CC_PR_PARTICIPANT fk_ao_e8b6cc_pr_participant_pull_request_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_PR_PARTICIPANT"
    ADD CONSTRAINT fk_ao_e8b6cc_pr_participant_pull_request_id FOREIGN KEY ("PULL_REQUEST_ID") REFERENCES public."AO_E8B6CC_PULL_REQUEST"("ID");


--
-- TOC entry 5272 (class 2606 OID 19816)
-- Name: AO_E8B6CC_PR_TO_COMMIT fk_ao_e8b6cc_pr_to_commit_commit_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_PR_TO_COMMIT"
    ADD CONSTRAINT fk_ao_e8b6cc_pr_to_commit_commit_id FOREIGN KEY ("COMMIT_ID") REFERENCES public."AO_E8B6CC_COMMIT"("ID");


--
-- TOC entry 5271 (class 2606 OID 19821)
-- Name: AO_E8B6CC_PR_TO_COMMIT fk_ao_e8b6cc_pr_to_commit_request_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_PR_TO_COMMIT"
    ADD CONSTRAINT fk_ao_e8b6cc_pr_to_commit_request_id FOREIGN KEY ("REQUEST_ID") REFERENCES public."AO_E8B6CC_PULL_REQUEST"("ID");


--
-- TOC entry 5266 (class 2606 OID 19727)
-- Name: AO_E8B6CC_REPO_TO_PROJECT fk_ao_e8b6cc_repo_to_project_repository_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."AO_E8B6CC_REPO_TO_PROJECT"
    ADD CONSTRAINT fk_ao_e8b6cc_repo_to_project_repository_id FOREIGN KEY ("REPOSITORY_ID") REFERENCES public."AO_E8B6CC_REPOSITORY_MAPPING"("ID");


-- Completed on 2020-03-09 03:15:41 UTC

--
-- PostgreSQL database dump complete
--

