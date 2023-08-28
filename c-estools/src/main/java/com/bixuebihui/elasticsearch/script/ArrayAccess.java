package com.bixuebihui.elasticsearch.script;

interface ArrayAccess {
/* Methods */
boolean offsetExists(Object $offset);
Object offsetGet(Object $offset);
void offsetSet(Object $offset, Object $value);
void offsetUnset(Object $offset);
}
