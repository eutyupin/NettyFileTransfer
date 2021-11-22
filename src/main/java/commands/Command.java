package commands;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS,
        property = "commandType")
public abstract class Command implements Serializable {

}
