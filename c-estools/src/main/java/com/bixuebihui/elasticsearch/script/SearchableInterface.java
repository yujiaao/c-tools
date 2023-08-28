package com.bixuebihui.elasticsearch.script;

import com.bixuebihui.elasticsearch.query.Query;

import java.util.Map;

/**
 * Elastica searchable interface.
 *
 * @author Thibault Duplessis <thibault.duplessis@gmail.com>
 *
 * @phpstan-import-type TCreateQueryArgs from Query
 * @phpstan-import-type TCreateQueryArgsMatching from Query
 */
interface SearchableInterface
{
    /**
     * Searches results for a query.
     *
     * {
     *     "from" : 0,
     *     "size" : 10,
     *     "sort" : {
     *          "postDate" : {"order" : "desc"},
     *          "user" : { },
     *          "_score" : { }
     *      },
     *      "query" : {
     *          "term" : { "user" : "kimchy" }
     *      }
     * }
     *
     * @param AbstractQuery|AbstractSuggest|array|Collapse|Query|string|Suggest|null $query Array with all query data inside or a Elastica\Query object
     *
     * @phpstan-param TCreateQueryArgs $query
     *
     * @param array<string, mixed>|null $options associative array of options (option=>value)
     * @param string                    $method  Request method, see Request's constants
     *

     */
    public ResultSet  search(Query $query , Map $options , String $method );

    /**
     * Counts results for a query.
     *
     * If no query is set, matchall query is created
     *
     * @param AbstractQuery|array|Query|string|null $query Array with all query data inside or a Elastica\Query object
     *
     * @phpstan-param TCreateQueryArgsMatching $query
     *
     * @param string $method Request method, see Request's constants
     *
     * @return int number of documents matching the query
     */

    public int count(Query query , String method );

    /**
     * @param AbstractQuery|AbstractSuggest|array|Collapse|Query|string|Suggest|null $query
     *
     * @phpstan-param TCreateQueryArgs $query
     *
     * @param array<string, mixed>|null $options
     */
    Search createSearch(Query query, Map $options);
}
