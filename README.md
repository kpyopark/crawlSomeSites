# crawlSomeSites

# Prerequiste
Before to run this app, you should make a target database which to store the test result in it and modify BaseDao.java file to connect the target database. 

1. Make a database (MySQL/PostgreSQL). - I tested it with PG.
2. Create target tables. Use below scripts. 
```
CREATE TABLE public.tb_house_image_info (
	house_key varchar(20) NOT NULL,
	img_hash varchar(50) NOT NULL,
	img_uri varchar(500),
	first_yn varchar(10)
) ;
CREATE UNIQUE INDEX tb_house_image_info_pkey ON public.tb_house_image_info (house_key,img_hash) ;

CREATE TABLE public.tb_house_image_rekog_label_infos (
	house_key varchar(20) NOT NULL,
	img_hash varchar(50) NOT NULL,
	label_hash varchar(50) NOT NULL,
	label_text varchar(200),
	confidence varchar(60)
) ;
CREATE UNIQUE INDEX tb_house_image_rekog_label_infos_pkey ON public.tb_house_image_rekog_label_infos (house_key,img_hash,label_hash) ;

CREATE TABLE public.tb_house_info (
	house_key varchar(20) NOT NULL,
	region_code varchar(20),
	region varchar(200),
	category varchar(100),
	ratio varchar(10),
	title varchar(200),
	review_count varchar(20),
	rel_uri varchar(500)
) ;
CREATE UNIQUE INDEX tb_house_info_pkey ON public.tb_house_info (house_key) ;

CREATE TABLE public.tb_house_review_comp_infos (
	house_key varchar(20) NOT NULL,
	review_hash varchar(50) NOT NULL,
	kor_eng varchar(10) NOT NULL,
	seq int4 NOT NULL,
	phrase varchar(200),
	score float8
) ;
CREATE UNIQUE INDEX tb_house_review_comp_infos_pkey ON public.tb_house_review_comp_infos (house_key,review_hash,kor_eng,seq) ;

CREATE TABLE public.tb_house_review_info (
	house_key varchar(20) NOT NULL,
	review_hash varchar(50) NOT NULL,
	review_kor varchar(4000),
	review_eng varchar(4000),
	review_ratio int4
) ;
CREATE UNIQUE INDEX tb_house_review_info_pkey ON public.tb_house_review_info (house_key,review_hash) ;
```
3. Install java JDK (v11>) - https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/downloads-list.html
4. Install maven (v3.5.0) - https://maven.apache.org/install.html)

# Install & Running. 

1. Clone this repository. 
2. Modify elevenquest.com.booking.dao.BaseDao.java such like below.
```
  public Connection getConnection() throws SQLException {
    Connection con = null;
    con = DriverManager.getConnection("jdbc:postgresql://testdb.suchlike.region.rds.amazonaws.com/dev", "userid", "pwd");
    return con;
  }
```
2. cd crawl.aws.utility. In this directory, you can find a pom.xml file.
3. mvn clean
4. mvn compile
5. mvn exec:java -Dexec.mainClass="elevenquest.com.booking.SiteAnalyzer"

# Limitation
 * This project is designed for the adhoc test not instead for production. 
 * This project has append only mode. If you want to rerun this app, before to run it, you should truncate all tables in the database
 * This project doesn't use SentimentalAnalysis. If you want to use it, you should modify tb_house_review_comp_infos table to store sentimental analysis result and modify HouseInfoDao.java and SiteAnalyzer.java files. 
 
