package edu.yu.cs.com1320.project.stage5;

import java.io.IOException;

public interface PersistenceManager<Key,Value> {
    void serialize(Key key, Value val) throws IOException;
    Value deserialize(Key key) throws IOException;
}
