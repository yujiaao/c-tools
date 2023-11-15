package com.bixuebihui.jdbc.event;


import java.util.function.Consumer;

public interface ChangeEventConsumer<ID> extends Consumer<ChangeEvent<ID>> {

}
