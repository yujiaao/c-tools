package com.bixuebihui.elasticsearch.script;

import com.bixuebihui.elasticsearch.query.Query;

interface BuilderInterface
{
    /**
     * Builds a ResultSet given a specific response and query.
     */
     ResultSet buildResultSet(Response $response, Query $query) ;
}
