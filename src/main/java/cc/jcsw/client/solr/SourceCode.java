package cc.jcsw.client.solr;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.SolrDocument;

/**
 * POJO Represents source code.
 * 
 * @author Chris Carcel
 *
 */
@SolrDocument(solrCoreName = "javasource")
public class SourceCode {

    @Field
    private String id;

    @Field("file_name")
    private String fileName;

    @Field("file_name2")
    private String fileName2;

    @Field("fqn")
    private String fqn;

    @Field
    private String branch;

    @Field("jboss_version")
    private Integer jbossVersion;

    @Field("java_source")
    private String javaSource;

    @Field("name_and_file")
    private String nameAndFile;

    @Field("file_extension")
    private String fileExtension;

    @Field
    private Long size;

    @Field("hash_value")
    private String hashValue;

    @Field
    private String folder;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String file_name) {
        this.fileName = file_name;
    }

    public String getFileName2() {
        return fileName2;
    }

    public void setFileName2(String file_name2) {
        this.fileName2 = file_name2;
    }

    public String getFqn() {
        return fqn;
    }

    public void setFqn(String fqn) {
        this.fqn = fqn;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Integer getJbossVersion() {
        return jbossVersion;
    }

    public void setJbossVersion(Integer jboss_version) {
        this.jbossVersion = jboss_version;
    }

    public String getJavaSource() {
        return javaSource;
    }

    public void setJavaSource(String java_source) {
        this.javaSource = java_source;
    }

    public String getNameAndFile() {
        return nameAndFile;
    }

    public void setNameAndFile(String name_and_file) {
        this.nameAndFile = name_and_file;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String file_extension) {
        this.fileExtension = file_extension;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getHashValue() {
        return hashValue;
    }

    public void setHashValue(String hash_value) {
        this.hashValue = hash_value;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    @Override
    public String toString() {
        return "SourceCode [id=" + id + ", fqn=" + fqn + ", hash_value=" + hashValue + "]";
    }

}
