alter table t_exam_paper add column set_method int(11) comment '出题方式';


CREATE TABLE t_exam_error_question (
   id int(11) NOT NULL AUTO_INCREMENT,
   question_id int(11) DEFAULT NULL,
   exam_paper_id int(11) DEFAULT NULL,
   exam_paper_answer_id int(11) DEFAULT NULL,
   question_type int(11) DEFAULT NULL,
   subject_id int(11) DEFAULT NULL,
   question_text_content_id int(11) DEFAULT NULL,
   answer varchar(255) DEFAULT NULL,
   create_user int(11) DEFAULT NULL,
   create_time datetime DEFAULT NULL,
   item_order int(11) DEFAULT NULL,
   del_flag int(1) not null default 0,
   PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=669 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT