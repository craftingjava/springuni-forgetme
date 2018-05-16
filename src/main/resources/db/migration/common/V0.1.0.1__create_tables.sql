CREATE TABLE subscriber (
  id uuid NOT NULL,
  email_hash char(128) not null,
  status varchar(25) not null,
  created_date timestamp NULL,
  last_modified_date timestamp NULL,
  version_ integer not null,
  CONSTRAINT click_pk PRIMARY KEY (id)
);
