package com.qindaorong.edu.discovery;

import java.util.List;

public interface LoadBalanceStrategy {

    String selectOne(List<String> repos);
}
