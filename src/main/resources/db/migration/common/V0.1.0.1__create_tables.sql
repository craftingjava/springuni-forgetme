CREATE TABLE subscriber (
  id uuid NOT NULL,
  email_hash char(128) not null,
  status varchar(25) not null,
  created_date timestamp not NULL,
  last_modified_date timestamp not NULL,
  version_ integer not null,
  CONSTRAINT subscriber_pk PRIMARY KEY (id)
);

CREATE TABLE subscriber_status_change (
  subscriber_id uuid NOT NULL,
  status varchar(25) not null,
  changed_date timestamp not NULL,
  CONSTRAINT subscriber_status_change_subscriber_id_fk FOREIGN KEY (subscriber_id) REFERENCES subscriber(id)
);
