package com.pofo.elasticsearch.repository;

import com.pofo.elasticsearch.document.TechStackAutoComplete;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TechStackAutoCompleteRepository extends ElasticsearchRepository<TechStackAutoComplete, String> {
}
