package com.bixuebihui.jdbc.event;

import org.glassfish.jaxb.core.v2.model.core.ID;

import java.util.function.Consumer;

public interface ChangeEventConsumer<ID> extends Consumer<ChangeEvent<ID>> {

}
