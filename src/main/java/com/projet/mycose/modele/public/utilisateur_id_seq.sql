create sequence utilisateur_id_seq;

alter sequence utilisateur_id_seq owner to postgres;

alter sequence utilisateur_id_seq owned by utilisateur.id;

