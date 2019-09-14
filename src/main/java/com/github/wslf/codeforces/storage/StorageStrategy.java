package com.github.wslf.codeforces.storage;

/**
 * Strategy to write and read data.
 *
 * @param <V> type of the data to be stored
 * @param <K> type of the identifier (Key) using which we write and read data.
 * @param <T> type of optional parameters
 */
public interface StorageStrategy<V, K, T> {
  void save(V data, K identifier, T parameters) throws StorageException;

  V load(K identifier, T parameters) throws StorageException;
}
