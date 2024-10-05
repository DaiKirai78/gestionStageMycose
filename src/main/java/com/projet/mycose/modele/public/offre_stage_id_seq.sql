create sequence offre_stage_id_seq;

alter sequence offre_stage_id_seq owner to postgres;

alter sequence offre_stage_id_seq owned by offre_stage.id;

