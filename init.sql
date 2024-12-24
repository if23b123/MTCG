--
-- PostgreSQL database dump
--

-- Dumped from database version 17.0 (Debian 17.0-1.pgdg120+1)
-- Dumped by pg_dump version 17.0 (Debian 17.0-1.pgdg120+1)

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
-- Name: battles; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.battles (
    battle_id uuid DEFAULT gen_random_uuid() NOT NULL,
    user1_id uuid,
    user2_id uuid,
    winner_id uuid,
    battle_log text,
    "timestamp" timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.battles OWNER TO postgres;

--
-- Name: cards; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.cards (
    card_id uuid DEFAULT gen_random_uuid() NOT NULL,
    name character varying(50) NOT NULL,
    damage double precision NOT NULL,
    card_type character varying(20),
    CONSTRAINT cards_card_type_check CHECK (((card_type)::text = ANY ((ARRAY['monster'::character varying, 'spell'::character varying])::text[])))
);


ALTER TABLE public.cards OWNER TO postgres;

--
-- Name: decks; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.decks (
    deck_id uuid DEFAULT gen_random_uuid() NOT NULL,
    user_id uuid,
    card_id uuid,
    "position" integer,
    CONSTRAINT decks_position_check CHECK ((("position" >= 1) AND ("position" <= 4)))
);


ALTER TABLE public.decks OWNER TO postgres;

--
-- Name: package_cards; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.package_cards (
    package_card_id uuid DEFAULT gen_random_uuid() NOT NULL,
    package_id uuid,
    card_id uuid
);


ALTER TABLE public.package_cards OWNER TO postgres;

--
-- Name: packages; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.packages (
    package_id uuid DEFAULT gen_random_uuid() NOT NULL,
    created_by uuid
);


ALTER TABLE public.packages OWNER TO postgres;

--
-- Name: trades; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.trades (
    trade_id uuid DEFAULT gen_random_uuid() NOT NULL,
    deal_id uuid,
    offered_card uuid,
    "timestamp" timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.trades OWNER TO postgres;

--
-- Name: trading_deals; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.trading_deals (
    deal_id uuid DEFAULT gen_random_uuid() NOT NULL,
    user_id uuid,
    card_to_trade uuid,
    required_type character varying(20),
    minimum_damage double precision,
    CONSTRAINT trading_deals_required_type_check CHECK (((required_type)::text = ANY ((ARRAY['monster'::character varying, 'spell'::character varying])::text[])))
);


ALTER TABLE public.trading_deals OWNER TO postgres;

--
-- Name: transactions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.transactions (
    transaction_id uuid DEFAULT gen_random_uuid() NOT NULL,
    user_id uuid,
    package_id uuid,
    "timestamp" timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.transactions OWNER TO postgres;

--
-- Name: user_cards; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_cards (
    user_card_id uuid DEFAULT gen_random_uuid() NOT NULL,
    user_id uuid,
    card_id uuid,
    in_deck boolean DEFAULT false
);


ALTER TABLE public.user_cards OWNER TO postgres;

--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    user_id uuid DEFAULT gen_random_uuid() NOT NULL,
    username character varying(50) NOT NULL,
    password character varying(100) NOT NULL,
    elo integer DEFAULT 1000,
    wins integer DEFAULT 0,
    losses integer DEFAULT 0,
    token character varying(60),
    name character varying(50),
    bio character varying(100),
    image character varying(20)
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Data for Name: battles; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.battles (battle_id, user1_id, user2_id, winner_id, battle_log, "timestamp") FROM stdin;
\.


--
-- Data for Name: cards; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.cards (card_id, name, damage, card_type) FROM stdin;
\.


--
-- Data for Name: decks; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.decks (deck_id, user_id, card_id, "position") FROM stdin;
\.


--
-- Data for Name: package_cards; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.package_cards (package_card_id, package_id, card_id) FROM stdin;
\.


--
-- Data for Name: packages; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.packages (package_id, created_by) FROM stdin;
\.


--
-- Data for Name: trades; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.trades (trade_id, deal_id, offered_card, "timestamp") FROM stdin;
\.


--
-- Data for Name: trading_deals; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.trading_deals (deal_id, user_id, card_to_trade, required_type, minimum_damage) FROM stdin;
\.


--
-- Data for Name: transactions; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.transactions (transaction_id, user_id, package_id, "timestamp") FROM stdin;
\.


--
-- Data for Name: user_cards; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.user_cards (user_card_id, user_id, card_id, in_deck) FROM stdin;
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (user_id, username, password, elo, wins, losses, token, name, bio, image) FROM stdin;
58ed362d-0e94-4809-a9d8-c149a474649d	beri	istrator	1000	0	0	\N	\N	\N	\N
7dfe60f9-2bd9-4c37-a571-0c934c37e40f	altenhof	markus	1000	0	0	altenhof-mtcgToken	Altenhofer	me codin...	:-D
1cad1f7d-aba0-4c76-bbf4-51492f998dd5	admin	istrator	1000	0	0	admin-mtcgToken	\N	\N	\N
4516fe44-64e0-4cc1-a12e-b0a9721f16d4	Burhan	MinaMiaw	1000	0	0	\N			
c3ad298c-a5cb-48eb-8b17-e5c41915b675	Gadse	MinaMiaw	1000	0	0	\N			
086317ec-4b25-4aa4-a34c-4fb9244a2d30	mhamad	hihi	1000	0	0	\N			
b2966ad3-235a-42f3-95c5-12a6be032fe2	Mina	Mimi	1000	0	0	Mina-mtcgToken			
cf8ad0d0-8502-4c94-ae9f-68473ded1754	kienboec	daniel	1000	0	0	kienboec-mtcgToken	Kienboeck	me playin...	:-)
\.


--
-- Name: battles battles_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.battles
    ADD CONSTRAINT battles_pkey PRIMARY KEY (battle_id);


--
-- Name: cards cards_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cards
    ADD CONSTRAINT cards_pkey PRIMARY KEY (card_id);


--
-- Name: decks decks_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.decks
    ADD CONSTRAINT decks_pkey PRIMARY KEY (deck_id);


--
-- Name: decks decks_user_id_position_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.decks
    ADD CONSTRAINT decks_user_id_position_key UNIQUE (user_id, "position");


--
-- Name: package_cards package_cards_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.package_cards
    ADD CONSTRAINT package_cards_pkey PRIMARY KEY (package_card_id);


--
-- Name: packages packages_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.packages
    ADD CONSTRAINT packages_pkey PRIMARY KEY (package_id);


--
-- Name: trades trades_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trades
    ADD CONSTRAINT trades_pkey PRIMARY KEY (trade_id);


--
-- Name: trading_deals trading_deals_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trading_deals
    ADD CONSTRAINT trading_deals_pkey PRIMARY KEY (deal_id);


--
-- Name: transactions transactions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transactions
    ADD CONSTRAINT transactions_pkey PRIMARY KEY (transaction_id);


--
-- Name: user_cards user_cards_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_cards
    ADD CONSTRAINT user_cards_pkey PRIMARY KEY (user_card_id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


--
-- Name: users users_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- Name: battles battles_user1_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.battles
    ADD CONSTRAINT battles_user1_id_fkey FOREIGN KEY (user1_id) REFERENCES public.users(user_id) ON DELETE SET NULL;


--
-- Name: battles battles_user2_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.battles
    ADD CONSTRAINT battles_user2_id_fkey FOREIGN KEY (user2_id) REFERENCES public.users(user_id) ON DELETE SET NULL;


--
-- Name: battles battles_winner_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.battles
    ADD CONSTRAINT battles_winner_id_fkey FOREIGN KEY (winner_id) REFERENCES public.users(user_id) ON DELETE SET NULL;


--
-- Name: decks decks_card_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.decks
    ADD CONSTRAINT decks_card_id_fkey FOREIGN KEY (card_id) REFERENCES public.cards(card_id) ON DELETE CASCADE;


--
-- Name: decks decks_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.decks
    ADD CONSTRAINT decks_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE;


--
-- Name: package_cards package_cards_card_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.package_cards
    ADD CONSTRAINT package_cards_card_id_fkey FOREIGN KEY (card_id) REFERENCES public.cards(card_id) ON DELETE CASCADE;


--
-- Name: package_cards package_cards_package_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.package_cards
    ADD CONSTRAINT package_cards_package_id_fkey FOREIGN KEY (package_id) REFERENCES public.packages(package_id) ON DELETE CASCADE;


--
-- Name: packages packages_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.packages
    ADD CONSTRAINT packages_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(user_id) ON DELETE SET NULL;


--
-- Name: trades trades_deal_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trades
    ADD CONSTRAINT trades_deal_id_fkey FOREIGN KEY (deal_id) REFERENCES public.trading_deals(deal_id) ON DELETE CASCADE;


--
-- Name: trades trades_offered_card_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trades
    ADD CONSTRAINT trades_offered_card_fkey FOREIGN KEY (offered_card) REFERENCES public.cards(card_id) ON DELETE SET NULL;


--
-- Name: trading_deals trading_deals_card_to_trade_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trading_deals
    ADD CONSTRAINT trading_deals_card_to_trade_fkey FOREIGN KEY (card_to_trade) REFERENCES public.cards(card_id) ON DELETE SET NULL;


--
-- Name: trading_deals trading_deals_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trading_deals
    ADD CONSTRAINT trading_deals_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE;


--
-- Name: transactions transactions_package_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transactions
    ADD CONSTRAINT transactions_package_id_fkey FOREIGN KEY (package_id) REFERENCES public.packages(package_id) ON DELETE SET NULL;


--
-- Name: transactions transactions_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transactions
    ADD CONSTRAINT transactions_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE;


--
-- Name: user_cards user_cards_card_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_cards
    ADD CONSTRAINT user_cards_card_id_fkey FOREIGN KEY (card_id) REFERENCES public.cards(card_id) ON DELETE CASCADE;


--
-- Name: user_cards user_cards_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_cards
    ADD CONSTRAINT user_cards_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

