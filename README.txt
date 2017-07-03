Runs over a directory of source code and uploads it to Solr.

This is a Spring Boot application.

The Solr 6 core configuration is in src/main/resources/solr.tar.gz

This code indexes directories containing code for different application servers (JBoss, Tomcat), different versions of those servers and different branches of code
for those platforms (typically a "master" and frequently long lived feature branches). Configuration for which files are indexed and which server/version/branch is
contained in the yaml configuration files.
