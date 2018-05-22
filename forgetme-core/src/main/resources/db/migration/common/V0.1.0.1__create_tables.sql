CREATE TABLE subscriber (
  id uuid NOT NULL,
  email_hash char(128) not null,
  created_date timestamp not NULL default CURRENT_TIMESTAMP,
  last_modified_date timestamp not NULL default CURRENT_TIMESTAMP,
  version_ integer not null,
  CONSTRAINT subscriber_pk PRIMARY KEY (id),
  CONSTRAINT subscriber_uk UNIQUE (email_hash)
);

CREATE TABLE data_handler (
  id uuid NOT NULL,
  name varchar(25) not null,
  key uuid not null,
  created_date timestamp not NULL default CURRENT_TIMESTAMP,
  last_modified_date timestamp not NULL default CURRENT_TIMESTAMP,
  version_ integer not null,
  CONSTRAINT data_handler_pk PRIMARY KEY (id),
  CONSTRAINT data_handler_uk UNIQUE (name)
);

CREATE TABLE subscription (
  id uuid NOT NULL,
  data_handler_id uuid NOT NULL,
  subscriber_id uuid NOT NULL,
  status varchar(25) not null,
  created_date timestamp not NULL default CURRENT_TIMESTAMP,
  last_modified_date timestamp not NULL default CURRENT_TIMESTAMP,
  version_ integer not null,
  CONSTRAINT subscription_pk PRIMARY KEY (id),
  CONSTRAINT subscription_uk UNIQUE (data_handler_id, subscriber_id),
  CONSTRAINT subscription_data_handler_id_fk FOREIGN KEY (data_handler_id) REFERENCES data_handler(id),
  CONSTRAINT subscription_subscriber_id_fk FOREIGN KEY (subscriber_id) REFERENCES subscriber(id)
);

CREATE INDEX subscription_subscriber_id_idx ON subscription(subscriber_id);

CREATE TABLE subscription_status_change (
  subscription_id uuid NOT NULL,
  status varchar(25) not null,
  changed_date timestamp not NULL default CURRENT_TIMESTAMP,
  CONSTRAINT subscription_status_change_subscription_id_fk FOREIGN KEY (subscription_id) REFERENCES subscription(id)
);

CREATE INDEX subscription_status_change_subscription_id_idx ON subscription_status_change(subscription_id);
