package com.bixuebihui.elasticsearch.query;

/**
 * Percolate query.
 *
 * @author Boris Popovschi zyqsempai@mail.ru
 *
 *  https://www.elastic.co/guide/en/elasticsearch/reference/5.0/query-dsl-percolate-query.html
 * Class Percolate.
 */
public class Percolate extends Query
{
    /**
     * The field of type percolator and that holds the indexed queries. This is a required parameter.
     *
     * @param field
     *
     * @return this
     */
    public Percolate setField(String field)
    {
        return (Percolate) this.setParam("field", field);
    }

    /**
     * The source of the document being percolated.
     *
     * @param document
     *
     * @return this
     */
    public Percolate setDocument(String document)
    {
        return (Percolate) this.setParam("document", document);
    }

    /**
     * The index the document resides in.
     *
     * @param index
     *
     * @return this
     */
    public Percolate setDocumentIndex(String index)
    {
        return (Percolate) this.setParam("index", index);
    }

    /**
     * The type of the document to fetch.
     *
     * @param type
     *
     * @return this
     */
    public Percolate setExistingDocumentType(String type)
    {
        return (Percolate) this.setParam("type", type);
    }

    /**
     * The id of the document to fetch.
     *
     * @param id
     *
     * @return this
     */
    public Percolate setDocumentId(int id)
    {
        return (Percolate) this.setParam("id", id);
    }

    /**
     * Optionally, routing to be used to fetch document to percolate.
     *
     * @param routing
     *
     * @return this
     */
    public Percolate setDocumentRouting(String routing)
    {
        return (Percolate) this.setParam("routing", routing);
    }

    /**
     * Optionally, preference to be used to fetch document to percolate.
     *
     * @param preference
     *
     * @return this
     */
    public Percolate setDocumentPreference(String preference)
    {
        return (Percolate) this.setParam("preference", preference);
    }

    /**
     * Optionally, the expected version of the document to be fetched.
     *
     * @param version
     *
     * @return this
     */
    public Percolate setDocumentVersion(String version)
    {
        return (Percolate) this.setParam("version", version);
    }
}
