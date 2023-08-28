package com.bixuebihui.elasticsearch.script;

import com.bixuebihui.elasticsearch.query.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

class DefaultBuilder implements BuilderInterface
{
    /**
     * Builds a ResultSet for a given Response.
     */
    public ResultSet buildResultSet(Response $response, Query $query)
    {
        List<Result> $results = this.buildResults($response);

        return new ResultSet($response, $query, $results);
    }

    /**
     * Builds individual result objects.
     *
     * @return Result[]
     */
    private List<Result> buildResults(Response response)
    {
        Map $data = response.getData();
        List<Result> results = new ArrayList<>();

        if (!((Map)$data.get("hits")).containsKey("hits")) {
            return results;
        }

        for(Map hit: (List<Map>)(((Map)$data.get("hits")).get("hits"))) {
            results.add(new Result(hit));
        }

        return results;
    }
}
