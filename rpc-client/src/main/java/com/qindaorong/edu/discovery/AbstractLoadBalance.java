package com.qindaorong.edu.discovery;

import java.util.List;

public abstract class AbstractLoadBalance implements LoadBalanceStrategy {

    @Override
    public String selectOne(List<String> repos) {
        if(repos== null || repos.isEmpty()){
            return null;
        }
        if(repos.size() ==1 ){
            return repos.get(0);
        }
        return this.doSelect(repos);
    }

    public abstract String doSelect(List<String> repos);
}
