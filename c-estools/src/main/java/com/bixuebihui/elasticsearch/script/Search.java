package com.bixuebihui.elasticsearch.script;

import com.bixuebihui.elasticsearch.query.Query;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Elastica search object.
 *
 * @author   Nicolas Ruflin <spam@ruflin.com>
 *
 * @phpstan-import-type TCreateQueryArgs from Query
 */
class Search
{
    /*
     * Options
     */
    public static final String OPTION_SEARCH_TYPE = "search_type";
    public static final String OPTION_ROUTING = "routing";
    public static final String OPTION_PREFERENCE = "preference";
    public static final String OPTION_VERSION = "version";
    public static final String OPTION_TIMEOUT = "timeout";
    public static final String OPTION_FROM = "from";
    public static final String OPTION_SIZE = "size";
    public static final String OPTION_SCROLL = "scroll";
    public static final String OPTION_SCROLL_ID = "scroll_id";
    public static final String OPTION_QUERY_CACHE = "query_cache";
    public static final String OPTION_TERMINATE_AFTER = "terminate_after";
    public static final String OPTION_SHARD_REQUEST_CACHE = "request_cache";
    public static final String OPTION_FILTER_PATH = "filter_path";
    public static final String OPTION_TYPED_KEYS = "typed_keys";

    /*
     * Search types
     */
    public static final String OPTION_SEARCH_TYPE_DFS_QUERY_THEN_FETCH = "dfs_query_then_fetch";
    public static final String OPTION_SEARCH_TYPE_QUERY_THEN_FETCH = "query_then_fetch";
    public static final String OPTION_SEARCH_TYPE_SUGGEST = "suggest";
    public static final String OPTION_SEARCH_IGNORE_UNAVAILABLE = "ignore_unavailable";
    /**
     * Array of indices names.
     *
     * @var string[]
     */
    protected Map<String, Index> _indices = new HashMap<>();

    /**
     * @var Query
     */
    protected Query _query;

    /**
     * @var array
     */
    protected Map _options = new HashMap<>();

    /**
     * Client object.
     *
     * @var Client
     */
    protected Client _client;

    /**
     * @var BuilderInterface|null
     */
    private com.bixuebihui.elasticsearch.script.BuilderInterface builder;

    public Search(Client $client, BuilderInterface $builder)
    {
        this._client = $client;
        this.builder = $builder!=null ? $builder: new DefaultBuilder();
    }

    /**
     * Adds a index to the list.
     */

    public Search addIndex(Index index)
    {
        this._indices.put(index.getName(), index);
        return this;
    }


//
//    /**
//     * Adds an index to the list.
//     */
//    public function addIndexByName(string $index): self
//    {
//        $this->_indices[] = $index;
//
//        return $this;
//    }
//
//    /**
//     * Add array of indices at once.
//     *
//     * @param Index[] $indices
//     */
//    public function addIndices(array $indices = []): self
//    {
//        foreach ($indices as $index) {
//            if (!$index instanceof Index) {
//                throw new InvalidException('Invalid param type for addIndices(), expected Index[]');
//            }
//
//            $this->addIndex($index);
//        }
//
//        return $this;
//    }
//
//    /**
//     * @param string[] $indices
//     */
//    public function addIndicesByName(array $indices = []): self
//    {
//        foreach ($indices as $index) {
//            if (!\is_string($index)) {
//                throw new InvalidException('Invalid param type for addIndicesByName(), expected string[]');
//            }
//            $this->addIndexByName($index);
//        }
//
//        return $this;
//    }
//
    /**
     * @param query AbstractQuery|AbstractSuggest|array|Collapse|Query|string|Suggest|null $query
     *
     * @phpstan-param TCreateQueryArgs $query
     */
    public Search setQuery(Query query)
    {
        this._query = query;
        return this;
    }


    /**
     * @param value mixed $value
     */
    public Search setOption(String key, Object value)
    {
        this.validateOption(key);

        this._options.put(key, value);

        return this;
    }

    public Search setOptions(Map<String, Object> options)
    {
        this._options.clear();
        for (Map.Entry<String, Object> entry : options.entrySet()) {
            this.setOption(entry.getKey(), entry.getValue());
        }
        return this;
    }


    public Search clearOptions()
    {
        this._options = new HashMap<>();
        return this;
    }



    /**
     * @param value mixed $value
     */

    public Search addOption(String key, Object value)
    {
        this.validateOption(key);

        List<Object> list = (List<Object>) this._options.get(key);
        if(list==null)
        {
            list = new ArrayList<>();
            this._options.put(key, list);
        }
        list.add(value);

        return this;
    }


    public boolean hasOption(String key)
    {
        return this._options.containsKey(key);
    }



    /**
     *
     * @return mixed
     */
    public Object getOption(String key)
    {
        if (!this.hasOption(key)) {
            throw new IllegalArgumentException("Option "+key+" does not exist");
        }

        return this._options.get(key);
    }

    public Map<String, Object> getOptions()
    {
        return this._options;
    }


    /**
     * Return client object.
     */
    public Client getClient()
    {
        return this._client;
    }

    /**
     * Return array of indices names.
     *
     * @return string[]
     */
    public Map getIndices()
    {
        return this._indices;
    }

    public boolean hasIndices()
    {
        return (this._indices.size()) > 0;
    }

    public boolean hasIndex(Index $index)
    {
        return this.hasIndexByName($index.getName());
    }

    public boolean hasIndexByName(String index)
    {
        return this._indices.containsKey(index);
    }



    public Query getQuery()
    {
        if (null == this._query) {
            this._query =  Query.match_all();
        }

        return this._query;
    }

    /**
     * Creates new search object.
     */
    public static Search create(SearchableInterface searchObject)
    {
        return searchObject.createSearch(null, null);
    }

    /**
     * Combines indices to the search request path.
     */
    public String getPath()
    {
        if (this.hasOption(this.OPTION_SCROLL_ID)) {
            return "_search/scroll";
        }

        return String.join(",", (String[])this.getIndices().keySet().toArray()) + "/_search";
    }

    /**
     * Search in the set indices.
     *
     * @param AbstractQuery|AbstractSuggest|array|Collapse|Query|string|Suggest|null $query
     *
     * @phpstan-param TCreateQueryArgs $query
     *
     * @param array<string, mixed>|null $options associative array of options (option=>value)
     *
     */
    public ResultSet search(Query _query, Map options , String method )

    {

        this.setOptionsAndQuery(options, _query);
        Query query = this.getQuery();
        String path = this.getPath();
        Map<String, Object> params = this.getOptions();


        // Send scroll_id via raw HTTP body to handle cases of very large (> 4kb) ids.

        Map<String, Object> data = null;
        if ("_search/scroll".equals(path)) {
            data = new HashMap<>();
            data.put(this.OPTION_SCROLL_ID, params.get(this.OPTION_SCROLL_ID));
            params.remove(this.OPTION_SCROLL_ID);
        } else {
            data = query.toArray();
        }

        Response response = this.getClient().request(path, method, data, params);
        return this.getResultSetBuilder().buildResultSet(response, query);



    }



    /**
     * @param array|Query|Query\AbstractQuery|string $query
     * @param bool                                   $fullResult By default only the total hit count is returned. If set to true, the full ResultSet including aggregations is returned
     *
     *
     * @return int|ResultSet
     */
//    public function count($query = '', bool $fullResult = false, string $method = Request::POST)
//    {
//        $this->setOptionsAndQuery(null, $query);
//        // Clone the object as we do not want to modify the original query.
//        $query = clone $this->getQuery();
//        $query->setSize(0);
//        $query->setTrackTotalHits(true);
//        $path = $this->getPath();
//        $response = $this->getClient()->request(           $path,            $method,            $query->toArray(),            [OPTION_SEARCH_TYPE => OPTION_SEARCH_TYPE_QUERY_THEN_FETCH]        );
//        $resultSet = $this->builder->buildResultSet($response, $query);
//        return $fullResult ? $resultSet : $resultSet->getTotalHits();
//    }
    public int count(Query query, String method)
    {
        this.setOptionsAndQuery(null, query);
        // Clone the object as we do not want to modify the original query.
        Query query1 = ObjectUtils.clone(this.getQuery());
        query1.setSize(0);
        query1.setTrackTotalHits(true);
        String path = this.getPath();
        Response response = this.getClient().request(           path,            method,            query1.toArray(),            Map.of(OPTION_SEARCH_TYPE, OPTION_SEARCH_TYPE_QUERY_THEN_FETCH)        );
        ResultSet resultSet = this.builder.buildResultSet(response, query1);
        return resultSet.getTotalHits();
    }

    /**
     * @param array<string, mixed>|null                                              $options
     * @param AbstractQuery|AbstractSuggest|array|Collapse|Query|string|Suggest|null $query
     *
     * @phpstan-param TCreateQueryArgs $query
     */

    public Search setOptionsAndQuery(Map<String, Object> options, Query query)
    {
        if (null != query) {
            this.setQuery(query);
        }

        if (null != options) {
            if (options.containsKey("limit")) {
                this.getQuery().setSize((Integer) options.get("limit"));
                options.remove("limit");
            }

            if (options.containsKey("explain")) {
                this.getQuery().setExplain((Boolean) options.get("explain"));
                options.remove("explain");
            }

            this.setOptions(options);
        }

        return this;
    }



//
//    public function setSuggest(Suggest $suggest): self
//    {
//        return $this->setOptionsAndQuery([OPTION_SEARCH_TYPE_SUGGEST => 'suggest'], $suggest);
//    }
//
//    /**
//     * Returns the Scroll Iterator.
//     *
//     * @see Scroll
//     */
//    public function scroll(string $expiryTime = '1m'): Scroll
//    {
//        return new Scroll($this, $expiryTime);
//    }

    public com.bixuebihui.elasticsearch.script.BuilderInterface getResultSetBuilder()
    {
        return this.builder;
    }

    /**
     */
    protected void validateOption(String $key)
    {
        switch ($key) {
            case OPTION_SEARCH_TYPE:
            case OPTION_ROUTING:
            case OPTION_PREFERENCE:
            case OPTION_VERSION:
            case OPTION_TIMEOUT:
            case OPTION_FROM:
            case OPTION_SIZE:
            case OPTION_SCROLL:
            case OPTION_SCROLL_ID:
            case OPTION_SEARCH_TYPE_SUGGEST:
            case OPTION_SEARCH_IGNORE_UNAVAILABLE:
            case OPTION_QUERY_CACHE:
            case OPTION_TERMINATE_AFTER:
            case OPTION_SHARD_REQUEST_CACHE:
            case OPTION_FILTER_PATH:
            case OPTION_TYPED_KEYS:
                return;
        }

        throw new IllegalArgumentException("Invalid option "+$key);
    }

}
