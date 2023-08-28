package com.bixuebihui.elasticsearch.script;

import com.bixuebihui.elasticsearch.query.Params;


import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Base class for things that can be sent to the update api (Document and
 * Script).
 *
 * @author   Nik Everett <nik9000@gmail.com>
 */
class AbstractUpdateAction extends Params
{
    /**
     * @var Document
     */
    protected  Document _upsert;

    /**
     * Sets the id of the document.
     *
     * @return $this
     */
    //convert below php code to java
//    public function setId(?string $id = null): self
//    {
//        return $this->setParam('_id', $id);
//    }
    public AbstractUpdateAction setId(String id){
        this.setParam("_id", id);
        return this;
    }


    /**
     * Returns document id.
     *
     * @return string|null Document id
     */
    public String  getId()
    {
        return this.hasParam("_id") ? (String) this.getParam("_id") : null;
    }

    public boolean hasId()
    {
        return null != this.getId();
    }

    /**
     * Sets the document index name.
     *
     * @param index Index|string $index Index name
     */
    //convert below php code to java
//    public  setIndex($index)
//    {
//        if ($index instanceof Index) {
//            $index = $index->getName();
//        }
//
//        return $this->setParam('_index', $index);
//    }
    public AbstractUpdateAction setIndex(Index index){
        this.setParam("_index", index.getName());
        return this;
    }
    public AbstractUpdateAction setIndex(String index){
        this.setParam("_index", index);
        return this;
    }

    /**
     * Get the document index name.
     *
     * @throws
     *
     * @return string Index name
     */
    //convert below php code to java
//    public function getIndex()
//    {
//        return $this->getParam('_index');
//    }
    public String getIndex()
    {
        return (String) this.getParam("_index");
    }

    /**
     * Sets the version parameters of a document for use with optimistic concurrency control.
     *
     * @return $this
     */
    //convert below php code to java
//    public function setVersionParams(array $responseData): self
//    {
//        if (isset($responseData['_version'])) {
//            $this->setVersion($responseData['_version']);
//        }
//
//        if (isset($responseData['_seq_no'])) {
//            $this->setSequenceNumber($responseData['_seq_no']);
//        }
//
//        if (isset($responseData['_primary_term'])) {
//            $this->setPrimaryTerm($responseData['_primary_term']);
//        }
//
//        return $this;
//    }
    public AbstractUpdateAction setVersionParams(Map<String, Object> responseData){
        if (responseData.containsKey("_version")) {
            this.setVersion((int) responseData.get("_version"));
        }

        if (responseData.containsKey("_seq_no")) {
            this.setSequenceNumber((int) responseData.get("_seq_no"));
        }

        if (responseData.containsKey("_primary_term")) {
            this.setPrimaryTerm((int) responseData.get("_primary_term"));
        }

        return this;
    }

    /**
     * Sets the sequence number of a document for use with optimistic concurrency control.
     *
     * @param number int $number Sequence Number
     *
     * @return $this
     *
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/6.8/optimistic-concurrency-control.html">...</a>
     */
    public AbstractUpdateAction setSequenceNumber(int number){
        this.setParam("if_seq_no", number);
        return this;
    }

    /**
     * Returns document version.
     *
     * @throws
     *
     * @return int Document version
     */

    public int getSequenceNumber(){
        return (int) this.getParam("if_seq_no");
    }


    public boolean hasSequenceNumber(){
        return this.hasParam("if_seq_no");
    }

    /**
     * Sets the primary term of a document for use with optimistic concurrency control.
     *
     * @param term int $term Primary Term
     *
     * @return $this
     *
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/6.8/optimistic-concurrency-control.html">...</a>
     */
    public AbstractUpdateAction setPrimaryTerm(int term){
        this.setParam("if_primary_term", term);
        return this;
    }

    /**
     * Returns document version.
     *
     * @throws
     *
     * @return int Document version
     */

    public int getPrimaryTerm()
    {
        return (int) this.getParam("if_primary_term");
    }
    public boolean hasPrimaryTerm()
    {
        return this.hasParam("if_primary_term");
    }



    /**
     * Sets the version of a document.
     *
     * @param version int $version Document version
     *
     * @return $this
     *
     * @see <a href="https://www.elastic.co/blog/versioning">...</a>
     */
    public AbstractUpdateAction setVersion(int version){
        this.setParam("version", version);
        return this;
    }

    /**
     * Returns document version.
     *
     * @throws
     *
     * @return int|string Document version
     */
    public String getVersion()
    {
        return (String) this.getParam("version");
    }

    /**
     * @return bool
     */
    public boolean hasVersion()
    {
        return  this.hasParam("version");
    }

    /**
     * Set operation type.
     *
     * @param opType string $opType Only accept create
     *
     * @return $this
     */
    public AbstractUpdateAction setOpType(String opType){
        this.setParam("op_type", opType);
        return this;
    }

    /**
     * Get operation type.
     *
     * @throws
     *
     * @return string
     */
    public String getOpType()
    {
        return (String) this.getParam("op_type");
    }

    /**
     * @return bool
     */
    public boolean hasOpType()
    {
        return this.hasParam("op_type");
    }

    /**
     * Set routing query param.
     *
     * @param value string $value routing
     *
     * @return $this
     */
    public AbstractUpdateAction setRouting(String value){
        this.setParam("routing", value);
        return this;
    }

    /**
     * Get routing parameter.
     *
     * @throws
     *
     * @return string
     */
    public String getRouting(){
        return (String) this.getParam("routing");
    }

    /**
     * @return bool
     */
    public boolean hasRouting()
    {
        return this.hasParam("routing");
    }

    /**
     * @param fields array|string $fields
     *
     * @return $this
     */
    public AbstractUpdateAction setFields(Object fields){
        if (fields instanceof String[]) {
            fields = String.join(",", (String[])fields);
        }

        this.setParam("fields", fields);
        return this;
    }

    /**
     * @return $this
     */
    public AbstractUpdateAction setFieldsSource(){
        return this.setFields("_source");
    }

    /**
     * @throws
     *
     * @return string
     */
    public String getFields(){
        return (String) this.getParam("fields");
    }

    /**
     * @return bool
     */
    public boolean hasFields()
    {
        return this.hasParam("fields");
    }

    /**
     * @param num int $num
     *
     * @return $this
     */
    public AbstractUpdateAction setRetryOnConflict(int num){
        this.setParam("retry_on_conflict", num);
        return this;
    }

    /**
     * @throws
     *
     * @return int
     */
    public int getRetryOnConflict(){
        return (int) this.getParam("retry_on_conflict");
    }

    /**
     * @return bool
     */
    public boolean hasRetryOnConflict()
    {
        return this.hasParam("retry_on_conflict");
    }

    /**
     * @param refresh bool|string $refresh
     *
     * @phpstan-param bool|Reindex::REFRESH_* $refresh
     *
     * @return $this
     */
    public AbstractUpdateAction setRefresh(boolean refresh){
        if (refresh) {
            refresh = Boolean.parseBoolean(Reindex.REFRESH_TRUE);
        }else{
            refresh = Boolean.parseBoolean(Reindex.REFRESH_FALSE);
        }

        this.setParam(Reindex.REFRESH, refresh);
        return this;
    }

    /**
     * @throws
     *
     * @return bool|string
     */
    public Object getRefresh(){
        Object refresh = this.getParam("refresh");

        return refresh.equals(Reindex.REFRESH_TRUE) || refresh.equals(Reindex.REFRESH_FALSE)
                ? refresh.equals(Reindex.REFRESH_TRUE)
                : refresh;
    }

    /**
     * @return bool
     */
    public boolean hasRefresh()
    {
        return this.hasParam(Reindex.REFRESH);
    }

    /**
     * @param timeout string $timeout
     *
     * @return $this
     */
    public AbstractUpdateAction setTimeout(String timeout){
        this.setParam("timeout", timeout);
        return this;
    }

    /**
     * @throws
     *
     * @return bool
     */
    public String getTimeout(){
        return (String) this.getParam("timeout");
    }

    /**
     * @return bool
     */
    public boolean hasTimeout()
    {
        return this.hasParam("timeout");
    }

    /**
     * @param timeout string $timeout
     *
     * @return $this
     */
    public AbstractUpdateAction setConsistency(String timeout){
        this.setParam("consistency", timeout);
        return this;
    }



    /**
     * @return bool
     */
    public boolean hasConsistency()
    {
        return this.hasParam("consistency");
    }

    /**
     * @param timeout string
     *
     * @return $this
     */
    public AbstractUpdateAction setReplication(String timeout){
        this.setParam("replication", timeout);
        return this;
    }

    /**
     * @throws
     *
     * @return string
     */

    public AbstractUpdateAction getReplication()
    {
        return (AbstractUpdateAction) this.getParam("replication");
    }

    /**
     * @return bool
     */
    public boolean hasReplication()
    {
        return this.hasParam("replication");
    }

    /**
     * @param data array|Document $data
     *
     * @return $this
     */
    public AbstractUpdateAction setUpsert(Object data){
        Document doc = Document.create(data);
        this._upsert = doc;
        return this;

    }


    /**
     * @return Document
     */
    public Document getUpsert()
    {
        return this._upsert;
    }
    /**
     * @return bool
     */
    public boolean hasUpsert()
    {
        return null != this._upsert;
    }

    /**
     * @param array $fields if empty array all options will be returned
     *
     * @return array
     */
//    public function getOptions(array $fields = [])
//    {
//        if ($fields) {
//            return \array_filter(\array_intersect_key($this->getParams(), \array_flip($fields)));
//        }
//
//        return \array_filter($this->getParams());
//    }
    public Map<String, Object> getOptions(String[] fields)
    {
        if (fields.length>0) {
            return this.getParams().entrySet().stream()
                    .filter(entry -> Arrays.asList(fields).contains(entry.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        return this.getParams();
    }

    /**
     * @return array
     */
    public Map<String, Object> getParams()
    {
        Map<String, Object> params = super.getParams();

        if (this.hasUpsert()) {
            params.put("upsert", this.getUpsert().toArray());
        }

        return params;
    }
}
