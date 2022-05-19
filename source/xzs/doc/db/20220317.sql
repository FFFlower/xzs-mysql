CREATE TABLE t_user_subject (
   id int(11) NOT NULL AUTO_INCREMENT,
   user_id int(11) DEFAULT NULL,
   level_id int(11) DEFAULT NULL,
   subject_id int(11) DEFAULT NULL,
   PRIMARY KEY (id) USING BTREE,
   index user_level_subjec_idx(user_id,level_id,subject_id)
) ENGINE=InnoDB AUTO_INCREMENT=669 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT

CREATE TABLE t_exam_paper_user (
    id int(11) NOT NULL AUTO_INCREMENT,
    exam_paper_id int(11) DEFAULT NULL,
    user_id int(11) DEFAULT NULL,
    PRIMARY KEY (id) USING BTREE,
    index user_id_idx(user_id),
    index exam_paper_id_idx(exam_paper_id)
) ENGINE=InnoDB AUTO_INCREMENT=669 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT