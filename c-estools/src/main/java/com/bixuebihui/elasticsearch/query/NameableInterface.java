package com.bixuebihui.elasticsearch.query;

/**
 * Interface for named objects.
 *
 *
 * @author Evgeniy Sokolov <ewgraf@gmail.com>
 */
interface NameableInterface
{
    /**
     * Retrieve the name of this object.
     *
     * @return string
     */
     String getName();

    /**
     * Set the name of this object.
     *
     * @param  name
     *
     * @return this
     */
    NameableInterface setName(String name);
}
