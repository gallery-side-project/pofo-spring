package org.pofo.infra.elasticsearch.repository;

import org.pofo.infra.elasticsearch.document.TechStackAutoComplete;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TechStackAutoCompleteRepository extends ElasticsearchRepository<TechStackAutoComplete, String> { }
