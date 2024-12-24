--
-- PostgreSQL database dump
--

-- Dumped from database version 17.0 (Debian 17.0-1.pgdg120+1)
-- Dumped by pg_dump version 17.0 (Debian 17.0-1.pgdg120+1)
-- modified by if23b123


-- Create battles table
CREATE TABLE battles (
                         battle_id uuid DEFAULT gen_random_uuid() NOT NULL,
                         user1_id uuid,
                         user2_id uuid,
                         winner_id uuid,
                         battle_log text,
                         "timestamp" timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT battles_pkey PRIMARY KEY (battle_id)
);

-- Create cards table
CREATE TABLE cards (
                       card_id uuid DEFAULT gen_random_uuid() NOT NULL,
                       name character varying(50) NOT NULL,
                       damage double precision NOT NULL,
                       card_type character varying(20),
                       CONSTRAINT cards_card_type_check CHECK (((card_type)::text = ANY ((ARRAY['monster', 'spell'])::text[]))),
                       CONSTRAINT cards_pkey PRIMARY KEY (card_id)
);

-- Create decks table
CREATE TABLE decks (
                       deck_id uuid DEFAULT gen_random_uuid() NOT NULL,
                       user_id uuid,
                       card_id uuid,
                       "position" integer,
                       CONSTRAINT decks_position_check CHECK ((("position" >= 1) AND ("position" <= 4))),
                       CONSTRAINT decks_pkey PRIMARY KEY (deck_id),
                       CONSTRAINT decks_user_id_position_key UNIQUE (user_id, "position")
);

-- Create package_cards table
CREATE TABLE package_cards (
                               package_card_id uuid DEFAULT gen_random_uuid() NOT NULL,
                               package_id uuid,
                               card_id uuid,
                               CONSTRAINT package_cards_pkey PRIMARY KEY (package_card_id)
);

-- Create packages table
CREATE TABLE packages (
                          package_id uuid DEFAULT gen_random_uuid() NOT NULL,
                          created_by uuid,
                          CONSTRAINT packages_pkey PRIMARY KEY (package_id)
);

-- Create trades table
CREATE TABLE trades (
                        trade_id uuid DEFAULT gen_random_uuid() NOT NULL,
                        deal_id uuid,
                        offered_card uuid,
                        "timestamp" timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT trades_pkey PRIMARY KEY (trade_id)
);

-- Create trading_deals table
CREATE TABLE trading_deals (
                               deal_id uuid DEFAULT gen_random_uuid() NOT NULL,
                               user_id uuid,
                               card_to_trade uuid,
                               required_type character varying(20),
                               minimum_damage double precision,
                               CONSTRAINT trading_deals_required_type_check CHECK (((required_type)::text = ANY ((ARRAY['monster', 'spell'])::text[]))),
                               CONSTRAINT trading_deals_pkey PRIMARY KEY (deal_id)
);

-- Create transactions table
CREATE TABLE transactions (
                              transaction_id uuid DEFAULT gen_random_uuid() NOT NULL,
                              user_id uuid,
                              package_id uuid,
                              "timestamp" timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
                              CONSTRAINT transactions_pkey PRIMARY KEY (transaction_id)
);

-- Create user_cards table
CREATE TABLE user_cards (
                            user_card_id uuid DEFAULT gen_random_uuid() NOT NULL,
                            user_id uuid,
                            card_id uuid,
                            in_deck boolean DEFAULT false,
                            CONSTRAINT user_cards_pkey PRIMARY KEY (user_card_id)
);

-- Create users table
CREATE TABLE users (
                       user_id uuid DEFAULT gen_random_uuid() NOT NULL,
                       username character varying(50) NOT NULL,
                       password character varying(100) NOT NULL,
                       elo integer DEFAULT 1000,
                       wins integer DEFAULT 0,
                       losses integer DEFAULT 0,
                       token character varying(60),
                       name character varying(50),
                       bio character varying(100),
                       image character varying(20),
                       CONSTRAINT users_pkey PRIMARY KEY (user_id),
                       CONSTRAINT users_username_key UNIQUE (username)
);

-- Define foreign keys
ALTER TABLE ONLY battles
    ADD CONSTRAINT battles_user1_id_fkey FOREIGN KEY (user1_id) REFERENCES users(user_id) ON DELETE SET NULL;
ALTER TABLE ONLY battles
    ADD CONSTRAINT battles_user2_id_fkey FOREIGN KEY (user2_id) REFERENCES users(user_id) ON DELETE SET NULL;
ALTER TABLE ONLY battles
    ADD CONSTRAINT battles_winner_id_fkey FOREIGN KEY (winner_id) REFERENCES users(user_id) ON DELETE SET NULL;

ALTER TABLE ONLY decks
    ADD CONSTRAINT decks_card_id_fkey FOREIGN KEY (card_id) REFERENCES cards(card_id) ON DELETE CASCADE;
ALTER TABLE ONLY decks
    ADD CONSTRAINT decks_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE;

ALTER TABLE ONLY package_cards
    ADD CONSTRAINT package_cards_card_id_fkey FOREIGN KEY (card_id) REFERENCES cards(card_id) ON DELETE CASCADE;
ALTER TABLE ONLY package_cards
    ADD CONSTRAINT package_cards_package_id_fkey FOREIGN KEY (package_id) REFERENCES packages(package_id) ON DELETE CASCADE;

ALTER TABLE ONLY packages
    ADD CONSTRAINT packages_created_by_fkey FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL;

ALTER TABLE ONLY trades
    ADD CONSTRAINT trades_deal_id_fkey FOREIGN KEY (deal_id) REFERENCES trading_deals(deal_id) ON DELETE CASCADE;
ALTER TABLE ONLY trades
    ADD CONSTRAINT trades_offered_card_fkey FOREIGN KEY (offered_card) REFERENCES cards(card_id) ON DELETE SET NULL;

ALTER TABLE ONLY trading_deals
    ADD CONSTRAINT trading_deals_card_to_trade_fkey FOREIGN KEY (card_to_trade) REFERENCES cards(card_id) ON DELETE SET NULL;
ALTER TABLE ONLY trading_deals
    ADD CONSTRAINT trading_deals_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE;

ALTER TABLE ONLY transactions
    ADD CONSTRAINT transactions_package_id_fkey FOREIGN KEY (package_id) REFERENCES packages(package_id) ON DELETE SET NULL;
ALTER TABLE ONLY transactions
    ADD CONSTRAINT transactions_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE;

ALTER TABLE ONLY user_cards
    ADD CONSTRAINT user_cards_card_id_fkey FOREIGN KEY (card_id) REFERENCES cards(card_id) ON DELETE CASCADE;
ALTER TABLE ONLY user_cards
    ADD CONSTRAINT user_cards_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE;

