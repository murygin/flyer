
# -----------------------------------------------------------------------
# RDF
# -----------------------------------------------------------------------
drop table if exists RDF;

CREATE TABLE RDF
(
    RDF_ID INTEGER NOT NULL AUTO_INCREMENT,
    TITLE VARCHAR (255),
    BODY VARCHAR (255),
    URL VARCHAR (255),
    AUTHOR VARCHAR (255),
    DEPT VARCHAR (255),
    PRIMARY KEY(RDF_ID)
);
