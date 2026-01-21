--
-- PostgreSQL database dump
--

\restrict TRaDIOMjSfdVbMLNlXVjGi9T0UxMvU8AqUM0Gauyfggu8ZEnPiBOU5lO5849i6o

-- Dumped from database version 18.1 (Debian 18.1-1.pgdg13+2)
-- Dumped by pg_dump version 18.1 (Debian 18.1-1.pgdg13+2)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: genre; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.genre (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name character varying(100) NOT NULL
);


ALTER TABLE public.genre OWNER TO admin;

--
-- Name: media_entry; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.media_entry (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    created_by uuid NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    title character varying(100) NOT NULL,
    description character varying(255),
    release_year integer,
    age_restriction integer,
    media_type character varying(50) NOT NULL
);


ALTER TABLE public.media_entry OWNER TO admin;

--
-- Name: media_favorite; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.media_favorite (
    user_id uuid NOT NULL,
    media_id uuid NOT NULL
);


ALTER TABLE public.media_favorite OWNER TO admin;

--
-- Name: media_genre; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.media_genre (
    media_id uuid NOT NULL,
    genre_id uuid NOT NULL
);


ALTER TABLE public.media_genre OWNER TO admin;

--
-- Name: rating; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.rating (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    created_by uuid NOT NULL,
    media_id uuid NOT NULL,
    is_public boolean DEFAULT false NOT NULL,
    stars integer CONSTRAINT rating_value_not_null NOT NULL,
    comment character varying(255),
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone,
    CONSTRAINT rating_value_check CHECK (((stars >= 1) AND (stars <= 5)))
);


ALTER TABLE public.rating OWNER TO admin;

--
-- Name: rating_like; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.rating_like (
    user_id uuid NOT NULL,
    rating_id uuid NOT NULL
);


ALTER TABLE public.rating_like OWNER TO admin;

--
-- Name: users; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.users (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    username character varying(100) NOT NULL,
    password_hash character varying(255) NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    email character varying(255),
    favorite_genre_id uuid
);


ALTER TABLE public.users OWNER TO admin;

--
-- Data for Name: genre; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.genre (id, name) FROM stdin;
fd820884-8305-42ad-a229-1545708a0833	sci-fi
d62c2e76-6a85-438a-8425-0639afaf3006	thriller
\.


--
-- Data for Name: media_entry; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.media_entry (id, created_by, created_at, title, description, release_year, age_restriction, media_type) FROM stdin;
5e4aac92-a829-43f3-a95a-d0d5a80a7f23	60cc1093-c3ce-4859-8ffb-71e39c32cb3c	2026-01-21 04:51:17.320293	Inception	Sci-fi thriller	2010	12	MOVIE
\.


--
-- Data for Name: media_favorite; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.media_favorite (user_id, media_id) FROM stdin;
\.


--
-- Data for Name: media_genre; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.media_genre (media_id, genre_id) FROM stdin;
5e4aac92-a829-43f3-a95a-d0d5a80a7f23	fd820884-8305-42ad-a229-1545708a0833
5e4aac92-a829-43f3-a95a-d0d5a80a7f23	d62c2e76-6a85-438a-8425-0639afaf3006
\.


--
-- Data for Name: rating; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.rating (id, created_by, media_id, is_public, stars, comment, created_at, updated_at) FROM stdin;
cccb3293-9e26-400e-963e-2f206bd16ac9	60cc1093-c3ce-4859-8ffb-71e39c32cb3c	5e4aac92-a829-43f3-a95a-d0d5a80a7f23	f	5	Amazing movie!	2026-01-21 05:02:57.19777	2026-01-21 05:02:57.19777
\.


--
-- Data for Name: rating_like; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.rating_like (user_id, rating_id) FROM stdin;
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.users (id, username, password_hash, created_at, email, favorite_genre_id) FROM stdin;
60cc1093-c3ce-4859-8ffb-71e39c32cb3c	user1	pass123	2026-01-21 04:40:02.913236	user1@mrp.at	\N
\.


--
-- Name: genre genre_name_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.genre
    ADD CONSTRAINT genre_name_key UNIQUE (name);


--
-- Name: genre genre_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.genre
    ADD CONSTRAINT genre_pkey PRIMARY KEY (id);


--
-- Name: media_entry media_entry_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.media_entry
    ADD CONSTRAINT media_entry_pkey PRIMARY KEY (id);


--
-- Name: media_favorite media_favorite_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.media_favorite
    ADD CONSTRAINT media_favorite_pkey PRIMARY KEY (user_id, media_id);


--
-- Name: media_genre media_genre_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.media_genre
    ADD CONSTRAINT media_genre_pkey PRIMARY KEY (media_id, genre_id);


--
-- Name: rating rating_created_by_media_id_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.rating
    ADD CONSTRAINT rating_created_by_media_id_key UNIQUE (created_by, media_id);


--
-- Name: rating_like rating_like_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.rating_like
    ADD CONSTRAINT rating_like_pkey PRIMARY KEY (user_id, rating_id);


--
-- Name: rating rating_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.rating
    ADD CONSTRAINT rating_pkey PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: users users_username_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- Name: media_entry media_entry_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.media_entry
    ADD CONSTRAINT media_entry_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id);


--
-- Name: media_favorite media_favorite_media_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.media_favorite
    ADD CONSTRAINT media_favorite_media_id_fkey FOREIGN KEY (media_id) REFERENCES public.media_entry(id) ON DELETE CASCADE;


--
-- Name: media_favorite media_favorite_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.media_favorite
    ADD CONSTRAINT media_favorite_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: media_genre media_genre_genre_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.media_genre
    ADD CONSTRAINT media_genre_genre_id_fkey FOREIGN KEY (genre_id) REFERENCES public.genre(id) ON DELETE CASCADE;


--
-- Name: media_genre media_genre_media_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.media_genre
    ADD CONSTRAINT media_genre_media_id_fkey FOREIGN KEY (media_id) REFERENCES public.media_entry(id) ON DELETE CASCADE;


--
-- Name: rating rating_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.rating
    ADD CONSTRAINT rating_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: rating_like rating_like_rating_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.rating_like
    ADD CONSTRAINT rating_like_rating_id_fkey FOREIGN KEY (rating_id) REFERENCES public.rating(id) ON DELETE CASCADE;


--
-- Name: rating_like rating_like_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.rating_like
    ADD CONSTRAINT rating_like_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: rating rating_media_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.rating
    ADD CONSTRAINT rating_media_id_fkey FOREIGN KEY (media_id) REFERENCES public.media_entry(id) ON DELETE CASCADE;


--
-- Name: users users_favorite_genre_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_favorite_genre_id_fkey FOREIGN KEY (favorite_genre_id) REFERENCES public.genre(id);


--
-- PostgreSQL database dump complete
--

\unrestrict TRaDIOMjSfdVbMLNlXVjGi9T0UxMvU8AqUM0Gauyfggu8ZEnPiBOU5lO5849i6o

