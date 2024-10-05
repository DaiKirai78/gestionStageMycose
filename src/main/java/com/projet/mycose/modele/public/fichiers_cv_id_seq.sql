create sequence fichiers_cv_id_seq;

alter sequence fichiers_cv_id_seq owner to postgres;

alter sequence fichiers_cv_id_seq owned by fichiers_cv.id;

